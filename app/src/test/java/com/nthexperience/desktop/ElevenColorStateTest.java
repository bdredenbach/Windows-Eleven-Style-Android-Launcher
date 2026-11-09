package com.nthexperience.desktop;
import org.junit.Test;
import org.json.*;
import static org.junit.Assert.*;

public class ElevenColorStateTest {
    @Test public void customColorsAreOpaqueAndRoundTrip(){for(String value:new String[]{"000000","#FFFFFF","0078d4"," aB13eF "}){int color=ElevenColors.parse(value);assertEquals(255,color>>>24);assertEquals(color,ElevenColors.parse(ElevenColors.hex(color)));}}
    @Test public void invalidColorsDoNotBecomeTransparent(){for(String value:new String[]{"","#","123","12345678","#12345G","red","-00001"})try{ElevenColors.parse(value);fail(value);}catch(IllegalArgumentException expected){}}
    @Test public void portableAppearanceRejectsInvalidTypes()throws Exception{ElevenColors.validate(new JSONObject().put("eleven_background_mode","Solid color").put("eleven_solid_color",0xff123456).put(ElevenColors.MODE,"Automatic").put(ElevenColors.RECENT,"[-1,-16777216]"));for(JSONObject invalid:new JSONObject[]{new JSONObject().put(ElevenColors.MODE,"auto"),new JSONObject().put(ElevenColors.RECENT,"[1,2,3,4,5,6]"),new JSONObject().put("eleven_solid_color","red"),new JSONObject().put("eleven_background_mode","video")})try{ElevenColors.validate(invalid);fail(invalid.toString());}catch(JSONException expected){}}
}
