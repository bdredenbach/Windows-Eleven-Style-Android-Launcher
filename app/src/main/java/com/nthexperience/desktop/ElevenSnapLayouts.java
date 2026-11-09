package com.nthexperience.desktop;

import static com.nthexperience.desktop.AeroSnap.Placement.*;

/** Both entry points use the same partitions, including the recorded half + stacked quarters. */
final class ElevenSnapLayouts {
    static AeroSnap.Placement[][] groups(){return new AeroSnap.Placement[][]{
        {LEFT,RIGHT},{TWO_THIRDS_LEFT,THIRD_RIGHT},{THIRD_LEFT,THIRD_MIDDLE,THIRD_RIGHT},
        {LEFT,TOP_RIGHT,BOTTOM_RIGHT},{TOP_LEFT,BOTTOM_LEFT,RIGHT},
        {TOP_LEFT,TOP_RIGHT,BOTTOM_LEFT,BOTTOM_RIGHT}
    };}
    private ElevenSnapLayouts(){}
}
