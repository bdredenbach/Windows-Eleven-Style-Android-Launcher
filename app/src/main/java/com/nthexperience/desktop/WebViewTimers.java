package com.nthexperience.desktop;
import android.webkit.WebView;import java.util.*;
/** WebView.pauseTimers is process-wide. Only the Activity lifecycle owns that switch. */
final class WebViewTimers {
 private static final Set<WebView> views=Collections.newSetFromMap(new WeakHashMap<>());private static boolean foreground=true;
 static void attach(WebView view){views.add(view);apply(view);}static void detach(WebView view){views.remove(view);}
 static void foreground(boolean value){foreground=value;for(WebView view:views){apply(view);break;}}
 private static void apply(WebView view){if(foreground)view.resumeTimers();else view.pauseTimers();}
 private WebViewTimers(){}
}
