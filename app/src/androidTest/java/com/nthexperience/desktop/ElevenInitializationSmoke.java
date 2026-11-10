package com.nthexperience.desktop;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.*;
import java.util.*;
import java.util.concurrent.*;

/** Actual Android Usage Access/App info returns through Eleven's ordered and manual guides. */
public final class ElevenInitializationSmoke extends TenThemeSmoke {
 private boolean complete;
 @Override public void onCreate(Bundle args){complete=args!=null&&"complete".equals(args.getString("phase"));super.onCreate(args);}
 @Override public void onStart(){Bundle result=new Bundle();try{
  store=new DesktopStore(getTargetContext());store.prefs.edit().clear().putInt("startup_mode",2).putBoolean("welcome_on_start",false).putBoolean("reduce_motion",true).putBoolean("taskbar_apps",false).putString("weather_city","Saved city").commit();
  getTargetContext().getSharedPreferences(InitializationState.PREFS,0).edit().clear().commit();
  getTargetContext().getSharedPreferences(StorageAccess.PREFS,0).edit().clear().commit();
  getTargetContext().getSharedPreferences(StartupExperience.SESSION,0).edit().clear().putBoolean("seen",true).putBoolean("welcome_seen",false).commit();
  home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();
  for(String title:new String[]{"Default Launcher","Enable File Browsing","Set up Usage Access","Allow Notifications Access","Modify System Settings (Brightness)"}){
   awaitSystem(title);
   check(TenSmoke.find(root,WelcomeCenter.class)!=null,"Classic Welcome Center remains behind setup");
   if(title.equals("Enable File Browsing"))shot("eleven-usage-file-guide-portrait");
   if(title.equals("Set up Usage Access")){
    systemClick("OK");awaitSystemPackage("com.android.settings");
    check(!ExternalTasks.permitted(home),"Usage Access starts off for the return regression");
    check(getUiAutomation().performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK),"Return from Android Usage Access without granting it");awaitSystem(title);
    check(new InitializationState(home).step()==InitializationState.Step.USAGE,"Denied or ungranted Usage Access returns to the same guide");
    shot("eleven-usage-usage-return-guide");recreateHome();awaitSystem(title);
    check(new InitializationState(home).step()==InitializationState.Step.USAGE,"Ungranted Usage Access guide survives recreation");
    shot("eleven-usage-usage-guide-portrait");
    if(Build.VERSION.SDK_INT>=33){
     check(node(getUiAutomation().getRootInActiveWindow(),"Open App info")!=null,"Illustrated guide exposes App info recovery");
     systemClick("Open App info");awaitSystemPackage("com.android.settings");awaitSystem(home.getString(R.string.app_name));
     check(getUiAutomation().performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK),"Android App info returns to guide");awaitSystem(title);
     check(new InitializationState(home).step()==InitializationState.Step.USAGE,"Reviewing App info preserves the Usage Access step");
    }
    ui(()->home.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));await(()->root.getWidth()>root.getHeight(),"Usage guide landscape");awaitSystem(title);assertControls();shot("eleven-usage-usage-guide-landscape");
    ui(()->home.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));await(()->root.getHeight()>root.getWidth(),"Usage guide portrait");awaitSystem(title);
   }
   if(title.equals("Enable File Browsing")){recreateHome();awaitSystem(title);}
   systemClick("Later");
  }
  awaitSystem("Allow Location");shot("eleven-usage-location-guide");
  if(complete){
   systemClick(Build.VERSION.SDK_INT>=31?"Choose location access":"Use approximate location");
   if(Build.VERSION.SDK_INT>=31)systemClick("Approximate");
   systemClick("While using the app");
  }else systemClick("Later");
  await(()->new InitializationState(home).step()==InitializationState.Step.DONE,"Optional setup completes");
  check(!StorageAccess.has(home)&&!ExternalTasks.permitted(home),"Declines leave optional access off");
  check(store.prefs.getString("weather_city","").equals("Saved city"),"Setup keeps the saved weather city");
  if(complete){check(LocationAccess.permitted(home)&&!LocationAccess.precise(home),"Android approximate-only grant is accepted");}
  recreateHome();await(()->home.hasWindowFocus(),"Focus after recreation");
  check(new InitializationState(home).step()==InitializationState.Step.DONE,"Completed setup does not replay after recreation");
  manualRecovery();
  result.putString("stream","PASS: ungranted Usage Access returns to the same guide before App info; recovery remains available after recreation; explicit Later continues setup; classic Welcome Center and illustrated scrollable guides; native ordered setup; Later leaves optional access off; completion survives recreation; saved city retained"+(complete?"; Android approximate-only grant":"")+"; manual Welcome Center recovery; real emulator grant refreshes the checkbox without replaying setup"+"\n");finish(Activity.RESULT_OK,result);
 }catch(Throwable error){try{shot("eleven-usage-setup-failure");}catch(Exception ignored){}result.putString("stream","FAIL: "+android.util.Log.getStackTraceString(error));finish(Activity.RESULT_CANCELED,result);}}
 private void awaitSystemPackage(String pkg)throws Exception{
  long end=SystemClock.uptimeMillis()+15000;
  while(SystemClock.uptimeMillis()<end){android.view.accessibility.AccessibilityNodeInfo window=getUiAutomation().getRootInActiveWindow();if(window!=null&&pkg.contentEquals(window.getPackageName()))return;Thread.sleep(80);}throw new AssertionError("Android screen unavailable: "+pkg);
 }
 private void recreateHome()throws Exception{
  MainActivity previous=home;BlockingQueue<MainActivity> next=new LinkedBlockingQueue<>();Application app=(Application)getTargetContext().getApplicationContext();
  Application.ActivityLifecycleCallbacks callback=new Application.ActivityLifecycleCallbacks(){public void onActivityResumed(Activity a){if(a instanceof MainActivity&&a!=previous)next.offer((MainActivity)a);}public void onActivityCreated(Activity a,Bundle b){}public void onActivityStarted(Activity a){}public void onActivityStopped(Activity a){}public void onActivityPaused(Activity a){}public void onActivitySaveInstanceState(Activity a,Bundle b){}public void onActivityDestroyed(Activity a){}};
  app.registerActivityLifecycleCallbacks(callback);try{ui(home::recreate);home=next.poll(20,TimeUnit.SECONDS);check(home!=null,"Home recreated");root=home.getWindow().getDecorView();}finally{app.unregisterActivityLifecycleCallbacks(callback);}
 }
 @Override android.view.accessibility.AccessibilityNodeInfo node(android.view.accessibility.AccessibilityNodeInfo current,String text){
  if(current==null)return null;
  if((text.equals("OK")&&"Uninstall".equalsIgnoreCase(String.valueOf(current.getText())))||text.equalsIgnoreCase(String.valueOf(current.getText()))||text.equalsIgnoreCase(String.valueOf(current.getContentDescription())))return current;
  for(int i=0;i<current.getChildCount();i++){android.view.accessibility.AccessibilityNodeInfo found=node(current.getChild(i),text);if(found!=null)return found;}return null;
 }
 @Override void systemClick(String label)throws Exception{
  android.view.accessibility.AccessibilityNodeInfo item=awaitSystem(label);
  android.graphics.Rect bounds=new android.graphics.Rect();item.getBoundsInScreen(bounds);
  for(int n=0;item!=null&&n<6;n++,item=item.getParent())if(item.isClickable()&&item.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK)){waitForIdleSync();Thread.sleep(180);return;}
  long now=SystemClock.uptimeMillis();MotionEvent down=MotionEvent.obtain(now,now,0,bounds.centerX(),bounds.centerY(),0),up=MotionEvent.obtain(now,now+60,1,bounds.centerX(),bounds.centerY(),0);
  down.setSource(InputDevice.SOURCE_TOUCHSCREEN);up.setSource(InputDevice.SOURCE_TOUCHSCREEN);
  try{check(getUiAutomation().injectInputEvent(down,true)&&getUiAutomation().injectInputEvent(up,true),"Click system control: "+label);}finally{down.recycle();up.recycle();}waitForIdleSync();Thread.sleep(180);
 }
 private void manualRecovery()throws Exception{
  ui(()->home.open("@welcome"));systemClick("Set up Usage Access");awaitSystem("Set up Usage Access");
  systemClick("OK");awaitSystemPackage("com.android.settings");backToGuide();
  check(new InitializationState(home).step()==InitializationState.Step.DONE,"Manual retry preserves completed initialization");shot("eleven-usage-manual-return");
  if(Build.VERSION.SDK_INT>=33){
   systemClick("Open App info");awaitSystemPackage("com.android.settings");awaitSystem(home.getString(R.string.app_name));backToGuide();
   check(new InitializationState(home).step()==InitializationState.Step.DONE,"Manual App info review cannot restart initialization");shot("eleven-usage-manual-app-info-return");
  }
  systemClick("OK");awaitSystemPackage("com.android.settings");
  // Set the real Android app-op in the disposable emulator, then verify the ordinary return path.
  try(ParcelFileDescriptor result=getUiAutomation().executeShellCommand("appops set --uid "+getTargetContext().getPackageName()+" GET_USAGE_STATS allow");java.io.InputStream input=new ParcelFileDescriptor.AutoCloseInputStream(result)){while(input.read()!=-1){}}
  check(getUiAutomation().performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK),"Return after real emulator Usage Access grant");
  await(()->home.hasWindowFocus()&&ExternalTasks.permitted(home),"Real grant closes the guide");
  ui(()->home.open("setting:page:taskbar"));
  await(()->{View control=TenSmoke.description(root,"Include apps opened outside Nth Desktop");return control instanceof android.widget.CheckBox&&((android.widget.CheckBox)control).isChecked();},"Grant refreshes the actual taskbar checkbox");
  check(new InitializationState(home).step()==InitializationState.Step.DONE,"Successful manual grant leaves setup completed");
 }
 private void backToGuide()throws Exception{
  check(getUiAutomation().performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK),"Return from Android Settings without a grant");
  awaitSystem("Set up Usage Access");assertControls();
 }
 private void assertControls()throws Exception{
  for(String label:Build.VERSION.SDK_INT>=33?new String[]{"OK","Open App info"}:new String[]{"OK"}){
   android.view.accessibility.AccessibilityNodeInfo control=awaitSystem(label);android.graphics.Rect at=new android.graphics.Rect();control.getBoundsInScreen(at);
   check(control.isVisibleToUser()&&!at.isEmpty()&&at.left>=0&&at.top>=0&&at.right<=root.getWidth()&&at.bottom<=root.getHeight(),"Recovery control is reachable: "+label);
  }
 }
}
