package com.nthexperience.desktop;
import org.json.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class NotaDraftStateTest {
    @Test public void fullLengthOrdinaryStateRetainsOriginAndReply()throws Exception{
        String text="x".repeat(15000);JSONObject s=NotaDraftStore.state(text,4,true,"Result",false,"Selected passage",true,23,14999);
        NotaDraftStore.validateState(new JSONObject(s.toString()));assertEquals(text,s.getString("draft"));assertEquals(4,s.getInt("task"));assertTrue(s.getBoolean("imported"));assertEquals("Result",s.getString("reply"));
    }
    @Test public void privatePlaceholderContainsNoPassageOrReply()throws Exception{
        JSONObject p=NotaDraftStore.privatePlaceholder();NotaDraftStore.validateState(p);assertEquals(2,p.length());assertFalse(p.has("draft"));assertFalse(p.has("reply"));
        p.put("draft","Private");reject(p);
    }
    @Test public void invalidModesAndTypesCannotRestoreAuthority()throws Exception{
        for(Object value:new Object[]{-1,6,1.5,"5"}){JSONObject s=NotaDraftStore.empty();s.put("task",value);reject(s);}
        JSONObject s=NotaDraftStore.empty();s.put("imported","false");reject(s);s=NotaDraftStore.empty();s.put("version",1.5);reject(s);
    }
    @Test public void sourceMetadataRequiresWritingOrigin()throws Exception{
        JSONObject s=NotaDraftStore.empty();s.put("source","From browser");reject(s);s.put("imported",true);NotaDraftStore.validateState(s);
    }
    @Test public void oversizedDraftAndOutOfBoundsSelectionAreRejected()throws Exception{
        JSONObject s=NotaDraftStore.empty();s.put("draft","x".repeat(15001));reject(s);s=NotaDraftStore.empty();s.put("end",1);reject(s);
    }
    @Test public void mapRejectsUnknownOwnersAndPrivatePayload()throws Exception{
        JSONObject desktops=new JSONObject("{\"desktops\":[{\"id\":\"desktop-1\"}]}");
        JSONObject entry=new JSONObject().put("private",false).put("ordinary",NotaDraftStore.empty());JSONObject map=new JSONObject().put("desktop-1",entry);NotaDraftStore.validate(map.toString(),desktops);
        map.put("wrong",entry);try{NotaDraftStore.validate(map.toString(),desktops);fail();}catch(JSONException expected){}
        map.remove("wrong");entry.put("privateText","secret");try{NotaDraftStore.validate(map.toString(),desktops);fail();}catch(JSONException expected){}
    }
    private void reject(JSONObject s)throws Exception{try{NotaDraftStore.validateState(s);fail("Unsafe state accepted");}catch(JSONException expected){}}
}
