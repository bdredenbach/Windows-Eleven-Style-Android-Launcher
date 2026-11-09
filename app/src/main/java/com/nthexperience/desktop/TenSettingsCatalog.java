package com.nthexperience.desktop;
import java.util.*;

/** One catalog owns links, search, ancestors and the recorded Eleven category order. */
final class TenSettingsCatalog {
    static final class Page {
        final String id,title,category,detail,route,parent;
        Page(String id,String title,String category,String detail,String route) { this(id,title,category,detail,route,category); }
        Page(String id,String title,String category,String detail,String route,String parent) {
            this.id=id;this.title=title;this.category=category;this.detail=detail;this.route=route;this.parent=parent;
        }
    }
    static final String[][] CATEGORIES={
        {"system","System","Display, sound, notifications, power"},
        {"devices","Bluetooth & devices","Devices, printers, keyboard and touch"},
        {"network","Network & internet","Wi-Fi, hotspot, VPN"},
        {"personalization","Personalization","Background, colors, themes, Start"},
        {"apps","Apps","Installed apps, defaults, Nth tools"},
        {"accounts","Accounts","Your information and Android accounts"},
        {"time","Time & language","Date, region, typing and speech"},
        {"gaming","Gaming","Games and Android game settings"},
        {"accessibility","Accessibility","Display, interaction, narration"},
        {"privacy","Privacy & security","Permissions, search and Nota assistance"},
        {"update","Updates","Launcher backup and Android updates"}
    };
    static final List<Page> PAGES=Arrays.asList(
        new Page("display","Display","system","Brightness, display information and desktop scale","setting:android:display"),
        new Page("night-light","Night light","system","Eye comfort and Android night display settings","setting:android:display","display"),
        new Page("color-management","Color management","system","Device display color preferences","setting:android:display","display"),
        new Page("hdr","HDR","system","Capabilities reported by the Android display","setting:android:display","display"),
        new Page("advanced-display","Advanced display","system","Display resolution, density and refresh rate","setting:android:display","display"),
        new Page("sound","Sound","system","Output devices, media volume and launcher sounds","setting:android:sound"),
        new Page("notifications","Notifications","system","App notifications and calendar",""),
        new Page("focus","Focus","system","Timed sessions and quiet launcher notifications",""),
        new Page("power","Power & battery","system","Battery, energy saver and screen timeout","setting:android:battery"),
        new Page("energy","Energy recommendations","system","Useful launcher and Android energy choices","setting:android:battery","power"),
        new Page("storage","Storage","system","Device capacity and file access","setting:android:storage"),
        new Page("shared","Share","system","Connected devices and Android sharing","setting:android:connected"),
        new Page("multitasking","Multitasking","system","Snap windows, Task View and virtual desktops",""),
        new Page("advanced","Advanced","system","Explorer, virtual workspaces and developer settings",""),
        new Page("recovery","Recovery","system","Backup, restore, guided setup and desktop restart",""),
        new Page("projecting","Projecting to this PC","system","Cast your Android screen to a compatible display","setting:android:cast"),
        new Page("remote","Remote Desktop","system","Use an installed remote desktop app",""),
        new Page("clipboard","Clipboard","system","Copied Nth files and shortcuts",""),
        new Page("components","System components","system","Installed Android system components",""),
        new Page("ai-components","AI components","system","Nth Nota local and optional cloud assistance",""),
        new Page("optional","Optional features","system","Device features reported by Android",""),
        new Page("about","About","system","Device and launcher specifications",""),
        new Page("bluetooth","Bluetooth","devices","Manage connected Bluetooth devices","setting:android:bluetooth"),
        new Page("sharing","Devices","devices","Android connected accessories","setting:android:connected"),
        new Page("printing","Printers & scanners","devices","Android print services","setting:android:print"),
        new Page("input","Keyboard & mouse","devices","Keyboard and input methods","setting:android:input"),
        new Page("touch","Touch","devices","Touch and interaction accessibility","setting:android:accessibility"),
        new Page("wifi","Wi-Fi","network","Wireless network connections","setting:android:network"),
        new Page("hotspot","Mobile hotspot","network","Android network sharing","setting:android:network"),
        new Page("vpn","VPN","network","Virtual private networks","setting:android:vpn"),
        new Page("airplane","Airplane mode","network","Android airplane mode","setting:android:airplane"),
        new Page("background","Background","personalization","Desktop pictures and slideshows",""),
        new Page("colors","Colors","personalization","Light, dark, transparency and accent colors",""),
        new Page("themes","Themes","personalization","Desktop backgrounds and sound schemes",""),
        new Page("lock","Lock screen","personalization","Android lock screen background and security",""),
        new Page("text-input","Text input","personalization","Android keyboard, voice typing and input methods","setting:android:input"),
        new Page("start","Start","personalization","Pinned and All apps, folders and menu layout",""),
        new Page("tablet","Full-screen Start","personalization","Use full-screen Start","","start"),
        new Page("taskbar","Taskbar","personalization","Alignment, Usage Access, search, previews and grouping",""),
        new Page("fonts","Fonts","personalization","Preview and choose launcher typefaces",""),
        new Page("desktop","Desktop icons","personalization","Icon size, spacing, labels and layout",""),
        new Page("installed","Installed apps","apps","Search and manage installed Android apps","setting:android:apps"),
        new Page("advanced-apps","Advanced app settings","apps","Defaults, app links and Nth commands","setting:android:apps"),
        new Page("app-aliases","App execution aliases","apps","Command names available in Nth Run","","advanced-apps"),
        new Page("defaults","Default apps","apps","Choose Android Home and app defaults","setting:android:defaults"),
        new Page("app-actions","Actions","apps","Local app and desktop actions with Nth Nota",""),
        new Page("app-links","Apps for websites","apps","Android supported links and app defaults","setting:android:defaults"),
        new Page("video-playback","Video playback","apps","Nth media and Android display/sound settings",""),
        new Page("startup-apps","Startup","apps","Nth startup experience and saved workspaces",""),
        new Page("resume","Resume","apps","Saved Nth workspaces and window layouts",""),
        new Page("tools","Nth tools","apps","Calculator, unit converter and text counter",""),
        new Page("profile","Your info","accounts","Local profile name and picture",""),
        new Page("signin","Sign-in options","accounts","Android screen lock and security","setting:android:security"),
        new Page("android-accounts","Your accounts","accounts","Android account settings","setting:android:accounts"),
        new Page("date","Date & time","time","Android date, time and time zone","setting:android:date"),
        new Page("language","Language & region","time","System language and region","setting:android:language"),
        new Page("typing","Typing","time","Keyboard and input preferences","setting:android:input"),
        new Page("speech","Speech","time","Text-to-speech settings","setting:android:speech"),
        new Page("games","Games","gaming","Tile puzzle and dice roller",""),
        new Page("ease","Android accessibility","accessibility","Accessibility services and controls","setting:android:accessibility"),
        new Page("motion","Visual effects","accessibility","Reduce motion and show the Android status bar",""),
        new Page("permissions","App permissions","privacy","Android permissions for Nth Desktop","setting:android:permissions"),
        new Page("security","Device security","privacy","Android lock and security settings","setting:android:security"),
        new Page("search-options","Search","privacy","Apps, settings and linked files",""),
        new Page("assistant","Nth Nota assistance","privacy","Local commands and optional free cloud writing",""),
        new Page("notification-access","Notification access","privacy","Show Android notifications in Eleven",""),
        new Page("backup","Backup","update","Export or restore launcher data",""),
        new Page("updates","Device updates","update","Android update settings","setting:android:device")
    );
    static Page page(String id){for(Page p:PAGES)if(p.id.equals(id))return p;return null;}
    static String first(String category){for(Page p:category(category))return p.id;return "home";}
    static List<Page> category(String id){List<Page> rows=new ArrayList<>();for(Page p:PAGES)if(p.parent.equals(id))rows.add(p);return rows;}
    static List<Page> search(String query){String q=query.trim().toLowerCase(Locale.ROOT);List<Page> rows=new ArrayList<>();if(q.isEmpty())return rows;for(Page p:PAGES)if((p.title+" "+p.detail+" "+title(p.category)).toLowerCase(Locale.ROOT).contains(q))rows.add(p);return rows;}
    static String title(String category){for(String[] row:CATEGORIES)if(row[0].equals(category))return row[1];return "Settings";}
    static String label(String id){if(id.equals("home"))return "Home";Page p=page(id);return p==null?title(id):p.title;}
    static List<String> path(String id){LinkedList<String> path=new LinkedList<>();String next=id;for(int i=0;i<8&&!path.contains(next);i++){path.addFirst(next);Page p=page(next);if(p==null)break;next=p.parent;}return path;}
    private TenSettingsCatalog(){}
}
