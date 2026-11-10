package com.nthexperience.desktop;
import org.json.*;
import java.util.*;

/** Validate new portable settings before the existing atomic backup restore. */
final class TenPreferences {
    static void validate(Map<String,Object> values)throws JSONException{
        if(values.containsKey(TaskbarRows.KEY)){Object raw=values.get(TaskbarRows.KEY);if(!(raw instanceof String))throw new JSONException("Invalid taskbar rows");TaskbarRows.parse((String)raw);}
        ElevenLocalState.validate(new JSONObject(values));TaskManagerState.validate(values);ElevenMarket.validate(values);
        if(values.containsKey("eleven_start_folders")){Object raw=values.get("eleven_start_folders");if(!(raw instanceof String)||((String)raw).length()>100)throw new JSONException("Invalid Start folders");for(String name:((String)raw).split(","))if(!Arrays.asList("","Settings","Documents","Downloads","Music","Pictures","Videos").contains(name))throw new JSONException("Invalid Start folder");}
        for(String key:new String[]{"eleven_taskbar_alignment","eleven_start_view","eleven_start_size"})if(values.containsKey(key)){List<String> valid=key.endsWith("alignment")?Arrays.asList("Left","Center"):key.endsWith("view")?Arrays.asList("Category","Grid","List"):Arrays.asList("Automatic","Small","Large");if(!valid.contains(values.get(key)))throw new JSONException("Invalid "+key);}
        if(values.containsKey("ten_font")&&!TenFonts.valid(String.valueOf(values.get("ten_font"))))throw new JSONException("Invalid launcher font");
        if(values.containsKey("ten_device_name")&&(!(values.get("ten_device_name") instanceof String)||((String)values.get("ten_device_name")).trim().isEmpty()||((String)values.get("ten_device_name")).length()>60))throw new JSONException("Invalid device name");
        if(values.containsKey("explorer_open_to")&&!Arrays.asList("Home","This PC").contains(values.get("explorer_open_to")))throw new JSONException("Invalid Explorer opening location");
        TenThemes.validateExtras(new JSONObject(values));
        if(values.containsKey("ten_news_mode")&&!Arrays.asList("Auto","Icon","Hidden").contains(values.get("ten_news_mode")))throw new JSONException("Invalid News display mode");
        if(values.containsKey("ten_saved_news")){JSONArray rows=new JSONArray((String)values.get("ten_saved_news"));if(rows.length()>40)throw new JSONException("Too many saved stories");for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);text(row,"title",300);text(row,"url",2048);if(!BrowserUrls.web(row.getString("url")))throw new JSONException("Invalid saved story link");if(row.has("summary"))text(row,"summary",360);if(row.has("image")){text(row,"image",2048);if(!row.getString("image").isEmpty()&&!FeedClient.validUrl(row.getString("image")))throw new JSONException("Invalid story image");}}}
        if(values.containsKey("ten_nota_history")){JSONArray rows=new JSONArray((String)values.get("ten_nota_history"));if(rows.length()>12)throw new JSONException("Too many recent writing requests");for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);text(row,"request",15000);text(row,"reply",15000);Object task=row.get("task");if(!(task instanceof Integer)||(int)task<0||(int)task>4)throw new JSONException("Invalid writing task");}}
        if(values.containsKey("ten_run_history")){JSONArray rows=new JSONArray((String)values.get("ten_run_history"));if(rows.length()>10)throw new JSONException("Too many Run commands");for(int i=0;i<rows.length();i++)if(!(rows.get(i) instanceof String)||rows.getString(i).length()>256)throw new JSONException("Invalid Run command");}
    }
    private static void text(JSONObject row,String key,int limit)throws JSONException{if(!(row.get(key) instanceof String)||row.getString(key).length()>limit)throw new JSONException("Invalid "+key);}
    private TenPreferences(){}
}
