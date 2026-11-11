package com.nthexperience.desktop;

import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/** Recording regression: changing taskbar height must not resnap the desktop behind it. */
public class ElevenTaskbarSmoke extends Instrumentation {
    MainActivity home;View root;DesktopStore store;int scenarios,frames,fixedScenarios,fixedFrames;
    @Override public void onCreate(Bundle args){super.onCreate(args);start();}
    @Override public void onStart(){Bundle result=new Bundle();try{
        shell("wm size 1080x2400");
        store=new DesktopStore(getTargetContext());
        store.prefs.edit().putInt("startup_mode",2).putBoolean("welcome_on_start",false).putBoolean("reduce_motion",true).putBoolean("taskbar_static",true).putBoolean("taskbar_expanded",false).commit();
        getTargetContext().getSharedPreferences(StorageAccess.PREFS,0).edit().putBoolean("seen",true).commit();
        getTargetContext().getSharedPreferences(StartupExperience.SESSION,0).edit().putBoolean("seen",true).putBoolean("welcome_seen",true).putBoolean(StartupExperience.HOME_OFFERED,true).commit();
        home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();settle();
        for(String alignment:new String[]{"Left","Center"})for(boolean landscape:new boolean[]{false,true}){
            ui(()->{store.prefs.edit().putString("eleven_taskbar_alignment",alignment).commit();home.setRequestedOrientation(landscape?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);});settle();
            scenario(alignment+(landscape?"-landscape":"-portrait"),true,true,false);
        }
        ui(()->home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));settle();
        scenario("automatic-unsaved",false,true,false);
        scenario("free-placed",true,false,false);
        scenario("scrolled",true,true,true);
        recentTaskbarStability();
        keyboardAndRows();
        runningPlacement();
        fixedControls();
        narrowRestore();
        proportionScreenshots();
        fullWidthUpperRow();
        lowerPinScroll();
        result.putString("stream","PASS: "+scenarios+" taskbar height scenarios / "+frames+" actual pre-draw frames; 21 desktop icons retain screen positions, cell sizes and icon sizes across repeated menu, Settings and Control Panel expansion/collapse; 5x9 imported-style grid at 80% icon size, left/center alignment, portrait/landscape, automatic unsaved layout, free placement and scrolled sheet; saved coordinates unchanged; unpinned Android task buttons retain order and row positions when Control Panel opens/closes; manual drag order and pin edits honored; taller taskbar only reduces visible scroll area; running and minimized apps on top, closed pins on bottom; narrow 320/360/411dp task buttons receive real restore touches with either alignment and battery preference; actual drawn Start/Search/Task View/Nota icon bounds share the app icon scale; uniform fixed/pinned button spacing; light/dark collapsed/expanded Left/Center proportion captures; fixed controls preserve identities and complete screen bounds in "+fixedScenarios+" scenarios / "+fixedFrames+" pre-draw frames\n");finish(Activity.RESULT_OK,result);
    }catch(Throwable error){result.putString("stream","FAIL: "+android.util.Log.getStackTraceString(error));try{shot("eleven-taskbar-failure");}catch(Exception ignored){}finish(Activity.RESULT_CANCELED,result);}}
    void recentTaskbarStability()throws Exception{
        List<String> probes=new ArrayList<>();for(AppCatalog.Entry entry:AppCatalog.load(home))if(entry.id.startsWith("com.nthexperience.desktop.fixture/"))probes.add(entry.id);
        check(probes.size()>=4,"Four installed Android fixture shortcuts");probes=new ArrayList<>(probes.subList(0,4));final List<String> apps=probes;
        ui(()->{for(String id:ExternalTasks.list())ExternalTasks.remove(id);for(String id:apps)ExternalTasks.opened(id);store.quickLaunch(Collections.emptyList());store.prefs.edit().putBoolean("taskbar_static",false).putBoolean("taskbar_apps",true).putBoolean("taskbar_expanded",true).commit();home.applyTenAppearance();});settle();
        check(barKeys().equals(apps),"Unpinned Android button order established");
        for(int n=0;n<2;n++){
            Map<String,String> before=barGeometry();
            ui(()->home.open("setting:page:taskbar"));settle();check(barKeys().subList(0,4).equals(apps),"Opening Control Panel appends its button after Android apps");check(barGeometry().entrySet().containsAll(before.entrySet()),"Existing buttons keep their rows while Control Panel is open");
            ui(this::closeProperties);settle();check(barKeys().equals(apps),"Closing Control Panel retains Android button order");check(barGeometry().equals(before),"Closing Control Panel does not flip existing rows");
            ui(this::toggleMenu);settle();check(barKeys().equals(apps),"Collapse retains session order");ui(this::toggleMenu);settle();check(barKeys().equals(apps),"Expansion retains session order");
        }
        List<String> manual=new ArrayList<>(apps);Collections.reverse(manual);ui(()->{store.taskbarOrder(manual);home.refreshTen();});settle();check(barKeys().equals(manual),"Explicit saved drag order wins over session order");
        ui(()->{store.quickLaunch(Collections.singletonList(apps.get(0)));home.refreshTen();});settle();check(barKeys().get(0).equals(apps.get(0)),"Changed pins reset the default pinned order");shot("eleven-taskbar-stable-android-taskbar");
    }
    void keyboardAndRows()throws Exception{
        ui(()->{store.quickLaunch(Arrays.asList("@calendar","@weather",ExternalTasks.list().get(0)));home.refreshTen();});settle();
        for(boolean fit:new boolean[]{false,true})for(boolean labels:new boolean[]{false,true}){
            ui(()->{store.prefs.edit().putBoolean("taskbar_static",fit).putBoolean("taskbar_show_labels",labels).putBoolean("taskbar_expanded",true).commit();home.refreshTen();});settle();
            ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);check(items.expanded&&items.getHeight()==Aero.dp(home,96),"Two full-height rows");
                for(int i=0;i<items.getChildCount();i++){View child=items.getChildAt(i);check(child.getHeight()==Aero.dp(home,48)&&child.getTop()==(ElevenTaskbarItems.running(child)?0:1)*Aero.dp(home,48),"Normal touch height on correct row");if(fit)check(child.getRight()<=((HorizontalScrollView)items.getParent()).getWidth(),"Static buttons fit the visible strip");}
                try{java.lang.reflect.Field field=MainActivity.class.getDeclaredField("taskbarReorder");field.setAccessible(true);Object reorder=field.get(home);java.lang.reflect.Method hit=TaskbarReorder.class.getDeclaredMethod("indexAt",float.class,float.class,String.class);hit.setAccessible(true);
                    for(int rowIndex:new int[]{0,1}){List<View> row=new ArrayList<>();for(int i=0;i<items.getChildCount();i++)if(ElevenTaskbarItems.row(items.getChildAt(i))==rowIndex)row.add(items.getChildAt(i));check(row.size()>=2,"Multiple buttons in each actual row");View target=row.get(0),moving=row.get(row.size()-1);List<String> keys=new ArrayList<>();for(int i=0;i<items.getChildCount();i++)if(items.getChildAt(i)!=moving)keys.add(TaskbarReorder.key(items.getChildAt(i)));int index=(Integer)hit.invoke(reorder,target.getLeft()+target.getWidth()/2f-1,(rowIndex*48+24)*home.getResources().getDisplayMetrics().density,TaskbarReorder.key(moving));check(index==keys.indexOf(TaskbarReorder.key(target)),"Drag chooses the target row insertion point");}

                }catch(Exception e){throw new RuntimeException(e);}

            });
        }
        List<String> before=barKeys();ui(()->{View first=find(ElevenTaskbarItems.class).getChildAt(0);check(first.requestFocusFromTouch(),"Task button receives keyboard focus after touch");check(first.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT,0,KeyEvent.META_CTRL_ON|KeyEvent.META_SHIFT_ON)),"Task button handles keyboard reorder");});settle();check(barKeys().get(1).equals(before.get(0)),"Actual keyboard reorder persists across rows");
        DesktopBackup.Plan plan=DesktopBackup.parse(DesktopBackup.snapshot(store).toString());check(Boolean.TRUE.equals(plan.values.get("taskbar_expanded")),"Expansion participates in portable backups");shot("eleven-taskbar-static-labels");
    }
    View slot(String id){ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int i=0;i<items.getChildCount();i++)if(id.equals(TaskbarReorder.key(items.getChildAt(i))))return items.getChildAt(i);return null;}
    void closeAll(){for(int i=0;i<40;i++){DesktopWindow window=find(DesktopWindow.class);if(window==null)return;window.closeWindow();}}
    void runningPlacement()throws Exception{
        ui(()->{closeAll();for(String id:ExternalTasks.list())ExternalTasks.remove(id);store.quickLaunch(Arrays.asList("@notes","@calendar","@weather"));store.prefs.edit().putBoolean("taskbar_expanded",true).putBoolean("taskbar_static",false).putBoolean("taskbar_show_labels",false).commit();home.applyTenAppearance();home.open("@notes");});settle();
        ui(()->{check(slot("@notes").getTop()==0,"Pinned open Notepad moves to the upper row");check(slot("@calendar").getTop()==Aero.dp(home,48)&&slot("@weather").getTop()==Aero.dp(home,48),"Idle pins stay on the bottom row");find(DesktopWindow.class).minimize();});settle();check(slot("@notes").getTop()==0,"Minimized app remains an open task on top");shot("eleven-taskbar-open-upper");
        ui(()->find(DesktopWindow.class).closeWindow());settle();check(slot("@notes").getTop()==Aero.dp(home,48),"Closed pinned app returns to bottom");
    }
    Map<String,View> fixedViews(){
        View bar=TenSmoke.description(root,"Eleven taskbar");Map<String,View> result=new LinkedHashMap<>();for(String name:new String[]{"Start menu","Search","Task View","Widgets","Clock and calendar","Notifications and calendar","Show desktop","Restore desktop windows"}){View child=TenSmoke.description(bar,name);if(child!=null&&child.isShown())result.put(name,child);}
        try{java.lang.reflect.Field field=MainActivity.class.getDeclaredField("tenNotaButton");field.setAccessible(true);result.put("Nth Nota",(View)field.get(home));}catch(Exception e){throw new RuntimeException(e);}result.put("System tray",TenSmoke.find(bar,SystemTray.class));return result;
    }
    String bounds(View view){int[] at=new int[2];view.getLocationOnScreen(at);return at[0]+","+at[1]+","+view.getWidth()+","+view.getHeight();}
    void fixedControls()throws Exception{
        for(String alignment:new String[]{"Left","Center"})for(boolean landscape:new boolean[]{false,true})for(boolean fit:new boolean[]{false,true}){
            ui(()->{closeAll();store.prefs.edit().putString("eleven_taskbar_alignment",alignment).putBoolean("taskbar_static",fit).putBoolean("taskbar_expanded",false).commit();store.quickLaunch(Arrays.asList("@notes","@calendar"));home.setRequestedOrientation(landscape?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);home.applyTenAppearance();if(fit)home.open("@notes");});settle();
            Map<String,View> controls=fixedViews();Map<String,String> before=new LinkedHashMap<>();for(Map.Entry<String,View> entry:controls.entrySet())before.put(entry.getKey(),bounds(entry.getValue()));String[] failure={null};int[] count={0};ViewTreeObserver.OnPreDrawListener watcher=()->{count[0]++;Map<String,View> current=fixedViews();for(String id:controls.keySet())if(failure[0]==null&&(current.get(id)!=controls.get(id)||!before.get(id).equals(bounds(controls.get(id)))))failure[0]=id+" changed from "+before.get(id)+" to "+bounds(controls.get(id));return true;};
            ui(()->root.getViewTreeObserver().addOnPreDrawListener(watcher));try{for(int n=0;n<2;n++){ui(this::toggleMenu);settle();check(failure[0]==null,"Fixed controls stay put: "+alignment+"/"+landscape+"/"+fit+": "+failure[0]);}}finally{ui(()->root.getViewTreeObserver().removeOnPreDrawListener(watcher));fixedFrames+=count[0];}fixedScenarios++;
        }
    }
    void narrowRestore()throws Exception{
        ui(()->{closeAll();for(String id:ExternalTasks.list())ExternalTasks.remove(id);store.quickLaunch(Collections.singletonList("@notes"));home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);});settle();
        for(int width:new int[]{320,360,411})for(String alignment:new String[]{"Left","Center"})for(boolean percentage:new boolean[]{false,true}){
            float density=home.getResources().getDisplayMetrics().density;shell("wm size "+Math.round(width*density)+"x"+Math.round(780*density));settle();
            ui(()->{store.prefs.edit().putString("eleven_taskbar_alignment",alignment).putBoolean("eleven_battery_percent",percentage).putBoolean("taskbar_static",false).putBoolean("taskbar_expanded",false).putBoolean("taskbar_show_labels",false).commit();home.applyTenAppearance();home.open("@notes");});settle();
            DesktopWindow window=find(DesktopWindow.class);ui(window::minimize);settle();
            ui(()->((HorizontalScrollView)find(ElevenTaskbarItems.class).getParent()).scrollTo(0,0));settle();
            ui(this::assertProportions);
            View button=slot("@notes");Rect touch=new Rect();check(button.getGlobalVisibleRect(touch)&&touch.width()>=Aero.dp(home,32),"Usable restore target at "+width+"dp/"+alignment+"/battery="+percentage);
            for(Map.Entry<String,View> control:fixedViews().entrySet()){
                Rect fixed=new Rect();check(control.getValue().getGlobalVisibleRect(fixed),"Fixed control visible: "+control.getKey());check(!Rect.intersects(touch,fixed),"Restore target avoids "+control.getKey());
            }
            long now=SystemClock.uptimeMillis();for(int action:new int[]{MotionEvent.ACTION_DOWN,MotionEvent.ACTION_UP}){MotionEvent event=MotionEvent.obtain(now,SystemClock.uptimeMillis(),action,touch.exactCenterX(),touch.exactCenterY(),0);event.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(event,true),"Restore touch accepted");event.recycle();Thread.sleep(70);}settle();check(window.isShown(),"Actual narrow taskbar touch restores Notepad");
            if(width==360&&percentage)shot("eleven-taskbar-narrow-"+alignment.toLowerCase(Locale.ROOT));ui(window::closeWindow);settle();
        }
        shell("wm size reset");settle();
    }
    /** The owner's recording had a half-width upper row even with free space above the tray. */
    void fullWidthUpperRow()throws Exception{
        float density=home.getResources().getDisplayMetrics().density;
        shell("wm size "+Math.round(411*density)+"x"+Math.round(860*density));settle();
        String[] tools={"@notes","@calendar","@recycle","@settings","@welcome","@recent","@backup","@taskmanager"};
        for(String alignment:new String[]{"Left","Center"})for(boolean fit:new boolean[]{false,true}){
            ui(()->{closeAll();for(String id:ExternalTasks.list())ExternalTasks.remove(id);store.quickLaunch(Arrays.asList("@photos","@browser"));store.taskbarOrder(Arrays.asList(tools));store.prefs.edit().putString("eleven_taskbar_alignment",alignment).putBoolean("taskbar_static",fit).putBoolean("taskbar_expanded",true).putBoolean("taskbar_show_labels",false).putBoolean("eleven_battery_percent",false).commit();home.applyTenAppearance();for(String tool:tools)home.open(tool);});settle();
            ui(()->{
                ElevenTaskbarItems items=find(ElevenTaskbarItems.class);HorizontalScrollView strip=(HorizontalScrollView)items.getParent();View bar=TenSmoke.description(root,"Eleven taskbar");
                check(strip.getWidth()>=bar.getWidth()-Aero.dp(home,10),"Upper row uses the whole bar including the space above the tray");
                Rect last=new Rect();check(slot("@taskmanager").getGlobalVisibleRect(last)&&last.width()>=Aero.dp(home,32),"Eighth upper app is fully visible beyond the old half-width cutoff");check(last.left>bar.getWidth()/2,"Actual eighth app occupies the right half of the taskbar");
                View clock=TenSmoke.description(root,"Clock and calendar"),notification=TenSmoke.description(root,"Notifications and calendar"),peek=TenSmoke.description(root,"Show desktop");int[] edge=new int[2],end=new int[2];bar.getLocationOnScreen(edge);notification.getLocationOnScreen(end);
                check(edge[0]+bar.getWidth()-(end[0]+notification.getWidth())==Aero.dp(home,8),"Notification sits next to the narrow right-edge desktop strip");
                check(clock.getTop()+clock.getHeight()<=((View)clock.getParent()).getHeight(),"Clock remains on the lower row");
                check(peek.getWidth()==Aero.dp(home,8),"No unused 24dp gap on the right");
                check(items.bottomRoom(strip.getWidth())<strip.getWidth()-items.bottomOffset(),"Idle pins keep their own lower-row tray reservation");
                try{java.lang.reflect.Field field=MainActivity.class.getDeclaredField("windows");field.setAccessible(true);((DesktopWindow)((Map<?,?>)field.get(home)).get("@taskmanager")).minimize();}catch(Exception error){throw new RuntimeException(error);}
            });settle();
            View target=slot("@taskmanager");Rect hit=new Rect();check(target.getGlobalVisibleRect(hit),"Right-side task target visible");
            long now=SystemClock.uptimeMillis();for(int action:new int[]{MotionEvent.ACTION_DOWN,MotionEvent.ACTION_UP}){MotionEvent event=MotionEvent.obtain(now,SystemClock.uptimeMillis(),action,hit.exactCenterX(),hit.exactCenterY(),0);event.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(event,true),"Right-side app touch accepted");event.recycle();}settle();
            ui(()->{try{java.lang.reflect.Field field=MainActivity.class.getDeclaredField("windows");field.setAccessible(true);check(((DesktopWindow)((Map<?,?>)field.get(home)).get("@taskmanager")).isShown(),"Right-side upper-row app restores through real input");}catch(Exception error){throw new RuntimeException(error);}});
            shot("eleven-taskbar-fullwidth-"+alignment.toLowerCase(Locale.ROOT)+(fit?"-static":"-scrollable"));
        }
        shell("wm size reset");settle();
    }
    void lowerPinScroll()throws Exception{
        for(String alignment:new String[]{"Left","Center"}){
            List<String> pins=Arrays.asList("@notes","@calendar","@recycle","@settings","@welcome","@recent","@backup","@taskmanager","@photos","@browser","@focus-timer");
            ui(()->{closeAll();for(String id:ExternalTasks.list())ExternalTasks.remove(id);store.quickLaunch(pins);store.taskbarOrder(pins);store.prefs.edit().putString("eleven_taskbar_alignment",alignment).putBoolean("taskbar_expanded",true).putBoolean("taskbar_static",false).commit();home.applyTenAppearance();});settle();
            ui(()->find(ElevenTaskbarItems.class).scrollRowTo(1,Integer.MAX_VALUE));settle();
            Rect hit=new Rect();ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);View pin=slot("@focus-timer");int[] at=new int[2];((View)items.getParent()).getLocationOnScreen(at);check(pin.getGlobalVisibleRect(hit)&&hit.width()>=Aero.dp(home,32),"Final lower pin has a complete touch target");check(hit.right<=at[0]+items.bottomOffset()+items.bottomRoom(((View)items.getParent()).getWidth()),"Final lower pin scrolls fully clear of the tray");});
            long now=SystemClock.uptimeMillis();for(int action:new int[]{MotionEvent.ACTION_DOWN,MotionEvent.ACTION_UP}){MotionEvent event=MotionEvent.obtain(now,SystemClock.uptimeMillis(),action,hit.exactCenterX(),hit.exactCenterY(),0);event.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(event,true),"Last idle pin touch accepted");event.recycle();}settle();
            ui(()->{try{java.lang.reflect.Field field=MainActivity.class.getDeclaredField("windows");field.setAccessible(true);DesktopWindow timer=(DesktopWindow)((Map<?,?>)field.get(home)).get("@focus-timer");check(timer!=null&&timer.isShown(),"Scrolled final idle pin launches its actual tool");}catch(Exception error){throw new RuntimeException(error);}});
        }
    }
    Rect iconInk(View view){
        Bitmap bitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(bitmap);
        if(view instanceof TenStyle.Glyph)((TenStyle.Glyph)view).onDraw(canvas);else if(view instanceof TenBrandMark)((TenBrandMark)view).onDraw(canvas);else view.draw(canvas);
        int left=bitmap.getWidth(),top=bitmap.getHeight(),right=0,bottom=0;
        for(int y=0;y<bitmap.getHeight();y++)for(int x=0;x<bitmap.getWidth();x++)if(Color.alpha(bitmap.getPixel(x,y))>32){left=Math.min(left,x);top=Math.min(top,y);right=Math.max(right,x+1);bottom=Math.max(bottom,y+1);}
        bitmap.recycle();return new Rect(left,top,right,bottom);
    }
    void assertProportions(){
        int width=home.getResources().getConfiguration().screenWidthDp,edge=Aero.dp(home,ElevenLayout.iconSize(width));
        Map<String,View> fixed=fixedViews();View nota=((ViewGroup)fixed.get("Nth Nota")).getChildAt(0);
        for(View icon:new View[]{fixed.get("Start menu"),fixed.get("Search"),fixed.get("Task View"),nota}){
            Rect ink=iconInk(icon);int size=Math.max(ink.width(),ink.height());check(size>=edge-Aero.dp(home,4)&&size<=edge+Aero.dp(home,2),"Actual fixed icon scale: "+icon.getContentDescription()+"="+ink);
            check(Math.abs(ink.centerY()-icon.getHeight()/2)<=Aero.dp(home,2),"Fixed icon artwork is vertically centered");
        }
        for(String name:new String[]{"Start menu","Search","Task View","Nth Nota"})check(fixed.get(name).getWidth()==Aero.dp(home,ElevenLayout.controlWidth(width)),"Uniform fixed button width: "+name);
        ViewGroup widgets=(ViewGroup)fixed.get("Widgets");View widgetIcon=widgets.getChildAt(0);check(widgetIcon.getWidth()==edge&&widgetIcon.getHeight()==edge&&Math.abs(widgetIcon.getLeft()+edge/2-widgets.getWidth()/2)<=1,"Widgets icon uses the common centered canvas");
        ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int i=0;i<items.getChildCount();i++){
            ViewGroup button=(ViewGroup)items.getChildAt(i);View icon=button.getChildAt(0);check(button.getWidth()==Aero.dp(home,ElevenLayout.taskButtonWidth(width)),"Pinned/running button uses common spacing");check(icon.getWidth()==edge&&icon.getHeight()==edge,"App icon shares the fixed icon canvas size");check(Math.abs(icon.getTop()+icon.getHeight()/2-button.getHeight()/2)<=1,"App icon centered vertically");
        }
    }
    void proportionScreenshots()throws Exception{
        float density=home.getResources().getDisplayMetrics().density;shell("wm size "+Math.round(411*density)+"x"+Math.round(880*density));settle();
        for(String alignment:new String[]{"Left","Center"})for(boolean expanded:new boolean[]{false,true})for(boolean light:new boolean[]{false,true}){
            ui(()->{closeAll();store.quickLaunch(Arrays.asList("@browser","@files"));store.prefs.edit().putString("eleven_taskbar_alignment",alignment).putBoolean("taskbar_expanded",expanded).putBoolean("taskbar_static",false).putBoolean("taskbar_show_labels",false).putBoolean("ten_light",light).putBoolean("eleven_battery_percent",false).commit();home.applyTenAppearance();if(expanded)home.open("@notes");});settle();ui(this::assertProportions);
            shot("eleven-taskbar-proportions-"+alignment.toLowerCase(Locale.ROOT)+(expanded?"-expanded":"-collapsed")+(light?"-light":"-dark"));
        }
        shell("wm size reset");settle();
    }
    List<String> barKeys()throws Exception{List<String> ids=new ArrayList<>();ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int n=0;n<items.getChildCount();n++){String id=TaskbarReorder.key(items.getChildAt(n));if(id!=null)ids.add(id);}});return ids;}
    Map<String,String> barGeometry()throws Exception{Map<String,String> values=new LinkedHashMap<>();ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int n=0;n<items.getChildCount();n++){View child=items.getChildAt(n);String id=TaskbarReorder.key(child);if(id!=null)values.put(id,child.getLeft()+","+child.getTop()+","+child.getWidth()+","+child.getHeight());}});return values;}
    void closeProperties(){View parent=find(VistaControlPanel.class);while(!(parent instanceof DesktopWindow))parent=(View)parent.getParent();((DesktopWindow)parent).closeWindow();}
    void scenario(String name,boolean custom,boolean snap,boolean scrolled)throws Exception{
        ui(()->{
            store.prefs.edit().putBoolean("taskbar_expanded",false).putBoolean("taskbar_static",true).putBoolean("snap_desktop",snap).putInt("desktop_columns_portrait",custom?5:0).putInt("desktop_rows_portrait",custom?9:0).putInt("desktop_columns_landscape",custom?5:0).putInt("desktop_rows_landscape",custom?6:0).putInt("desktop_icon_percent",80).putInt("desktop_spacing_x",0).putInt("desktop_spacing_y",0).commit();
            try{JSONObject folders=store.folders();List<String> ids=new ArrayList<>();for(int n=0;n<20;n++){String id="folder:"+new UUID(0,n+1);ids.add(id);folders.put(id,new JSONObject().put("name","Folder "+(n+1)).put("items",new JSONArray().put("@notes")));}ids.add("@recycle");store.prefs.edit().putString("app_folders",folders.toString()).commit();store.desktop(ids);TenDesktopItems.positions(store,"portrait",new JSONObject());TenDesktopItems.positions(store,"landscape",new JSONObject());}catch(JSONException e){throw new RuntimeException(e);}
            store.quickLaunch(Arrays.asList("@computer","@browser","@notes","@calendar","@weather","@photos","@files","@taskmanager","@nota","@recycle","@backup","@appearance"));home.applyTenAppearance();
        });settle();
        ui(()->{
            DesktopIconGrid grid=find(DesktopIconGrid.class);check(grid.getChildCount()==21,"Restored-style desktop fixture");
            if(custom){JSONObject points=new JSONObject();float density=home.getResources().getDisplayMetrics().density;
                for(int n=0;n<grid.getChildCount();n++){View child=grid.getChildAt(n);float y=child.getTop()/density;if(scrolled&&n==grid.getChildCount()-1)y=(grid.viewportHeight()+grid.cellHeight()*3)/density;else if(!snap)y+=n%3*2.5f;
                    JSONObject point=new JSONObject();GadgetLayout.put(point,"x",DesktopGeometry.fraction(child.getLeft(),grid.getWidth()-grid.cellWidth()));GadgetLayout.put(point,"y",y);GadgetLayout.put(points,String.valueOf(child.getTag()),point);}
                String axis=home.getResources().getConfiguration().orientation==android.content.res.Configuration.ORIENTATION_LANDSCAPE?"landscape":"portrait";TenDesktopItems.positions(store,axis,points);grid.requestLayout();
            }
        });settle();
        if(scrolled){ui(()->((ScrollView)find(DesktopIconGrid.class).getParent()).scrollTo(0,find(DesktopIconGrid.class).cellHeight()/2));settle();}
        String[] saved={null};Watcher[] watcher={null};ui(()->{saved[0]=TenDesktopItems.portable(store);watcher[0]=new Watcher();root.getViewTreeObserver().addOnPreDrawListener(watcher[0]);});
        try{
            for(int round=0;round<2;round++){
                ui(this::toggleMenu);settle();verify(watcher[0],name+" expanded from menu");
                if(name.equals("Left-portrait")&&round==0)shot("eleven-taskbar-stable-expanded");
                if(round==0)ui(this::toggleSettings);else ui(this::toggleProperties);settle();verify(watcher[0],name+" collapsed from settings/properties");
            }
            ui(()->check(saved[0].equals(TenDesktopItems.portable(store)),"Taskbar does not write desktop positions: "+name));
            if(name.equals("Left-portrait"))shot("eleven-taskbar-stable-collapsed");scenarios++;
        }finally{ui(()->{root.getViewTreeObserver().removeOnPreDrawListener(watcher[0]);frames+=watcher[0].count;});}
    }
    void verify(Watcher watcher,String action)throws Exception{check(watcher.failure==null,action+": "+watcher.failure);boolean expanded=action.contains("expanded from menu");ui(()->{ElevenTaskbarItems bar=find(ElevenTaskbarItems.class);check(bar.expanded==expanded&&store.prefs.getBoolean("taskbar_expanded",false)==expanded&&bar.getHeight()==Aero.dp(home,expanded?96:48),"Actual taskbar height follows toggle: "+action);});}
    final class Watcher implements ViewTreeObserver.OnPreDrawListener{
        final Map<String,String> baseline=geometry();int count;String failure;
        public boolean onPreDraw(){count++;Map<String,String> now=geometry();if(failure==null&&!baseline.equals(now)){for(String id:baseline.keySet())if(!Objects.equals(baseline.get(id),now.get(id))){failure=id+" moved from "+baseline.get(id)+" to "+now.get(id);break;}if(failure==null)failure="Desktop membership changed";}return true;}
    }
    Map<String,String> geometry(){
        DesktopIconGrid grid=find(DesktopIconGrid.class);Map<String,String> values=new LinkedHashMap<>();
        for(int n=0;n<grid.getChildCount();n++){View child=grid.getChildAt(n);int[] at=new int[2];child.getLocationOnScreen(at);View icon=((ViewGroup)child).getChildAt(0);values.put(String.valueOf(child.getTag()),at[0]+","+at[1]+","+child.getWidth()+","+child.getHeight()+","+icon.getWidth()+","+icon.getHeight());}View clock=TenSmoke.description(root,"Clock and calendar");int[] at=new int[2];clock.getLocationOnScreen(at);values.put("fixed-clock",String.valueOf(at[1]));return values;
    }
    void toggleMenu(){try{View taskbar=TenSmoke.description(root,"Desktop taskbar");java.lang.reflect.Method method=MainActivity.class.getDeclaredMethod("taskbarExtras",View.class,float.class,float.class);method.setAccessible(true);method.invoke(home,taskbar,10f,10f);java.lang.reflect.Field field=MainActivity.class.getDeclaredField("desktopMenu");field.setAccessible(true);Object menu=field.get(home);java.lang.reflect.Field items=DesktopContextMenu.class.getDeclaredField("items");items.setAccessible(true);View toggle=TenSmoke.description((View)items.get(menu),"Expand taskbar");check(toggle!=null,"Actual expansion menu row");toggle.performClick();}catch(Exception error){throw new RuntimeException(error);}}
    void toggleProperties(){home.open("setting:page:taskbar");VistaControlPanel properties=find(VistaControlPanel.class);((CompoundButton)TenSmoke.description(properties,"Expand taskbar to two rows")).performClick();closeTool(properties);}
    void toggleSettings(){home.open("ten-setting:taskbar");TenSettings settings=find(TenSettings.class);((CompoundButton)TenSmoke.description(settings,"Expand taskbar to two rows")).performClick();closeTool(settings);}
    void closeTool(View tool){View parent=tool;while(!(parent instanceof DesktopWindow))parent=(View)parent.getParent();((DesktopWindow)parent).closeWindow();}
    void settle()throws Exception{
        CountDownLatch ready=new CountDownLatch(1);ui(()->{ViewTreeObserver.OnPreDrawListener listener=new ViewTreeObserver.OnPreDrawListener(){int draws;public boolean onPreDraw(){if(++draws==3){root.getViewTreeObserver().removeOnPreDrawListener(this);ready.countDown();}else root.postOnAnimation(()->root.invalidate());return true;}};root.getViewTreeObserver().addOnPreDrawListener(listener);root.invalidate();});check(ready.await(8,TimeUnit.SECONDS),"Native frames settled");waitForIdleSync();
    }
    void ui(Runnable action)throws Exception{FutureTask<Void> job=new FutureTask<>(action,null);home.runOnUiThread(job);job.get(25,TimeUnit.SECONDS);waitForIdleSync();}
    <T extends View>T find(Class<T> type){return TenSmoke.find(root,type);}void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    void shell(String command)throws IOException{try(ParcelFileDescriptor descriptor=getUiAutomation().executeShellCommand(command);InputStream in=new FileInputStream(descriptor.getFileDescriptor())){byte[] buffer=new byte[4096];while(in.read(buffer)!=-1){}}}
    void shot(String name)throws Exception{settle();Bitmap bitmap=Bitmap.createBitmap(root.getWidth(),root.getHeight(),Bitmap.Config.ARGB_8888);ui(()->root.draw(new Canvas(bitmap)));try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+".png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}finally{bitmap.recycle();}}
}
