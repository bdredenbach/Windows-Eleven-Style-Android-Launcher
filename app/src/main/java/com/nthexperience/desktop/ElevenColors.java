package com.nthexperience.desktop;

import android.graphics.*;
import org.json.*;
import java.util.*;

final class ElevenColors {
    static final String MODE="eleven_accent_mode",RECENT="eleven_recent_colors";
    static int parse(String value){String hex=value.trim().replaceFirst("^#","");if(!hex.matches("[0-9a-fA-F]{6}"))throw new IllegalArgumentException("Enter six hexadecimal digits, such as 0078D4.");return 0xff000000|Integer.parseInt(hex,16);}
    static String hex(int color){return String.format(Locale.ROOT,"#%06X",color&0xffffff);}
    static List<Integer> recent(DesktopStore store){List<Integer> values=new ArrayList<>();try{JSONArray a=new JSONArray(store.prefs.getString(RECENT,"[]"));for(int i=0;i<Math.min(5,a.length());i++)values.add(a.getInt(i));}catch(JSONException ignored){}return values;}
    static void choose(DesktopStore store,int color){List<Integer> values=recent(store);values.remove(Integer.valueOf(color));values.add(0,color);while(values.size()>5)values.remove(values.size()-1);store.prefs.edit().putInt("ten_accent",color).putString(MODE,"Manual").putString(RECENT,new JSONArray(values).toString()).apply();}
    static boolean automatic(DesktopStore store,AeroWallpaper wallpaper){
        if(!"Automatic".equals(store.prefs.getString(MODE,"Manual"))||wallpaper==null||wallpaper.getWidth()<1)return false;
        Bitmap image=Bitmap.createBitmap(32,24,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(image);canvas.scale(32f/wallpaper.getWidth(),24f/Math.max(1,wallpaper.getHeight()));wallpaper.draw(canvas);
        double r=0,g=0,b=0,total=0;float[] hsv=new float[3];
        for(int x=0;x<32;x++)for(int y=0;y<24;y++){int color=image.getPixel(x,y);Color.colorToHSV(color,hsv);double weight=.05+hsv[1]*hsv[1];r+=Color.red(color)*weight;g+=Color.green(color)*weight;b+=Color.blue(color)*weight;total+=weight;}image.recycle();
        int color=Color.rgb((int)(r/total),(int)(g/total),(int)(b/total));Color.colorToHSV(color,hsv);hsv[1]=Math.max(.35f,hsv[1]);hsv[2]=Math.max(.45f,Math.min(.78f,hsv[2]));color=Color.HSVToColor(hsv);
        if(color==store.prefs.getInt("ten_accent",TenStyle.ACCENT))return false;store.prefs.edit().putInt("ten_accent",color).apply();return true;
    }
    static void validate(JSONObject values)throws JSONException{
        if(values.has(MODE)&&!Arrays.asList("Manual","Automatic").contains(values.get(MODE)))throw new JSONException("Invalid accent mode");
        if(values.has(RECENT)){if(!(values.get(RECENT) instanceof String))throw new JSONException("Invalid recent colors");JSONArray rows=new JSONArray(values.getString(RECENT));if(rows.length()>5)throw new JSONException("Too many recent colors");for(int i=0;i<rows.length();i++)if(!(rows.get(i) instanceof Integer))throw new JSONException("Invalid recent color");}
        if(values.has("eleven_background_mode")&&!Arrays.asList("Picture","Solid color","Slideshow").contains(values.get("eleven_background_mode")))throw new JSONException("Invalid background mode");
        if(values.has("eleven_solid_color")&&!(values.get("eleven_solid_color") instanceof Integer))throw new JSONException("Invalid solid color");
    }
    private ElevenColors(){}
}
