package com.nthexperience.desktop;

import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.util.*;
import org.json.*;

/** Unified Eleven Start, measured from the November laptop recordings. */
final class StartMenu extends LinearLayout {
    interface Actions { void open(String id); void options(View anchor,String id); void home(); void settings(); boolean isHome(); }
    final Actions actions; final DesktopStore store; final TenStartData data;
    final List<AppCatalog.Entry> all=new ArrayList<>();
    final Map<String,View> tileViews=new LinkedHashMap<>();
    final EditText search; final LinearLayout searchTabs,dashboard,appList,tileGroups,rail,content;
    final ScrollView landing,apps,tiles;
    boolean searchMode,showApps,expanded; String searchFilter="All";
    private final LinearLayout footer; private int columns;

    StartMenu(Context c,Actions actions,DesktopStore store,AeroWallpaper wallpaper) {
        super(c);this.actions=actions;this.store=store;data=new TenStartData(store);
        setOrientation(VERTICAL);setClickable(true);setElevation(dp(28));setPadding(dp(24),dp(16),dp(24),0);
        setBackground(new AeroGlass(wallpaper,AeroGlass.START));setContentDescription("Windows 11 style Start menu");
        setClipToOutline(true);setOutlineProvider(new ViewOutlineProvider(){public void getOutline(View v,android.graphics.Outline o){o.setRoundRect(0,0,v.getWidth(),v.getHeight(),dp(8));}});
        search=new EditText(c);search.setSingleLine();search.setTextSize(14);search.setTypeface(Aero.face(c));
        search.setHint("Search for apps, settings, and documents");search.setContentDescription("Search apps, settings and linked files");
        search.setPadding(dp(14),0,dp(12),0);addView(search,new LayoutParams(-1,dp(44)));
        searchTabs=new LinearLayout(c);addView(searchTabs,new LayoutParams(-1,dp(44)));
        content=Aero.column(c);addView(content,new LayoutParams(-1,0,1));
        landing=new ScrollView(c);landing.setFillViewport(false);landing.setVerticalScrollBarEnabled(true);
        dashboard=Aero.column(c);tileGroups=dashboard;tiles=landing;landing.addView(dashboard);landing.setContentDescription("Pinned and all apps");
        content.addView(landing,new LayoutParams(-1,0,1));
        apps=new ScrollView(c);appList=Aero.column(c);apps.addView(appList);apps.setContentDescription("All apps");content.addView(apps,new LayoutParams(-1,0,1));
        rail=Aero.column(c);rail.setVisibility(GONE);footer=new LinearLayout(c);footer.setGravity(Gravity.CENTER_VERTICAL);addView(footer,new LayoutParams(-1,dp(56)));
        search.setOnClickListener(v->{searchMode=true;render();});
        search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int count,int after){}public void afterTextChanged(Editable e){}public void onTextChanged(CharSequence s,int st,int before,int count){render();}});
        addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{int n=ElevenLayout.pinColumns(Math.round((r-l)/getResources().getDisplayMetrics().density)-40);if(n!=columns){columns=n;render();}});
        appearanceChanged();
    }
    private int dp(int value){return Aero.dp(getContext(),value);}
    void appearanceChanged(){
        int fg=TenStyle.text(getContext());search.setTextColor(fg);search.setHintTextColor(TenStyle.light(getContext())?0xff596371:0xffb2b7bf);
        search.setBackground(ElevenSurface.round(getContext(),TenStyle.light(getContext())?0xfffafbff:0xff2b303d,20));render();
    }
    void entries(List<AppCatalog.Entry> entries){all.clear();all.addAll(entries);render();}
    void refreshName(){renderFooter();} void refreshHome(){renderFooter();}
    private List<StartSearch.Result> tools(){
        List<StartSearch.Result> out=new ArrayList<>();
        String[][] rows={{"@nota","Nth Nota","notes"},{"@files","File Explorer","file-explorer"},{"@settings","Settings","settings"},{"@browser",BrowserPanel.NAME,"browser"},{"@notes","Notepad","notes"},{"@sticky","Sticky Notes","notes"},{"@gadget:calculator","Calculator","calculator"},{"@calendar","Calendar","calendar"},{"@weather","Weather","weather"},{"@news","Widgets","widgets"},{"@photos","Photos","pictures-app"},{MediaLibrary.PLAYER,"Nth Media Player","mediaplayer"},{"@taskview","Task View","taskview"},{"@taskmanager","Task Manager","taskmanager"},{"@recycle","Recycle Bin","recycle"},{"@run","Run","run"},{"@events","Event Viewer","notes"},{"@devices","Device Manager","computer"},{"@management","Computer Management","computer"},{"@powershell","Nth PowerShell","run"}};
        for(String[] r:rows)out.add(new StartSearch.Result(r[0],r[1],"Nth tools",r[2],null,true));return out;
    }
    private StartSearch.Result resolve(String id){
        for(StartSearch.Result r:tools())if(r.id.equals(id))return r;
        for(AppCatalog.Entry a:all)if(a.id.equals(id))return new StartSearch.Result(a.id,store.name(a.id,a.label),"Installed apps","programs",a.icon,true);
        JSONObject link=FolderLinks.get(store,id);if(link!=null)return new StartSearch.Result(id,link.optString("title","Folder"),"Folders","files",null,true);
        if(store.isFolder(id))return new StartSearch.Result(id,store.name(id,"Folder"),"Folders","folderapps",null,true);return null;
    }
    private void render(){
        if(search==null)return;boolean query=search.length()>0;boolean results=query||showApps||searchMode;
        landing.setVisibility(results?GONE:VISIBLE);apps.setVisibility(results?VISIBLE:GONE);
        searchTabs.removeAllViews();searchTabs.setVisibility(results?VISIBLE:GONE);
        if(results){TextView back=command("‹ Back",()->{searchMode=false;showApps=false;search.setText("");render();});searchTabs.addView(back,new LayoutParams(dp(76),-1));
            for(String f:new String[]{"All","Apps","Settings"}){TextView tab=command(f,()->{searchFilter=f;render();});tab.setContentDescription("Search tab "+f);if(f.equals(searchFilter))tab.setBackground(ElevenSurface.round(getContext(),TenStyle.light(getContext())?0xffdae6fa:0xff394154,4));searchTabs.addView(tab,new LayoutParams(0,-1,1));}}
        renderPins();renderApps();renderFooter();
    }
    private TextView command(String title,Runnable run){TextView v=TenStyle.command(getContext(),title,run);v.setTextColor(TenStyle.text(getContext()));return v;}
    private void heading(String title,String action,Runnable click){LinearLayout line=new LinearLayout(getContext());line.setGravity(Gravity.CENTER_VERTICAL);TextView label=Aero.text(getContext(),title,14,TenStyle.text(getContext()));Aero.bold(label);line.addView(label,new LayoutParams(0,dp(52),1));if(action!=null)line.addView(command(action,click),new LayoutParams(dp(136),dp(40)));dashboard.addView(line);}
    private void renderPins(){
        String focused=null;for(Map.Entry<String,View> e:tileViews.entrySet())if(e.getValue().hasFocus())focused=e.getKey();
        tileViews.clear();dashboard.removeAllViews();
        if(store.prefs.getBoolean("eleven_start_pinned",true)){
            heading("Pinned",null,null);
            GridLayout grid=new GridLayout(getContext());int ncols=Math.max(3,columns);grid.setColumnCount(ncols);int n=0;
            for(String id:store.startPins()){StartSearch.Result item=resolve(id);if(item==null)continue;View pin=shortcut(item,true);pin.setContentDescription("Pinned "+item.title);tileViews.put(id,pin);
                GridLayout.LayoutParams gp=new GridLayout.LayoutParams(GridLayout.spec(n/ncols),GridLayout.spec(n%ncols,1f));gp.width=0;gp.height=dp(82);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(pin,gp);n++;}
            dashboard.addView(grid,new LayoutParams(-1,-2));if(n==0)hint(dashboard,"Hold an app in All to pin it here.");
        }
        if(store.prefs.getBoolean("eleven_start_recent",false)){
            heading("Recent",null,null);int count=0;
            for(String id:store.recents()){StartSearch.Result item=resolve(id);if(item!=null){dashboard.addView(shortcut(item,false),new LayoutParams(-1,dp(48)));if(++count==4)break;}}
            if(count==0)hint(dashboard,"Your recently opened apps appear here.");
        }
        if(store.prefs.getBoolean("eleven_start_all",true)){
            String view=store.prefs.getString("eleven_start_view","List");heading("All","View: "+view+"  ⌄",this::viewMenu);
            List<StartSearch.Result> rows=allApps();
            if(view.equals("Grid")){
                GridLayout grid=new GridLayout(getContext());int cols=Math.max(3,columns);grid.setColumnCount(cols);int n=0;
                for(StartSearch.Result item:rows){GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(n/cols),GridLayout.spec(n%cols,1f));lp.width=0;lp.height=dp(86);grid.addView(shortcut(item,true),lp);n++;}dashboard.addView(grid,new LayoutParams(-1,-2));
            }else{
                String last="";for(StartSearch.Result item:rows){String letter=ElevenLayout.initial(item.title);if(!last.equals(letter)){TextView header=command(letter,()->letterMenu(rows));header.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);header.setContentDescription("Jump to letter "+letter);dashboard.addView(header,new LayoutParams(-1,dp(40)));last=letter;}View row=shortcut(item,false);row.setTag("start-letter:"+letter);dashboard.addView(row,new LayoutParams(-1,dp(48)));}
            }
        }
        ContextMenus.blank(landing,(x,y)->{DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Start options");menu.item("Start settings","settings",()->actions.open("ten-setting:start"));ContextMenus.show(menu,landing,x,y);});
        if(focused!=null&&tileViews.containsKey(focused))tileViews.get(focused).requestFocus();
    }
    private List<StartSearch.Result> allApps(){
        List<StartSearch.Result> rows=new ArrayList<>(tools());for(AppCatalog.Entry a:all)rows.add(new StartSearch.Result(a.id,store.name(a.id,a.label),"Installed apps","programs",a.icon,true));
        rows.sort(Comparator.comparing(r->r.title.toLowerCase(Locale.ROOT)));Set<String> seen=new HashSet<>();rows.removeIf(r->!seen.add(r.id));return rows;
    }
    private void viewMenu(){Aero.dialog(getContext()).setTitle("View all apps").setSingleChoiceItems(new String[]{"List","Grid"},store.prefs.getString("eleven_start_view","List").equals("Grid")?1:0,(d,n)->{store.prefs.edit().putString("eleven_start_view",n==0?"List":"Grid").apply();d.dismiss();renderPins();}).setNegativeButton("Cancel",null).show();}
    private void letterMenu(List<StartSearch.Result> rows){LinkedHashSet<String> initials=new LinkedHashSet<>();for(StartSearch.Result r:rows)initials.add(ElevenLayout.initial(r.title));String[] letters=initials.toArray(new String[0]);Aero.dialog(getContext()).setTitle("Jump to letter").setItems(letters,(d,n)->{View row=dashboard.findViewWithTag("start-letter:"+letters[n]);if(row!=null)landing.smoothScrollTo(0,row.getTop());}).show();}
    private void hint(LinearLayout host,String message){TextView v=Aero.text(getContext(),message,12,TenStyle.text(getContext()));v.setPadding(dp(4),dp(8),dp(4),dp(18));host.addView(v);}
    private View shortcut(StartSearch.Result item,boolean pin){
        LinearLayout row=new LinearLayout(getContext());row.setOrientation(pin?VERTICAL:HORIZONTAL);row.setGravity(pin?Gravity.CENTER:Gravity.CENTER_VERTICAL);row.setPadding(dp(6),dp(6),dp(6),dp(6));row.setBackground(TenStyle.hover(getContext(),Color.TRANSPARENT));
        View icon;if(item.icon!=null){ImageView image=new ImageView(getContext());image.setImageDrawable(item.icon);icon=image;}else icon=item.id.equals("@nota")?new TenStyle.Glyph(getContext(),"nota",TenAccent.readableOn(TenStyle.accent(getContext()),TenStyle.surface(getContext()))):new AeroIcon(getContext(),item.kind);
        row.addView(icon,new LayoutParams(dp(pin?32:28),dp(pin?32:28)));
        TextView text=Aero.text(getContext(),item.title,12,TenStyle.text(getContext()));text.setMaxLines(2);text.setEllipsize(TextUtils.TruncateAt.END);text.setGravity(pin?Gravity.CENTER:Gravity.CENTER_VERTICAL);text.setPadding(pin?0:dp(12),pin?dp(6):0,0,0);
        row.addView(text,new LayoutParams(pin?-1:0,pin?-2:-1,pin?0:1));row.setFocusableInTouchMode(true);row.setContentDescription("Open "+item.title);row.setOnClickListener(v->actions.open(item.id));ContextMenus.bind(row,(x,y)->options(row,item));
        row.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_MENU){options(row,item);return true;}if(pin&&event.getAction()==KeyEvent.ACTION_DOWN&&event.isCtrlPressed()&&(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT)){move(item.id,key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);return true;}return false;});
        return row;
    }
    private void move(String id,int direction){List<String> pins=new ArrayList<>(store.startPins());int from=pins.indexOf(id),to=from+direction;if(from<0||to<0||to>=pins.size())return;Collections.swap(pins,from,to);store.startPins(pins);renderPins();View p=tileViews.get(id);if(p!=null)p.requestFocus();}
    private void options(View anchor,StartSearch.Result item){
        PopupMenu menu=new PopupMenu(getContext(),anchor);boolean pinned=store.startPins().contains(item.id);
        if(item.shortcut)menu.getMenu().add(pinned?"Unpin from Start":"Pin to Start").setOnMenuItemClickListener(v->{List<String> ids=new ArrayList<>(store.startPins());if(pinned)ids.remove(item.id);else if(ids.size()<32)ids.add(item.id);store.startPins(ids);render();return true;});
        if(pinned){menu.getMenu().add("Move earlier").setOnMenuItemClickListener(v->{move(item.id,-1);return true;});menu.getMenu().add("Move later").setOnMenuItemClickListener(v->{move(item.id,1);return true;});}
        menu.getMenu().add("More options").setOnMenuItemClickListener(v->{actions.options(anchor,item.id);return true;});menu.show();
    }
    private void renderApps(){
        appList.removeAllViews();String query=search.getText().toString().trim();List<StartSearch.Result> found=new ArrayList<>();
        if(query.isEmpty()){if(searchMode&&!showApps){for(String id:store.recents()){StartSearch.Result r=resolve(id);if(r!=null)found.add(r);if(found.size()==6)break;}if(found.isEmpty())found.addAll(tools().subList(0,6));hint(appList,"Suggested");}else found.addAll(allApps());}
        else{found.addAll(StartSearch.find(store,all,query));for(StartSearch.Result r:tools())if(r.title.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))&&found.stream().noneMatch(i->i.id.equals(r.id)))found.add(r);}
        if(searchFilter.equals("Settings"))found.clear();
        if(!searchFilter.equals("Apps"))for(TenSettingsCatalog.Page p:TenSettingsCatalog.PAGES)if(searchFilter.equals("Settings")&&(query.isEmpty()||TenSettingsCatalog.search(query).contains(p))||!query.isEmpty()&&TenSettingsCatalog.search(query).contains(p))found.add(new StartSearch.Result("ten-setting:"+p.id,p.title,"Settings","settings",null,false));
        if(searchFilter.equals("Apps"))found.removeIf(r->r.id.startsWith("ten-setting:")||r.id.startsWith("search-file:")||r.id.startsWith("recent-file:"));
        found.sort(Comparator.comparing(r->r.title.toLowerCase(Locale.ROOT)));Set<String> seen=new HashSet<>();for(StartSearch.Result r:found)if(seen.add(r.id))appList.addView(shortcut(r,false),new LayoutParams(-1,dp(52)));
        if(seen.isEmpty())hint(appList,"No matching results.");
        if(!query.isEmpty()){appList.addView(command("Ask Nth Nota",()->actions.open("nota-query:"+android.net.Uri.encode(query))));appList.addView(command("Search linked files and folders",()->actions.open("start-search:"+android.net.Uri.encode(query))));appList.addView(command("Search the web",()->actions.open("web-search:"+android.net.Uri.encode(query))));}
    }
    private void renderFooter(){
        footer.removeAllViews();footer.setBackground(ElevenSurface.round(getContext(),TenStyle.light(getContext())?0xffe4e9f3:0xff202532,0));
        View portrait=ProfilePortrait.view(getContext(),store);footer.addView(portrait,new LayoutParams(dp(32),dp(32)));
        TextView name=command(store.prefs.getString("desktop_name","Nth Desktop Eleven"),()->actions.open("ten-setting:profile"));name.setMaxLines(1);name.setEllipsize(TextUtils.TruncateAt.END);name.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);name.setPadding(dp(10),0,0,0);footer.addView(name,new LayoutParams(0,-1,1));
        String selected=store.prefs.getString("eleven_start_folders","");List<String[]> shortcuts=new ArrayList<>();for(String[] row:new String[][]{{"settings","Settings","@settings"},{"document","Documents","@documents"},{"folder","Downloads","@downloads"},{"sound","Music","@music"},{"picture","Pictures","@pictures"},{"display","Videos","@videos"}})if(Arrays.asList(selected.split(",")).contains(row[1]))shortcuts.add(row);shortcuts.add(new String[]{"power","Power","@power"});HorizontalScrollView iconScroll=new HorizontalScrollView(getContext());iconScroll.setHorizontalScrollBarEnabled(false);LinearLayout iconRow=new LinearLayout(getContext());iconRow.setGravity(Gravity.CENTER_VERTICAL);iconScroll.addView(iconRow);footer.addView(iconScroll,new LayoutParams(dp(Math.min(160,40*shortcuts.size())),dp(48)));for(String[] a:shortcuts){View b=new TenStyle.Glyph(getContext(),a[0],TenStyle.text(getContext()));b.setFocusable(true);b.setContentDescription(a[1]);b.setTooltipText(a[1]);b.setPadding(dp(10),dp(10),dp(10),dp(10));b.setBackground(TenStyle.hover(getContext(),Color.TRANSPARENT));b.setOnClickListener(v->{if(a[2].equals("@power"))powerMenu(b);else actions.open(a[2]);});iconRow.addView(b,new LayoutParams(dp(40),dp(44)));}

    }
    private void powerMenu(View anchor){DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Start power options");menu.item("Show desktop","computer",()->actions.open("setting:show-desktop"));menu.item("Restart Nth Desktop","refresh",()->actions.open("setting:restart-desktop"));menu.item("Android power settings","power",()->actions.open("setting:android:battery"));ContextMenus.centered(anchor,(x,y)->ContextMenus.show(menu,anchor,x,y));}
    void show(boolean keyboard){searchMode=keyboard;showApps=false;landing.scrollTo(0,0);searchFilter="All";search.setText("");render();setVisibility(VISIBLE);bringToFront();requestFocus();if(!store.prefs.getBoolean("reduce_motion",false)){setAlpha(0);setTranslationY(dp(14));animate().alpha(1).translationY(0).setDuration(180).start();}}
    void hide(){animate().cancel();setAlpha(1);setTranslationY(0);((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindowToken(),0);search.clearFocus();setVisibility(GONE);}
    JSONObject state(){JSONObject s=new JSONObject();GadgetLayout.put(s,"visible",getVisibility()==VISIBLE);GadgetLayout.put(s,"query",search.getText().toString());GadgetLayout.put(s,"apps",showApps);GadgetLayout.put(s,"searchMode",searchMode);GadgetLayout.put(s,"filter",searchFilter);return s;}
    void restore(JSONObject s){if(s==null||!s.optBoolean("visible"))return;show(s.optBoolean("searchMode"));showApps=s.optBoolean("apps");searchFilter=s.optString("filter","All");search.setText(s.optString("query"));render();}
}
