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
import java.lang.reflect.Field;
import java.util.concurrent.*;

/** Native coverage for the recorded Settings, cascading menus, Focus and drag Snap gaps. */
public final class ElevenGapSmoke extends Instrumentation {
    MainActivity home;View root;DesktopStore store;TenSettings settings;DesktopWindow settingsWindow;int checks;
    @Override public void onCreate(Bundle args){super.onCreate(args);start();}
    @Override public void onStart(){Bundle out=new Bundle();try{
        store=new DesktopStore(getTargetContext());store.prefs.edit().putInt("startup_mode",2).putBoolean("welcome_on_start",false).putBoolean("reduce_motion",true).putBoolean("sidebar_visible",false).putBoolean("taskbar_apps",false).putBoolean("ten_snap",true).putBoolean("eleven_snap_bar",true).putBoolean("ten_apps_dark",true).putBoolean("ten_light",false).commit();
        getTargetContext().getSharedPreferences(StorageAccess.PREFS,0).edit().putBoolean("seen",true).commit();getTargetContext().getSharedPreferences(StartupExperience.SESSION,0).edit().putBoolean("seen",true).putBoolean("welcome_seen",true).putBoolean(StartupExperience.HOME_OFFERED,true).commit();
        home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();waitForIdleSync();
        ui(()->home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));settle();openSettings();
        navigation();pageCards();focus();menus();snapping();
        out.putString("stream","PASS: Eleven recording gaps "+checks+" assertions; Settings Back/Forward, nested ancestors, search/scroll/recreation, cards/pages, Focus timer/expiry/manual-state preservation, cascading menus and top drag Snap/drop/cancellation.\n");finish(Activity.RESULT_OK,out);
    }catch(Throwable e){out.putString("stream","FAIL: "+e+"\n"+android.util.Log.getStackTraceString(e));finish(Activity.RESULT_CANCELED,out);}}
    void openSettings()throws Exception{ui(()->{home.open("@settings");settings=TenSmoke.find(root,TenSettings.class);settingsWindow=window(settings);settingsWindow.maximize();});waitForIdleSync();}
    void navigation()throws Exception{
        ui(()->settings.navigate("system"));waitForIdleSync();ui(()->settings.scroll.scrollTo(0,Aero.dp(home,140)));waitForIdleSync();final int[] savedScroll={0};
        ui(()->{savedScroll[0]=settings.scroll.getScrollY();check(savedScroll[0]>0,"Category actually scrolled before navigation");settings.navigate("display");settings.navigate("night-light");check(settings.page.equals("night-light"),"Deep display page is open");home.onBackPressed();check(settings.page.equals("display")&&settingsWindow.getVisibility()==View.VISIBLE,"Android Back returns within Settings");settings.goBack();});waitForIdleSync();
        ui(()->{check(settings.page.equals("system"),"Second Back returns to category");check(settings.scroll.getScrollY()==savedScroll[0],"Category scroll is restored");check(settings.goForward()&&settings.page.equals("display"),"Forward restores the next page");settings.navigate("advanced-apps");settings.navigate("app-aliases");});
        ActivityMonitor monitor=addMonitor(MainActivity.class.getName(),null,false);ui(()->home.recreate());Activity resumed=waitForMonitorWithTimeout(monitor,12000);removeMonitor(monitor);check(resumed instanceof MainActivity,"Settings activity recreated");home=(MainActivity)resumed;root=home.getWindow().getDecorView();settle();
        ui(()->{settings=TenSmoke.find(root,TenSettings.class);settingsWindow=window(settings);check(settings!=null&&settings.page.equals("app-aliases"),"Nested page survives recreation");check(settings.goBack()&&settings.page.equals("advanced-apps"),"History survives recreation");settings.search.setText("HDR");settings.navigate("hdr");check(settings.goBack()&&settings.search.getText().toString().equals("HDR"),"Back restores search results");check(settings.goBack()&&settings.search.length()==0,"Back clears search before leaving its page");});
    }
    void pageCards()throws Exception{
        for(String id:new String[]{"home","system","personalization","display","night-light","color-management","hdr","advanced-display","sound","advanced-apps","app-aliases","installed","startup-apps","video-playback","recovery","time","accounts"}){
            ui(()->{settings.navigate(id);check(settings.page.equals(id),"Settings route "+id);check(settings.body.getChildCount()>0,"Real content exists for "+id);});waitForIdleSync();
        }
        ui(()->{settings.navigate("sound");ElevenSettingsCard output=(ElevenSettingsCard)TenSmoke.description(settings,"Output");check(output!=null&&output.children.getVisibility()==View.VISIBLE,"Output card starts expanded");output.header.performClick();check(output.children.getVisibility()==View.GONE,"Output card collapses");JSONObject state=settings.state();TenSettings copy=new TenSettings(home,store,home::open);copy.restoreState(state);ElevenSettingsCard restored=(ElevenSettingsCard)TenSmoke.description(copy,"Output");check(restored!=null&&restored.children.getVisibility()==View.GONE,"Collapsed card state restores");output.header.performClick();});shot("eleven-gaps-sound");
        ui(()->settings.navigate("personalization"));shot("eleven-gaps-personalization");
        ui(()->{home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);settings.navigate("night-light");});settle();
        ui(()->{check(settings.nav.getVisibility()==View.VISIBLE&&settings.scroll.getVisibility()==View.VISIBLE,"Portrait retains sidebar and content");check(TenSmoke.description(settings,"Settings ancestors")!=null,"Narrow deep breadcrumb offers ancestor overflow");});shot("eleven-gaps-settings-portrait");
        ui(()->{store.prefs.edit().putBoolean("ten_apps_dark",false).putBoolean("ten_light",true).commit();home.applyTenAppearance();settings.navigate("display");});shot("eleven-gaps-display-light");
        ui(()->{store.prefs.edit().putBoolean("ten_apps_dark",true).putBoolean("ten_light",false).commit();home.applyTenAppearance();});
    }
    void focus()throws Exception{
        ui(()->{store.prefs.edit().putBoolean("ten_focus",false).putBoolean("eleven_focus_quiet",true).putBoolean("eleven_focus_show_timer",false).commit();ElevenFocus.stop(home);settings.navigate("focus");View start=TenSmoke.description(settings,"Start focus session");check(start!=null&&start.performClick(),"Start Focus uses the real control");check(ElevenFocus.remaining(home)>0&&ElevenFocus.quiet(home),"Focus starts with a running monotonic session");check(!store.prefs.getBoolean("ten_focus",true),"Session does not overwrite manual quiet mode");home.open("@focus-timer");check(TenSmoke.find(root,ElevenFocus.Timer.class)!=null,"Focus timer opens in a Nth window");});shot("eleven-gaps-focus-timer");
        ui(()->{store.prefs.edit().putBoolean("ten_focus",true).commit();ElevenFocus.stop(home);check(store.prefs.getBoolean("ten_focus",false),"Stopping preserves preexisting manual quiet mode");check(!ElevenFocus.quiet(home),"Stopping removes the session mask");check(ElevenFocus.start(home,5),"Focus can start again");long now=SystemClock.elapsedRealtime();home.getSharedPreferences("eleven_focus_session",0).edit().putLong("start",now-2000).putLong("end",now-1).commit();check(ElevenFocus.remaining(home)==0&&!ElevenFocus.quiet(home),"Expired session cannot keep notifications hidden");ElevenFocus.stop(home);store.prefs.edit().putBoolean("ten_focus",false).commit();check(!backupText().contains("eleven_focus_session"),"Active session is not exported in a backup");settings.navigate("focus");});shot("eleven-gaps-focus-settings");
    }
    void menus()throws Exception{
        final DesktopContextMenu[] parent={null},child={null};final boolean[] selected={false};
        ui(()->{parent[0]=new DesktopContextMenu(home,"Cascade parent");parent[0].submenu("View","apps",()->{child[0]=new DesktopContextMenu(home,"Cascade child");child[0].item("Child choice","checked",()->selected[0]=true);ContextMenus.show(child[0],root,1,1);});ContextMenus.show(parent[0],root,1,1);});waitForIdleSync();
        ui(()->{items(parent[0]).getChildAt(0).performClick();check(parent[0].showing()&&child[0]!=null&&child[0].showing(),"Cascading child keeps its parent open");});shot("eleven-gaps-cascade");
        ui(()->{View choice=items(child[0]).getChildAt(0);int[] point=new int[2];choice.getLocationOnScreen(point);check(point[0]>=0&&point[1]>=0&&point[0]+choice.getWidth()<=root.getWidth()+Aero.dp(home,2),"Cascade stays inside the viewport");choice.performClick();check(selected[0]&&!parent[0].showing()&&!child[0].showing(),"Leaf choice closes the full cascade once");});
    }
    void snapping()throws Exception{
        ui(()->{home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);home.open("@settings");settingsWindow.restoreDown();});settle();
        FrameLayout host=(FrameLayout)settingsWindow.getParent();final View[] label={null};final int[] start={0,0},origin={0,0};
        ui(()->{label[0]=((ViewGroup)settingsWindow.getChildAt(0)).getChildAt(1);label[0].getLocationOnScreen(start);start[0]+=label[0].getWidth()/2;start[1]+=label[0].getHeight()/2;host.getLocationOnScreen(origin);});
        long down=SystemClock.uptimeMillis();input(down,MotionEvent.ACTION_DOWN,start[0],start[1]);input(down,MotionEvent.ACTION_MOVE,origin[0]+host.getWidth()/2,origin[1]+Aero.dp(home,8));waitForIdleSync();
        final RectF[] zone={null};ui(()->{check(settingsWindow.snapBar!=null,"Caption drag opens the top Snap bar");zone[0]=settingsWindow.snapBar.zone(AeroSnap.Placement.TOP_RIGHT);check(zone[0]!=null,"Mixed/quarter zones are viable in landscape");});
        float x=origin[0]+zone[0].centerX(),y=origin[1]+zone[0].centerY();input(down,MotionEvent.ACTION_MOVE,x,y);shot("eleven-gaps-drag-snap");input(down,MotionEvent.ACTION_UP,x,y);waitForIdleSync();
        ui(()->{check(settingsWindow.snapBar==null,"Dropping removes the bar");check(settingsWindow.geometry().optString("snap").equals("TOP_RIGHT"),"Drop uses the selected quarter placement");settingsWindow.restoreDown();});waitForIdleSync();
        ui(()->{label[0].getLocationOnScreen(start);start[0]+=label[0].getWidth()/2;start[1]+=label[0].getHeight()/2;});down=SystemClock.uptimeMillis();input(down,MotionEvent.ACTION_DOWN,start[0],start[1]);input(down,MotionEvent.ACTION_MOVE,origin[0]+host.getWidth()/2,origin[1]+Aero.dp(home,8));input(down,MotionEvent.ACTION_CANCEL,origin[0]+host.getWidth()/2,origin[1]+Aero.dp(home,8));
        ui(()->check(settingsWindow.snapBar==null&&!settingsWindow.snapped(),"Cancelled top drag cleans up and restores floating placement"));
    }
    String backupText(){try{return DesktopBackup.snapshot(store).toString();}catch(JSONException e){throw new RuntimeException(e);}}
    void input(long down,int action,float x,float y){MotionEvent e=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);try{e.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(e,true),"Injected drag event "+action);}finally{e.recycle();}}
    LinearLayout items(DesktopContextMenu menu){try{Field f=DesktopContextMenu.class.getDeclaredField("items");f.setAccessible(true);return (LinearLayout)f.get(menu);}catch(Exception e){throw new RuntimeException(e);}}
    DesktopWindow window(View view){for(ViewParent p=view.getParent();p!=null;p=p.getParent())if(p instanceof DesktopWindow)return (DesktopWindow)p;throw new AssertionError("No Settings window");}
    void ui(Runnable run)throws Exception{FutureTask<Void> f=new FutureTask<>(run,null);home.runOnUiThread(f);f.get(30,TimeUnit.SECONDS);}
    void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    void settle()throws Exception{SystemClock.sleep(1500);waitForIdleSync();}
    void shot(String name)throws Exception{waitForIdleSync();SystemClock.sleep(250);ui(()->{Bitmap b=Bitmap.createBitmap(root.getWidth(),root.getHeight(),Bitmap.Config.ARGB_8888);try{root.draw(new Canvas(b));try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+"-view.png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}}catch(IOException e){throw new RuntimeException(e);}finally{b.recycle();}});Bitmap b=getUiAutomation().takeScreenshot();if(b!=null)try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}finally{b.recycle();}}
}
