package com.nthexperience.desktop;

import android.graphics.*;

/** Original resolution-independent counterparts for the recorded wallpaper collections. */
final class ElevenWallpapers {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path=new Path();
    void draw(Canvas c,float w,float h,int theme){
        int save=c.save();c.scale(w/1200f,h/900f);
        switch(theme){case 15:glow(c);break;case 16:motion(c);break;case 17:sunrise(c);break;case 18:flow(c);break;case 19:bubbles(c);break;default:spotlight(c);}
        p.setShader(null);p.setStyle(Paint.Style.FILL);c.restoreToCount(save);
    }
    private void fill(Canvas c,int top,int bottom){p.setShader(new LinearGradient(0,0,1000,900,new int[]{top,bottom},null,Shader.TileMode.CLAMP));c.drawRect(0,0,1200,900,p);}
    private void glow(Canvas c){
        fill(c,0xff080016,0xff1b0531);
        p.setShader(new RadialGradient(720,655,490,new int[]{0xffad18c7,0xff5a0779,0x000c001d},new float[]{0,.42f,1},Shader.TileMode.CLAMP));c.drawRect(0,0,1200,900,p);
        p.setShader(new RadialGradient(650,500,310,new int[]{0xff250636,0xff4c095f,0xffe082d5,0xfffbd7b0,0x001a032a},new float[]{0,.84f,.94f,.975f,1},Shader.TileMode.CLAMP));c.drawOval(285,175,1015,845,p);
        p.setShader(new LinearGradient(0,420,0,700,new int[]{0xff120021,0x00120021},null,Shader.TileMode.CLAMP));c.drawRect(200,120,1100,650,p);
    }
    private void motion(Canvas c){
        fill(c,0xff100c18,0xff05050b);int[] colors={0xffffc05d,0xffff6d79,0xffe5006c,0xffa870d3,0xffffe39c,0xffe83853};
        for(int i=0;i<12;i++){int s=c.save();c.translate(640,580);c.rotate(-105+i*27);float n=170+(i%4)*42;path.reset();path.moveTo(-30,160);path.cubicTo(-180,10,-155,-n,0,-n-80);path.cubicTo(150,-n+15,145,-10,-30,160);path.close();p.setShader(new LinearGradient(-140,0,125,-n,new int[]{0xff391127,colors[i%colors.length],0xffffe0dc,colors[(i+1)%colors.length]},new float[]{0,.42f,.73f,1},Shader.TileMode.CLAMP));c.drawPath(path,p);c.restoreToCount(s);}
    }
    private void sunrise(Canvas c){
        fill(c,0xffbcd6df,0xfff5d9c6);p.setShader(new RadialGradient(875,380,360,new int[]{0xfffff4ce,0x00f2c2ac},null,Shader.TileMode.CLAMP));c.drawRect(0,0,1200,650,p);
        p.setShader(new LinearGradient(0,430,0,900,new int[]{0xff8baeb7,0xff2e646e,0xffbedbda},null,Shader.TileMode.CLAMP));c.drawRect(0,430,1200,900,p);
        for(int side:new int[]{-1,1}){int s=c.save();if(side==1){c.translate(1200,0);c.scale(-1,1);}path.reset();path.moveTo(0,270);path.lineTo(160,365);path.lineTo(220,344);path.lineTo(455,430);path.lineTo(720,450);path.lineTo(0,510);path.close();p.setShader(new LinearGradient(0,280,600,490,new int[]{0xff4b6b6d,0xffa8b9b2},null,Shader.TileMode.CLAMP));c.drawPath(path,p);c.scale(1,-.64f,0,460);p.setAlpha(80);c.drawPath(path,p);p.setAlpha(255);c.restoreToCount(s);}
        p.setShader(new LinearGradient(0,450,0,900,new int[]{0x00e5d8c4,0x66d9edf0},null,Shader.TileMode.CLAMP));c.drawRect(0,450,1200,900,p);
    }
    private void flow(Canvas c){
        fill(c,0xffd1dbe1,0xff8e9eac);
        for(int i=26;i>=0;i--){float t=i/26f;path.reset();path.moveTo(-200,940);path.cubicTo(260,-150+500*t,390,800-900*t,660,-180);path.cubicTo(420,330,1100,150,1400,960);path.close();p.setShader(new LinearGradient(210,260,1050,780,new int[]{0xfff1f7fa,0xffc9d6dd,0xff8397a5,0xffe5edf1},new float[]{0,.38f,.64f,1},Shader.TileMode.CLAMP));int s=c.save();c.translate(i*9,i*9);c.drawPath(path,p);c.restoreToCount(s);}
    }
    private void bubbles(Canvas c){
        fill(c,0xff7e9d06,0xffeef9a6);float[][] circles={{220,110,350},{850,100,255},{730,620,350},{110,780,220},{1180,470,290},{555,340,85},{430,495,59},{489,206,46}};
        for(float[] a:circles){p.setShader(new RadialGradient(a[0]-a[2]*.3f,a[1]-a[2]*.35f,a[2]*1.35f,new int[]{0xfff8ffd1,0xffb7cf3b,0xff74911e,0xff324525},new float[]{0,.68f,.91f,1},Shader.TileMode.CLAMP));c.drawCircle(a[0],a[1],a[2],p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(4);p.setColor(0x99647724);c.drawCircle(a[0],a[1],a[2],p);p.setStyle(Paint.Style.FILL);}
    }
    private void spotlight(Canvas c){
        fill(c,0xffbad8e8,0xfff1d2b5);p.setShader(null);p.setColor(0xffbdb09d);path.reset();path.moveTo(0,560);path.lineTo(260,290);path.lineTo(405,435);path.lineTo(600,210);path.lineTo(1050,580);path.lineTo(1200,490);path.lineTo(1200,900);path.lineTo(0,900);path.close();c.drawPath(path,p);
        p.setColor(0xfff5f1e9);path.reset();path.moveTo(480,380);path.lineTo(600,210);path.lineTo(800,380);path.lineTo(646,335);path.lineTo(592,370);path.lineTo(565,320);path.close();c.drawPath(path,p);
        p.setShader(new LinearGradient(0,570,0,900,new int[]{0xff98bdc1,0xff3a6f7b},null,Shader.TileMode.CLAMP));c.drawRect(0,580,1200,900,p);p.setShader(null);p.setColor(0xff314f40);path.reset();path.moveTo(0,480);path.lineTo(400,610);path.lineTo(0,750);path.close();c.drawPath(path,p);
    }
}
