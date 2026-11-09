package com.nthexperience.desktop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.view.*;
import android.widget.*;

/** Viewport-bounded Eleven menus with pointer, touch and keyboard cascading. */
final class DesktopContextMenu {
    private final Context context;
    private final LinearLayout items;
    private final PopupWindow popup;
    private final java.util.List<View> rows=new java.util.ArrayList<>();
    private View owner;
    private static DesktopContextMenu openingParent;private static View openingRow;
    private DesktopContextMenu parent,child;private View parentRow;private boolean branchClosing;
    private final android.os.Handler handler=new android.os.Handler(android.os.Looper.getMainLooper());
    static boolean openingSubmenu(){return openingParent!=null;}
    private final int rowHeight,menuWidth;
    private final boolean plain;private boolean hasSubmenus;
    private final View.OnLayoutChangeListener bounds=(v,l,t,r,b,ol,ot,or,ob)->{if(r-l!=or-ol||b-t!=ob-ot)dismiss();};
    private final View.OnAttachStateChangeListener lifetime=new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View v){}public void onViewDetachedFromWindow(View v){dismiss();}};
    DesktopContextMenu(Context context){this(context,"Desktop context menu");}
    DesktopContextMenu(Context context,String description){this(context,description,36,238);}
    DesktopContextMenu(Context context,String description,int rowHeight,int menuWidth){
        this.context=context;this.plain=description.equals("Start button context menu");this.rowHeight=rowHeight;this.menuWidth=menuWidth;items=Aero.column(context);items.setContentDescription(description);
        ScrollView scroll=new ScrollView(context);scroll.setFillViewport(false);scroll.setBackground(plain?ElevenMaterial.shell(context,0):ElevenMaterial.menu(context));scroll.setPadding(dp(4),dp(4),dp(4),dp(4));scroll.addView(items);scroll.setFocusableInTouchMode(true);scroll.setOnKeyListener(this::menuKey);
        if(plain){scroll.setBackground(ElevenMaterial.shell(context,0));TenAppearance.preserve(scroll);}else TenAppearance.bind(scroll);popup=new PopupWindow(scroll,dp(menuWidth),-2,true);popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));popup.setOutsideTouchable(true);popup.setElevation(dp(8));popup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);popup.setOnDismissListener(()->{handler.removeCallbacksAndMessages(null);if(child!=null){child.closeBranch();child=null;}if(owner!=null){owner.removeOnAttachStateChangeListener(lifetime);owner.removeOnLayoutChangeListener(bounds);owner=null;}if(parent!=null&&parent.child==this)parent.child=null;if(!branchClosing&&parent!=null)parent.dismiss();});
    }
    void item(String title,String icon,Runnable action){item(title,icon,true,action);}
    void item(String title,String icon,boolean enabled,Runnable action){
        if(plain){TextView text=Aero.text(context,title,12,TenStyle.text(context));text.setPadding(dp(26),dp(4),dp(18),dp(4));text.setMaxLines(2);text.setEllipsize(android.text.TextUtils.TruncateAt.END);text.setMinHeight(dp(rowHeight));text.setContentDescription(title);text.setFocusable(true);text.setBackground(TenStyle.hover(context,Color.TRANSPARENT));text.setEnabled(enabled);keyboardBack(text);text.setOnClickListener(v->{if(enabled){dismiss();action.run();}});items.addView(text,new LinearLayout.LayoutParams(-1,dp(rowHeight)));rows.add(text);return;}
        LinearLayout row=new LinearLayout(context);row.setGravity(Gravity.CENTER_VERTICAL);row.setFocusable(true);row.setContentDescription(title);
        row.setBackground(TenStyle.appHover(context,Color.TRANSPARENT));
        FrameLayout gutter=new FrameLayout(context);gutter.setBackgroundColor(Color.TRANSPARENT);View image;
        if(icon.equals("empty")){image=new View(context);}else if(icon.equals("checked")){TextView mark=Aero.text(context,"✓",18,0xff235f91);mark.setGravity(Gravity.CENTER);image=mark;}else if(icon.equals("refresh")){TextView glyph=Aero.text(context,"↻",20,0xff4b86ab);glyph.setGravity(Gravity.CENTER);image=glyph;}else if(icon.equals("star")||icon.equals("share"))image=new ElevenSymbols(context,icon);else image=new AeroIcon(context,icon);
        gutter.addView(image,new FrameLayout.LayoutParams(dp(20),dp(20),Gravity.CENTER));row.addView(gutter,new LinearLayout.LayoutParams(dp(31),-1));
        TextView label=Aero.text(context,title,rowHeight<36?12:13,0xff151a1f);label.setPadding(dp(9),0,dp(12),0);label.setMaxLines(2);label.setEllipsize(android.text.TextUtils.TruncateAt.END);row.addView(label,new LinearLayout.LayoutParams(0,-1,1));keyboardBack(row);row.setEnabled(enabled);row.setAlpha(enabled?1:.42f);row.setOnClickListener(v->{if(enabled){dismiss();action.run();}});items.addView(row,new LinearLayout.LayoutParams(-1,dp(rowHeight)));rows.add(row);
    }
    private boolean menuKey(View row,int key,KeyEvent event){
        // Keep the popup for the complete Escape pair so Android cannot turn its orphaned key-up into Back.
        if(key==KeyEvent.KEYCODE_ESCAPE){if(event.getAction()==KeyEvent.ACTION_UP)dismiss();return true;}
        if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_LEFT&&parent!=null){closeBranch();if(parentRow!=null)parentRow.requestFocus();return true;}return false;
    }
    private void keyboardBack(View row){row.setOnKeyListener(this::menuKey);}
    private void fitRows(int width){
        for(View row:rows){int wanted=dp(rowHeight);
            if(row instanceof TextView){row.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));wanted=Math.max(wanted,row.getMeasuredHeight());}
            else{LinearLayout line=(LinearLayout)row;TextView label=(TextView)line.getChildAt(1);int available=width;for(int i=0;i<line.getChildCount();i++)if(i!=1){View child=line.getChildAt(i);LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)child.getLayoutParams();if(lp.width>=0)available-=lp.width;else{child.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));available-=child.getMeasuredWidth();}}
                label.measure(View.MeasureSpec.makeMeasureSpec(Math.max(1,available),View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));wanted=Math.max(wanted,label.getMeasuredHeight()+dp(8));}
            android.view.ViewGroup.LayoutParams p=row.getLayoutParams();p.height=wanted;row.setLayoutParams(p);
        }
    }
    void custom(View view){items.addView(view,new LinearLayout.LayoutParams(-1,-2));}
    void shortcut(String title,String icon,String shortcut,Runnable action){item(title,icon,action);View row=items.getChildAt(items.getChildCount()-1);if(row instanceof LinearLayout){TextView hint=Aero.text(context,shortcut,11,0xff626673);hint.setPadding(dp(4),0,dp(8),0);((LinearLayout)row).addView(hint,new LinearLayout.LayoutParams(-2,-1));}}
    void section(String title){
        TextView label=Aero.text(context,title,11,0xff345974);
        label.setPadding(dp(10),0,dp(6),0);
        label.setBackgroundColor(0xffe3edf5);
        items.addView(label,new LinearLayout.LayoutParams(-1,dp(23)));
    }
    void checked(String title,boolean checked,Runnable action){item(title,checked?"checked":"empty",action);}
    void submenu(String title,String icon,Runnable action){
        hasSubmenus=true;
        item(plain?title+"   ›":title,icon,action);View row=items.getChildAt(items.getChildCount()-1);row.setContentDescription(title);
        if(!plain){TextView arrow=Aero.text(context,"›",18,0xff476277);((LinearLayout)row).addView(arrow,new LinearLayout.LayoutParams(dp(20),-1));}
        Runnable open=()->{if(!showing())return;if(child!=null&&child.parentRow==row&&child.showing())return;if(child!=null)child.closeBranch();openingParent=this;openingRow=row;try{action.run();}finally{openingParent=null;openingRow=null;}if(child==null)dismiss();};
        row.setOnClickListener(v->open.run());
        row.setOnHoverListener((v,e)->{if(e.getActionMasked()==MotionEvent.ACTION_HOVER_ENTER){handler.removeCallbacksAndMessages(null);handler.postDelayed(open,200);}else if(e.getActionMasked()==MotionEvent.ACTION_HOVER_EXIT)handler.removeCallbacksAndMessages(null);return false;});
        row.setOnKeyListener((v,key,event)->{if(menuKey(v,key,event))return true;if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_RIGHT){open.run();return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT&&parent!=null){closeBranch();if(parentRow!=null)parentRow.requestFocus();return true;}return false;});
    }
    void separator(){LinearLayout row=new LinearLayout(context);row.setPadding(dp(plain?10:34),dp(3),dp(10),dp(3));View line=new View(context);line.setBackgroundColor(plain?0xff696969:0xffc6cbcf);row.addView(line,new LinearLayout.LayoutParams(-1,dp(1)));items.addView(row,new LinearLayout.LayoutParams(-1,dp(7)));}
    void show(View root,float screenX,float screenY,int leftInset,int topInset,int rightInset,int bottomInset){
        if(owner!=null){owner.removeOnAttachStateChangeListener(lifetime);owner.removeOnLayoutChangeListener(bounds);}owner=root;root.addOnAttachStateChangeListener(lifetime);root.addOnLayoutChangeListener(bounds);int[] location=new int[2];root.getLocationOnScreen(location);int left=location[0]+leftInset+dp(3),top=location[1]+topInset+dp(3),right=location[0]+root.getWidth()-rightInset-dp(3),bottom=location[1]+root.getHeight()-bottomInset-dp(3);
        boolean cascading=openingParent!=null&&openingParent.showing()&&openingRow!=null;
        android.graphics.Rect anchor=null;
        if(cascading){parent=openingParent;parentRow=openingRow;parent.child=this;int[] at=new int[2];parentRow.getLocationOnScreen(at);anchor=new android.graphics.Rect(at[0],at[1],at[0]+parentRow.getWidth(),at[1]+parentRow.getHeight());}
        int width=Math.min(dp(menuWidth),Math.max(1,right-left));
        if(anchor!=null)width=Math.min(width,Math.max(1,Math.max(anchor.left-left,right-anchor.right)+dp(3)));
        else if(hasSubmenus)width=Math.min(width,Math.max(1,(right-left)/2));
        fitRows(Math.max(1,width-dp(8)));
        popup.getContentView().measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(Math.max(1,bottom-top),View.MeasureSpec.AT_MOST));int height=Math.min(popup.getContentView().getMeasuredHeight(),Math.max(1,bottom-top));popup.setWidth(width);popup.setHeight(height);
        int x=Math.max(left,Math.min(Math.round(screenX),right-width)),y=Math.max(top,Math.min(Math.round(screenY),bottom-height));
        if(anchor!=null){int rightSpace=right-anchor.right,leftSpace=anchor.left-left;x=rightSpace>=leftSpace?anchor.right-dp(3):anchor.left-width+dp(3);x=Math.max(left,Math.min(x,right-width));y=Math.max(top,Math.min(anchor.top,bottom-height));}
        popup.showAtLocation(root,Gravity.TOP|Gravity.LEFT,x,y);
        if(root.isInTouchMode())popup.getContentView().requestFocus();
        else for(int i=0;i<items.getChildCount();i++){View row=items.getChildAt(i);if(row.isEnabled()&&row.isFocusable()&&row.requestFocus())break;}
    }
    boolean showing(){return popup.isShowing();}
    void dismiss(){DesktopContextMenu root=this;while(root.parent!=null)root=root.parent;root.closeBranch();}
    private void closeBranch(){branchClosing=true;handler.removeCallbacksAndMessages(null);if(child!=null){child.closeBranch();child=null;}popup.dismiss();branchClosing=false;}
    private int dp(int value){return Aero.dp(context,value);}
}
