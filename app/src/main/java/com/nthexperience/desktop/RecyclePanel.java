package com.nthexperience.desktop;

import android.content.Context;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;
import java.util.concurrent.*;

/** Virtual recovery folder over the verified bin, not a raw directory of internal payloads. */
final class RecyclePanel extends VistaShell {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();private final Handler main=new Handler(Looper.getMainLooper());private final Runnable changed;private final DocumentOps.Cancel cancel=new DocumentOps.Cancel();
    private static final String[] COLUMNS={"Name","Original location","Date deleted","Size","Type","Date modified"};
    private static final String[] VIEWS={"Extra large icons","Large icons","Medium icons","Small icons","List","Details","Tiles","Content"};
    private static final float[] WEIGHTS={.25f,.23f,.15f,.09f,.12f,.16f};
    private List<JSONObject> items=new ArrayList<>();
    private final Set<String> selection=new LinkedHashSet<>();
    private String selected="",sort="Date deleted",view="Details";
    private boolean closed,busy,loaded,refreshPending,descending=true;
    private final TextView restoreButton,emptyButton;
    private final View classicMenu;
    private LinearLayout tenRibbonTools;
    private View tenRibbon;
    private TextView restoreSelection,deleteSelection;
    private final Map<TextView,LinearLayout> ribbonCommands=new LinkedHashMap<>();
    private int viewWidth;private boolean checks=true,compact,extensions=true,hidden=true,detailsPane,previewPane;private int previewGeneration;

