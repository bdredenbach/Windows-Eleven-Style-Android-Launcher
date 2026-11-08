package com.nthexperience.desktop;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/** Pixel-preserving WindowsIcons assets; provenance is in docs/ELEVEN_ICON_ASSETS.json. */
final class ElevenIconAssets {
    static int resource(String kind,boolean light){
        switch(kind){
            case "files": case "folderapps": return R.drawable.win11_folder;
            case "file-explorer": return R.drawable.win11_explorer;
            case "documents": return R.drawable.win11_documents;
            case "pictures": return R.drawable.win11_pictures;
            case "music": return R.drawable.win11_music;
            case "videos": return R.drawable.win11_videos;
            case "downloads": return R.drawable.win11_downloads;
            case "desktop": return R.drawable.win11_desktop;
            case "settings": return R.drawable.win11_settings;
            case "calculator": return R.drawable.win11_calculator;
            case "calendar": return R.drawable.win11_calendar;
            case "pictures-app": case "photo": case "photos": return R.drawable.win11_photos;
            case "notes": case "notepad": return R.drawable.win11_notepad;
            case "sticky": return R.drawable.win11_sticky;
            case "browser": return R.drawable.win11_browser;
            case "taskmanager": case "meters": return R.drawable.win11_taskmanager;
            case "widgets": case "news": return R.drawable.win11_widgets;
            case "feeds": return R.drawable.win11_news;
            case "run": return R.drawable.win11_run;
            case "terminal": return R.drawable.win11_terminal;
            case "computer": case "system": case "display": return R.drawable.win11_computer;
            case "drive": case "storage": return R.drawable.win11_drive;
            case "network": return R.drawable.win11_network;
            case "appearance": return R.drawable.win11_personalization;
            case "control": return R.drawable.win11_control;
            case "accounts": return R.drawable.win11_accounts;
            case "security": return R.drawable.win11_security;
            case "accessibility": return R.drawable.win11_accessibility;
            case "weather": return R.drawable.win11_weather;
            case "clock": case "timer": case "stopwatch": case "worldclock": return R.drawable.win11_clock;
            case "programs": case "apps": return R.drawable.win11_programs;
            case "documentfile": return R.drawable.win11_document;
            case "picturefile": return R.drawable.win11_image;
            case "musicfile": return R.drawable.win11_audio;
            case "videofile": return R.drawable.win11_video;
            case "archivefile": return R.drawable.win11_archive;
            case "fonts": return R.drawable.win11_font;
            case "copy": return R.drawable.win11_copy;
            case "paste": return R.drawable.win11_paste;
            case "cut": return R.drawable.win11_cut;
            case "delete": return R.drawable.win11_delete;
            case "sound": return R.drawable.win11_sound;
            case "recycle": return R.drawable.win11_recycle_empty;
            case "recycle-full": return R.drawable.win11_recycle_full;
            case "taskview":return light?R.drawable.win11_taskview_light:R.drawable.win11_taskview_dark;
            default:return 0;
        }
    }
    static Drawable drawable(Context context,String kind,boolean light){
        int id=resource(kind,light);if(id==0)return null;Drawable icon=context.getDrawable(id);
        if(icon instanceof BitmapDrawable){((BitmapDrawable)icon).setFilterBitmap(true);((BitmapDrawable)icon).setDither(true);}
        return icon;
    }
    static Drawable drawable(Context context,String kind){return drawable(context,kind,TenStyle.light(context));}
    private ElevenIconAssets(){}
}
