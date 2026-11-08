package com.nthexperience.desktop;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.function.*;

/** General/View/Search dialog: edits stay local until Apply or OK. */
final class ElevenFolderOptions {
    private final Context context;private final DesktopStore store;private final Consumer<ElevenExplorerOptions> apply;
    private ElevenExplorerOptions draft;private final LinearLayout tabs,pages;private final AlertDialog dialog;private int page;
    ElevenFolderOptions(Context c,DesktopStore store,Consumer<ElevenExplorerOptions> apply){
        context=c;this.store=store;this.apply=apply;draft=new ElevenExplorerOptions(store.prefs.getAll());
        LinearLayout root=Aero.column(c);root.setPadding(dp(12),dp(4),dp(12),dp(4));tabs=new LinearLayout(c);root.addView(tabs,new LinearLayout.LayoutParams(-1,dp(44)));ScrollView scroll=new ScrollView(c);pages=Aero.column(c);scroll.addView(pages);root.addView(scroll,new LinearLayout.LayoutParams(-1,dp(Math.max(140,Math.min(360,(int)(c.getResources().getDisplayMetrics().heightPixels/c.getResources().getDisplayMetrics().density)-190)))));
        dialog=Aero.dialog(c).setTitle("Folder Options").setView(root).setPositiveButton("OK",null).setNegativeButton("Cancel",null).setNeutralButton("Apply",null).create();
        dialog.setOnShowListener(d->{dialog.getButton(-1).setOnClickListener(v->{commit();dialog.dismiss();});dialog.getButton(-3).setOnClickListener(v->commit());});render();
    }
    void show(){dialog.show();}
    private int dp(int n){return Aero.dp(context,n);}
    private void commit(){apply.accept(draft);draft=new ElevenExplorerOptions(store.prefs.getAll());render();}
    private void render(){
        tabs.removeAllViews();pages.removeAllViews();String[] names={"General","View","Search"};for(int i=0;i<names.length;i++){int index=i;TextView tab=TenStyle.command(context,names[i],()->{page=index;render();});tab.setGravity(Gravity.CENTER);tab.setContentDescription("Folder Options "+names[i]+" tab");if(page==i)tab.setBackgroundColor(TenStyle.mix(TenAppearance.background(context),TenStyle.accent(context),.18f));tabs.addView(tab,new LinearLayout.LayoutParams(0,-1,1));}
        if(page==0){
            select("Open File Explorer to",new String[]{"Home","This PC"},draft.openTo,v->draft.openTo=v);
            heading("Browse folders");check("Open folders in their own window",draft.newWindow,v->draft.newWindow=v);
            heading("Click items as follows");select("Open items with",new String[]{"Single click","Double click"},draft.doubleClick?"Double click":"Single click",v->draft.doubleClick=v.equals("Double click"));
            heading("Privacy");check("Keep recently opened file and folder history",draft.keepHistory,v->draft.keepHistory=v);check("Show recently opened files in Home",draft.homeRecent,v->draft.homeRecent=v);check("Show recently opened folders in Home",draft.homeFolders,v->draft.homeFolders=v);
            pages.addView(TenStyle.command(context,"Clear File Explorer history",()->Aero.dialog(context).setTitle("Clear Explorer history?").setMessage("Remove the recent file and folder list on this device. Files and saved searches are kept.").setPositiveButton("Clear",(d,n)->store.prefs.edit().remove("recent_files").apply()).setNegativeButton("Cancel",null).show()));
        }else if(page==1){
            heading("Folder views");select("Default view",ElevenExplorerOptions.VIEWS,draft.view,v->draft.view=v);
            pages.addView(TenStyle.command(context,draft.applyViews?"View will apply to all folders on Apply":"Apply this view to all folders",()->{draft.applyViews=true;render();}));
            heading("Advanced settings");check("Show picture thumbnails",draft.thumbnails,v->draft.thumbnails=v);check("Show hidden files and folders",draft.hidden,v->draft.hidden=v);check("Compact view",draft.compact,v->draft.compact=v);check("Item check boxes",draft.checks,v->draft.checks=v);check("File name extensions",draft.extensions,v->draft.extensions=v);
        }else{
            heading("When searching names");check("Match case",draft.matchCase,v->draft.matchCase=v);check("Include hidden items in search results",draft.searchHidden,v->draft.searchHidden=v);
            TextView note=Aero.text(context,"Search filters names in the current folder or library. Use Filter for file types. Search does not read document contents or folders without access.",12,TenAppearance.ink(context));note.setPadding(dp(6),dp(12),dp(6),dp(12));pages.addView(note);
        }
        pages.addView(TenStyle.command(context,"Restore Defaults",()->{draft=new ElevenExplorerOptions();render();}));TenAppearance.apply(pages);
    }
    private void heading(String text){TextView h=Aero.text(context,text,14,TenAppearance.ink(context));h.setPadding(dp(4),dp(14),0,dp(8));Aero.bold(h);pages.addView(h);}
    private void check(String title,boolean value,Consumer<Boolean> change){CheckBox box=Aero.check(context,title,value);box.setContentDescription(title);box.setMinHeight(dp(44));box.setOnCheckedChangeListener((v,checked)->change.accept(checked));pages.addView(box,new LinearLayout.LayoutParams(-1,-2));}
    private void select(String title,String[] choices,String current,Consumer<String> change){TextView label=Aero.text(context,title,13,TenAppearance.ink(context));label.setPadding(dp(4),dp(10),0,0);pages.addView(label);Spinner spinner=new Spinner(context);spinner.setContentDescription(title);ArrayAdapter<String> adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,choices);spinner.setAdapter(adapter);for(int i=0;i<choices.length;i++)if(choices[i].equals(current))spinner.setSelection(i);spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int i,long id){change.accept(choices[i]);}});pages.addView(spinner,new LinearLayout.LayoutParams(-1,dp(44)));}
}
