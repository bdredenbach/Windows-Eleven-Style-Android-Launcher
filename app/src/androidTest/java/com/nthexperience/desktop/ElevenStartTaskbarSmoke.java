package com.nthexperience.desktop;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.*;
import java.io.*;

/** Actual touch holds and repeated Start openings, including the scroll-owning blank row. */
public class ElevenStartTaskbarSmoke extends ElevenTaskbarSmoke {
    int checks;long worstWarm;
    @Override public void onStart(){Bundle out=new Bundle();try{
        store=new DesktopStore(getTargetContext());store.prefs.edit().putInt("startup_mode",2).putBoolean("welcome_on_start",false).putBoolean("reduce_motion",true).putBoolean("taskbar_static",false).putBoolean("taskbar_expanded",true).putString("eleven_taskbar_alignment","Left").commit();
        home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();settle();
        ui(()->home.open("start-menu:"));settle();
        ui(()->{StartMenu menu=find(StartMenu.class);View pin=menu.tileViews.values().iterator().next(),row=menu.dashboard.getChildAt(menu.dashboard.getChildCount()-1);for(int i=0;i<10;i++){menu.hide();long before=SystemClock.elapsedRealtimeNanos();menu.show(false);worstWarm=Math.max(worstWarm,SystemClock.elapsedRealtimeNanos()-before);check(pin==menu.tileViews.values().iterator().next(),"Warm Start retains pin views");check(row==menu.dashboard.getChildAt(menu.dashboard.getChildCount()-1),"Warm Start retains all-app rows");}check(menu.appList.getChildCount()==0,"Start never builds hidden results");menu.search.setText("Calculator");check(TenSmoke.description(menu.appList,"Open Calculator")!=null,"Search still finds tools");check(pin==menu.tileViews.values().iterator().next(),"Search retains hidden Start content");menu.hide();menu.show(false);check(pin==menu.tileViews.values().iterator().next(),"Returning from search reuses cached Start");menu.hide();});
        ui(()->{closeAll();for(String id:ExternalTasks.list())ExternalTasks.remove(id);store.quickLaunch(Collections.emptyList());home.refreshTen();});settle();
        for(boolean expanded:new boolean[]{true,false}){
            ui(()->{store.prefs.edit().putBoolean("taskbar_expanded",expanded).putBoolean("taskbar_static",false).commit();home.refreshTen();});settle();
            holdBlank(expanded);check(node("Task Manager")!=null,"Real blank hold offers Task Manager");check(node("Taskbar settings")!=null,"Real blank hold offers Taskbar settings");check(node("Nth Desktop options")!=null,"Nth controls remain reachable");
            capture("eleven-taskbar-menu-"+(expanded?"expanded":"collapsed"));
            if(expanded){tapNode("Task Manager");settle();check(find(TaskManagerPanel.class)!=null,"Menu opens actual Task Manager");ui(this::closeAll);settle();}
            else{tapNode("Taskbar settings");settle();check(find(TenSettings.class)!=null,"Menu opens taskbar Settings");ui(this::closeAll);settle();}
        }
        ui(()->{store.prefs.edit().putBoolean("taskbar_expanded",true).commit();home.refreshTen();});settle();holdBlank(true);tapNode("Nth Desktop options");check(node("Expand taskbar")!=null,"Expansion control is in submenu");tapNode("Expand taskbar");settle();check(!find(ElevenTaskbarItems.class).expanded,"Submenu changes expansion");
        out.putString("stream","PASS: "+checks+" Start cache and actual taskbar hold checks; worst of ten warm Start show calls = "+(worstWarm/1000000.0)+" ms.\n");finish(Activity.RESULT_OK,out);
    }catch(Throwable e){out.putString("stream","FAIL: "+android.util.Log.getStackTraceString(e));finish(Activity.RESULT_CANCELED,out);}}
    @Override void check(boolean ok,String message){checks++;super.check(ok,message);}
    void holdBlank(boolean expanded)throws Exception{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);int[] at=new int[2];items.getLocationOnScreen(at);float x=at[0]+(items.rowStart(0)+items.rowEnd(0))/2f,y=at[1]+Aero.dp(home,24);long down=SystemClock.uptimeMillis();input(MotionEvent.ACTION_DOWN,x,y,down);Thread.sleep(ViewConfiguration.getLongPressTimeout()+250);input(MotionEvent.ACTION_UP,x,y,down);waitForIdleSync();}
    void input(int action,float x,float y,long down){MotionEvent e=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);e.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(e,true),"Native input accepted");e.recycle();}
    AccessibilityNodeInfo node(String text){return node(getUiAutomation().getRootInActiveWindow(),text);}
    AccessibilityNodeInfo node(AccessibilityNodeInfo n,String text){if(n==null)return null;if(text.contentEquals(n.getText()==null?"":n.getText())||text.contentEquals(n.getContentDescription()==null?"":n.getContentDescription()))return n;for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo found=node(n.getChild(i),text);if(found!=null)return found;}return null;}
    void tapNode(String text)throws Exception{AccessibilityNodeInfo n=node(text);check(n!=null,"Menu item exists: "+text);Rect box=new Rect();n.getBoundsInScreen(box);long down=SystemClock.uptimeMillis();input(MotionEvent.ACTION_DOWN,box.centerX(),box.centerY(),down);input(MotionEvent.ACTION_UP,box.centerX(),box.centerY(),down);waitForIdleSync();}
    void capture(String name)throws Exception{Bitmap image=getUiAutomation().takeScreenshot();try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+".png"))){image.compress(Bitmap.CompressFormat.PNG,100,out);}finally{image.recycle();}}
}
