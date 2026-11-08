package com.nthexperience.desktop;

import android.graphics.*;

/** Original layered shell artwork in a 64-unit coordinate space. */
final class ElevenIcons {
    static boolean draw(Canvas canvas,String kind){
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);Path path=new Path();
        switch(kind){
            case "files":case "folderapps":case "file-explorer":
                folder(canvas,paint,path,kind.equals("file-explorer"));return true;
            case "documents":case "pictures":case "music":case "videos":case "downloads":
                folder(canvas,paint,path,false);badge(canvas,paint,path,kind);return true;
            case "settings":case "control":
                paint.setColor(0xff748391);canvas.drawCircle(32,32,20,paint);
                for(int n=0;n<8;n++){canvas.save();canvas.rotate(n*45,32,32);canvas.drawRoundRect(27,4,37,19,2,2,paint);canvas.restore();}
                paint.setColor(0xffc9e2ee);canvas.drawCircle(32,32,12,paint);paint.setColor(0xff188ed2);canvas.drawCircle(32,32,8,paint);return true;
            case "computer":case "system":
                paint.setShader(new LinearGradient(6,9,58,46,0xff6bd7f8,0xff1564ba,Shader.TileMode.CLAMP));canvas.drawRoundRect(4,7,60,47,3,3,paint);paint.setShader(null);paint.setColor(0xff172843);canvas.drawRoundRect(7,10,57,43,1,1,paint);paint.setShader(new LinearGradient(8,11,55,41,0xff15b4e6,0xff2466c9,Shader.TileMode.CLAMP));canvas.drawRect(9,12,55,41,paint);paint.setShader(null);paint.setColor(0xffa7bccd);canvas.drawRoundRect(27,47,37,55,1,1,paint);canvas.drawRoundRect(19,54,45,58,2,2,paint);return true;
            case "recycle":case "recycle-full":
                paint.setShader(new LinearGradient(14,10,50,58,0xfff3fbff,0xffaec3d3,Shader.TileMode.CLAMP));path.moveTo(14,14);path.lineTo(50,14);path.lineTo(46,58);path.lineTo(18,58);path.close();canvas.drawPath(path,paint);paint.setShader(null);paint.setColor(0xfff8fcff);canvas.drawOval(11,8,53,20,paint);paint.setColor(0xffa6becd);canvas.drawOval(15,11,49,16,paint);
                if(kind.equals("recycle-full")){paint.setColor(Color.WHITE);canvas.save();canvas.rotate(-15,32,12);canvas.drawRect(18,5,37,19,paint);canvas.restore();canvas.save();canvas.rotate(18,37,12);canvas.drawRect(31,3,47,20,paint);canvas.restore();}
                paint.setColor(0xff1685d3);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(3);canvas.drawArc(23,28,42,47,15,295,false,paint);path.reset();path.moveTo(40,27);path.lineTo(44,34);path.lineTo(36,34);canvas.drawPath(path,paint);paint.setStyle(Paint.Style.FILL);return true;
            case "calculator":
                paint.setShader(new LinearGradient(12,5,51,59,0xff6dbde7,0xff376cae,Shader.TileMode.CLAMP));canvas.drawRoundRect(12,5,52,59,5,5,paint);paint.setShader(null);paint.setColor(0xffd9f4ff);canvas.drawRoundRect(17,11,47,25,2,2,paint);for(int y=31;y<=49;y+=9)for(int x=18;x<=42;x+=12){paint.setColor(x==42?0xff80dbf4:0xfff2f6fc);canvas.drawRoundRect(x,y,x+7,y+5,1,1,paint);}return true;
            case "calendar":
                paint.setColor(0xffeaf4fb);canvas.drawRoundRect(8,9,56,58,5,5,paint);paint.setShader(new LinearGradient(8,9,56,25,0xff55c8f5,0xff177dcc,Shader.TileMode.CLAMP));canvas.drawRoundRect(8,9,56,26,5,5,paint);paint.setShader(null);paint.setColor(0xfff8fbff);for(int x=19;x<=45;x+=26)canvas.drawRoundRect(x,5,x+4,17,2,2,paint);paint.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));paint.setTextAlign(Paint.Align.CENTER);paint.setTextSize(26);paint.setColor(0xff255071);canvas.drawText("31",32,49,paint);return true;
            default:return false;
        }
    }
    private static void folder(Canvas canvas,Paint paint,Path path,boolean explorer){
        paint.setColor(0xffce8d16);path.moveTo(5,14);path.quadTo(5,10,9,10);path.lineTo(26,10);path.lineTo(32,16);path.lineTo(55,16);path.quadTo(59,16,59,20);path.lineTo(59,52);path.lineTo(5,52);path.close();canvas.drawPath(path,paint);
        paint.setColor(0xffffe5a0);canvas.drawRoundRect(8,19,56,31,2,2,paint);
        paint.setShader(new LinearGradient(7,25,58,53,0xffffdb64,0xfff5b63b,Shader.TileMode.CLAMP));canvas.drawRoundRect(5,25,59,55,4,4,paint);paint.setShader(null);
        paint.setColor(0xffffe894);canvas.drawRect(9,25,55,27,paint);
        if(explorer){paint.setShader(new LinearGradient(20,43,47,59,0xff4ac5fc,0xff158bdc,Shader.TileMode.CLAMP));canvas.drawRoundRect(20,43,45,59,2,2,paint);paint.setShader(null);paint.setColor(0xffd8f8ff);canvas.drawRoundRect(24,47,41,50,1,1,paint);}
    }
    private static void badge(Canvas canvas,Paint paint,Path path,String kind){
        paint.setColor(0xff2684cf);paint.setStrokeWidth(3);paint.setStyle(Paint.Style.STROKE);
        switch(kind){
            case "downloads":paint.setColor(0xff148c79);canvas.drawLine(32,31,32,48,paint);canvas.drawLine(25,42,32,49,paint);canvas.drawLine(32,49,39,42,paint);break;
            case "documents":for(int y=34;y<=46;y+=6)canvas.drawLine(24,y,42,y,paint);break;
            case "pictures":canvas.drawCircle(39,34,2,paint);path.reset();path.moveTo(21,47);path.lineTo(28,36);path.lineTo(35,43);path.lineTo(41,38);path.lineTo(46,47);canvas.drawPath(path,paint);break;
            case "music":paint.setColor(0xff9a52ad);canvas.drawLine(35,31,35,46,paint);canvas.drawLine(35,31,43,33,paint);canvas.drawOval(25,43,35,49,paint);break;
            case "videos":paint.setColor(0xffa052aa);path.reset();path.moveTo(28,32);path.lineTo(42,40);path.lineTo(28,48);path.close();canvas.drawPath(path,paint);break;
        }
        paint.setStyle(Paint.Style.FILL);
    }
    private ElevenIcons(){}
}
