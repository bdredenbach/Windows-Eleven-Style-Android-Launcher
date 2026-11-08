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
        navigation();pageCards();focus();menus();snapping();explorer();
        out.putString("stream","PASS: Eleven recording gaps "+checks+" assertions; Settings Back/Forward, nested ancestors, search/scroll/recreation, cards/pages, Focus timer/expiry/manual-state preservation, cascading menus, top drag Snap/drop/cancellation, provider-backed Explorer filters, tab state and staged Folder Options.\n");finish(Activity.RESULT_OK,out);
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
        setInTouchMode(false);waitForIdleSync();
        final DesktopContextMenu[] parent={null},child={null};final boolean[] selected={false};
        ui(()->{parent[0]=new DesktopContextMenu(home,"Cascade parent");parent[0].submenu("View","apps",()->{child[0]=new DesktopContextMenu(home,"Cascade child");child[0].item("Child choice","checked",()->selected[0]=true);ContextMenus.show(child[0],root,1,1);});ContextMenus.show(parent[0],root,1,1);});waitForIdleSync();
        ui(()->{items(parent[0]).getChildAt(0).performClick();check(parent[0].showing()&&child[0]!=null&&child[0].showing(),"Cascading child keeps its parent open");});shot("eleven-gaps-cascade");
        ui(()->{Rect a=menuRect(parent[0]),b=menuRect(child[0]);check(b.left>=a.right-Aero.dp(home,10)||b.right<=a.left+Aero.dp(home,10),"Portrait cascade keeps both menus visible side by side");View choice=items(child[0]).getChildAt(0);check(choice.requestFocus(),"Child choice receives keyboard focus");check(choice.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT))&&!child[0].showing()&&parent[0].showing(),"Left arrow closes only the child branch");check(items(parent[0]).getChildAt(0).dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT))&&child[0].showing(),"Right arrow reopens the focused submenu");choice=items(child[0]).getChildAt(0);int[] point=new int[2];choice.getLocationOnScreen(point);check(point[0]>=0&&point[1]>=0&&point[0]+choice.getWidth()<=root.getWidth()+Aero.dp(home,2),"Cascade stays inside the viewport");choice.performClick();check(selected[0]&&!parent[0].showing()&&!child[0].showing(),"Leaf choice closes the full cascade once");});
    }
    void explorer()throws Exception{
        String authority="com.nthexperience.desktop.fixture.documents";
        android.net.Uri tree=android.provider.DocumentsContract.buildTreeDocumentUri(authority,"root:");
        try(ParcelFileDescriptor fd=getUiAutomation().executeShellCommand("am start -n com.nthexperience.desktop.fixture/.GrantActivity --ez reset true");InputStream in=new ParcelFileDescriptor.AutoCloseInputStream(fd)){byte[] b=new byte[1024];while(in.read(b)!=-1){}}
        long deadline=SystemClock.uptimeMillis()+12000;boolean granted=false;while(SystemClock.uptimeMillis()<deadline){try{getTargetContext().getContentResolver().takePersistableUriPermission(tree,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);granted=true;break;}catch(SecurityException waiting){SystemClock.sleep(100);}}
        check(granted,"Explorer receives a real fixture tree grant");
        android.net.Uri parent=android.provider.DocumentsContract.buildDocumentUriUsingTree(tree,"root:");
        for(String name:new String[]{"Alpha.PDF","Sheet.XLSX","Photo.AVIF","Word.DOCX"})DocumentOps.createText(getTargetContext(),parent,name,"Isolated recording-gap fixture",new DocumentOps.Cancel());
        final VistaExplorer[] browser={null};
        ui(()->{store.prefs.edit().putBoolean("explorer_extensions",true).putBoolean("explorer_item_checks",false).putBoolean("explorer_double_click",false).putString("explorer_view_mode","Details").commit();home.open("@files");browser[0]=TenSmoke.find(root,VistaExplorer.class);window(browser[0]).maximize();browser[0].openLinked(tree.toString(),"Recording gap files");});
        awaitText(browser[0],"Alpha.PDF");
        ui(()->{browser[0].setFilter("PDF");});awaitText(browser[0],"Alpha.PDF");ui(()->{VistaExplorer e=browser[0];check(TenSmoke.text(e,"Alpha.PDF")!=null&&TenSmoke.text(e,"Sheet.XLSX")==null,"PDF filter changes the actual provider-backed rows");check(TenSmoke.text(e,"Target")!=null,"Type filtering keeps folders navigable");e.newTab();check(e.fileFilter().equals("All file types"),"A fresh tab starts without another tab's filter");e.setFilter("Images");e.selectTab(0);check(e.fileFilter().equals("PDF"),"Switching tabs restores the first filter");e.selectTab(e.tabCount()-1);check(e.fileFilter().equals("Images"),"Second tab retains its own filter");e.selectTab(0);});
        awaitText(browser[0],"Alpha.PDF");shot("eleven-gaps-explorer-filter");
        final ElevenFolderOptions[] options={null};
        ui(()->{options[0]=new ElevenFolderOptions(home,store,browser[0]::applyOptions);options[0].show();View dialog=folderDialog(options[0]).getWindow().getDecorView();TenSmoke.description(dialog,"Folder Options View tab").performClick();((CheckBox)TenSmoke.description(dialog,"File name extensions")).setChecked(false);check(store.prefs.getBoolean("explorer_extensions",false),"Folder Options edits stay staged");folderDialog(options[0]).getButton(AlertDialog.BUTTON_NEGATIVE).performClick();check(store.prefs.getBoolean("explorer_extensions",false),"Cancel discards the draft");
            options[0]=new ElevenFolderOptions(home,store,browser[0]::applyOptions);options[0].show();dialog=folderDialog(options[0]).getWindow().getDecorView();TenSmoke.description(dialog,"Folder Options View tab").performClick();((CheckBox)TenSmoke.description(dialog,"File name extensions")).setChecked(false);((CheckBox)TenSmoke.description(dialog,"Item check boxes")).setChecked(true);TenSmoke.description(dialog,"Folder Options Search tab").performClick();((CheckBox)TenSmoke.description(dialog,"Match case")).setChecked(true);TenSmoke.description(dialog,"Folder Options View tab").performClick();check(!((CheckBox)TenSmoke.description(dialog,"File name extensions")).isChecked(),"Draft survives switching Folder Options tabs");folderDialog(options[0]).getButton(AlertDialog.BUTTON_NEUTRAL).performClick();check(folderDialog(options[0]).isShowing()&&!store.prefs.getBoolean("explorer_extensions",true)&&store.prefs.getBoolean("explorer_match_case",false),"Apply commits without closing the dialog");});
        shot("eleven-gaps-folder-options");
        ui(()->{folderDialog(options[0]).getButton(AlertDialog.BUTTON_POSITIVE).performClick();});awaitText(browser[0],"Alpha");ui(()->{VistaExplorer e=browser[0];check(TenSmoke.text(e,"Alpha")!=null&&TenSmoke.text(e,"Alpha.PDF")==null,"Applied extension choice changes the file label");check(TenSmoke.description(e,"Select Alpha.PDF")!=null,"Applied check-box choice changes actual rows");e.search.setText("alpha");});waitForIdleSync();ui(()->{check(TenSmoke.text(browser[0],"Alpha")==null,"Applied case-sensitive search filters actual rows");browser[0].search.setText("Alpha");});awaitText(browser[0],"Alpha");ui(()->{VistaExplorer e=browser[0];check(TenSmoke.text(e,"Alpha")!=null,"Case-sensitive search accepts an exact name");e.search.setText("");e.setFilter("All file types");
            try{JSONObject state=e.state();state.getJSONObject("extra").put("metadataPane",true).put("previewEnabled",false);e.restore(state);}catch(JSONException failure){throw new RuntimeException(failure);}
        });
        awaitText(browser[0],"Alpha");ui(()->check(TenSmoke.description(browser[0],"Explorer metadata pane")!=null,"Details pane restores with its tab"));shot("eleven-gaps-explorer-details");
        ui(()->{store.prefs.edit().putBoolean("explorer_extensions",true).putBoolean("explorer_item_checks",false).putBoolean("explorer_match_case",false).commit();browser[0].setFilter("All file types");browser[0].navigate("home");check(TenSmoke.description(browser[0],"Explorer Home")!=null,"Home uses the recorded recent/favorites composition");});
    }
    AlertDialog folderDialog(ElevenFolderOptions options){try{Field f=ElevenFolderOptions.class.getDeclaredField("dialog");f.setAccessible(true);return (AlertDialog)f.get(options);}catch(Exception e){throw new RuntimeException(e);}}
    void awaitText(View view,String text)throws Exception{long until=SystemClock.uptimeMillis()+10000;final boolean[] found={false};while(SystemClock.uptimeMillis()<until){ui(()->found[0]=TenSmoke.text(view,text)!=null);if(found[0])return;SystemClock.sleep(80);}throw new AssertionError("Explorer row did not appear: "+text);}
    void snapping()throws Exception{snapOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,AeroSnap.Placement.TOP_RIGHT);snapOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,AeroSnap.Placement.LEFT);}
    void snapOrientation(int orientation,AeroSnap.Placement target)throws Exception{
        ui(()->{home.setRequestedOrientation(orientation);home.open("@settings");settingsWindow.restoreDown();});settle();
        FrameLayout host=(FrameLayout)settingsWindow.getParent();final View[] label={null};final int[] start={0,0},origin={0,0};
        ui(()->{label[0]=((ViewGroup)settingsWindow.getChildAt(0)).getChildAt(1);label[0].getLocationOnScreen(start);start[0]+=label[0].getWidth()/2;start[1]+=label[0].getHeight()/2;host.getLocationOnScreen(origin);});
        long down=SystemClock.uptimeMillis();input(down,MotionEvent.ACTION_DOWN,start[0],start[1]);input(down,MotionEvent.ACTION_MOVE,origin[0]+host.getWidth()/2,origin[1]+Aero.dp(home,8));waitForIdleSync();
        final RectF[] zone={null};ui(()->{check(settingsWindow.snapBar!=null,"Caption drag opens the top Snap bar");zone[0]=settingsWindow.snapBar.zone(target);check(zone[0]!=null,"Requested zone is viable for this orientation: "+target);});
        float x=origin[0]+zone[0].centerX(),y=origin[1]+zone[0].centerY();input(down,MotionEvent.ACTION_MOVE,x,y);shot("eleven-gaps-drag-snap-"+target.name().toLowerCase(java.util.Locale.ROOT));input(down,MotionEvent.ACTION_UP,x,y);waitForIdleSync();
        ui(()->{check(settingsWindow.snapBar==null,"Dropping removes the bar");check(settingsWindow.geometry().optString("snap").equals(target.name()),"Drop uses the selected placement "+target);settingsWindow.restoreDown();});waitForIdleSync();
        ui(()->{label[0].getLocationOnScreen(start);start[0]+=label[0].getWidth()/2;start[1]+=label[0].getHeight()/2;});down=SystemClock.uptimeMillis();input(down,MotionEvent.ACTION_DOWN,start[0],start[1]);input(down,MotionEvent.ACTION_MOVE,origin[0]+host.getWidth()/2,origin[1]+Aero.dp(home,8));input(down,MotionEvent.ACTION_CANCEL,origin[0]+host.getWidth()/2,origin[1]+Aero.dp(home,8));
        ui(()->check(settingsWindow.snapBar==null&&!settingsWindow.snapped(),"Cancelled top drag cleans up and restores floating placement"));
    }
    String backupText(){try{return DesktopBackup.snapshot(store).toString();}catch(JSONException e){throw new RuntimeException(e);}}
    void input(long down,int action,float x,float y){MotionEvent e=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);try{e.setSource(InputDevice.SOURCE_TOUCHSCREEN);check(getUiAutomation().injectInputEvent(e,true),"Injected drag event "+action);}finally{e.recycle();}}
    Rect menuRect(DesktopContextMenu menu){View v=items(menu);int[] at=new int[2];v.getLocationOnScreen(at);return new Rect(at[0],at[1],at[0]+v.getWidth(),at[1]+v.getHeight());}
    LinearLayout items(DesktopContextMenu menu){try{Field f=DesktopContextMenu.class.getDeclaredField("items");f.setAccessible(true);return (LinearLayout)f.get(menu);}catch(Exception e){throw new RuntimeException(e);}}
    DesktopWindow window(View view){for(ViewParent p=view.getParent();p!=null;p=p.getParent())if(p instanceof DesktopWindow)return (DesktopWindow)p;throw new AssertionError("No Settings window");}
    void ui(Runnable run)throws Exception{FutureTask<Void> f=new FutureTask<>(run,null);home.runOnUiThread(f);f.get(30,TimeUnit.SECONDS);}
    void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    void settle()throws Exception{SystemClock.sleep(1500);waitForIdleSync();}
    void shot(String name)throws Exception{waitForIdleSync();SystemClock.sleep(250);ui(()->{Bitmap b=Bitmap.createBitmap(root.getWidth(),root.getHeight(),Bitmap.Config.ARGB_8888);try{root.draw(new Canvas(b));try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+"-view.png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}}catch(IOException e){throw new RuntimeException(e);}finally{b.recycle();}});Bitmap b=getUiAutomation().takeScreenshot();if(b!=null)try(OutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}finally{b.recycle();}}
}
