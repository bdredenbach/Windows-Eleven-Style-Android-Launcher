package com.nthexperience.desktop;

import org.json.*;
import java.util.*;

/** Bounded view choices and measured-counter arithmetic; no fabricated samples. */
final class TaskManagerState {
    static final List<String> PAGES=Arrays.asList("Processes","Performance","App history","Startup apps","Users","Details","Services","Settings");
    static final List<String> RESOURCES=Arrays.asList("CPU","Memory","Storage","Network","Java heap");
    String page="Processes",resource="CPU",query="",selected="",sort="Name";boolean descending,paused;int interval=2000,navigation=-1;
    static float rate(long current,long previous,long elapsed){return elapsed<=0||previous<0||current<previous?Float.NaN:(current-previous)*1000f/elapsed;}
    static float cpu(long current,long previous,long elapsed,int cores){return elapsed<=0||previous<0||current<previous||cores<1?Float.NaN:Math.min(100,Math.max(0,100f*(current-previous)/elapsed/cores));}
    static int compareMeasured(double a,double b,boolean descending){if(!Double.isFinite(a))return Double.isFinite(b)?1:0;if(!Double.isFinite(b))return -1;return descending?Double.compare(b,a):Double.compare(a,b);}
    static String page(String value){return PAGES.contains(value)?value:"Processes";}
    JSONObject json(){JSONObject o=new JSONObject();GadgetLayout.put(o,"version",2);GadgetLayout.put(o,"page",page);GadgetLayout.put(o,"resource",resource);GadgetLayout.put(o,"query",query);GadgetLayout.put(o,"selected",selected);GadgetLayout.put(o,"sort",sort);GadgetLayout.put(o,"descending",descending);GadgetLayout.put(o,"paused",paused);GadgetLayout.put(o,"interval",interval);GadgetLayout.put(o,"navigation",navigation);return o;}
    void restore(JSONObject o){if(o==null)return;String old=o.optString("tab","Applications");page=page(o.has("page")?o.optString("page"):old.equals("Applications")?"Processes":old.equals("Processes")?"Details":old.equals("Networking")?"Performance":old);resource=RESOURCES.contains(o.optString("resource"))?o.optString("resource"):old.equals("Networking")?"Network":"CPU";query=clip(o.optString("query"),256);selected=clip(o.optString("selected"),2048);sort=Arrays.asList("Name","Status","PID","Memory","CPU").contains(o.optString("sort"))?o.optString("sort"):"Name";descending=o.optBoolean("descending");paused=o.optBoolean("paused");interval=speed(o.optInt("interval",2000));navigation=Math.max(-1,Math.min(1,o.optInt("navigation",-1)));}
    static int speed(int value){return value==1000||value==5000?value:2000;}
    private static String clip(String text,int size){return text.substring(0,Math.min(size,text.length()));}
    static void validate(Map<String,Object> values)throws JSONException{if(values.containsKey("taskmanager_default_page")&&!PAGES.contains(values.get("taskmanager_default_page")))throw new JSONException("Invalid Task Manager page");if(values.containsKey("taskmanager_theme")&&!Arrays.asList("System","Light","Dark").contains(values.get("taskmanager_theme")))throw new JSONException("Invalid Task Manager theme");if(values.containsKey("taskmanager_speed")){Object speed=values.get("taskmanager_speed");if(!(speed instanceof Integer)||!Arrays.asList(1000,2000,5000).contains(speed))throw new JSONException("Invalid Task Manager speed");}}
}
