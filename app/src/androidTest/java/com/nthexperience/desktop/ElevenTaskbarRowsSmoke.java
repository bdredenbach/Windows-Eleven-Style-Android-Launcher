package com.nthexperience.desktop;

import android.app.*;import android.content.*;import android.content.pm.ActivityInfo;import android.graphics.*;import android.os.*;import android.view.*;import android.widget.*;import org.json.*;import java.util.*;

/** Real swipe/drag regression for the owner's independently scrolling taskbar rows. */
public final class ElevenTaskbarRowsSmoke extends ElevenTaskbarSmoke {
    int checks;
    @Override public void onStart(){Bundle out=new Bundle();try{
        shell("wm size 1080x2400");store=new DesktopStore(getTargetContext());
        store.prefs.edit().putInt("startup_mode",2).putBoolean("welcome_on_start",false).putBoolean("reduce_motion",true).putBoolean("taskbar_static",false).putBoolean("taskbar_expanded",true).putBoolean("taskbar_show_labels",false).putString("ten_search_mode","Icon").remove(TaskbarRows.KEY).commit();
        getTargetContext().getSharedPreferences(InitializationState.PREFS,0).edit().putInt("flow_version",2).putInt("step",7).commit();
        getTargetContext().getSharedPreferences(StartupExperience.SESSION,0).edit().putBoolean("seen",true).putBoolean("welcome_seen",true).commit();
        home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();settle();
        for(String alignment:new String[]{"Left","Center"}){
            ui(()->{closeAll();for(String id:ExternalTasks.list())ExternalTasks.remove(id);store.prefs.edit().remove(TaskbarRows.KEY).putString("eleven_taskbar_alignment",alignment).putBoolean("taskbar_static",false).putBoolean("taskbar_show_labels",true).putBoolean("taskbar_expanded",true).commit();store.quickLaunch(Arrays.asList("@photos","@browser","@recycle","@calendar","@welcome","@focus-timer"));home.applyTenAppearance();for(String id:new String[]{"@notes","@settings","@recent","@backup","@taskmanager"})home.open(id);});settle();
            ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);items.scrollRowTo(0,0);items.scrollRowTo(1,0);check(items.rowRange(0)>Aero.dp(home,60)&&items.rowRange(1)>Aero.dp(home,60),"Both rows actually overflow");});settle();
            Map<String,String> fixed=new LinkedHashMap<>();for(Map.Entry<String,View> e:fixedViews().entrySet())fixed.put(e.getKey(),bounds(e.getValue()));
            List<String> top=geometryRow(0),bottom=geometryRow(1);swipeRow(0);waitFling();
            check(find(ElevenTaskbarItems.class).rowScroll(0)>0,"Upper swipe moves upper row");check(geometryRow(1).equals(bottom),"Upper swipe leaves every bottom app stationary");
            top=geometryRow(0);swipeRow(1);waitFling();check(find(ElevenTaskbarItems.class).rowScroll(1)>0,"Lower swipe moves lower row");check(geometryRow(0).equals(top),"Lower swipe leaves every upper app stationary");
            for(Map.Entry<String,View> e:fixedViews().entrySet())check(fixed.get(e.getKey()).equals(bounds(e.getValue())),"Fixed controls never scroll: "+e.getKey());
            ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);items.stopScrolling();items.scrollRowTo(0,0);items.scrollRowTo(1,0);store.prefs.edit().putBoolean("taskbar_show_labels",false).commit();home.refreshTen();});settle();
            dragAcross("@photos",0);checkRow("@photos",0,"Idle pin dragged to top");
            dragAcross("@notes",1);checkRow("@notes",1,"Running app dragged to bottom");
            String saved=store.prefs.getString(TaskbarRows.KEY,"");
            ui(()->{home.open("@photos");try{java.lang.reflect.Field f=MainActivity.class.getDeclaredField("windows");f.setAccessible(true);Map<?,?> windows=(Map<?,?>)f.get(home);((DesktopWindow)windows.get("@notes")).closeWindow();}catch(Exception e){throw new RuntimeException(e);}});settle();
            checkRow("@photos",0,"Pin retains chosen row after launch");ui(()->home.open("@notes"));settle();checkRow("@notes",1,"Running app retains chosen bottom row after reopen");
            ui(this::toggleMenu);settle();check(!find(ElevenTaskbarItems.class).expanded,"Collapsed mode remains one strip");ui(this::toggleMenu);settle();checkRow("@photos",0,"Expand restores saved top choice");checkRow("@notes",1,"Expand restores saved bottom choice");
            ui(()->home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));settle();checkRow("@photos",0,"Top choice survives rotation");ui(()->home.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));settle();checkRow("@notes",1,"Bottom choice survives rotation");
            DesktopBackup.Plan plan=DesktopBackup.parse(DesktopBackup.snapshot(store).toString());check(saved.equals(plan.values.get(TaskbarRows.KEY)),"Portable backup retains row choices");
            ui(()->{store.prefs.edit().putBoolean("taskbar_static",true).putBoolean("taskbar_show_labels",true).commit();home.refreshTen();});settle();
            ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int i=0;i<items.getChildCount();i++){View child=items.getChildAt(i);int row=ElevenTaskbarItems.row(child);check(child.getLeft()>=items.rowStart(row)&&child.getRight()<=items.rowEnd(row),"Static fit respects each manually assigned row");}check(items.rowScroll(0)==0&&items.rowScroll(1)==0,"Static mode resets both scroll offsets");});
            shot("eleven-rows-"+alignment.toLowerCase(Locale.ROOT));
        }
        ui(()->{View notes=slot("@notes");check(notes.performAccessibilityAction(R.id.taskbar_move_top,null),"Accessible move-to-top action accepted");});settle();checkRow("@notes",0,"Accessibility row move persisted");
        ui(()->{View notes=slot("@notes");check(notes.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,0,KeyEvent.META_CTRL_ON|KeyEvent.META_SHIFT_ON)),"Keyboard row move accepted");});settle();checkRow("@notes",1,"Keyboard row move persisted");
        out.putString("stream","PASS: "+checks+" independent-row assertions; real upper/lower swipes leave all apps in the opposite row stationary, native drag/drop in both directions, default and saved placement through launch/close/reopen, collapse/expand, rotation, portable backup, static fitting, fixed controls and accessible/keyboard row moves.\n");finish(Activity.RESULT_OK,out);
    }catch(Throwable e){out.putString("stream","FAIL: "+android.util.Log.getStackTraceString(e));try{shot("eleven-rows-failure");}catch(Exception ignored){}finish(Activity.RESULT_CANCELED,out);}}
    @Override void check(boolean ok,String message){checks++;super.check(ok,message);}
    List<String> geometryRow(int row){List<String> result=new ArrayList<>();ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int i=0;i<items.getChildCount();i++){View c=items.getChildAt(i);if(ElevenTaskbarItems.row(c)==row)result.add(TaskbarReorder.key(c)+":"+bounds(c));}return result;}
    void checkRow(String id,int row,String message)throws Exception{ui(()->{View child=slot(id);check(child!=null&&ElevenTaskbarItems.row(child)==row&&child.getTop()==row*Aero.dp(home,48),message);});}
    void touch(int action,float x,float y,long down){MotionEvent event=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);event.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(event,true),"Native row input accepted");event.recycle();}
    void swipeRow(int row)throws Exception{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);int[] at=new int[2];items.getLocationOnScreen(at);float start=at[0]+items.rowEnd(row)-Aero.dp(home,8),end=at[0]+items.rowStart(row)+Aero.dp(home,8),y=at[1]+Aero.dp(home,row*48+24);long now=SystemClock.uptimeMillis();touch(MotionEvent.ACTION_DOWN,start,y,now);for(int i=1;i<=6;i++){Thread.sleep(10);touch(MotionEvent.ACTION_MOVE,start+(end-start)*i/6,y,now);}touch(MotionEvent.ACTION_UP,end,y,now);}
    void waitFling()throws Exception{Thread.sleep(700);ui(()->find(ElevenTaskbarItems.class).stopScrolling());settle();}
    void dragAcross(String id,int row)throws Exception{
        ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);items.stopScrolling();items.scrollRowTo(0,0);items.scrollRowTo(1,0);});settle();
        View source=slot(id);int[] at=new int[2],origin=new int[2];source.getLocationOnScreen(origin);ElevenTaskbarItems items=find(ElevenTaskbarItems.class);items.getLocationOnScreen(at);float x=origin[0]+source.getWidth()/2f,y=origin[1]+source.getHeight()/2f,targetX=at[0]+items.rowStart(row)+Aero.dp(home,16),targetY=at[1]+Aero.dp(home,row*48+24);long down=SystemClock.uptimeMillis();touch(MotionEvent.ACTION_DOWN,x,y,down);Thread.sleep(185);touch(MotionEvent.ACTION_MOVE,x+Aero.dp(home,14),y,down);Thread.sleep(120);
        for(int i=1;i<=5;i++){touch(MotionEvent.ACTION_MOVE,x+(targetX-x)*i/5,y+(targetY-y)*i/5,down);Thread.sleep(35);}touch(MotionEvent.ACTION_UP,targetX,targetY,down);Thread.sleep(150);settle();
    }
}
