package com.nthexperience.desktop;
/** Shared density-independent bounds policy, kept separate from Android views. */
final class ElevenLayout {
    static int pinColumns(int availableDp){return Math.max(3,Math.min(6,availableDp/76));}
    static int taskbarWidth(int available,int desired){return Math.max(1,Math.min(available,desired));}
    static int taskbarLeft(int width,int leftReserved,int rightReserved,int desired){int w=taskbarWidth(Math.max(1,width-leftReserved-rightReserved),desired);return Math.max(leftReserved,Math.min((width-w)/2,width-rightReserved-w));}
    private ElevenLayout(){}
}
