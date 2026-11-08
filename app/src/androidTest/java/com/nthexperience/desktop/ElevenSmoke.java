package com.nthexperience.desktop;
import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/** First Eleven behavior regression, independent of Ten's appearance assertions. */
public final class ElevenSmoke extends Instrumentation {
 MainActivity home;View root;DesktopStore store;int checks;
 @Override public void onCreate(Bundle args){super.onCreate(args);start();}
 @Override public void onStart(){Bundle result=new Bundle();try{
  store=new DesktopStore(getTargetContext());store.prefs.edit().putInt("startup_mode",2).putBoolean("welcome_on_start",false).putBoolean("reduce_motion",true).putBoolean("sidebar_visible",false).putBoolean("taskbar_apps",false).commit();
  getTargetContext().getSharedPreferences(StorageAccess.PREFS,0).edit().putBoolean("seen",true).commit();getTargetContext().getSharedPreferences(StartupExperience.SESSION,0).edit().putBoolean("seen",true).putBoolean("welcome_seen",true).putBoolean(StartupExperience.HOME_OFFERED,true).commit();
  home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();waitForIdleSync();
  check(getTargetContext().getPackageName().equals("com.nthexperience.desktop.eleven"),"Independent Eleven package");
  for(int orientation:new int[]{ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}){
   ui(()->home.setRequestedOrientation(orientation));SystemClock.sleep(1800);waitForIdleSync();
   for(boolean light:new boolean[]{true,false}){
    ui(()->{store.prefs.edit().putBoolean("ten_light",light).putBoolean("ten_apps_dark",!light).commit();home.applyTenAppearance();});waitForIdleSync();
    String suffix=(orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?"portrait":"landscape")+(light?"-light":"-dark");
    ui(()->home.open("start-menu:"));waitForIdleSync();SystemClock.sleep(300);
    ui(()->{StartMenu menu=TenSmoke.find(root,StartMenu.class);check(menu!=null&&menu.isShown(),"Start opens");check(menu.search.getTop()<menu.content.getTop(),"Search precedes pins");check(TenSmoke.text(menu,"Pinned")!=null,"Pinned heading");check(TenSmoke.text(menu,"All apps  ›")!=null,"All apps available");Rect box=new Rect(),parent=new Rect();menu.getGlobalVisibleRect(box);((View)menu.getParent()).getGlobalVisibleRect(parent);check(parent.contains(box),"Start fits work area");});shot("eleven-start-"+suffix);
    ui(()->{View start=TenSmoke.description(root,"Start menu");start.performClick();check(!TenSmoke.find(root,StartMenu.class).isShown(),"Start second tap closes");start.performClick();TenSmoke.description(root,"Quick Settings").performClick();check(!TenSmoke.find(root,StartMenu.class).isShown(),"Quick Settings dismisses Start");check(TenSmoke.description(root,"Quick Settings panel")!=null,"Quick panel exists");check(TenSmoke.description(root,"Quick Settings volume")!=null&&TenSmoke.description(root,"Brightness")!=null,"Working sliders present");});shot("eleven-quick-"+suffix);
    ui(()->{TenSmoke.description(root,"Quick Settings").performClick();check(TenSmoke.description(root,"Quick Settings panel")==null,"Quick second tap closes");TenSmoke.description(root,"Clock and calendar").performClick();check(TenSmoke.description(root,"Notifications and calendar panel")!=null,"Clock opens notifications and calendar");check(TenSmoke.find(root,MonthCalendarView.class)!=null,"Month calendar exists");check(TenSmoke.description(root,"Quick Settings panel")==null,"Notification panel excludes Quick Settings");});shot("eleven-notifications-"+suffix);
    ui(()->{TenSmoke.description(root,"Clock and calendar").performClick();check(TenSmoke.description(root,"Notifications and calendar panel")==null,"Clock second tap closes");home.open("@settings");TenSettings settings=TenSmoke.find(root,TenSettings.class);check(settings!=null,"Settings opens");check(TenSmoke.description(settings,"Settings category Bluetooth & devices")!=null,"Eleven category naming");settings.navigate("start");check(TenSmoke.text(settings,"Pinned apps")!=null,"Start guidance matches Eleven");});shot("eleven-settings-"+suffix);
   }
  }
  ui(()->{home.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_A,0,KeyEvent.META_META_ON));check(TenSmoke.description(root,"Quick Settings panel")!=null,"Windows+A opens Quick Settings");home.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_N,0,KeyEvent.META_META_ON));check(TenSmoke.description(root,"Quick Settings panel")==null&&TenSmoke.description(root,"Notifications and calendar panel")!=null,"Windows+N opens notifications separately");home.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_N,0,KeyEvent.META_META_ON));check(TenSmoke.description(root,"Notifications and calendar panel")==null,"Windows+N closes notifications on repeat");});
  ui(()->{home.open("@search");StartMenu menu=TenSmoke.find(root,StartMenu.class);menu.search.setText("Calculator");check(TenSmoke.description(menu,"Open Calculator")!=null,"Local tool searchable");JSONObject state=menu.state();menu.hide();menu.restore(state);check(menu.search.getText().toString().equals("Calculator"),"Start state restores query");menu.hide();menu.show(false);List<String> pins=new ArrayList<>(store.startPins());check(pins.size()>1,"Default pins exist");View first=menu.tileViews.get(pins.get(0));first.requestFocus();first.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT,0,KeyEvent.META_CTRL_ON));check(store.startPins().get(1).equals(pins.get(0)),"Pin reorder persists");store.startPins(pins);menu.show(false);home.open("@notes");EditText note=(EditText)TenSmoke.description(root,"Notepad text");note.setText("Eleven keeps this document.");home.open("@taskview");check(TenSmoke.description(root,"Task View")!=null,"Task View opens");home.open("@nota");check(TenSmoke.find(root,TenNotaPanel.class)!=null,"Nota opens");});shot("eleven-nota");
  ui(()->{home.open("@files");check(TenSmoke.find(root,VistaExplorer.class)!=null,"Real Explorer retained");});shot("eleven-explorer");
  result.putString("stream","PASS: Eleven "+checks+" checks; identity, Start, search/state/reorder, split panels, sliders, Settings, windows, Nota and Explorer across orientations and modes\n");finish(Activity.RESULT_OK,result);
 }catch(Throwable ex){try{shot("eleven-failure");}catch(Exception ignored){}result.putString("stream","FAIL: "+android.util.Log.getStackTraceString(ex));finish(Activity.RESULT_CANCELED,result);}}
 void check(boolean yes,String message){checks++;if(!yes)throw new AssertionError(message);}
 void ui(Runnable task)throws Exception{FutureTask<Void> f=new FutureTask<>(task,null);home.runOnUiThread(f);f.get(30,TimeUnit.SECONDS);}
 void shot(String name)throws Exception{waitForIdleSync();SystemClock.sleep(200);Bitmap b=getUiAutomation().takeScreenshot();if(b!=null){try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}finally{b.recycle();}}}
}
