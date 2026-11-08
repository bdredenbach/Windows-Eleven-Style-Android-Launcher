package com.nthexperience.desktop;

/** Edge targeting in workspace coordinates, with the top edge taking priority at corners. */
final class AeroSnap {
    static final int MIN_SIDE_WIDTH_DP=160;
    enum Placement { FLOATING, MAXIMIZED, LEFT, RIGHT, THIRD_LEFT, THIRD_MIDDLE, THIRD_RIGHT, TWO_THIRDS_LEFT, TWO_THIRDS_RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    static boolean canSplit(int width, int height, int minimumWidth, int minimumHeight) {
        return width / 2 >= minimumWidth && height >= minimumHeight;
    }

    static boolean side(Placement p){return p!=Placement.FLOATING&&p!=Placement.MAXIMIZED;}
    static int[] bounds(Placement p,int width,int height){int x=0,y=0,w=width,h=height;switch(p){
        case LEFT:w=width/2;break;case RIGHT:x=width/2;w=width-x;break;
        case THIRD_LEFT:w=width/3;break;case THIRD_MIDDLE:x=width/3;w=2*width/3-x;break;case THIRD_RIGHT:x=2*width/3;w=width-x;break;
        case TWO_THIRDS_LEFT:w=2*width/3;break;case TWO_THIRDS_RIGHT:x=width/3;w=width-x;break;
        case TOP_LEFT:w=width/2;h=height/2;break;case TOP_RIGHT:x=width/2;w=width-x;h=height/2;break;
        case BOTTOM_LEFT:w=width/2;y=height/2;h=height-y;break;case BOTTOM_RIGHT:x=width/2;w=width-x;y=height/2;h=height-y;break;default:break;}
        return new int[]{x,y,w,h};
    }
    static boolean canPlace(Placement p,int width,int height,int minimumWidth,int minimumHeight){if(!side(p))return true;int[] b=bounds(p,width,height);return b[2]>=minimumWidth&&b[3]>=minimumHeight;}
    static Placement target(float x, float y, int width, int top, int height, int edge,
                            int minimumWidth, int minimumHeight) {
        if (x < 0 || x > width || y < 0 || y >= height) return Placement.FLOATING;
        if (y <= top + edge) return Placement.MAXIMIZED;
        if (canSplit(width, height - top, minimumWidth, minimumHeight)) {
            if (x <= edge) return Placement.LEFT;
            if (x >= width - edge) return Placement.RIGHT;
        }
        return Placement.FLOATING;
    }
}
