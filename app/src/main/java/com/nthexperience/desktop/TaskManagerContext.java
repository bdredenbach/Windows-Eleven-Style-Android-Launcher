package com.nthexperience.desktop;

import android.content.*;
import android.view.ContextThemeWrapper;
import java.util.*;

/** A local app palette. Edits and all non-palette reads retain the shared desktop preferences. */
final class TaskManagerContext extends ContextThemeWrapper {
    private SharedPreferences wrapped;
    TaskManagerContext(Context base){super(base,0);}
    @Override public SharedPreferences getSharedPreferences(String name,int mode){SharedPreferences prefs=super.getSharedPreferences(name,mode);if(!name.equals("desktop"))return prefs;if(wrapped!=null)return wrapped;wrapped=new SharedPreferences(){
        boolean dark(){String choice=prefs.getString("taskmanager_theme","System");return choice.equals("Dark")||!choice.equals("Light")&&prefs.getBoolean("ten_apps_dark",false);}
        public Map<String,?> getAll(){Map<String,Object> all=new HashMap<>(prefs.getAll());all.put("ten_apps_dark",dark());return all;}
        public boolean getBoolean(String key,boolean fallback){return key.equals("ten_apps_dark")?dark():prefs.getBoolean(key,fallback);}
        public String getString(String key,String fallback){return prefs.getString(key,fallback);}
        public Set<String> getStringSet(String key,Set<String> fallback){return prefs.getStringSet(key,fallback);}
        public int getInt(String key,int fallback){return prefs.getInt(key,fallback);}
        public long getLong(String key,long fallback){return prefs.getLong(key,fallback);}
        public float getFloat(String key,float fallback){return prefs.getFloat(key,fallback);}
        public boolean contains(String key){return prefs.contains(key);}
        public Editor edit(){return prefs.edit();}
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){prefs.registerOnSharedPreferenceChangeListener(listener);}
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){prefs.unregisterOnSharedPreferenceChangeListener(listener);}
    };return wrapped;}
}
