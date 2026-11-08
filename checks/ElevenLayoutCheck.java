package com.nthexperience.desktop;
public final class ElevenLayoutCheck {
 public static void main(String[] args){int n=0;
  for(int width=280;width<=2200;width+=7)for(int left=0;left<=140;left+=28)for(int right=90;right<=210;right+=24)for(int desired=140;desired<=1600;desired+=97){
   if(width-left-right<1)continue;int size=ElevenLayout.taskbarWidth(width-left-right,desired),x=ElevenLayout.taskbarLeft(width,left,right,desired);
   if(x<left||x+size>width-right||size<1)throw new AssertionError("Taskbar overlaps reserved controls");
   if((width-size)/2>=left&&(width+size)/2<=width-right&&Math.abs(x-(width-size)/2)>1)throw new AssertionError("Unnecessarily off center");n++;
  }
  for(int width=1;width<2000;width++){int cols=ElevenLayout.pinColumns(width);if(cols<3||cols>6)throw new AssertionError("Pin columns");}
  System.out.println("PASS: "+n+" taskbar bounds, reserved controls and pin column policies");
 }
}
