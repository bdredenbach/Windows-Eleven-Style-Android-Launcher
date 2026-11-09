package com.nthexperience.desktop;
/** Android-declared app categories, with explicit mappings for Nth built-ins. */
final class ElevenCategories {
 static String group(String id,int category){if(!id.startsWith("@")){switch(category){case 0:return "Games";case 1:case 2:return "Entertainment";case 3:return "Creativity";case 4:return "Social";case 5:return "News & weather";case 6:return "Travel";case 7:return "Productivity";case 8:return "Accessibility";default:return "Other";}}if(id.contains("powershell")||id.contains("terminal"))return "Developer Tools";if(id.equals("@photos"))return "Creativity";if(id.contains("media")||id.contains("music"))return "Entertainment";if(id.contains("puzzle")||id.contains("dice")||id.contains("spinner")||id.contains("solitaire")||id.contains("mines"))return "Games";if(id.contains("news")||id.contains("weather"))return "News & weather";if(id.contains("notes")||id.contains("nota")||id.contains("sticky")||id.contains("calendar")||id.contains("files")||id.contains("computer")||id.contains("browser"))return "Productivity";return "Utilities & Tools";}
 private ElevenCategories(){}
}
