package com.nthexperience.desktop;

/** Monotonic duration policy: wall-clock changes and phone reboots cannot extend a session. */
final class ElevenFocusState {
    static final int MIN_MINUTES=5, MAX_MINUTES=240;
    static int minutes(int value){return Math.max(MIN_MINUTES,Math.min(MAX_MINUTES,value));}
    static long remaining(long start,long end,int boot,long now,int currentBoot){
        if(boot<0||boot!=currentBoot||start<0||now<start||end<=start||end-start>MAX_MINUTES*60_000L)return 0;
        return Math.max(0,end-now);
    }
    private ElevenFocusState(){}
}
