package com.nthexperience.desktop;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.widget.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk=35)
public class StartCacheTest {
    ActivityController<Activity> controller;Activity activity;DesktopStore store;StartMenu menu;AeroWallpaper wallpaper;
    static final String ID="example.cache/.Main";
    @Before public void setup(){controller=Robolectric.buildActivity(Activity.class).setup().visible();activity=controller.get();store=new DesktopStore(activity);store.prefs.edit().clear().putBoolean("reduce_motion",true).commit();store.startPins(Collections.singletonList(ID));wallpaper=new AeroWallpaper(activity,11);menu=new StartMenu(activity,new StartMenu.Actions(){public void open(String id){}public void options(View v,String id){}public void home(){}public void settings(){}public boolean isHome(){return true;}},store,wallpaper);activity.setContentView(menu);menu.entries(Collections.singletonList(entry("Original")));menu.prepareWidth(420);menu.show(false);}
    AppCatalog.Entry entry(String name){return new AppCatalog.Entry(ID,name,new ColorDrawable(),System.currentTimeMillis());}
    @After public void close(){menu.hide();wallpaper.close();controller.pause().stop().destroy();}
    @Test public void warmOpenReusesPinsAndAllAppRows(){View pin=menu.tileViews.get(ID),row=menu.dashboard.getChildAt(menu.dashboard.getChildCount()-1);for(int i=0;i<10;i++){menu.hide();menu.show(false);assertSame(pin,menu.tileViews.get(ID));assertSame(row,menu.dashboard.getChildAt(menu.dashboard.getChildCount()-1));}assertEquals(0,menu.appList.getChildCount());}
    @Test public void searchDoesNotRebuildHiddenLanding(){View pin=menu.tileViews.get(ID);menu.search.setText("Original");assertTrue(menu.appList.getChildCount()>0);assertSame(pin,menu.tileViews.get(ID));menu.hide();menu.show(false);assertSame(pin,menu.tileViews.get(ID));assertEquals("",menu.search.getText().toString());assertEquals(View.VISIBLE,menu.landing.getVisibility());}
    @Test public void renameAndCatalogChangesInvalidateCache(){View before=menu.tileViews.get(ID);store.rename(ID,"Renamed");menu.show(false);assertNotSame(before,menu.tileViews.get(ID));assertNotNull(find(menu.dashboard,"Renamed"));store.prefs.edit().remove("shortcut_names").commit();menu.entries(Collections.singletonList(entry("Updated")));assertNotNull(find(menu.dashboard,"Updated"));menu.entries(Collections.emptyList());assertFalse(menu.tileViews.containsKey(ID));assertNull(find(menu.dashboard,"Updated"));}
    @Test public void pinsViewsAppearanceAndWidthInvalidateCache(){View pin=menu.tileViews.get(ID);menu.prepareWidth(640);assertNotSame(pin,menu.tileViews.get(ID));pin=menu.tileViews.get(ID);menu.appearanceChanged();assertNotSame(pin,menu.tileViews.get(ID));store.prefs.edit().putString("eleven_start_view","Category").commit();menu.show(false);assertNotNull(find(menu.dashboard,"All apps categories"));store.startPins(Collections.emptyList());menu.show(false);assertTrue(menu.tileViews.isEmpty());}
    @Test public void unrelatedTaskbarChangesKeepStartRows(){View pin=menu.tileViews.get(ID);store.prefs.edit().putBoolean("taskbar_expanded",true).commit();menu.show(false);assertSame(pin,menu.tileViews.get(ID));}
    @Test public void seenBadgeAndRecentFilesInvalidateCache(){assertNotNull(find(menu.tileViews.get(ID),"New app: Original"));ElevenLocalState.launched(store,ID);menu.show(false);assertNull(find(menu.tileViews.get(ID),"New app: Original"));store.prefs.edit().putBoolean("eleven_start_files",true).putString("recent_files","[{\"uri\":\"content://example/one\",\"name\":\"First file\"}]").commit();menu.show(false);assertNotNull(find(menu.dashboard,"First file"));store.prefs.edit().putString("recent_files","[]").commit();menu.show(false);assertNull(find(menu.dashboard,"First file"));}
    static View find(View view,String text){if(view==null)return null;if(text.contentEquals(view.getContentDescription()==null?"":view.getContentDescription())||view instanceof TextView&&text.contentEquals(((TextView)view).getText()))return view;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){View found=find(((ViewGroup)view).getChildAt(i),text);if(found!=null)return found;}return null;}
}
