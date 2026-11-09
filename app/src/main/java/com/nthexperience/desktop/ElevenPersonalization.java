package com.nthexperience.desktop;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.*;
import org.json.JSONObject;

/** Recorded Personalization controls backed by the existing theme and background engines. */
final class ElevenPersonalization {
    static boolean render(TenSettings s,String page){
        switch(page){
            case "colors":colors(s);return true;
            case "background":background(s);return true;
            case "background-slideshow":s.body.addView(new BackgroundPicker(s.getContext(),s.store,new JSONObject(),()->s.open.accept("setting:wallpaper-browse"),()->s.open.accept("setting:wallpaper-folder"),s::appearanceChanged),new LinearLayout.LayoutParams(-1,-2));return true;
            case "themes":
                preview(s);s.detail("Current theme",TenThemes.currentName(s.store));
                s.card("Background",backgroundName(s),"pictures",()->s.navigate("background"));
                s.card("Color",ElevenColors.hex(TenStyle.accent(s.getContext())),"personalization",()->s.navigate("colors"));
                s.card("Sounds",DesktopSounds.scheme(s.store),"sound",()->s.open.accept("setting:page:sounds"));
                s.body.addView(new TenThemeGallery(s.getContext(),s.store,Math.max(s.dp(180),s.width-s.navWidth-s.dp(32)),s.open,s::appearanceChanged),new LinearLayout.LayoutParams(-1,-2));return true;
            default:return false;
        }
    }
    static void preview(TenSettings s){
        FrameLayout sample=new FrameLayout(s.getContext());sample.setContentDescription("Current theme preview");TenAppearance.preserve(sample);
        AeroWallpaper art=new AeroWallpaper(s.getContext(),s.store.prefs.getInt("wallpaper",13));s.extraPreviews.add(art);sample.addView(art,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout window=Aero.column(s.getContext());window.setPadding(s.dp(8),s.dp(5),s.dp(8),s.dp(8));int fill=s.dark()?0xff262633:0xfffafafa;window.setBackground(ElevenSurface.round(s.getContext(),fill,5));
        for(int i=0;i<3;i++){View line=new View(s.getContext());line.setBackgroundColor(s.dark()?0xffc4c5d0:0xff676779);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(s.dp(74-i*9),s.dp(2));lp.setMargins(0,s.dp(7),0,s.dp(2));window.addView(line,lp);}
        View accent=new View(s.getContext());accent.setBackground(ElevenSurface.round(s.getContext(),TenStyle.accent(s.getContext()),3));LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(s.dp(32),s.dp(13));ap.gravity=Gravity.END;ap.topMargin=s.dp(12);window.addView(accent,ap);
        FrameLayout.LayoutParams wp=new FrameLayout.LayoutParams(s.dp(98),s.dp(100),Gravity.CENTER_VERTICAL|Gravity.END);wp.rightMargin=s.dp(16);sample.addView(window,wp);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-1,s.dp(150));size.bottomMargin=s.dp(12);s.body.addView(sample,size);
    }
    private static void colors(TenSettings s){
        preview(s);s.modeChoice();
        ElevenSettingsPages.select(s,"Choose your default Windows mode",new String[]{"Light","Dark"},s.store.prefs.getBoolean("ten_light",false)?0:1,n->{s.store.prefs.edit().putBoolean("ten_light",n==0).apply();s.appearanceChanged();});
        ElevenSettingsPages.select(s,"Choose your default app mode",new String[]{"Light","Dark"},s.dark()?1:0,n->{s.store.prefs.edit().putBoolean("ten_apps_dark",n==1).apply();s.appearanceChanged();});
        s.appearanceToggle("Transparency effects","ten_transparency",false);
        s.detail("","Windows and surfaces use a softly tinted glass effect. Turn this off for solid surfaces.");
        String mode=s.store.prefs.getString(ElevenColors.MODE,"Manual");
        ElevenSettingsPages.select(s,"Accent color",new String[]{"Manual","Automatic"},mode.equals("Automatic")?1:0,n->{s.store.prefs.edit().putString(ElevenColors.MODE,n==1?"Automatic":"Manual").apply();s.appearanceChanged();});
        List<Integer> recent=ElevenColors.recent(s.store);if(!recent.isEmpty()){s.detail("Recent colors","");s.body.addView(swatches(s,recent,false),new LinearLayout.LayoutParams(-1,-2));}
        s.detail("Windows colors","");s.body.addView(new TenAccentPicker(s.getContext(),s.store,s::appearanceChanged),new LinearLayout.LayoutParams(-1,-2));
        s.card("Custom colors",ElevenColors.hex(TenStyle.accent(s.getContext())),"personalization",()->custom(s,false));
        s.appearanceToggle("Show accent color on Start and taskbar","ten_accent_shell",false);
        s.appearanceToggle("Show accent color on title bars and window borders","ten_accent_titles",false);
        ElevenSettingsPages.select(s,"Window finish",new String[]{"Eleven","Classic Aero"},TenThemes.aero(s.store.prefs)?1:0,n->{s.store.prefs.edit().putString("ten_shell_style",n==1?"Aero":"Ten").apply();s.appearanceChanged();});
        if(TenThemes.aero(s.store.prefs))s.body.addView(new TenAeroColors(s.getContext(),s.store,s::appearanceChanged),new LinearLayout.LayoutParams(-1,-2));
    }
    private static void background(TenSettings s){
        preview(s);String mode=s.store.prefs.getString("eleven_background_mode",DesktopBackgrounds.current(s.store.prefs).size()>1?"Slideshow":"Picture");
        ElevenSettingsPages.select(s,"Personalize your background",new String[]{"Picture","Solid color","Slideshow"},mode.equals("Solid color")?1:mode.equals("Slideshow")?2:0,n->{
            if(n==2){s.navigate("background-slideshow");return;}android.content.SharedPreferences.Editor edit=s.store.prefs.edit();
            if(n==0){List<String> pictures=DesktopBackgrounds.current(s.store.prefs);DesktopBackgrounds.choose(edit,Collections.singletonList(pictures.get(0)),13);}else edit.putString("eleven_background_mode","Solid color");edit.apply();s.appearanceChanged();
        });
        if(mode.equals("Solid color")){s.detail("Choose your background color","");List<Integer> values=new ArrayList<>();for(int color:TenAccent.COLORS)values.add(color);s.body.addView(swatches(s,values,true),new LinearLayout.LayoutParams(-1,-2));s.button("View custom background colors",()->custom(s,true));return;}
        s.detail("Choose a picture","");HorizontalScrollView row=new HorizontalScrollView(s.getContext());LinearLayout tiles=new LinearLayout(s.getContext());row.addView(tiles);
        for(int i:new int[]{13,12,15,16,17,18,19,20,14,5,6,7}){final int id=i;AeroWallpaper art=new AeroWallpaper(s.getContext(),id,false);s.extraPreviews.add(art);art.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);art.setContentDescription("Background picture: "+DesktopBackgrounds.NAMES[i]);art.setFocusable(true);art.setOnClickListener(v->{android.content.SharedPreferences.Editor edit=s.store.prefs.edit();DesktopBackgrounds.choose(edit,Collections.singletonList(DesktopBackgrounds.stock(id)),id);edit.apply();s.appearanceChanged();});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(s.dp(82),s.dp(64));lp.rightMargin=s.dp(6);tiles.addView(art,lp);}s.body.addView(row,new LinearLayout.LayoutParams(-1,s.dp(72)));
        s.button("Browse photos",()->s.open.accept("setting:wallpaper-browse"));
        String fit=s.store.prefs.getString("wallpaper_position","Fill");int index=Arrays.asList(BackgroundPicker.POSITIONS).indexOf(fit);ElevenSettingsPages.select(s,"Choose a fit for your desktop image",BackgroundPicker.POSITIONS,index,n->{s.store.prefs.edit().putString("wallpaper_position",BackgroundPicker.POSITIONS[n]).apply();s.appearanceChanged();});
        s.card("Slideshow", "Choose pictures, interval and shuffle", "pictures",()->s.navigate("background-slideshow"));
        s.card("All desktop backgrounds","More Nth collections and your pictures","pictures",()->s.navigate("background-slideshow"));
    }
    private static View swatches(TenSettings s,List<Integer> colors,boolean background){GridLayout grid=new GridLayout(s.getContext());grid.setColumnCount(5);int i=0;for(int color:colors){TextView sample=Aero.text(s.getContext(),"",17,TenAccent.on(color));TenAppearance.preserve(sample);sample.setBackground(Aero.literal(s.getContext(),new int[]{color,color},3,0xff888888));sample.setGravity(Gravity.CENTER);boolean selected=background?color==s.store.prefs.getInt("eleven_solid_color",0xff0a263b):color==TenStyle.accent(s.getContext());sample.setText(selected?"✓":"");sample.setContentDescription((background?"Solid background ":"Recent accent ")+ElevenColors.hex(color));sample.setFocusable(true);sample.setOnClickListener(v->{if(background)s.store.prefs.edit().putString("eleven_background_mode","Solid color").putInt("eleven_solid_color",color).apply();else ElevenColors.choose(s.store,color);s.appearanceChanged();});GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(i/5),GridLayout.spec(i%5,1f));lp.width=0;lp.height=s.dp(40);lp.setMargins(s.dp(2),s.dp(2),s.dp(2),s.dp(2));grid.addView(sample,lp);i++;}return grid;}
    private static void custom(TenSettings s,boolean background){
        LinearLayout content=Aero.column(s.getContext());Aero.pad(content,16);EditText hex=new EditText(s.getContext());hex.setSingleLine();hex.setText(ElevenColors.hex(background?s.store.prefs.getInt("eleven_solid_color",0xff0a263b):TenStyle.accent(s.getContext())));hex.setContentDescription("Custom color hexadecimal");hex.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(7)});content.addView(hex);TextView sample=Aero.text(s.getContext(),"Color preview",15,Color.WHITE);sample.setGravity(Gravity.CENTER);TenAppearance.preserve(sample);content.addView(sample,new LinearLayout.LayoutParams(-1,s.dp(64)));
        Runnable preview=()->{try{int color=ElevenColors.parse(hex.getText().toString());sample.setBackgroundColor(color);sample.setTextColor(TenAccent.on(color));}catch(IllegalArgumentException ignored){}};hex.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence a,int b,int c,int d){}public void onTextChanged(CharSequence a,int b,int c,int d){preview.run();}public void afterTextChanged(android.text.Editable e){}});preview.run();
        android.app.AlertDialog dialog=Aero.dialog(s.getContext()).setTitle(background?"Custom background color":"Custom accent color").setView(content).setPositiveButton("Done",null).setNegativeButton("Cancel",null).show();dialog.getButton(-1).setOnClickListener(v->{try{int color=ElevenColors.parse(hex.getText().toString());if(background)s.store.prefs.edit().putInt("eleven_solid_color",color).putString("eleven_background_mode","Solid color").apply();else ElevenColors.choose(s.store,color);dialog.dismiss();s.appearanceChanged();}catch(IllegalArgumentException invalid){hex.setError(invalid.getMessage());}});
    }
    private static String backgroundName(TenSettings s){if("Solid color".equals(s.store.prefs.getString("eleven_background_mode","Picture")))return "Solid color · "+ElevenColors.hex(s.store.prefs.getInt("eleven_solid_color",0xff0a263b));List<String> values=DesktopBackgrounds.current(s.store.prefs);int id=DesktopBackgrounds.stockId(values.get(0));return values.size()>1?"Slideshow · "+values.size()+" pictures":id>=0?DesktopBackgrounds.NAMES[id]:"Your picture";}
    private ElevenPersonalization(){}
}
