package com.nthexperience.desktop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.*;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import org.json.JSONArray;
import java.util.*;

/** The desktop owns the window. This view owns one desktop's transcript and composer. */
final class ElevenNotaChat extends LinearLayout {
    interface Desktop {
        List<TenNotaActions.Target> notaAppTargets();
        String performNotaAction(TenNotaActions.Action action);
        View notaActionIcon(TenNotaActions.Action action);
        void open(String id);
    }

    final ElevenChatStore store;
    final EditText question;
    final Button send, web;
    final Switch cloud;
    final LinearLayout transcript;
    final ScrollView scroll;
    private final SharedPreferences consent;
    private final NotaSettingsClient client;
    private final Map<LiveAnswer, TenNotaActions.Plan> localChoices = new WeakHashMap<>();
    private TextView progress;
    private String pending;
    private boolean contentOnly, restoring;
    private final Context c;

    ElevenNotaChat(Context c, String desktop) {
        this(c, desktop, LiveQuestionHttp::new);
    }

    ElevenNotaChat(Context c, String desktop, NotaSettingsClient.Factory transport) {
        super(c);
        this.c = c;
        store = new ElevenChatStore(c, desktop);
        store.save();
        consent = ElevenChatConsent.preferences(c);
        client = new NotaSettingsClient(consent, this::stop, transport, java.time.Clock.systemDefaultZone());
        setOrientation(VERTICAL);
        setBackgroundColor(NotaDesign.CANVAS);
        setFocusableInTouchMode(true);
        setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        scroll = new ScrollView(c);
        scroll.setFillViewport(true);
        transcript = Aero.column(c);
        scroll.addView(transcript);
        addView(scroll, new LayoutParams(-1, 0, 1));
        LinearLayout footer = Aero.column(c);
        footer.setPadding(dp(8), dp(4), dp(8), 0);
        addView(footer, new LayoutParams(-1, -2));
        LinearLayout field = new LinearLayout(c);
        field.setGravity(Gravity.CENTER_VERTICAL);
        field.setBackground(NotaDesign.surface(c, 0xfffcfdff, 8));
        footer.addView(field, new LayoutParams(-1,-2));
        question = new EditText(c);
        question.setContentDescription("Message Nth Nota AI");
        question.setHint("Message Nth Nota AI");
        question.setTextSize(16);
        question.setTextColor(NotaDesign.TEXT);
        question.setHintTextColor(NotaDesign.MUTED);
        question.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        question.setPadding(dp(10),dp(6),dp(4),dp(6));
        question.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        question.setImeOptions(EditorInfo.IME_ACTION_SEND | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        question.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2000)});
        question.setMaxLines(3);
        field.addView(question,new LayoutParams(0,-2,1));
        send = button("↑", this::sendOrStop);
        send.setTextSize(22);
        field.addView(send, new LayoutParams(dp(44),dp(44)));
        LinearLayout modes = new LinearLayout(c);
        modes.setGravity(Gravity.CENTER_VERTICAL);
        cloud = ElevenChatConsent.control(c);
        modes.addView(cloud, new LayoutParams(0,-2,1));
        web = button("", () -> { store.web(!store.web()); updateControls(); });
        modes.addView(web, new LayoutParams(-2,-2));
        footer.addView(modes, new LayoutParams(-1,-2));
        question.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) {}
            public void afterTextChanged(Editable value) {
                if (!restoring) {
                    if (value.length()==0) contentOnly=false;
                    store.draft(value.toString(),contentOnly);
                }
                updateControls();
            }
        });
        question.setOnEditorActionListener((v,action,event) -> {
            if(action != EditorInfo.IME_ACTION_SEND) return false;
            if(pending==null) sendOrStop();
            return true;
        });
        restoreDraft();
        render(false);
    }

    private int dp(int n) { return Aero.dp(c,n); }
    private Button button(String text, Runnable click) {
        Button b=NotaDesign.action(c,text,click);
        TenAppearance.accentText(b);
        return b;
    }
    private TextView label(String text, int size, boolean bold) {
        TextView t=NotaReplyView.text(c,text,size,NotaDesign.TEXT);
        if(bold) t.setTypeface(Aero.face(c),Typeface.BOLD);
        return t;
    }
    private void restoreDraft() {
        restoring=true;
        contentOnly=store.draftIsContent();
        question.setText(store.draft());
        question.setSelection(question.length());
        restoring=false;
        updateControls();
    }
    void offerDraft(String text, boolean asContent) {
        Runnable replace=()->{
            contentOnly=asContent;
            question.setText(text);
            question.setSelection(question.length());
            question.requestFocus();
        };
        if(question.length()==0 || question.getText().toString().equals(text)) replace.run();
        else Aero.dialog(c).setTitle("Replace this question?").setMessage("Use the new text in your composer?")
                .setNegativeButton("Keep draft",null).setPositiveButton("Replace",(d,w)->replace.run()).show();
    }
    private void updateControls() {
        boolean busy=pending!=null;
        send.setEnabled(busy || !question.getText().toString().trim().isEmpty());
        send.setText(busy?"■":"↑");
        send.setContentDescription(busy?"Stop reply":"Send to Nth Nota AI");
        web.setText(store.web()?"Web · Always":"Web · Auto");
        web.setContentDescription(store.web()?"Web search always. Switch to automatic":"Web search automatic. Switch to always");
    }
    private void sendOrStop() {
        if(pending!=null) { stop(); return; }
        String text=question.getText().toString().trim();
        if(text.isEmpty()) return;
        Desktop home=c instanceof Desktop?(Desktop)c:null;
        TenNotaActions.Plan plan=contentOnly?null:TenNotaActions.resolve(text,
                home==null?Collections.emptyList():home.notaAppTargets());
        JSONArray history=store.conversations.current().context();
        if(plan!=null) {
            String message=plan.autoOpen && home!=null ? home.performNotaAction(plan.actions.get(0)) : plan.message.replace("above","below");
            pending=text;
            question.setText("");
            LiveAnswer reply=LiveAnswer.plain(message,"On this device");
            localChoices.put(reply,plan);
            finish(reply,false);
            return;
        }
        pending=text;
        question.setText("");
        String setting=contentSetting(text);
        if(setting!=null){
            finish(LiveAnswer.plain(setting,"On this device"),false);
            return;
        }
        if(!consent.getBoolean(ElevenChatConsent.KEY,false)) {
            finish(LiveAnswer.plain("Cloud questions are off. Turn on the switch below to chat with Nth Nota AI. You can still open apps and change launcher settings with local commands.","On this device"),false);
            return;
        }
        render(true);
        client.ask(text,settingsContext(),history,store.web(),stage->{
            if(progress!=null) progress.setText(stage);
        },reply->finish(reply,!reply.searchFallback));
    }
    private String contentSetting(String question) {
        String q=LiveQuestionPolicy.words(question);
        if(q.matches("(what (is|s) (my|the|my current)|show my) (current )?(app theme|taskbar alignment|transparency effects)")) {
            for(String line:settingsContext().split("\n")) {
                if(q.endsWith(LiveQuestionPolicy.words(line.substring(0,line.indexOf(':'))))) return line;
            }
        }
        return null;
    }
    private String settingsContext() {
        SharedPreferences settings=c.getSharedPreferences("desktop",Context.MODE_PRIVATE);
        return "App theme: "+(TenAppearance.dark(c)?"Dark":"Light")
                +"\nTaskbar alignment: "+("Left".equals(settings.getString("eleven_taskbar_alignment","Left"))?"Left":"Center")
                +"\nTransparency effects: "+(settings.getBoolean("ten_transparency",false)?"On":"Off");
    }
    void stop() {
        client.cancel();
        if(pending!=null) finish(LiveAnswer.plain("Reply stopped. You can edit the question and send it again.","Stopped"),false);
    }
    void close() { stop(); client.close(); }
    private boolean following() { return transcript.getHeight()-scroll.getHeight()-scroll.getScrollY()<dp(90); }
    private void finish(LiveAnswer reply, boolean includeInContext) {
        if(pending==null) return;
        boolean follow=progress==null || following();
        store.conversations.append(pending,reply,includeInContext);
        pending=null;
        store.save();
        render(follow);
    }
    void newChat() {
        stop();
        if(store.conversations.current().turns.isEmpty() && question.length()>0) {
            // Keep an unsent question when opening a new conversation.
            store.conversations.startBlankChat();
        } else store.conversations.newChat();
        switched();
    }
    void recent() {
        List<NotaConversation.Chat> chats=new ArrayList<>(store.conversations.chats);
        String[] names=new String[chats.size()];
        for(int i=0;i<names.length;i++) names[i]=(chats.get(i)==store.conversations.current()?"●  ":"")+chats.get(i).title();
        Aero.dialog(c).setTitle("Recent chats").setItems(names,(d,i)->{
            stop();store.conversations.select(chats.get(i).id);switched();
        }).setNegativeButton("Close",null).show();
    }
    void delete() {
        Aero.dialog(c).setTitle("Delete this conversation?").setMessage("Remove its messages and draft from this device?")
            .setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{
                stop();store.conversations.deleteCurrent();switched();
            }).show();
    }
    private void switched() { store.save(); restoreDraft(); render(true); }
    private void render(boolean latest) {
        int oldY=scroll.getScrollY();
        progress=null;
        transcript.removeAllViews();
        NotaConversation.Chat chat=store.conversations.current();
        if(chat.turns.isEmpty() && pending==null) welcome();
        for(NotaConversation.Turn turn:chat.turns) {
            user(turn.question);
            reply(turn);
        }
        if(pending!=null) {
            user(pending);
            progress=label("Connecting to Nth Nota AI…",15,false);
            progress.setAccessibilityLiveRegion(ACCESSIBILITY_LIVE_REGION_POLITE);
            transcript.addView(progress);
        }
        updateControls();
        scroll.post(()->{
            View last=transcript.getChildAt(transcript.getChildCount()-1);
            scroll.scrollTo(0,latest && last!=null?Math.max(0,last.getTop()-dp(8)):oldY);
        });
    }
    private void welcome() {
        TextView title=label("What’s on your mind?",24,true);
        title.setPadding(0,dp(8),0,dp(12));
        transcript.addView(title);
        String[][] prompts={{"Explore an idea","Why does the sky change color at sunset?"},
                {"Make something","Help me write a warm thank-you message."},
                {"Plan my day","Help me plan a relaxed, productive day."},
                {"Shape my desktop","How can I make my desktop feel calmer?"}};
        for(String[] prompt:prompts) {
            Button b=button(prompt[0]+"  ↗",()->offerDraft(prompt[1],true));
            b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            b.setContentDescription(prompt[0]);
            b.setBackground(NotaDesign.surface(c,NotaDesign.SURFACE,8));
            LayoutParams p=new LayoutParams(-1,-2);
            p.bottomMargin=dp(6);transcript.addView(b,p);
        }
    }
    private void user(String text) {
        TextView bubble=label(text,16,false);
        bubble.setTextIsSelectable(true);
        bubble.setBackground(NotaDesign.surface(c,0xffe7ecf8,12));
        bubble.setPadding(dp(12),dp(9),dp(12),dp(9));
        LayoutParams p=new LayoutParams(-2,-2);
        p.gravity=Gravity.END;p.leftMargin=dp(18);p.bottomMargin=dp(18);
        transcript.addView(bubble,p);
    }
    private void reply(NotaConversation.Turn turn) {
        LiveAnswer a=turn.answer;
        LinearLayout block=Aero.column(c);
        TextView byline=label("Nth Nota AI",13,true);
        TenAppearance.accentText(byline);
        byline.setPadding(0,0,0,dp(8));block.addView(byline);
        block.addView(new NotaReplyView(c,a.text));
        for(LiveAnswer.Source source:a.sources) {
            String host=Uri.parse(source.url).getHost();
            Button link=button(source.title+"  ↗\n"+host,()->open(source.url));
            link.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            link.setContentDescription("Open source: "+source.title+", "+host);
            link.setBackground(NotaDesign.surface(c,NotaDesign.SURFACE,8));
            link.setPadding(dp(12),dp(8),dp(12),dp(8));
            LayoutParams p=new LayoutParams(-1,-2);p.bottomMargin=dp(6);block.addView(link,p);
        }
        TenNotaActions.Plan plan=localChoices.get(a);
        if(plan!=null && c instanceof Desktop) for(TenNotaActions.Action action:plan.actions) {
            Desktop home=(Desktop)c;
            LinearLayout row=new LinearLayout(c);
            row.setGravity(Gravity.CENTER_VERTICAL);row.setMinimumHeight(dp(48));
            row.addView(home.notaActionIcon(action),new LayoutParams(dp(32),dp(32)));
            TextView caption=label(action.label,15,true);caption.setPadding(dp(8),0,0,0);
            row.addView(caption,new LayoutParams(0,-2,1));row.setContentDescription(action.label);
            row.setFocusable(true);row.setBackground(Aero.selection(c));
            row.setOnClickListener(v->home.performNotaAction(action));block.addView(row);
        }
        if(a.searchFallback) block.addView(button("Search the web  ↗",()->open("https://www.google.com/search?q="+Uri.encode(turn.question))));
        TextView meta=label(a.kind+(a.checkedAt>0?" · "+java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT).format(new Date(a.checkedAt)):""),12,false);
        block.addView(meta);
        LinearLayout actions=new LinearLayout(c);
        actions.addView(button("Copy",()->NotaReplyView.copy(c,a.text)));
        actions.addView(button("Edit question",()->offerDraft(turn.question,true)));
        block.addView(actions);
        LayoutParams p=new LayoutParams(-1,-2);p.bottomMargin=dp(20);transcript.addView(block,p);
    }
    private void open(String url) {
        if(!LiveAnswer.safeLink(url)) return;
        if(c instanceof Desktop) {
            ((Desktop)c).open("favorite-web:"+url);
            return;
        }
        try { c.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); }
        catch(android.content.ActivityNotFoundException | SecurityException e) {
            Toast.makeText(c,"A browser is unavailable.",Toast.LENGTH_SHORT).show();
        }
    }
    @Override protected void onSizeChanged(int w,int h,int oldW,int oldH) {
        super.onSizeChanged(w,h,oldW,oldH);
        int margin=dp(w<dp(320)?10:20);
        transcript.setPadding(margin,dp(12),margin,dp(8));
        question.setMaxLines(h<dp(280)?1:3);
        cloud.setText(w<dp(250)?"Cloud":"Cloud questions");
    }
}
