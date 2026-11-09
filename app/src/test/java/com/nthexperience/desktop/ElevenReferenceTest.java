package com.nthexperience.desktop;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.json.JSONException;
public class ElevenReferenceTest {
 @Test public void snapRectanglesFitAndPartition(){
  for(int width=201;width<1700;width+=37)for(int height=207;height<1100;height+=41){
   for(AeroSnap.Placement p:AeroSnap.Placement.values()){int[] b=AeroSnap.bounds(p,width,height);assertTrue(b[0]>=0&&b[1]>=0&&b[2]>0&&b[3]>0&&b[0]+b[2]<=width&&b[1]+b[3]<=height);assertEquals(!AeroSnap.side(p)||(b[2]>=160&&b[3]>=190),AeroSnap.canPlace(p,width,height,160,190));}
   for(AeroSnap.Placement[] row:new AeroSnap.Placement[][]{{AeroSnap.Placement.LEFT,AeroSnap.Placement.RIGHT},{AeroSnap.Placement.TWO_THIRDS_LEFT,AeroSnap.Placement.THIRD_RIGHT},{AeroSnap.Placement.THIRD_LEFT,AeroSnap.Placement.THIRD_MIDDLE,AeroSnap.Placement.THIRD_RIGHT},{AeroSnap.Placement.TOP_LEFT,AeroSnap.Placement.TOP_RIGHT,AeroSnap.Placement.BOTTOM_LEFT,AeroSnap.Placement.BOTTOM_RIGHT}}){int area=0;for(AeroSnap.Placement p:row){int[] b=AeroSnap.bounds(p,width,height);area+=b[2]*b[3];}assertEquals(width*height,area);}
  }
 }
 @Test public void lettersUseUnicodeAndNumbersGroup(){assertEquals("C",ElevenLayout.initial("calculator"));assertEquals("É",ElevenLayout.initial("écriture"));assertEquals("#",ElevenLayout.initial("7-Zip"));assertEquals("#",ElevenLayout.initial(""));}
 @Test public void taskbarReservesActualSearchWidth(){assertEquals(168,ElevenLayout.taskbarFixed(411,"Auto"));assertEquals(168,ElevenLayout.taskbarFixed(700,"Auto"));assertEquals(288,ElevenLayout.taskbarFixed(1100,"Auto"));assertEquals(168,ElevenLayout.taskbarFixed(1100,"Icon"));assertEquals(128,ElevenLayout.taskbarFixed(1100,"Hidden"));}
 @Test public void referenceChoicesValidate()throws Exception{Map<String,Object> p=new HashMap<>();p.put("eleven_taskbar_alignment","Left");p.put("eleven_start_view","Grid");p.put("eleven_start_size","Large");TenPreferences.validate(p);p.put("eleven_taskbar_alignment","Bottom");try{TenPreferences.validate(p);fail("Bad alignment accepted");}catch(JSONException expected){}}
}
