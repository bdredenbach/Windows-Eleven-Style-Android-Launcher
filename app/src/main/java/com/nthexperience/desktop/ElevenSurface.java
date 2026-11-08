package com.nthexperience.desktop;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
final class ElevenSurface {
    static GradientDrawable round(Context c,int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(Aero.dp(c,radius));d.setStroke(Aero.dp(c,1),TenStyle.light(c)?0x1a000000:0x20ffffff);return d;}
    private ElevenSurface(){}
}
