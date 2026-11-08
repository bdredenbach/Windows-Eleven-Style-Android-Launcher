package com.nthexperience.desktop;

import java.util.*;

/** Typed, staged Explorer choices shared by Folder Options, View and actual rendering. */
final class ElevenExplorerOptions {
    static final String[] VIEWS={"Extra large icons","Large icons","Medium icons","Small icons","List","Details","Tiles","Content"};
    static final String[] FILTERS={"All file types","Word","Excel","PowerPoint","Images","Videos","PDF"};
    boolean thumbnails=true,hidden,compact,checks,extensions=true,homeRecent=true,homeFolders=true,keepHistory=true,matchCase,searchHidden,doubleClick,newWindow,applyViews;
    String openTo="Home",view="Details";
    ElevenExplorerOptions(){}
    ElevenExplorerOptions(Map<String,?> map){
        thumbnails=bool(map,"explorer_thumbnails",true);hidden=bool(map,"explorer_hidden",false);compact=bool(map,"explorer_compact",false);checks=bool(map,"explorer_item_checks",false);extensions=bool(map,"explorer_extensions",true);
        homeRecent=bool(map,"explorer_home_recent",true);homeFolders=bool(map,"explorer_home_folders",true);keepHistory=bool(map,"recent_items_enabled",true);matchCase=bool(map,"explorer_match_case",false);searchHidden=bool(map,"explorer_search_hidden",false);doubleClick=bool(map,"explorer_double_click",false);newWindow=bool(map,"explorer_new_window",false);
        openTo="This PC".equals(map.get("explorer_open_to"))?"This PC":"Home";Object v=map.get("explorer_view_mode");view=v instanceof String&&Arrays.asList(VIEWS).contains(v)?(String)v:bool(map,"explorer_details",true)?"Details":"Tiles";
    }
    Map<String,Object> values(){Map<String,Object> m=new LinkedHashMap<>();m.put("explorer_thumbnails",thumbnails);m.put("explorer_hidden",hidden);m.put("explorer_compact",compact);m.put("explorer_item_checks",checks);m.put("explorer_extensions",extensions);m.put("explorer_home_recent",homeRecent);m.put("explorer_home_folders",homeFolders);m.put("recent_items_enabled",keepHistory);m.put("explorer_match_case",matchCase);m.put("explorer_search_hidden",searchHidden);m.put("explorer_double_click",doubleClick);m.put("explorer_new_window",newWindow);m.put("explorer_open_to",openTo);m.put("explorer_view_mode",view);m.put("explorer_details",view.equals("Details"));return m;}
    private static boolean bool(Map<String,?> m,String key,boolean fallback){return m.get(key) instanceof Boolean?(Boolean)m.get(key):fallback;}
    static String validFilter(String value){return Arrays.asList(FILTERS).contains(value)?value:FILTERS[0];}
    static boolean filter(String type,String name,String mime,boolean directory){
        if(directory||type.equals(FILTERS[0]))return true;
        String n=name==null?"":name.toLowerCase(Locale.ROOT),m=mime==null?"":mime.toLowerCase(Locale.ROOT);int dot=n.lastIndexOf('.');String ext=dot<0?"":n.substring(dot+1);
        switch(type){
            case "Word":return m.contains("word")||Arrays.asList("doc","docx","docm","dot","dotx","odt").contains(ext);
            case "Excel":return m.contains("excel")||m.contains("spreadsheet")||Arrays.asList("xls","xlsx","xlsm","xlsb","ods").contains(ext);
            case "PowerPoint":return m.contains("powerpoint")||m.contains("presentation")||Arrays.asList("ppt","pptx","pptm","pps","ppsx","odp").contains(ext);
            case "Images":return m.startsWith("image/")||Arrays.asList("png","jpg","jpeg","webp","gif","bmp","heic","heif","avif").contains(ext);
            case "Videos":return m.startsWith("video/")||Arrays.asList("mp4","mkv","webm","avi","mov","m4v","3gp").contains(ext);
            case "PDF":return m.equals("application/pdf")||ext.equals("pdf");default:return true;
        }
    }
    static String displayName(String name,boolean directory,boolean extensions){int dot=name.lastIndexOf('.');return !directory&&!extensions&&dot>0?name.substring(0,dot):name;}
}
