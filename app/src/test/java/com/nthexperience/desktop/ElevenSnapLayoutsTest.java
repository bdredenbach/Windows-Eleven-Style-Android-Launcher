package com.nthexperience.desktop;
import org.junit.Test;
import static org.junit.Assert.*;

public class ElevenSnapLayoutsTest {
    @Test public void everyDiagramPartitionsTheWholeWorkAreaWithoutOverlap(){
        for(int w:new int[]{413,731,1021})for(int h:new int[]{299,887})for(AeroSnap.Placement[] group:ElevenSnapLayouts.groups()){
            long area=0;for(int i=0;i<group.length;i++){int[] a=AeroSnap.bounds(group[i],w,h);assertTrue(a[0]>=0&&a[1]>=0&&a[0]+a[2]<=w&&a[1]+a[3]<=h);area+=(long)a[2]*a[3];
                for(int j=0;j<i;j++){int[] b=AeroSnap.bounds(group[j],w,h);assertFalse(a[0]<b[0]+b[2]&&a[0]+a[2]>b[0]&&a[1]<b[1]+b[3]&&a[1]+a[3]>b[1]);}}
            assertEquals((long)w*h,area);
        }
    }
}
