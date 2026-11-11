package com.nthexperience.desktop;
import android.app.Activity;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;
import java.time.Duration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.android.controller.ActivityController;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35) @LooperMode(LooperMode.Mode.PAUSED)
public class TaskbarHoldTest {
    @Test public void realTouchHoldOnExpandedBlankSpaceOpensMenu(){
        ActivityController<Activity> controller=Robolectric.buildActivity(Activity.class).setup().visible();Activity activity=controller.get();ElevenTaskbarItems items=new ElevenTaskbarItems(activity,true);int[] opened={0};ContextMenus.bind(items,(x,y)->opened[0]++);activity.setContentView(items);int height=Aero.dp(activity,96);items.measure(View.MeasureSpec.makeMeasureSpec(500,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));items.layout(0,0,500,height);
        long down=SystemClock.uptimeMillis();MotionEvent event=MotionEvent.obtain(down,down,MotionEvent.ACTION_DOWN,100,20,0);items.dispatchTouchEvent(event);event.recycle();shadowOf(android.os.Looper.getMainLooper()).idleFor(Duration.ofMillis(ViewConfiguration.getLongPressTimeout()+200));assertEquals(1,opened[0]);event=MotionEvent.obtain(down,SystemClock.uptimeMillis(),MotionEvent.ACTION_UP,100,20,0);items.dispatchTouchEvent(event);event.recycle();assertEquals(1,opened[0]);controller.pause().stop().destroy();
    }
}
