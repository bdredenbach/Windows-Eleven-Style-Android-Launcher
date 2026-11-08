package com.nthexperience.desktop;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.widget.FrameLayout;
import java.util.*;

/** Drag-only overlay: never consumes an unrelated desktop tap or keyboard focus. */
final class ElevenSnapBar extends View {
    private final Paint paint=new Paint(3);
    private final java.util.List<Zone> zones=new ArrayList<>();
    private final RectF plate=new RectF();
    private final int width,top,height,minWidth,minHeight;
    private final AeroSnap.Placement[][] groups=ElevenSnapLayouts.groups();
    private Bitmap thumbnail;
    private float pointerX,pointerY;
    private AeroSnap.Placement target=AeroSnap.Placement.FLOATING;
    static final class Zone {final RectF rect;final AeroSnap.Placement placement;final boolean enabled;Zone(RectF r,AeroSnap.Placement p,boolean e){rect=r;placement=p;enabled=e;}}
    ElevenSnapBar(Context c,DesktopWindow window,int width,int top,int height,int minWidth,int minHeight){
        super(c);this.width=width;this.top=top;this.height=height;this.minWidth=minWidth;this.minHeight=minHeight;
        setContentDescription("Drag Snap layouts");setClickable(false);setFocusable(false);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        setElevation(dp(80));layoutZones();
        if(WindowPreviews.allowed(window)&&window.getWidth()>0&&window.getHeight()>0)try{
            float scale=Math.min(160f/window.getWidth(),100f/window.getHeight());
            thumbnail=Bitmap.createBitmap(Math.max(1,(int)(window.getWidth()*scale)),Math.max(1,(int)(window.getHeight()*scale)),Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(thumbnail);canvas.scale(scale,scale);window.drawPreview(canvas);
        }catch(RuntimeException|OutOfMemoryError ignored){if(thumbnail!=null)thumbnail.recycle();thumbnail=null;}
    }
    private int dp(int value){return Aero.dp(getContext(),value);}
    private void layoutZones(){
        java.util.List<AeroSnap.Placement[]> available=new ArrayList<>();
        for(AeroSnap.Placement[] group:groups){boolean fits=true;for(AeroSnap.Placement p:group)fits&=AeroSnap.canPlace(p,width,height-top,minWidth,minHeight);if(fits)available.add(group);}
        if(available.isEmpty())return;
        int columns=Math.min(available.size(),Math.max(1,(width-dp(20))/dp(108))),rows=(available.size()+columns-1)/columns;
        float gap=dp(8),cell=Math.min(dp(108),(width-dp(32)-(columns-1)*gap)/columns),cellH=dp(58);
        float left=(width-(columns*cell+(columns-1)*gap))/2f,barTop=top+dp(12);
        plate.set(left-dp(8),barTop-dp(8),left+columns*cell+(columns-1)*gap+dp(8),barTop+rows*cellH+(rows-1)*gap+dp(8));
        for(int i=0;i<available.size();i++){
            float x=left+(i%columns)*(cell+gap),y=barTop+(i/columns)*(cellH+gap);
            for(AeroSnap.Placement p:available.get(i)){int[] b=AeroSnap.bounds(p,1000,1000);RectF r=new RectF(x+cell*b[0]/1000f+dp(1),y+cellH*b[1]/1000f+dp(1),x+cell*(b[0]+b[2])/1000f-dp(1),y+cellH*(b[1]+b[3])/1000f-dp(1));zones.add(new Zone(r,p,true));}
        }
    }
    boolean tooFar(float y){return y>plate.bottom+dp(70);}
    AeroSnap.Placement point(float x,float y){pointerX=x;pointerY=y;target=AeroSnap.Placement.FLOATING;for(Zone z:zones)if(z.rect.contains(x,y)&&z.enabled){target=z.placement;break;}invalidate();return target;}
    RectF zone(AeroSnap.Placement placement){for(Zone z:zones)if(z.placement==placement)return new RectF(z.rect);return null;}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);if(plate.isEmpty())return;
        paint.setStyle(Paint.Style.FILL);paint.setColor(TenStyle.light(getContext())?0xf0edf1f8:0xf0272e40);c.drawRoundRect(plate,dp(8),dp(8),paint);
        for(Zone z:zones){paint.setColor(z.rect.contains(pointerX,pointerY)?TenAppearance.accent(getContext()):TenStyle.light(getContext())?0xffbac6d8:0xff596378);c.drawRoundRect(z.rect,dp(3),dp(3),paint);}
        if(target!=AeroSnap.Placement.FLOATING){float w=dp(100),h=thumbnail==null?dp(56):w*thumbnail.getHeight()/Math.max(1,thumbnail.getWidth());float x=Math.max(0,Math.min(width-w,pointerX-w/2)),y=Math.min(getHeight()-h,Math.max(plate.bottom+dp(6),pointerY+dp(16)));RectF r=new RectF(x,y,x+w,y+h);paint.setColor(0xff232c40);c.drawRoundRect(r,dp(4),dp(4),paint);if(thumbnail!=null)c.drawBitmap(thumbnail,null,r,paint);else{paint.setColor(Color.WHITE);paint.setTextSize(dp(12));c.drawText("Window",x+dp(12),y+h/2,paint);}}
    }
    void release(){if(getParent() instanceof FrameLayout)((FrameLayout)getParent()).removeView(this);if(thumbnail!=null){thumbnail.recycle();thumbnail=null;}}
}
