package com.nthexperience.desktop;

import android.content.ComponentName;
import org.json.*;
import java.util.*;

/** Bounded local search/sender choices. Notification contents never enter preferences. */
final class ElevenLocalState {
    static final String MUTED="eleven_muted_senders",EXCLUDED="eleven_search_excluded",SEARCHES="eleven_search_history",SEEN="eleven_seen_apps";
    static List<String> strings(DesktopStore store,String key){List<String> out=new ArrayList<>();try{JSONArray rows=new JSONArray(store.prefs.getString(key,"[]"));for(int i=0;i<Math.min(512,rows.length());i++){String s=rows.optString(i);if(!s.isEmpty()&&!out.contains(s))out.add(s);}}catch(JSONException ignored){}return out;}
    static void membership(DesktopStore store,String key,String value,boolean included,int limit){List<String> values=strings(store,key);values.remove(value);if(included)values.add(0,value);while(values.size()>limit)values.remove(values.size()-1);store.prefs.edit().putString(key,new JSONArray(values).toString()).apply();}
    static void searched(DesktopStore store,String query){String text=query.trim();if(!store.prefs.getBoolean("eleven_search_history_enabled",true)||text.isEmpty()||text.length()>256)return;membership(store,SEARCHES,text,true,12);}
    static void launched(DesktopStore store,String id){ComponentName name=ComponentName.unflattenFromString(id);if(id.length()<=1024&&name!=null&&name.getPackageName().matches("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+"))membership(store,SEEN,id,true,512);}
    static boolean source(DesktopStore store,String name){return store.prefs.getBoolean("eleven_search_"+name,true);}
    static boolean excluded(DesktopStore store,String uri){String value=uri;try{if(uri.startsWith("folder:"))value=DocumentFolders.path(uri).optString("tree");}catch(Exception ignored){}for(String root:strings(store,EXCLUDED))if(value.equals(root)||value.startsWith(root+"/"))return true;return false;}
    static boolean accepts(DesktopStore store,StartSearch.Result result){String group=result.group.toLowerCase(Locale.ROOT),id=result.id;if(id.startsWith("recent-file:"))return source(store,"recent")&&!excluded(store,id.substring(12));if(id.startsWith("search-file:"))return source(store,"files")&&!excluded(store,id.substring(12));if(id.startsWith("ten-setting:")||id.startsWith("setting:")||group.contains("setting")||group.contains("control panel"))return source(store,"settings");if(group.contains("note"))return source(store,"notes");if(group.contains("favorite"))return source(store,"favorites");if(group.contains("folder")||group.contains("linked"))return source(store,"files");return source(store,"apps");}
    static void validate(JSONObject values)throws JSONException{for(String key:new String[]{MUTED,EXCLUDED})if(values.has(key)){if(!(values.get(key) instanceof String))throw new JSONException("Invalid local filter");JSONArray a=new JSONArray(values.getString(key));if(a.length()>(key.equals(MUTED)?200:32))throw new JSONException("Too many local filters");Set<String> seen=new HashSet<>();for(int i=0;i<a.length();i++){Object o=a.get(i);if(!(o instanceof String))throw new JSONException("Invalid local filter entry");String s=(String)o;if(s.length()>8192||!seen.add(s)||key.equals(MUTED)&&!s.matches("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")||key.equals(EXCLUDED)&&!s.startsWith("content://"))throw new JSONException("Invalid local filter entry");}}}
    private ElevenLocalState(){}
}
