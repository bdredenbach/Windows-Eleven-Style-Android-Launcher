package com.nthexperience.desktop;

import java.util.*;
import org.json.*;

/** One ordinary draft per desktop. A private marker never contains private text. */
final class NotaDraftStore {
    static final String KEY="eleven_nota_drafts";
    static final int VERSION=1;
    static JSONObject empty(){return state("",0,false,"",false,"",true,0,0);}
    static JSONObject privatePlaceholder(){JSONObject state=new JSONObject();GadgetLayout.put(state,"version",VERSION);GadgetLayout.put(state,"private",true);return state;}
    static JSONObject state(String draft,int task,boolean imported,String reply,boolean command,String source,boolean guidance,int start,int end){
        JSONObject state=new JSONObject();GadgetLayout.put(state,"version",VERSION);GadgetLayout.put(state,"private",false);
        GadgetLayout.put(state,"draft",limit(draft,15000));GadgetLayout.put(state,"task",Math.max(0,Math.min(5,task)));GadgetLayout.put(state,"imported",imported);
        GadgetLayout.put(state,"reply",limit(reply,15000));GadgetLayout.put(state,"command",command);GadgetLayout.put(state,"source",limit(source,512));GadgetLayout.put(state,"guidance",guidance);
        GadgetLayout.put(state,"start",Math.max(0,Math.min(Math.min(15000,draft.length()),start)));GadgetLayout.put(state,"end",Math.max(0,Math.min(Math.min(15000,draft.length()),end)));return state;
    }
    private static String limit(String value,int max){return value.substring(0,Math.min(value.length(),max));}
    static void validateState(JSONObject state)throws JSONException{
        if(integer(state,"version")!=VERSION||!(state.get("private") instanceof Boolean))throw new JSONException("Unsupported Nota state");
        Set<String> allowed=state.getBoolean("private")?new HashSet<>(Arrays.asList("version","private")):new HashSet<>(Arrays.asList("version","private","draft","task","imported","reply","command","source","guidance","start","end"));
        for(Iterator<String> keys=state.keys();keys.hasNext();)if(!allowed.contains(keys.next()))throw new JSONException("Unexpected Nota state field");
        if(state.getBoolean("private"))return;
        text(state,"draft",15000);text(state,"reply",15000);text(state,"source",512);
        for(String key:new String[]{"imported","command","guidance"})if(!(state.get(key) instanceof Boolean))throw new JSONException("Invalid Nota flag");
        int task=integer(state,"task"),start=integer(state,"start"),end=integer(state,"end");
        if(task<0||task>5||start<0||end<0||start>state.getString("draft").length()||end>state.getString("draft").length())throw new JSONException("Invalid Nota task or selection");
        if(!state.getString("source").isEmpty()&&!state.getBoolean("imported"))throw new JSONException("Nota source requires writing origin");
    }
    private static void text(JSONObject state,String key,int max)throws JSONException{if(!(state.get(key) instanceof String)||state.getString(key).length()>max)throw new JSONException("Invalid Nota text");}
    private static int integer(JSONObject state,String key)throws JSONException{Object n=state.get(key);if(!(n instanceof Number)||((Number)n).doubleValue()!=((Number)n).intValue())throw new JSONException("Invalid Nota number");return ((Number)n).intValue();}
    static JSONObject validate(String text,JSONObject desktops)throws JSONException{
        JSONObject map=new JSONObject(text);if(map.length()>TenDesktops.LIMIT)throw new JSONException("Too many Nota drafts");Set<String> owners=new HashSet<>();
        if(desktops!=null){JSONArray rows=desktops.getJSONArray("desktops");for(int i=0;i<rows.length();i++)owners.add(rows.getJSONObject(i).getString("id"));}
        for(Iterator<String> keys=map.keys();keys.hasNext();){String id=keys.next();if(!id.matches("[a-z0-9-]{1,40}")||desktops!=null&&!owners.contains(id))throw new JSONException("Unknown Nota desktop");
            JSONObject entry=map.getJSONObject(id);for(Iterator<String> fields=entry.keys();fields.hasNext();)if(!Arrays.asList("ordinary","private").contains(fields.next()))throw new JSONException("Invalid Nota draft entry");
            if(!(entry.get("private") instanceof Boolean))throw new JSONException("Invalid private marker");JSONObject normal=entry.getJSONObject("ordinary");validateState(normal);if(normal.getBoolean("private"))throw new JSONException("Ordinary Nota draft is missing");
        }return map;
    }
    private static JSONObject map(DesktopStore store){try{return validate(store.prefs.getString(KEY,"{}"),null);}catch(JSONException ignored){return new JSONObject();}}
    static void migrate(DesktopStore store){
        if(store.prefs.contains(KEY))return;String owner="desktop-1";
        try{owner=new JSONObject(store.prefs.getString(TenDesktops.KEY,"{}")).getJSONArray("desktops").getJSONObject(0).getString("id");}catch(JSONException ignored){}
        String draft=store.prefs.getString("ten_nota_draft",""),reply=store.prefs.getString("ten_nota_reply","");JSONObject map=new JSONObject();
        if(!draft.isEmpty()||!reply.isEmpty())GadgetLayout.put(map,owner,entry(state(draft,store.prefs.getInt("ten_nota_task",0),store.prefs.getBoolean("ten_nota_imported",!draft.isEmpty()),reply,false,"",reply.isEmpty(),draft.length(),draft.length()),false));
        store.prefs.edit().putString(KEY,map.toString()).remove("ten_nota_draft").remove("ten_nota_reply").remove("ten_nota_task").remove("ten_nota_imported").apply();
    }
    private static JSONObject entry(JSONObject ordinary,boolean privateNow){JSONObject value=new JSONObject();GadgetLayout.put(value,"ordinary",ordinary);GadgetLayout.put(value,"private",privateNow);return value;}
    static JSONObject read(DesktopStore store,String scope){migrate(store);JSONObject entry=map(store).optJSONObject(scope);if(entry==null)return empty();return entry.optBoolean("private")?privatePlaceholder():entry.optJSONObject("ordinary");}
    static void write(DesktopStore store,String scope,JSONObject state){
        migrate(store);JSONObject map=map(store);boolean privateNow=state.optBoolean("private");JSONObject previous=map.optJSONObject(scope);
        JSONObject normal=privateNow?(previous==null?empty():previous.optJSONObject("ordinary")):state;
        GadgetLayout.put(map,scope,entry(normal==null?empty():normal,privateNow));store.prefs.edit().putString(KEY,map.toString()).apply();
    }
    static void move(DesktopStore store,String from,String to){migrate(store);JSONObject map=map(store);Object value=map.remove(from);GadgetLayout.put(map,to,value==null?entry(empty(),false):value);store.prefs.edit().putString(KEY,map.toString()).apply();}
    static void remove(DesktopStore store,String scope){JSONObject map=map(store);map.remove(scope);store.prefs.edit().putString(KEY,map.toString()).apply();}
    private NotaDraftStore(){}
}
