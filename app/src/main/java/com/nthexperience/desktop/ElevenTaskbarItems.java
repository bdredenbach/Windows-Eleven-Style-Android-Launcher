package com.nthexperience.desktop;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/** Running app buttons use the upper row; idle pinned shortcuts use the lower row. */
final class ElevenTaskbarItems extends LinearLayout {
    boolean expanded;
    private int bottomOffset;
    private static final class Slot extends LinearLayout.LayoutParams {
        final boolean running;final int preferredWidth;
        Slot(LinearLayout.LayoutParams source,boolean running){super(source);this.running=running;preferredWidth=source.width;}
    }
    ElevenTaskbarItems(Context context,boolean expanded){super(context);this.expanded=expanded;}
    static void slot(View child,boolean running){child.setLayoutParams(new Slot((LinearLayout.LayoutParams)child.getLayoutParams(),running));}
    static boolean running(View child){return child.getLayoutParams() instanceof Slot&&((Slot)child.getLayoutParams()).running;}
    void expanded(boolean value){if(expanded!=value){expanded=value;requestLayout();}}
    int bottomOffset(){return bottomOffset;}
    void bottomOffset(int value){if(bottomOffset!=value){bottomOffset=value;requestLayout();}}
    int singleRowWidth(){int total=0;for(int i=0;i<getChildCount();i++){View child=getChildAt(i);if(child.getVisibility()==GONE)continue;LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)child.getLayoutParams();total+=(p instanceof Slot?((Slot)p).preferredWidth:p.width)+p.rightMargin;}return total;}
    @Override protected void onMeasure(int width,int height){
        if(!expanded){super.onMeasure(width,height);return;}
        int[] widths={0,bottomOffset};
        for(int i=0;i<getChildCount();i++){
            View child=getChildAt(i);if(child.getVisibility()==GONE)continue;LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)child.getLayoutParams();
            child.measure(MeasureSpec.makeMeasureSpec(Math.max(0,p.width),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(Aero.dp(getContext(),48),MeasureSpec.EXACTLY));
            widths[running(child)?0:1]+=child.getMeasuredWidth()+p.rightMargin;
        }
        setMeasuredDimension(Math.max(Math.max(widths[0],widths[1]),MeasureSpec.getMode(width)==MeasureSpec.EXACTLY?MeasureSpec.getSize(width):getSuggestedMinimumWidth()),resolveSize(Aero.dp(getContext(),96),height));
    }
    @Override protected void onLayout(boolean changed,int left,int top,int right,int bottom){
        if(!expanded){super.onLayout(changed,left,top,right,bottom);return;}
        int[] x={0,bottomOffset};int rowHeight=Aero.dp(getContext(),48);
        for(int i=0;i<getChildCount();i++){
            View child=getChildAt(i);if(child.getVisibility()==GONE)continue;LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)child.getLayoutParams();int row=running(child)?0:1,y=row*rowHeight;
            child.layout(x[row],y,x[row]+child.getMeasuredWidth(),y+child.getMeasuredHeight());x[row]+=child.getMeasuredWidth()+p.rightMargin;
        }
    }
    @Override protected boolean drawChild(android.graphics.Canvas canvas,View child,long time){
        if(!expanded||running(child))return super.drawChild(canvas,child,time);
        // Bottom pins scroll behind the fixed controls without painting through their transparent faces.
        int save=canvas.save(),scroll=getParent() instanceof View?((View)getParent()).getScrollX():0;
        canvas.clipRect(bottomOffset+scroll,0,getWidth(),getHeight());boolean drawn=super.drawChild(canvas,child,time);canvas.restoreToCount(save);return drawn;
    }
}
