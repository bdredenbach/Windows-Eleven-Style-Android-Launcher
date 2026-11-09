package com.nthexperience.desktop;
import android.content.Context;import android.graphics.*;import android.view.View;

/** Small original vector strokes for command/navigation symbols, following the active app palette. */
final class ElevenSymbols extends View {
    private final String name;private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    ElevenSymbols(Context c,String name){super(c);this.name=name;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    @Override protected void onDraw(Canvas original){Canvas c=original;int save=c.save();c.translate((getWidth()-Math.min(getWidth(),getHeight()))/2f,(getHeight()-Math.min(getWidth(),getHeight()))/2f);float scale=Math.min(getWidth(),getHeight())/24f;c.scale(scale,scale);p.setColor(TenAppearance.ink(getContext()));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.5f);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);
        switch(name){
            case "Processes":for(int x:new int[]{3,13})for(int y:new int[]{3,13})c.drawRoundRect(x,y,x+7,y+7,1,1,p);break;
            case "Performance":case "taskmanager":c.drawRect(3,3,21,21,p);Path graph=new Path();graph.moveTo(4,16);graph.lineTo(8,16);graph.lineTo(11,8);graph.lineTo(14,18);graph.lineTo(17,12);graph.lineTo(21,12);c.drawPath(graph,p);break;
            case "App history":c.drawArc(4,4,20,20,35,290,false,p);c.drawLine(12,7,12,12,p);c.drawLine(12,12,16,14,p);c.drawLine(3,4,3,9,p);c.drawLine(3,9,8,9,p);break;
            case "Startup apps":c.drawArc(4,6,20,22,-60,300,false,p);c.drawLine(12,2,12,12,p);break;
            case "Users":c.drawCircle(10,7,3,p);c.drawArc(3,12,17,25,180,180,false,p);c.drawArc(16,4,22,10,-90,180,false,p);c.drawArc(15,12,23,23,220,130,false,p);break;
            case "Details":c.drawRoundRect(3,3,21,21,1,1,p);for(int y:new int[]{7,12,17}){c.drawPoint(6,y,p);c.drawLine(10,y,18,y,p);}break;
            case "Services":case "Settings":for(int i=0;i<8;i++){double a=i*Math.PI/4;c.drawLine(12+8*(float)Math.cos(a),12+8*(float)Math.sin(a),12+10*(float)Math.cos(a),12+10*(float)Math.sin(a),p);}c.drawCircle(12,12,7,p);c.drawCircle(12,12,3,p);break;
            case "star":Path star=new Path();for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float r=i%2==0?10:4.2f,x=12+r*(float)Math.cos(a),y=12+r*(float)Math.sin(a);if(i==0)star.moveTo(x,y);else star.lineTo(x,y);}star.close();c.drawPath(star,p);break;
            case "share":c.drawLine(6,14,6,21,p);c.drawLine(6,21,20,21,p);c.drawLine(20,21,20,14,p);c.drawLine(13,16,13,3,p);c.drawLine(13,3,8,8,p);c.drawLine(13,3,18,8,p);break;
            default:for(int y:new int[]{6,12,18})c.drawLine(3,y,21,y,p);
        }c.restoreToCount(save);
    }
}
