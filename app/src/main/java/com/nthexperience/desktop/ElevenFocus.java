package com.nthexperience.desktop;

import android.content.*;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;

/** Focus sessions are separate from manual quiet mode and excluded from desktop backups. */
final class ElevenFocus {
    private static SharedPreferences session(Context c){return c.getSharedPreferences("eleven_focus_session",0);}
    static long remaining(Context c){SharedPreferences p=session(c);return ElevenFocusState.remaining(p.getLong("start",-1),p.getLong("end",-1),p.getInt("boot",-1),SystemClock.elapsedRealtime(),StartupExperience.boot(c));}
    static boolean quiet(Context c){return remaining(c)>0&&c.getSharedPreferences("desktop",0).getBoolean("eleven_focus_quiet",true);}
    static boolean start(Context c,int minutes){int boot=StartupExperience.boot(c);if(boot<0)return false;long now=SystemClock.elapsedRealtime();session(c).edit().putLong("start",now).putLong("end",now+ElevenFocusState.minutes(minutes)*60_000L).putInt("boot",boot).apply();TenNotifications.refresh();return true;}
    static void stop(Context c){session(c).edit().clear().apply();TenNotifications.refresh();}
    static String label(long remaining){long seconds=(remaining+999)/1000;return String.format(java.util.Locale.getDefault(),"%02d:%02d",seconds/60,seconds%60);}
    static void panel(TenSettings s){
        s.detail("Focus sessions", "Reduce distractions for a set amount of time. Android controls notification sounds and delivery.");
        boolean running=remaining(s.getContext())>0;
        ElevenSettingsCard card=s.group("Focus",running?"Session in progress":"Choose your session duration","focus",true);
        TextView action=TenStyle.command(s.getContext(),running?"Stop focus session":"▶  Start focus session",()->{
            if(remaining(s.getContext())>0)stop(s.getContext());else{
                if(!start(s.getContext(),s.store.prefs.getInt("eleven_focus_minutes",30))){Toast.makeText(s.getContext(),"A session cannot start until Android reports this phone's boot state.",Toast.LENGTH_LONG).show();return;}
                if(s.store.prefs.getBoolean("eleven_focus_show_timer",true))s.open.accept("@focus-timer");
            }s.refreshAppearance();
        });action.setContentDescription(running?"Stop focus session":"Start focus session");
        action.setGravity(Gravity.CENTER);action.setBackground(ElevenSurface.round(s.getContext(),TenStyle.accent(s.getContext()),4));action.setTextColor(TenAccent.on(TenStyle.accent(s.getContext())));
        if(s.width-s.navWidth>=s.dp(390))card.header.addView(action,Math.max(0,card.header.getChildCount()-1),new LinearLayout.LayoutParams(s.dp(180),s.dp(42)));else card.child(action);
        LinearLayout duration=new LinearLayout(s.getContext());duration.setGravity(Gravity.CENTER_VERTICAL);duration.setPadding(s.dp(10),s.dp(8),s.dp(10),s.dp(8));
        TextView title=Aero.text(s.getContext(),"Session duration",13,TenAppearance.ink(s.getContext()));duration.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        TextView amount=Aero.text(s.getContext(),"",13,TenAppearance.ink(s.getContext()));amount.setGravity(Gravity.CENTER);amount.setContentDescription("Focus duration");
        TextView minus=TenStyle.command(s.getContext(),"−",()->{int value=ElevenFocusState.minutes(s.store.prefs.getInt("eleven_focus_minutes",30)-5);s.store.prefs.edit().putInt("eleven_focus_minutes",value).apply();amount.setText(value+" min");});minus.setContentDescription("Decrease Focus duration");
        TextView plus=TenStyle.command(s.getContext(),"+",()->{int value=ElevenFocusState.minutes(s.store.prefs.getInt("eleven_focus_minutes",30)+5);s.store.prefs.edit().putInt("eleven_focus_minutes",value).apply();amount.setText(value+" min");});plus.setContentDescription("Increase Focus duration");
        amount.setText(ElevenFocusState.minutes(s.store.prefs.getInt("eleven_focus_minutes",30))+" min");duration.addView(minus,new LinearLayout.LayoutParams(s.dp(36),s.dp(44)));duration.addView(amount,new LinearLayout.LayoutParams(s.dp(62),s.dp(44)));duration.addView(plus,new LinearLayout.LayoutParams(s.dp(36),s.dp(44)));minus.setEnabled(!running);plus.setEnabled(!running);card.child(duration);
        Switch clock=ElevenSettingsPages.toggle(s,"Show the Focus timer when a session starts","eleven_focus_show_timer",true);card.child(clock);
        Switch quiet=ElevenSettingsPages.toggle(s,"Hide app notifications in the Nth feed during Focus","eleven_focus_quiet",true);quiet.setOnCheckedChangeListener((v,value)->{s.store.prefs.edit().putBoolean("eleven_focus_quiet",value).apply();TenNotifications.refresh();});card.child(quiet);
        if(running)card.child(new Countdown(s.getContext(),18));
        s.card("Do not disturb","Manage Android notification sounds and interruptions","notifications",()->s.open.accept("setting:android:dnd"));
        ElevenSettingsPages.related(s,"Notifications","notifications");
    }
    static final class Timer extends LinearLayout {
        Timer(Context c,java.util.function.Consumer<String> open){super(c);setOrientation(VERTICAL);setGravity(Gravity.CENTER);setPadding(Aero.dp(c,20),Aero.dp(c,20),Aero.dp(c,20),Aero.dp(c,20));setContentDescription("Focus session timer");addView(Aero.text(c,"Focus session",20,TenAppearance.ink(c)));addView(new Countdown(c,48),new LayoutParams(-1,Aero.dp(c,120)));addView(TenStyle.command(c,"Stop session",()->stop(c)));addView(TenStyle.command(c,"Focus settings",()->open.accept("ten-setting:focus")));}
    }
    static final class Countdown extends TextView {
        private final Runnable tick=new Runnable(){public void run(){long value=remaining(getContext());setText(value>0?label(value):"Session finished");setContentDescription("Focus time remaining: "+getText());postDelayed(this,1000);}};
        Countdown(Context c,int size){super(c);setTextSize(size);setTypeface(Aero.face(c));setTextColor(TenAppearance.ink(c));setGravity(Gravity.CENTER);setMinHeight(Aero.dp(c,56));}
        @Override protected void onAttachedToWindow(){super.onAttachedToWindow();removeCallbacks(tick);tick.run();}
        @Override protected void onDetachedFromWindow(){removeCallbacks(tick);super.onDetachedFromWindow();}
    }
    private ElevenFocus(){}
}