    RecyclePanel(Context context,DesktopStore store,Actions actions,Runnable changed){
        super(context,store,actions,"Search Recycle Bin");
        this.changed=changed;checks=store.prefs.getBoolean("recycle_checks",true);compact=store.prefs.getBoolean("recycle_compact",false);extensions=store.prefs.getBoolean("recycle_extensions",true);hidden=store.prefs.getBoolean("recycle_hidden",true);
        addCommand("Organize ▾",this::organize);
        addCommand("Views ▾",this::views);
        emptyButton=addCommand("Empty Recycle Bin",this::confirmEmpty);
        restoreButton=addCommand("Restore all items",this::restoreAll);
        classicMenu=explorerChrome(this::menu);
        classicMenu.setVisibility(GONE);
        useCompactExplorerToolbar();
        addTenRibbon();
        content.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{
            if(r-l!=viewWidth){viewWidth=r-l;if(!view.equals("Details"))render();}
        });
        setOnDragListener((v,event)->{
            if(!(event.getLocalState() instanceof FileDrag))return false;
            if(event.getAction()==DragEvent.ACTION_DROP){
                FileDrag drag=(FileDrag)event.getLocalState();
                main.post(()->drag.explorer.dropInRecycleBin(drag));
            }
            return true;
        });
        start("recycle");
        refresh();
    }
    private TextView addCommand(String label,Runnable action){
        TextView button=command(label,action);
        button.setMaxLines(2);
        button.setPadding(dp(2),0,dp(2),0);
        commands.addView(button,new LayoutParams(0,-1,label.length()>10?1.45f:1f));
        return button;
    }
    private void addTenRibbon(){
        restoreSelection=command("Restore selected items",this::restoreSelected);
        LinearLayout ribbon=Aero.column(getContext());tenRibbon=ribbon;ribbon.setBackgroundColor(0xfff5f6f7);ribbon.setContentDescription("Recycle Bin command bar");
        HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setHorizontalScrollBarEnabled(false);tenRibbonTools=new LinearLayout(getContext());tenRibbonTools.setGravity(Gravity.CENTER_VERTICAL);scroll.addView(tenRibbonTools);ribbon.addView(scroll,new LayoutParams(-1,dp(48)));
        ((View)commands.getParent()).setVisibility(GONE);addView(ribbon,indexOfChild((View)address.getParent())+1,new LayoutParams(-1,-2));recycleRibbon("Manage");
        LinearLayout tab=new LinearLayout(getContext());tab.setGravity(Gravity.CENTER_VERTICAL);tab.setBackgroundColor(TenAppearance.dark(getContext())?0xff20232d:0xffe5e7ef);TextView name=command("Recycle Bin",()->{});name.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);tab.addView(name,new LayoutParams(0,dp(40),1));TextView more=command("+",()->actions.open("@files"));more.setContentDescription("Open File Explorer");tab.addView(more,new LayoutParams(dp(44),dp(40)));addView(tab,0,new LayoutParams(-1,dp(40)));
        LinearLayout location=(LinearLayout)address.getParent();location.setBackgroundColor(TenAppearance.dark(getContext())?0xff242632:0xfff6f6fb);address.setBackground(ElevenSurface.round(getContext(),TenAppearance.dark(getContext())?0xff2d303c:0xffeaeaf0,4));for(TextView nav:new TextView[]{back,forward,up})nav.setBackground(TenStyle.hover(getContext(),android.graphics.Color.TRANSPARENT));
    }
    private void recycleRibbon(String tab){
        // The command instances retain their enabled state while moving between ribbon tabs.
        for(TextView button:new TextView[]{emptyButton,restoreButton,restoreSelection})if(button.getParent() instanceof android.view.ViewGroup)((android.view.ViewGroup)button.getParent()).removeView(button);
        tenRibbonTools.removeAllViews();ribbonCommands.clear();
        if(tab.equals("View")){
            recycleCommand(command("Layout",this::views),"apps");recycleCommand(command("Sort by",()->menu("View")),"menu");recycleCommand(command("Refresh",this::refresh),"update");
        }else{
            recycleCommand(emptyButton,"delete");recycleCommand(command("Recycle Bin properties",this::properties),"document");recycleCommand(restoreButton,"update");recycleCommand(restoreSelection,"update");deleteSelection=command("Delete selected",this::confirmSelected);recycleCommand(deleteSelection,"delete");recycleCommand(command("Sort",()->menu("View")),"menu");recycleCommand(command("View",this::views),"apps");recycleCommand(command("More",()->menu("Edit")),"menu");
        }
        updateCommands();TenAppearance.apply(tenRibbon);
    }
    private void recycleCommand(TextView button,String icon){
        LinearLayout tile=new LinearLayout(getContext());tile.setPadding(dp(9),dp(2),dp(9),dp(2));tile.setGravity(Gravity.CENTER);tile.setBackground(Aero.selection(getContext()));TenStyle.Glyph glyph=new TenStyle.Glyph(getContext(),icon,0xff52616c);tile.addView(glyph,new LayoutParams(dp(20),dp(20)));button.setTag(R.id.ten_appearance_accent,null);button.setTextColor(Aero.INK);button.setTextSize(11);button.setGravity(Gravity.CENTER);button.setPadding(dp(6),0,dp(2),0);button.setMaxLines(1);tile.addView(button,new LayoutParams(-2,dp(44)));tile.setContentDescription("Recycle Bin "+button.getText()+" command");tile.setFocusable(true);tile.setOnClickListener(v->{if(button.isEnabled())button.performClick();});tenRibbonTools.addView(tile,new LayoutParams(-2,dp(48)));ribbonCommands.put(button,tile);
    }
    @Override void render(){
        if(closed)return;
        if(side.getChildCount()==0){sideLink("Home",()->actions.open("@files"));sideLink("Gallery",()->actions.open("@photos"));sideLink("Documents",()->actions.open("@documents"));sideLink("Pictures",()->actions.open("@pictures"));sideLink("This PC",()->actions.open("@computer"));sideLink("Desktop",()->actions.action("show-desktop"));}
        address.setText("Recycle Bin");LinearLayout column=Aero.column(getContext());List<JSONObject> shown=visibleItems();Set<String> visible=new HashSet<>();for(JSONObject item:shown)visible.add(item.optString("id"));if(loaded){selection.retainAll(visible);if(!visible.contains(selected))selected="";}updateCommands();
        shown.sort((a,b)->{int result=sort.equals("Size")?Long.compare(a.optLong("size"),b.optLong("size")):sort.equals("Date deleted")?Long.compare(a.optLong("date"),b.optLong("date")):sort.equals("Date modified")?Long.compare(a.optLong("modified"),b.optLong("modified")):(sort.equals("Original location")?a.optString("location"):sort.equals("Type")?type(a):a.optString("name")).compareToIgnoreCase(sort.equals("Original location")?b.optString("location"):sort.equals("Type")?type(b):b.optString("name"));return descending?-result:result;});
        LinearLayout listing=Aero.column(getContext());
        if(view.equals("Details")){
            LinearLayout header=new LinearLayout(getContext());if(checks){CheckBox all=Aero.check(getContext(),"",!shown.isEmpty()&&selection.containsAll(visible));all.setMinHeight(dp(32));all.setContentDescription("Select all recovery items");all.setOnClickListener(v->{if(all.isChecked())selection.addAll(visible);else selection.clear();render();});header.addView(all,new LayoutParams(dp(36),dp(36)));}
            for(int i=0;i<COLUMNS.length;i++){String name=COLUMNS[i];TextView label=command(name,()->sortBy(name));label.setTextSize(11);label.setContentDescription("Sort Recycle Bin by "+name);header.addView(label,new LayoutParams(0,dp(36),WEIGHTS[i]));}listing.addView(header);for(JSONObject item:shown)listing.addView(itemRow(item,true));
            HorizontalScrollView horizontal=new HorizontalScrollView(getContext());horizontal.setContentDescription("Recycle Bin details columns");horizontal.addView(listing,new HorizontalScrollView.LayoutParams(Math.max(viewWidth,dp(800)),-2));column.addView(horizontal);
        }else{
            int minimum=view.equals("Content")?400:view.equals("Tiles")?165:view.equals("List")?135:view.equals("Extra large icons")?140:view.equals("Large icons")?105:view.equals("Medium icons")?88:74;int count=Math.max(1,viewWidth/dp(minimum));LinearLayout row=null;
            for(int i=0;i<shown.size();i++){if(i%count==0){row=new LinearLayout(getContext());listing.addView(row);}row.addView(itemRow(shown.get(i),false),new LayoutParams(0,-2,1));}if(row!=null&&shown.size()%count!=0)for(int i=shown.size()%count;i<count;i++)row.addView(new View(getContext()),new LayoutParams(0,1,1));column.addView(listing);
        }
        if(shown.isEmpty()){TextView message=Aero.text(getContext(),loaded?query().isEmpty()?"Recycle Bin is empty":"No matching recovery items":"Loading Recycle Bin…",13,Aero.INK);Aero.pad(message,16);column.addView(message);}
        if(detailsPane||previewPane){JSONObject item=selected();LinearLayout pane=Aero.column(getContext());Aero.pad(pane,12);pane.setContentDescription("Recycle Bin "+(previewPane?"preview":"details")+" pane");pane.addView(Aero.text(getContext(),selection.size()>1?selection.size()+" selected items":item==null?"Select an item":item.optString("name"),15,Aero.INK));if(item!=null&&selection.size()==1){pane.addView(Aero.text(getContext(),metadataText(item),12,Aero.INK));if(previewPane&&item.optString("mime").startsWith("image/")&&!isFolder(item)){ImageView image=new ImageView(getContext());image.setAdjustViewBounds(true);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setContentDescription("Recovery image preview");pane.addView(image,new LayoutParams(-1,dp(170)));int generation=++previewGeneration;worker.execute(()->{android.graphics.Bitmap picture=null;try{android.graphics.BitmapFactory.Options options=new android.graphics.BitmapFactory.Options();options.inJustDecodeBounds=true;try(java.io.InputStream in=RecycleBin.preview(getContext(),item)){android.graphics.BitmapFactory.decodeStream(in,null,options);}int sample=1;while(options.outWidth/sample>700||options.outHeight/sample>700)sample*=2;options.inJustDecodeBounds=false;options.inSampleSize=sample;try(java.io.InputStream in=RecycleBin.preview(getContext(),item)){picture=android.graphics.BitmapFactory.decodeStream(in,null,options);}}catch(Exception|OutOfMemoryError ignored){}android.graphics.Bitmap ready=picture;main.post(()->{if(!closed&&generation==previewGeneration)image.setImageBitmap(ready);else if(ready!=null)ready.recycle();});});}}column.addView(pane);}
        status.setText(ElevenLabels.items(items.size())+" · "+selection.size()+" selected · "+android.text.format.Formatter.formatShortFileSize(getContext(),items.stream().mapToLong(i->i.optLong("size")).sum())+(busy?" · Working…":""));body(scrolling(column));
    }
    private List<JSONObject> visibleItems(){List<JSONObject> result=new ArrayList<>();for(JSONObject item:items)if((hidden||!item.optString("name").startsWith("."))&&(query().isEmpty()||item.optString("name").toLowerCase(Locale.getDefault()).contains(query())))result.add(item);return result;}
    private View itemRow(JSONObject item,boolean details){
        String id=item.optString("id");boolean gallery=!details&&view.endsWith("icons");LinearLayout row=new LinearLayout(getContext());row.setOrientation(gallery?VERTICAL:HORIZONTAL);row.setGravity(gallery?Gravity.CENTER:Gravity.CENTER_VERTICAL);row.setMinimumHeight(dp(gallery?90:compact?32:44));row.setPadding(dp(3),dp(3),dp(3),dp(3));row.setFocusable(true);row.setContentDescription("Recycle item: "+item.optString("name"));row.setTag(id);row.setBackground(selection.contains(id)?Aero.panel(getContext(),0xffe4f3ff,0xffc8e3f5,1,0xff89b3cf):TenStyle.appHover(getContext(),android.graphics.Color.TRANSPARENT));
        if(checks){CheckBox check=Aero.check(getContext(),"",selection.contains(id));check.setMinHeight(dp(30));check.setContentDescription("Select recovery item: "+item.optString("name"));check.setOnClickListener(v->{selected=id;if(check.isChecked())selection.add(id);else selection.remove(id);render();});row.addView(check,new LayoutParams(dp(36),dp(32)));}
        LinearLayout name=new LinearLayout(getContext());name.setOrientation(gallery?VERTICAL:HORIZONTAL);name.setGravity(gallery?Gravity.CENTER:Gravity.CENTER_VERTICAL);int size=details||view.equals("List")?18:view.equals("Extra large icons")?72:view.equals("Large icons")?48:view.equals("Small icons")?23:33;View icon=itemIcon(item,size);icon.setTag("recycle-item-icon");icon.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);name.addView(icon,new LayoutParams(dp(size),dp(size)));String label=item.optString("name");if(!extensions&&!isFolder(item)&&label.lastIndexOf('.')>0)label=label.substring(0,label.lastIndexOf('.'));TextView title=cell(label);if(gallery)title.setGravity(Gravity.CENTER);name.addView(title,new LayoutParams(gallery?-1:0,-2,gallery?0:1));row.addView(name,new LayoutParams(gallery?-1:0,-2,details?WEIGHTS[0]:gallery?0:1));
        if(details){String[] values={item.optString("location","Original folder"),date(item.optLong("date")),isFolder(item)||item.has("shortcut")?"":android.text.format.Formatter.formatShortFileSize(getContext(),item.optLong("size")),type(item),date(item.optLong("modified"))};for(int i=0;i<values.length;i++)row.addView(cell(values[i]),new LayoutParams(0,-2,WEIGHTS[i+1]));}else if(view.equals("Content")||view.equals("Tiles"))row.addView(cell(type(item)+" · "+date(item.optLong("date"))),new LayoutParams(0,-2,1));
        row.setOnClickListener(v->selectItem(item));row.setOnKeyListener((v,key,e)->{if(e.getAction()==KeyEvent.ACTION_DOWN&&e.isCtrlPressed()&&key==KeyEvent.KEYCODE_SPACE){selected=id;if(!selection.add(id))selection.remove(id);render();return true;}return false;});ContextMenus.bind(row,(x,y)->{if(!selection.contains(id))selectItem(item);DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Recycle item commands");menu.item("Restore selected items","recycle",!busy,this::restoreSelected);menu.item("Delete selected items permanently…","delete",!busy,this::confirmSelected);menu.item("Properties","control",()->itemProperties(item));ContextMenus.show(menu,row,x,y);});return row;
    }
    private boolean isFolder(JSONObject item){return android.provider.DocumentsContract.Document.MIME_TYPE_DIR.equals(item.optString("mime"))||item.optString("shortcut").startsWith("folder:")||FolderLinks.id(item.optString("shortcut"));}
    private String type(JSONObject item){if(item.has("shortcut"))return "Shortcut";if(isFolder(item))return "File folder";String name=item.optString("name");int dot=name.lastIndexOf('.');return dot>0&&dot<name.length()-1?name.substring(dot+1).toUpperCase(Locale.ROOT)+" file":item.optString("mime","File");}
    private String date(long value){return value>0?java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT).format(new Date(value)):"Not retained";}
    private String metadataText(JSONObject item){return "Type: "+type(item)+"\nOriginal location: "+item.optString("location","Original folder")+"\nDeleted: "+date(item.optLong("date"))+"\nModified: "+date(item.optLong("modified"))+"\nSize: "+android.text.format.Formatter.formatFileSize(getContext(),item.optLong("size"));}
    private View itemIcon(JSONObject item,int size){
        if(item.has("shortcut")&&getContext() instanceof MainActivity)
            return ((MainActivity)getContext()).icon(item.optString("shortcut"),size);
        return new AeroIcon(getContext(),VistaExplorer.icon(item.optString("mime"),isFolder(item)));
    }
    private TextView cell(String value){
        TextView cell=Aero.text(getContext(),value,11,Aero.INK);
        cell.setMaxLines(2);
        cell.setEllipsize(android.text.TextUtils.TruncateAt.END);
        cell.setPadding(dp(3),dp(3),dp(2),dp(3));
        return cell;
    }
    private void selectItem(JSONObject item){selected=item.optString("id");selection.clear();selection.add(selected);render();}
    private List<JSONObject> selectedItems(){List<JSONObject> values=new ArrayList<>();for(JSONObject item:items)if(selection.contains(item.optString("id")))values.add(item);return values;}
    private void updateCommands(){
        if(restoreSelection!=null)restoreSelection.setEnabled(!busy&&!selectedItems().isEmpty());if(deleteSelection!=null)deleteSelection.setEnabled(!busy&&!selectedItems().isEmpty());
        for(TextView button:new TextView[]{restoreButton,emptyButton}){
            button.setEnabled(!busy&&!items.isEmpty());
            button.setAlpha(button.isEnabled()?1f:.5f);
        }
        for(Map.Entry<TextView,LinearLayout> entry:ribbonCommands.entrySet()){entry.getValue().setEnabled(entry.getKey().isEnabled());entry.getValue().setAlpha(entry.getKey().isEnabled()?1f:.5f);}
    }
    private void sortBy(String name){descending=sort.equals(name)?!descending:name.equals("Date deleted");sort=name;render();}
    private JSONObject selected(){for(JSONObject item:items)if(item.optString("id").equals(selected))return item;return null;}
    private void restoreSelected(){batch(true);}
    private void batch(boolean restore){batch(restore,selectedItems());}
    private void batch(boolean restore,List<JSONObject> snapshot){if(snapshot.isEmpty()||busy)return;operation(()->{int done=0,failed=0;for(JSONObject item:snapshot){cancel.check();try{if(restore)RecycleBin.restore(getContext(),item,cancel);else RecycleBin.remove(getContext(),item);done++;}catch(Exception error){failed++;}}return done+(restore?" items restored":" recovery copies removed")+(failed>0?"; "+failed+" could not be processed and remain in the bin":"");});}
    private void confirmSelected(){List<JSONObject> snapshot=selectedItems();if(snapshot.isEmpty()||busy)return;StringBuilder names=new StringBuilder();for(int i=0;i<Math.min(6,snapshot.size());i++)names.append(snapshot.get(i).optString("name")).append("\n");Aero.dialog(getContext()).setTitle("Delete "+snapshot.size()+" selected recovery copies?").setMessage(names+"This cannot be undone.").setPositiveButton("Delete",(d,w)->batch(false,snapshot)).setNegativeButton("Cancel",null).show();}
    private void restoreItem(JSONObject item){operation(()->{RecycleBin.restore(getContext(),item,cancel);return "Item restored";});}
    private void restoreAll(){if(items.isEmpty()||busy)return;List<JSONObject> snapshot=new ArrayList<>(items);operation(()->{int done=0;for(JSONObject item:snapshot){RecycleBin.restore(getContext(),item,cancel);done++;}return done+" items restored";});}
    private void confirmRemove(JSONObject item){Aero.dialog(getContext()).setTitle("Delete recovery copy?").setMessage(item.optString("name")+". This cannot be undone.").setPositiveButton("Delete",(d,w)->operation(()->{RecycleBin.remove(getContext(),item);return "Recovery copy removed";})).setNegativeButton("Cancel",null).show();}
    private void confirmEmpty(){
        if(!busy)confirmEmpty(getContext(),()->operation(()->{RecycleBin.empty(getContext());return "Recycle Bin emptied";}));
    }
    static void confirmEmpty(Context context,Runnable empty){
        Aero.dialog(context).setTitle("Empty Recycle Bin?")
                .setMessage("Permanently remove all recovery copies? This cannot be undone.")
                .setPositiveButton("Empty",(d,w)->empty.run()).setNegativeButton("Cancel",null).show();
    }
    private void itemProperties(JSONObject item){Aero.dialog(getContext()).setTitle(item.optString("name")).setMessage(metadataText(item)+"\n"+(item.optBoolean("deleted")?"Original deletion confirmed":"Recovery copy; original deletion unconfirmed")+"\nRestore keeps both names if the destination already exists.").setPositiveButton("OK",null).show();}
    void properties(){LinearLayout body=column();body.addView(Aero.text(getContext(),"Maximum bin size (new deletions)",14,Aero.INK));Spinner capacity=new Spinner(getContext());String[] choices={"100 MB","250 MB","500 MB","1 GB","2 GB"};int[] values={100,250,500,1024,2048};capacity.setAdapter(new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,choices));int current=store.prefs.getInt("recycle_capacity_mb",250);for(int i=0;i<values.length;i++)if(values[i]==current)capacity.setSelection(i);capacity.setContentDescription("Recycle Bin maximum size");body.addView(capacity);CheckBox icon=Aero.check(getContext(),"Show Recycle Bin on desktop",store.pinned("@recycle"));body.addView(icon);body.addView(Aero.text(getContext(),"Up to 100 MB per file or folder and 200 recovery items. Existing copies are kept when the limit is lowered. Items from locations without recovery support stay in place unless you explicitly choose permanent deletion.",12,Aero.INK));Aero.dialog(getContext()).setTitle("Recycle Bin Properties").setView(body).setPositiveButton("Apply",(d,w)->{store.prefs.edit().putInt("recycle_capacity_mb",values[capacity.getSelectedItemPosition()]).apply();if(icon.isChecked()!=store.pinned("@recycle"))store.toggle("@recycle");changed.run();render();}).setNegativeButton("Cancel",null).show();}
    private void show(DesktopContextMenu menu){View anchor=tenRibbon==null?commands:tenRibbon;ContextMenus.centered(anchor,(x,y)->ContextMenus.show(menu,anchor,x,y));}
    private void organize(){
        DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Recycle Bin Organize");
        menu.submenu("Layout","appearance",this::layoutMenu);
        menu.separator();
        menu.item("Properties","control",this::properties);
        show(menu);
    }
    private void layoutMenu(){
        DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Recycle Bin Layout");
        menu.checked("Menu bar",classicMenu.getVisibility()==VISIBLE,()->classicMenu.setVisibility(classicMenu.getVisibility()==VISIBLE?GONE:VISIBLE));
        menu.checked("Navigation pane",navigationPaneVisible(),this::toggleNavigationPane);
        show(menu);
    }
    private void views(){
        DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Recycle Bin Views");
        for(String label:VIEWS)menu.checked(label,view.equals(label),()->{view=label;render();});
        menu.separator();menu.submenu("Show","appearance",this::showOptions);show(menu);
    }
    private void showOptions(){DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Recycle Bin Show");menu.checked("Navigation pane",navigationPaneVisible(),this::toggleNavigationPane);menu.checked("Compact view",compact,()->{compact=!compact;saveOptions();});menu.checked("Item check boxes",checks,()->{checks=!checks;saveOptions();});menu.checked("File name extensions",extensions,()->{extensions=!extensions;saveOptions();});menu.checked("Hidden items",hidden,()->{hidden=!hidden;saveOptions();});menu.checked("Details pane",detailsPane,()->{detailsPane=!detailsPane;previewPane=false;render();});menu.checked("Preview pane",previewPane,()->{previewPane=!previewPane;detailsPane=false;render();});show(menu);}
    private void saveOptions(){store.prefs.edit().putBoolean("recycle_checks",checks).putBoolean("recycle_compact",compact).putBoolean("recycle_extensions",extensions).putBoolean("recycle_hidden",hidden).apply();render();}
    private void menu(String label){
        DesktopContextMenu menu=new DesktopContextMenu(getContext(),"Recycle Bin "+label+" menu");
        JSONObject item=selected();
        switch(label){
            case "File":
                menu.item("Restore","recycle",item!=null&&!busy,()->restoreItem(item));
                menu.item("Delete permanently…","delete",item!=null&&!busy,()->confirmRemove(item));
                menu.item("Properties","control",()->{if(item==null)properties();else itemProperties(item);});
                menu.separator();
                menu.item("Close","close",this::closeWindow);
                break;
            case "Edit":
                menu.item("Select all","checked",()->{for(JSONObject row:visibleItems())selection.add(row.optString("id"));render();});menu.item("Select none","empty",()->{selection.clear();selected="";render();});menu.item("Invert selection","checked",()->{for(JSONObject row:visibleItems()){String id=row.optString("id");if(!selection.add(id))selection.remove(id);}render();});menu.item("Delete selected items…","delete",!selection.isEmpty()&&!busy,this::confirmSelected);
                break;
            case "View":
                menu.submenu("Views","appearance",this::views);
                for(String name:COLUMNS)menu.checked("Sort by "+name,sort.equals(name),()->sortBy(name));
                menu.item("Refresh","refresh",this::refresh);
                break;
            case "Tools":
                menu.item("Recycle Bin Properties","control",this::properties);
                break;
            case "Help":
                menu.item("Recycle Bin Help","control",()->Aero.dialog(getContext()).setTitle("Recycle Bin")
                        .setMessage("Select an item to restore it to its original location. Hold an item for individual commands. Empty Recycle Bin permanently removes every recovery item.")
                        .setPositiveButton("OK",null).show());
                break;
        }
        show(menu);
    }
    private void closeWindow(){
        for(View view=this;view.getParent() instanceof View;view=(View)view.getParent())
            if(view.getParent() instanceof DesktopWindow){((DesktopWindow)view.getParent()).closeWindow();return;}
    }
    interface Job<T>{T run()throws Exception;}
    private <T> void run(Job<T> job,java.util.function.Consumer<T> done){if(busy||closed)return;busy=true;updateCommands();worker.execute(()->{try{T result=job.run();main.post(()->{busy=false;if(!closed){done.accept(result);refreshIfPending();}});}catch(Exception ex){main.post(()->{busy=false;if(!closed){Toast.makeText(getContext(),ex.getMessage()==null?"Operation unavailable":ex.getMessage(),Toast.LENGTH_LONG).show();DesktopSounds.play(getContext(),"error");loaded=true;render();refreshIfPending();}});}});}
    private void operation(Job<String> job){run(job,text->{Toast.makeText(getContext(),text,Toast.LENGTH_LONG).show();DesktopSounds.play(getContext(),"complete");changed.run();refresh();});}
    private void refreshIfPending(){if(refreshPending&&!busy)refresh();}
    void refresh(){if(busy){refreshPending=true;return;}refreshPending=false;run(()->RecycleBin.list(getContext()),values->{items=values;loaded=true;render();});}
    @Override JSONObject extraState(){JSONObject value=new JSONObject();GadgetLayout.put(value,"sort",sort);GadgetLayout.put(value,"descending",descending);GadgetLayout.put(value,"selected",selected);GadgetLayout.put(value,"selection",new JSONArray(selection));GadgetLayout.put(value,"detailsPane",detailsPane);GadgetLayout.put(value,"previewPane",previewPane);GadgetLayout.put(value,"view",view);GadgetLayout.put(value,"menuBar",classicMenu.getVisibility()==VISIBLE);return value;}
    @Override void restoreExtra(JSONObject state){if(state!=null){sort=state.optString("sort","Date deleted");descending=state.optBoolean("descending",true);selected=state.optString("selected");selection.clear();JSONArray ids=state.optJSONArray("selection");if(ids!=null)for(int i=0;i<Math.min(200,ids.length());i++){String id=ids.optString(i);try{java.util.UUID.fromString(id);selection.add(id);}catch(IllegalArgumentException ignored){}}else if(!selected.isEmpty())selection.add(selected);detailsPane=state.optBoolean("detailsPane");previewPane=state.optBoolean("previewPane");String saved=state.optString("view");if(Arrays.asList(VIEWS).contains(saved))view=saved;classicMenu.setVisibility(state.optBoolean("menuBar")?VISIBLE:GONE);}}
    @Override void showFolders(){toggleNavigationPane();}
    @Override void goUp(){actions.open("@computer");}
    @Override void close(){if(closed)return;closed=true;cancel.cancel();worker.shutdown();main.removeCallbacksAndMessages(null);}
}
