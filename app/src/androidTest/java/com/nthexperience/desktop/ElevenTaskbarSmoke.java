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
public final class ElevenTaskbarSmoke extends Instrumentation {
    MainActivity home;View root;DesktopStore store;int scenarios,frames;
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
        result.putString("stream","PASS: "+scenarios+" taskbar height scenarios / "+frames+" actual pre-draw frames; 21 desktop icons retain screen positions, cell sizes and icon sizes across repeated menu, Settings and Control Panel expansion/collapse; 5x9 imported-style grid at 80% icon size, left/center alignment, portrait/landscape, automatic unsaved layout, free placement and scrolled sheet; saved coordinates unchanged; unpinned Android task buttons retain order and row positions when Control Panel opens/closes; manual drag order and pin edits honored; taller taskbar only reduces visible scroll area\n");finish(Activity.RESULT_OK,result);
    }catch(Throwable error){result.putString("stream","FAIL: "+android.util.Log.getStackTraceString(error));try{shot("eleven-taskbar-failure");}catch(Exception ignored){}finish(Activity.RESULT_CANCELED,result);}}
    void recentTaskbarStability()throws Exception{
        List<String> probes=new ArrayList<>();for(AppCatalog.Entry entry:AppCatalog.load(home))if(entry.id.startsWith("com.nthexperience.desktop.fixture/"))probes.add(entry.id);
        check(probes.size()>=5,"Five installed Android fixture shortcuts");probes=new ArrayList<>(probes.subList(0,5));final List<String> apps=probes;
        ui(()->{for(String id:ExternalTasks.list())ExternalTasks.remove(id);for(String id:apps)ExternalTasks.opened(id);store.quickLaunch(Collections.emptyList());store.prefs.edit().putBoolean("taskbar_static",false).putBoolean("taskbar_apps",true).putBoolean("taskbar_expanded",true).commit();home.applyTenAppearance();});settle();
        check(barKeys().equals(apps),"Unpinned Android button order established");
        for(int n=0;n<2;n++){
            Map<String,String> before=barGeometry();
            ui(()->home.open("setting:page:taskbar"));settle();check(barKeys().subList(0,5).equals(apps),"Opening Control Panel appends its button after Android apps");check(barGeometry().entrySet().containsAll(before.entrySet()),"Existing buttons keep their rows while Control Panel is open");
            ui(this::closeProperties);settle();check(barKeys().equals(apps),"Closing Control Panel retains Android button order");check(barGeometry().equals(before),"Closing Control Panel does not flip existing rows");
            ui(this::toggleMenu);settle();check(barKeys().equals(apps),"Collapse retains session order");ui(this::toggleMenu);settle();check(barKeys().equals(apps),"Expansion retains session order");
        }
        List<String> manual=new ArrayList<>(apps);Collections.reverse(manual);ui(()->{store.taskbarOrder(manual);home.refreshTen();});settle();check(barKeys().equals(manual),"Explicit saved drag order wins over session order");
        ui(()->{store.quickLaunch(Collections.singletonList(apps.get(0)));home.refreshTen();});settle();check(barKeys().get(0).equals(apps.get(0)),"Changed pins reset the default pinned order");shot("eleven-taskbar-stable-android-taskbar");
    }
    void keyboardAndRows()throws Exception{
        for(boolean fit:new boolean[]{false,true})for(boolean labels:new boolean[]{false,true}){
            ui(()->{store.prefs.edit().putBoolean("taskbar_static",fit).putBoolean("taskbar_show_labels",labels).putBoolean("taskbar_expanded",true).commit();home.refreshTen();});settle();
            ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);check(items.expanded&&items.getHeight()==Aero.dp(home,96),"Two full-height rows");
                for(int i=0;i<items.getChildCount();i++){View child=items.getChildAt(i);check(child.getHeight()==Aero.dp(home,48)&&child.getTop()==i%2*Aero.dp(home,48),"Normal touch height on correct row");if(fit)check(child.getRight()<=((HorizontalScrollView)items.getParent()).getWidth(),"Static buttons fit the visible strip");}
                try{java.lang.reflect.Field field=MainActivity.class.getDeclaredField("taskbarReorder");field.setAccessible(true);Object reorder=field.get(home);java.lang.reflect.Method hit=TaskbarReorder.class.getDeclaredMethod("indexAt",float.class,float.class,String.class);hit.setAccessible(true);View top=items.getChildAt(0),bottom=items.getChildAt(1);String moving=TaskbarReorder.key(items.getChildAt(items.getChildCount()-1));float x=top.getLeft()+top.getWidth()/2f-1;int a=(Integer)hit.invoke(reorder,x,top.getTop()+top.getHeight()/2f,moving),b=(Integer)hit.invoke(reorder,x,bottom.getTop()+bottom.getHeight()/2f,moving);check(a==0&&b==1,"Drag insertion distinguishes upper and lower rows");}catch(Exception e){throw new RuntimeException(e);}
            });
        }
        List<String> before=barKeys();ui(()->{View first=find(ElevenTaskbarItems.class).getChildAt(0);first.dispatchKeyEvent(new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT,0,KeyEvent.META_CTRL_ON|KeyEvent.META_SHIFT_ON));});settle();check(barKeys().get(1).equals(before.get(0)),"Actual keyboard reorder persists across rows");
        DesktopBackup.Plan plan=DesktopBackup.parse(DesktopBackup.snapshot(store).toString());check(Boolean.TRUE.equals(plan.values.get("taskbar_expanded")),"Expansion participates in portable backups");shot("eleven-taskbar-static-labels");
    }
    List<String> barKeys()throws Exception{List<String> ids=new ArrayList<>();ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int n=0;n<items.getChildCount();n++){String id=TaskbarReorder.key(items.getChildAt(n));if(id!=null)ids.add(id);}});return ids;}
    Map<String,String> barGeometry()throws Exception{Map<String,String> values=new LinkedHashMap<>();ui(()->{ElevenTaskbarItems items=find(ElevenTaskbarItems.class);for(int n=0;n<items.getChildCount();n++){View child=items.getChildAt(n);String id=TaskbarReorder.key(child);if(id!=null)values.put(id,child.getLeft()+","+child.getTop()+","+child.getWidth()+","+child.getHeight());}});return values;}
    void closeProperties(){View parent=find(VistaControlPanel.class);while(!(parent instanceof DesktopWindow))parent=(View)parent.getParent();((DesktopWindow)parent).closeWindow();}
    void scenario(String name,boolean custom,boolean snap,boolean scrolled)throws Exception{
        ui(()->{
            store.prefs.edit().putBoolean("taskbar_expanded",false).putBoolean("taskbar_static",true).putBoolean("snap_desktop",snap).putInt("desktop_columns_portrait",custom?5:0).putInt("desktop_rows_portrait",custom?9:0).putInt("desktop_columns_landscape",custom?5:0).putInt("desktop_rows_landscape",custom?6:0).putInt("desktop_icon_percent",80).putInt("desktop_spacing_x",0).putInt("desktop_spacing_y",0).commit();
            try{JSONObject folders=new JSONObject();List<String> ids=new ArrayList<>();for(int n=0;n<20;n++){String id="folder:"+new UUID(0,n+1);ids.add(id);folders.put(id,new JSONObject().put("name","Folder "+(n+1)).put("items",new JSONArray().put("@notes")));}ids.add("@recycle");store.prefs.edit().putString("app_folders",folders.toString()).commit();store.desktop(ids);TenDesktopItems.positions(store,"portrait",new JSONObject());TenDesktopItems.positions(store,"landscape",new JSONObject());}catch(JSONException e){throw new RuntimeException(e);}
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
    void verify(Watcher watcher,String action){check(watcher.failure==null,action+": "+watcher.failure);}
    final class Watcher implements ViewTreeObserver.OnPreDrawListener{
        final Map<String,String> baseline=geometry();int count;String failure;
        public boolean onPreDraw(){count++;Map<String,String> now=geometry();if(failure==null&&!baseline.equals(now)){for(String id:baseline.keySet())if(!Objects.equals(baseline.get(id),now.get(id))){failure=id+" moved from "+baseline.get(id)+" to "+now.get(id);break;}if(failure==null)failure="Desktop membership changed";}return true;}
    }
    Map<String,String> geometry(){
        DesktopIconGrid grid=find(DesktopIconGrid.class);Map<String,String> values=new LinkedHashMap<>();
        for(int n=0;n<grid.getChildCount();n++){View child=grid.getChildAt(n);int[] at=new int[2];child.getLocationOnScreen(at);View icon=((ViewGroup)child).getChildAt(0);values.put(String.valueOf(child.getTag()),at[0]+","+at[1]+","+child.getWidth()+","+child.getHeight()+","+icon.getWidth()+","+icon.getHeight());}View clock=TenSmoke.description(root,"Clock and calendar");int[] at=new int[2];clock.getLocationOnScreen(at);values.put("fixed-clock",String.valueOf(at[1]));return values;
    }
    void toggleMenu(){try{View taskbar=TenSmoke.description(root,"Desktop taskbar");java.lang.reflect.Method method=MainActivity.class.getDeclaredMethod("taskbarMenu",View.class,float.class,float.class);method.setAccessible(true);method.invoke(home,taskbar,10f,10f);java.lang.reflect.Field field=MainActivity.class.getDeclaredField("desktopMenu");field.setAccessible(true);Object menu=field.get(home);java.lang.reflect.Field items=DesktopContextMenu.class.getDeclaredField("items");items.setAccessible(true);View toggle=TenSmoke.description((View)items.get(menu),"Expand taskbar");check(toggle!=null,"Actual expansion menu row");toggle.performClick();}catch(Exception error){throw new RuntimeException(error);}}
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
