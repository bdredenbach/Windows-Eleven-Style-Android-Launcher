package com.nthexperience.desktop;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.View;

/** Wallpaper-only Mica/Acrylic approximation, with independent app and shell palettes. */
final class ElevenMaterial extends Drawable {
    private final Context context;
    private final boolean shell, menu;
    private final float radius;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect=new RectF();
    private final Path clip=new Path();
    private int alpha=255,appearanceKey;
    ElevenMaterial(Context c,boolean shell,boolean menu,int radius){context=c;this.shell=shell;this.menu=menu;this.radius=Aero.dp(c,radius);if(c instanceof MainActivity){AeroWallpaper wallpaper=((MainActivity)c).wallpaperSurface();if(wallpaper!=null)wallpaper.register(this);}}
    static ElevenMaterial app(Context c,int radius){return new ElevenMaterial(c,false,false,radius);}
    static ElevenMaterial menu(Context c){return new ElevenMaterial(c,false,true,7);}
    static ElevenMaterial shell(Context c,int radius){return new ElevenMaterial(c,true,true,radius);}
    void appearance(){int key=31*fallback()+(effects()?1:0);if(appearanceKey!=key){appearanceKey=key;invalidateSelf();}}
    boolean effects(){return context.getSharedPreferences("desktop",0).getBoolean("ten_transparency",false);}
    boolean dark(){return shell?!TenStyle.light(context):TenAppearance.dark(context);}
    int fallback(){return shell?TenStyle.surface(context):dark()?(menu?0xff292929:0xff202020):(menu?0xfff9f9f9:0xfff3f3f3);}
    int[] textSurfaces(){int base=fallback();return effects()?new int[]{base,TenStyle.mix(base,Color.BLACK,.10f),TenStyle.mix(base,Color.WHITE,.10f)}:new int[]{base};}
    @Override public void draw(Canvas canvas){
        rect.set(getBounds());if(rect.isEmpty())return;
        int saved=canvas.save();clip.reset();clip.addRoundRect(rect,radius,radius,Path.Direction.CW);canvas.clipPath(clip);
        paint.setStyle(Paint.Style.FILL);paint.setColor(fallback());paint.setAlpha(alpha);canvas.drawRect(rect,paint);
        if(effects()&&getCallback() instanceof View&&context instanceof MainActivity){
            AeroWallpaper wallpaper=((MainActivity)context).wallpaperSurface();
            if(wallpaper!=null){
                // Sample only the user's wallpaper. Notes, app content and private windows never enter this buffer.
                int layer=canvas.saveLayerAlpha(rect,alpha);wallpaper.drawGlass(canvas,(View)getCallback());
                paint.setColor(fallback());paint.setAlpha(menu?230:239);canvas.drawRect(rect,paint);canvas.restoreToCount(layer);
            }
        }
        if(menu){paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(Aero.dp(context,1));paint.setColor(dark()?0x24ffffff:0x20000000);paint.setAlpha(Math.round(Color.alpha(paint.getColor())*alpha/255f));canvas.drawRoundRect(rect.left+.5f,rect.top+.5f,rect.right-.5f,rect.bottom-.5f,radius,radius,paint);}
        paint.setStyle(Paint.Style.FILL);paint.setAlpha(255);canvas.restoreToCount(saved);
    }
    @Override public void setAlpha(int value){alpha=Math.max(0,Math.min(255,value));invalidateSelf();}
    @Override public void setColorFilter(ColorFilter value){paint.setColorFilter(value);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
