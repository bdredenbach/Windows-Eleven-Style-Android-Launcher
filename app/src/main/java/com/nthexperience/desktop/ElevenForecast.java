package com.nthexperience.desktop;

import org.json.*;
import java.time.*;
import java.util.*;

/** Ranges of the actual forecast samples, grouped in the device's local time zone. */
final class ElevenForecast {
    static final class Day {final LocalDate date;double low=Double.POSITIVE_INFINITY,high=Double.NEGATIVE_INFINITY;int samples;Day(LocalDate d){date=d;}void add(double value){low=Math.min(low,value);high=Math.max(high,value);samples++;}}
    static List<Day> days(String json,ZoneId zone)throws JSONException {
        JSONArray points=new JSONObject(json).getJSONObject("properties").getJSONArray("timeseries");Map<LocalDate,Day> days=new TreeMap<>();
        for(int i=0;i<Math.min(400,points.length());i++)try{JSONObject point=points.getJSONObject(i);LocalDate date=Instant.parse(point.getString("time")).atZone(zone).toLocalDate();double temp=point.getJSONObject("data").getJSONObject("instant").getJSONObject("details").getDouble("air_temperature");if(!Double.isFinite(temp)||temp< -120||temp>80)continue;Day day=days.get(date);if(day==null){day=new Day(date);days.put(date,day);}day.add(temp);}catch(JSONException|RuntimeException ignored){}
        return new ArrayList<>(days.values()).subList(0,Math.min(4,days.size()));
    }
    private ElevenForecast(){}
}
