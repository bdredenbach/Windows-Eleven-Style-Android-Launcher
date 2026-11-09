package com.nthexperience.desktop;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Recorded page composition built from real Nth state and Android-owned destinations. */
final class ElevenSettingsPages {
    static boolean render(TenSettings s, TenSettingsCatalog.Page p) {
        switch (p.id) {
            case "display":
                s.detail("Brightness & color", "");
                ElevenSettingsCard brightness=s.group("Brightness", "Adjust the built-in display", "display", true);
                LinearLayout light=Aero.column(s.getContext());new BrightnessControl((Activity)s.getContext()).content(light,true);brightness.child(light);
                s.card("Night light", "Eye comfort and color temperature", "display", ()->s.navigate("night-light"));
                s.card("Color profile", "Device color preferences", "personalization", ()->s.navigate("color-management"));
                s.card("HDR", "Reported display capabilities", "display", ()->s.navigate("hdr"));
                s.detail("Scale & layout", "");
                s.card("Desktop icons and spacing", "Size, labels and grid layout", "apps", ()->s.navigate("desktop"));
                s.card("Display orientation", s.getResources().getConfiguration().orientation==2?"Landscape":"Portrait", "display", ()->s.open.accept(p.route));
                s.card("Advanced display", displaySummary(s), "display", ()->s.navigate("advanced-display"));
                related(s,"Multitasking","multitasking");return true;
            case "night-light":
                s.detail("Use warmer colors", "Manage eye comfort, strength and scheduling in your Android display settings.");
                s.card("Night light settings", "Open this device's display controls", "display", ()->s.open.accept("setting:android:night-light"));
                related(s,"Display","display");return true;
            case "color-management":
                s.detail("Color profiles", "Android and the device manufacturer manage the display's available color modes.");
                s.card("Display colors", "Choose an available mode in Android", "personalization", ()->s.open.accept("setting:android:display"));
                related(s,"Colors","colors");return true;
            case "hdr":
                Display d=((Activity)s.getContext()).getWindowManager().getDefaultDisplay();
                int[] types=d.getHdrCapabilities().getSupportedHdrTypes();List<String> names=new ArrayList<>();
                for(int type:types)names.add(type==1?"Dolby Vision":type==2?"HDR10":type==3?"HLG":type==4?"HDR10+":"HDR type "+type);
                ElevenSettingsCard caps=s.group("Display capabilities",displaySummary(s),"display",true);
                caps.child(note(s,"Reported HDR formats",names.isEmpty()?"No HDR format reported for this display":String.join(", ",names)));
                s.card("Display preferences","Manage available video/display modes in Android","display",()->s.open.accept("setting:android:display"));
                related(s,"Video playback","video-playback");return true;
            case "advanced-display":
                ElevenSettingsCard info=s.group("Display information",displaySummary(s),"display",true);
                DisplayMetrics metrics=s.getResources().getDisplayMetrics();
                info.child(note(s,"Density",metrics.densityDpi+" dpi · Android controls system scaling"));
                info.child(note(s,"Orientation",s.getResources().getConfiguration().orientation==2?"Landscape":"Portrait"));
                s.card("Android display settings","Resolution, rotation and refresh options available on this phone","display",()->s.open.accept("setting:android:display"));return true;
            case "sound":sound(s);return true;
            case "focus":ElevenFocus.panel(s);return true;
            case "installed":
                s.body.addView(new TenDeviceFeatures(s.getContext(),false,true,s.open),new LinearLayout.LayoutParams(-1,-2));
                related(s,"System components","components");related(s,"AI components","ai-components");return true;
            case "advanced-apps":
                s.card("Default apps","Home, browser and other Android defaults","apps",()->s.navigate("defaults"));
                s.card("Apps for websites","Manage supported app links","browser",()->s.navigate("app-links"));
                s.card("App execution aliases","Available commands in Nth Run","terminal",()->s.navigate("app-aliases"));return true;
            case "app-aliases":
                s.detail("Nth Run commands","These names open the corresponding Nth tool. Installed apps can also be opened by their full name.");
                for(String[] alias:new String[][]{{"explorer","File Explorer"},{"calc / calculator","Calculator"},{"notepad","Notepad"},{"settings / control","Settings"},{"taskmgr","Task Manager"},{"winver","About"},{"nota / nth nota","Nth Nota"}})
                    s.body.addView(note(s,alias[0],alias[1]),new LinearLayout.LayoutParams(-1,-2));
                s.button("Open Run",()->s.open.accept("@run"));return true;
            case "app-actions":s.detail("Apps and desktop actions","Open installed apps, find settings and manage desktops using Nth Nota's local actions.");s.button("Open Nth Nota",()->s.open.accept("@nota"));return true;
            case "app-links":s.detail("Opening links","Android manages which installed apps can open supported website addresses.");s.button("Manage Android app links",()->s.open.accept("setting:android:app-links"));return true;
            case "startup-apps":
                s.detail("Nth startup experience","Choose when the Nth welcome animation plays. Android manages startup permissions for other apps.");
                select(s,"Show startup animation",new String[]{"First launch only","Once per phone reboot","Off"},s.store.prefs.getInt("startup_mode",0),index->s.store.prefs.edit().putInt("startup_mode",index).apply());
                s.body.addView(toggle(s,"Play Nth startup chime","startup_sound",false),new LinearLayout.LayoutParams(-1,-2));
                s.button("Saved workspaces",()->s.open.accept("@workspaces"));return true;
            case "resume":s.detail("Saved workspaces","Reopen saved Nth window layouts using the current file permissions. Your live note contents stay intact.");s.button("Open Saved Workspaces",()->s.open.accept("@workspaces"));return true;
            case "video-playback":
                s.card("Nth Media Player","Play your audio and video files","video",()->s.open.accept(MediaLibrary.PLAYER));
                s.card("Display","Available Android display preferences","display",()->s.navigate("display"));
                s.card("Sound","Output devices and volume","sound",()->s.navigate("sound"));
                related(s,"Power & battery","power");return true;
            case "text-input":case "typing":
                s.detail("Keyboard and voice input","Your selected Android keyboard manages its themes, dictation and typing preferences.");
                s.card("Keyboard settings","Choose keyboards and input methods","input",()->s.open.accept("setting:android:input"));
                related(s,"Speech","speech");return true;
            case "advanced":
                s.detail("File Explorer","");s.card("File Explorer","Views, search and connected folders","files",()->s.open.accept("@files"));
                s.detail("Virtual workspaces","");s.card("Virtual desktops","Switch and manage Nth desktops","taskview",()->s.open.accept("@taskview"));s.card("Saved workspaces","Save and restore window layouts","taskview",()->s.open.accept("@workspaces"));
                s.detail("For developers","");s.card("Terminal","Nth command tools","terminal",()->s.open.accept("@powershell"));s.card("Developer options","Android development settings","settings",()->s.open.accept("setting:android:developer"));return true;
            case "recovery":
                s.detail("Recovery options","");s.card("Backup and restore","Save or restore your Nth desktop setup","update",()->s.open.accept("@backup"));
                s.card("Guided setup","Return to Home, files and permission guidance","settings",()->s.open.accept("@welcome"));
                s.card("Restart Nth Desktop","Refresh the launcher and restore its state","refresh",()->s.open.accept("setting:restart-desktop"));return true;
            default:return false;
        }
    }
    static boolean landing(TenSettings s,String category) {
        if(category.equals("home")) {
            deviceHeader(s);
            s.detail("Recommended settings","");
            for(String id:new String[]{"display","sound","storage","bluetooth"}){TenSettingsCatalog.Page p=TenSettingsCatalog.page(id);s.card(p.title,p.detail,id,()->s.navigate(id));}
            s.detail("Personalize your device","");themes(s);
            s.card("Color mode",s.dark()?"Dark":"Light","personalization",()->s.navigate("colors"));
            s.card("Nth Nota","Write, capture and launch apps","nota",()->s.open.accept("@nota"));
            return true;
        }
        if(category.equals("system"))deviceHeader(s);
        else if(category.equals("personalization")){preview(s,150);s.detail("Select a theme to apply","");themes(s);}
        else if(category.equals("devices"))s.detail("Connected devices","Pair devices and manage accessories in Android.");
        else if(category.equals("network"))s.detail("Connection status",connection(s));
        else if(category.equals("accounts")){
            s.detail(s.store.prefs.getString("desktop_name","Nth Desktop Eleven"),"Local Nth profile");
            s.card("Profile picture and name","Personalize your local profile","accounts",()->s.navigate("profile"));
        }else if(category.equals("time")){
            s.detail(android.text.format.DateFormat.getTimeFormat(s.getContext()).format(new Date()),new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy",Locale.getDefault()).format(new Date()));
            s.detail("Time zone & region",TimeZone.getDefault().getDisplayName()+" · "+Locale.getDefault().getDisplayCountry());
        }
        for(TenSettingsCatalog.Page p:TenSettingsCatalog.category(category))s.card(p.title,p.detail,p.id,()->s.navigate(p.id));
        return true;
    }
    static void deviceHeader(TenSettings s) {
        ElevenSettingsCard card=s.cardPanel(s.store.prefs.getString("ten_device_name",Build.MODEL),Build.MANUFACTURER+" "+Build.MODEL+"\nAndroid "+Build.VERSION.RELEASE,"computer");
        card.link(()->s.navigate("about"));
        AeroWallpaper image=new AeroWallpaper(s.getContext(),s.store.prefs.getInt("wallpaper",11),false);s.extraPreviews.add(image);
        LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(s.dp(90),s.dp(58));ip.setMargins(s.dp(12),0,s.dp(12),s.dp(12));
        card.addView(image,0,ip);s.body.addView(note(s,"Network",connection(s)),new LinearLayout.LayoutParams(-1,-2));
    }
    static void preview(TenSettings s,int height) {
        AeroWallpaper image=new AeroWallpaper(s.getContext(),s.store.prefs.getInt("wallpaper",11),false);s.extraPreviews.add(image);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,s.dp(height));lp.bottomMargin=s.dp(12);s.body.addView(image,lp);
    }
    static void themes(TenSettings s) {
        GridLayout grid=new GridLayout(s.getContext());int columns=s.width-s.navWidth>=s.dp(440)?3:2;grid.setColumnCount(columns);
        for(int i=0;i<TenThemes.LANDING.size();i++) {
            TenThemes.Preset preset=TenThemes.LANDING.get(i);FrameLayout tile=new FrameLayout(s.getContext());
            AeroWallpaper art=new AeroWallpaper(s.getContext(),preset.wallpaper,false);s.extraPreviews.add(art);tile.addView(art,new FrameLayout.LayoutParams(-1,-1));TenAppearance.preserve(tile);
            View bar=new View(s.getContext());bar.setBackgroundColor(preset.light?0xffeeeeee:0xff202532);tile.addView(bar,new FrameLayout.LayoutParams(-1,s.dp(10),Gravity.BOTTOM));
            if(preset.matches(s.store)){TextView check=Aero.text(s.getContext(),"✓",18,TenAccent.on(preset.accent));check.setGravity(Gravity.CENTER);check.setBackgroundColor(preset.accent);tile.addView(check,new FrameLayout.LayoutParams(s.dp(24),s.dp(24),Gravity.END|Gravity.BOTTOM));}
            tile.setContentDescription("Apply theme: "+preset.name);tile.setTooltipText(preset.name);tile.setFocusable(true);tile.setOnClickListener(v->{preset.apply(s.store);s.appearanceChanged();});
            GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(i/columns),GridLayout.spec(i%columns,1f));lp.width=0;lp.height=s.dp(72);lp.setMargins(0,0,s.dp(6),s.dp(6));grid.addView(tile,lp);
        }s.body.addView(grid,new LinearLayout.LayoutParams(-1,-2));
    }
    static View note(TenSettings s,String title,String text) {
        LinearLayout box=Aero.column(s.getContext());box.setPadding(s.dp(14),s.dp(10),s.dp(14),s.dp(12));
        if(!title.isEmpty()){TextView name=Aero.text(s.getContext(),title,13,TenAppearance.ink(s.getContext()));Aero.bold(name);box.addView(name);}
        TextView summary=Aero.text(s.getContext(),text,12,s.dark()?0xffc2c3cf:0xff646471);summary.setPadding(0,s.dp(4),0,0);box.addView(summary);return box;
    }
    static Switch toggle(TenSettings s,String title,String key,boolean fallback) {
        Switch toggle=new Switch(s.getContext());toggle.setText(title);toggle.setTextSize(13);toggle.setTypeface(Aero.face(s.getContext()));toggle.setTextColor(TenAppearance.ink(s.getContext()));
        toggle.setPadding(s.dp(14),s.dp(12),s.dp(14),s.dp(12));toggle.setMinimumHeight(s.dp(52));toggle.setChecked(s.store.prefs.getBoolean(key,fallback));toggle.setContentDescription(title);
        toggle.setOnCheckedChangeListener((v,checked)->s.store.prefs.edit().putBoolean(key,checked).apply());return toggle;
    }
    static void select(TenSettings s,String title,String[] choices,int selected,java.util.function.IntConsumer apply) {
        ElevenSettingsCard card=s.cardPanel(title,"","settings");Spinner spinner=new Spinner(s.getContext());spinner.setContentDescription(title+" choice");
        ArrayAdapter<String> adapter=new ArrayAdapter<>(s.getContext(),android.R.layout.simple_spinner_item,choices);adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spinner.setAdapter(adapter);spinner.setSelection(Math.max(0,Math.min(selected,choices.length-1)));
        if(s.width-s.navWidth<s.dp(350)){card.header.setOrientation(LinearLayout.VERTICAL);card.labels.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));card.header.addView(spinner,new LinearLayout.LayoutParams(-1,s.dp(44)));}
        else card.header.addView(spinner,new LinearLayout.LayoutParams(s.dp(180),s.dp(44)));
        final int[] previous={spinner.getSelectedItemPosition()};spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> parent){}public void onItemSelected(AdapterView<?> parent,View view,int index,long id){if(previous[0]!=index){previous[0]=index;apply.accept(index);}}});
    }
    private static void sound(TenSettings s) {
        AudioManager audio=s.getContext().getSystemService(AudioManager.class);if(audio==null){s.detail("Sound","Audio controls unavailable.");return;}
        ElevenSettingsCard output=s.group("Output",deviceNames(audio.getDevices(AudioManager.GET_DEVICES_OUTPUTS)),"sound",true);
        LinearLayout level=Aero.column(s.getContext());level.setPadding(s.dp(12),s.dp(8),s.dp(12),s.dp(10));
        level.addView(Aero.text(s.getContext(),"Volume",13,TenAppearance.ink(s.getContext())));
        SeekBar seek=new SeekBar(s.getContext());seek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));seek.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));seek.setContentDescription("Media volume");
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}public void onProgressChanged(SeekBar b,int n,boolean user){if(user)audio.setStreamVolume(AudioManager.STREAM_MUSIC,n,0);}});level.addView(seek,new LinearLayout.LayoutParams(-1,s.dp(48)));output.child(level);
        output.child(action(s,"Pair a new output device",()->s.open.accept("setting:android:bluetooth")));
        ElevenSettingsCard input=s.group("Input",deviceNames(audio.getDevices(AudioManager.GET_DEVICES_INPUTS)),"sound",true);input.child(action(s,"Input device preferences",()->s.open.accept("setting:android:sound")));
        s.body.addView(toggle(s,"Play launcher sounds","desktop_sounds",false),new LinearLayout.LayoutParams(-1,-2));
        s.detail("Advanced","");s.card("All sound devices","Output, input and Android sound controls","sound",()->s.open.accept("setting:android:sound"));
    }
    static View action(TenSettings s,String title,Runnable action) {
        TextView v=TenStyle.command(s.getContext(),title,action);v.setTextColor(TenAppearance.ink(s.getContext()));v.setPadding(s.dp(14),s.dp(8),s.dp(14),s.dp(8));v.setMinHeight(s.dp(48));v.setContentDescription(title);return v;
    }
    static void related(TenSettings s,String title,String page){s.detail("Related settings","");s.card(title,"","settings",()->s.navigate(page));}
    static String connection(TenSettings s) {
        try{ConnectivityManager manager=s.getContext().getSystemService(ConnectivityManager.class);NetworkCapabilities c=manager==null?null:manager.getNetworkCapabilities(manager.getActiveNetwork());
            if(c==null)return "Not connected";String transport=c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)?"Wi-Fi":c.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)?"Ethernet":c.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)?"Mobile network":c.hasTransport(NetworkCapabilities.TRANSPORT_VPN)?"VPN":"Network";
            return transport+(c.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)?" · Internet available":" · Internet not validated");
        }catch(SecurityException e){return "View connection details in Android settings";}
    }
    private static String deviceNames(AudioDeviceInfo[] devices){LinkedHashSet<String> names=new LinkedHashSet<>();for(AudioDeviceInfo d:devices)if(d.getProductName()!=null)names.add(d.getProductName().toString());return names.isEmpty()?"No devices reported":String.join(", ",names);}
    private static String displaySummary(TenSettings s){Display d=((Activity)s.getContext()).getWindowManager().getDefaultDisplay();DisplayMetrics m=new DisplayMetrics();d.getRealMetrics(m);return m.widthPixels+" × "+m.heightPixels+" pixels · "+String.format(Locale.getDefault(),"%.1f Hz",d.getRefreshRate());}
    private ElevenSettingsPages(){}
}
