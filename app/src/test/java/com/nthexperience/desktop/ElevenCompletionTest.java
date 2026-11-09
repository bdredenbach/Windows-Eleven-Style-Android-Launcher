package com.nthexperience.desktop;
import org.junit.Test;
import org.json.*;
import java.time.*;
import java.util.*;
import static org.junit.Assert.*;

public class ElevenCompletionTest {
 @Test public void forecastUsesLocalDaysAndRejectsBadSamples()throws Exception{JSONArray times=new JSONArray();for(String[] sample:new String[][]{{"2026-11-09T00:00:00Z","8"},{"2026-11-09T03:00:00Z","3"},{"2026-11-09T08:00:00Z","12"},{"broken","5"},{"2026-11-09T09:00:00Z","500"}})times.put(new JSONObject().put("time",sample[0]).put("data",new JSONObject().put("instant",new JSONObject().put("details",new JSONObject().put("air_temperature",Double.parseDouble(sample[1]))))));List<ElevenForecast.Day> days=ElevenForecast.days(new JSONObject().put("properties",new JSONObject().put("timeseries",times)).toString(),ZoneId.of("America/New_York"));assertEquals(2,days.size());assertEquals(LocalDate.of(2026,11,8),days.get(0).date);assertEquals(3,days.get(0).low,0);assertEquals(8,days.get(0).high,0);assertEquals(2,days.get(0).samples);assertEquals(12,days.get(1).high,0);}
 @Test public void portableFiltersRejectDuplicateMalformedAndOverLimitEntries()throws Exception{ElevenLocalState.validate(new JSONObject().put(ElevenLocalState.MUTED,"[\"com.example.app\"]").put(ElevenLocalState.EXCLUDED,"[\"content://example/tree/root\"]"));for(String bad:new String[]{"[\"com.example.app\",\"com.example.app\"]","[\"not a package\"]","[3]"}){boolean rejected=false;try{ElevenLocalState.validate(new JSONObject().put(ElevenLocalState.MUTED,bad));}catch(JSONException expected){rejected=true;}assertTrue(bad,rejected);}boolean rejected=false;try{ElevenLocalState.validate(new JSONObject().put(ElevenLocalState.EXCLUDED,"[\"file:///sdcard\"]"));}catch(JSONException expected){rejected=true;}assertTrue(rejected);}
}
