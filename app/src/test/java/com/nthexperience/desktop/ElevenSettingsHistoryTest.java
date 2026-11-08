package com.nthexperience.desktop;

import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class ElevenSettingsHistoryTest {
    @Test public void nestedVisitsRetainSearchAndScrollThroughRecreation() {
        ElevenSettingsHistory h = new ElevenSettingsHistory();
        h.remember("home", "", 87, 14); h.visit("system");
        h.remember("system", "display", 226, 20); h.visit("display");
        h.remember("display", "", 410, 20); h.visit("colors");
        ElevenSettingsHistory restored = new ElevenSettingsHistory(); restored.restore(h.json());
        assertEquals("display", restored.back().page); assertEquals(410, restored.current.scroll);
        assertEquals("system", restored.back().page); assertEquals("display", restored.current.query);
        assertEquals(226, restored.current.scroll); assertEquals(20, restored.current.sideScroll);
        assertEquals("display", restored.forward().page);
        restored.visit("sound"); assertNull(restored.forward());
        restored.back(); restored.back(); assertEquals("home", restored.back().page); assertNull(restored.back());
    }
    @Test public void boundedHistoryAndUntrustedStateCannotCreateUnknownPages() throws Exception {
        ElevenSettingsHistory h = new ElevenSettingsHistory();
        for (int i=0; i<100; i++) h.visit(i%2==0 ? "system" : "sound");
        assertEquals(ElevenSettingsHistory.LIMIT, h.back.size());
        h.restore(new JSONObject("{\"current\":{\"page\":\"https://untrusted\",\"scroll\":-3,\"side\":2000000},\"back\":[{\"page\":\"unknown\"},{\"page\":\"home\"}]}"));
        assertEquals("home",h.current.page); assertEquals(0,h.current.scroll); assertEquals(1000000,h.current.sideScroll);
        assertEquals(1,h.back.size()); h.visit("home"); assertEquals(1,h.back.size());
    }
}
