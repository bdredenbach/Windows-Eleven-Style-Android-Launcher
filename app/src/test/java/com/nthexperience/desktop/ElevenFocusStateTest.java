package com.nthexperience.desktop;
import org.junit.Test;
import static org.junit.Assert.*;

public class ElevenFocusStateTest {
    @Test public void expiryAndRebootRestoreNormalFeedWithoutChangingManualQuietState(){
        long start=2000,end=start+30*60000L;
        assertEquals(30*60000L,ElevenFocusState.remaining(start,end,7,start,7));
        assertEquals(1,ElevenFocusState.remaining(start,end,7,end-1,7));
        assertEquals(0,ElevenFocusState.remaining(start,end,7,end,7));
        assertEquals(0,ElevenFocusState.remaining(start,end,7,start,8));
        assertEquals(0,ElevenFocusState.remaining(start,end,7,start-1,7));
    }
    @Test public void badDurationsAndUnknownBootCannotCreateIndefiniteSessions(){
        assertEquals(5,ElevenFocusState.minutes(-10));assertEquals(240,ElevenFocusState.minutes(300));
        assertEquals(0,ElevenFocusState.remaining(0,240*60000L+1,1,20,1));
        assertEquals(0,ElevenFocusState.remaining(100,90,1,110,1));
        assertEquals(0,ElevenFocusState.remaining(0,1000,-1,20,-1));
    }
}
