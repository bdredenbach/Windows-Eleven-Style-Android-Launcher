package com.nthexperience.desktop;

import android.content.Context;
import java.io.File;
import java.util.*;

/** Enumerated app-cache cleanup; changed files and all personal folders are left alone. */
final class ElevenCleanup {
    static final class Entry {final File file;final long size,modified;final String canonical;Entry(File f)throws java.io.IOException{file=f;size=f.length();modified=f.lastModified();canonical=f.getCanonicalPath();}}
    static final class Snapshot {final List<Entry> files=new ArrayList<>();long bytes;boolean limited;}
    static Snapshot scan(Context c){Snapshot s=new Snapshot();walk(c.getCacheDir(),c.getCacheDir(),s,0);File external=c.getExternalCacheDir();if(external!=null)walk(external,external,s,0);return s;}
    private static void walk(File root,File directory,Snapshot s,int depth){if(depth>8||s.files.size()>=5000){s.limited=true;return;}File[] files=directory.listFiles();if(files==null)return;for(File file:files)try{if(!file.getCanonicalPath().startsWith(root.getCanonicalPath()+File.separator))continue;if(file.isDirectory())walk(root,file,s,depth+1);else if(file.isFile()){Entry e=new Entry(file);s.files.add(e);s.bytes+=e.size;}if(s.files.size()>=5000){s.limited=true;return;}}catch(java.io.IOException ignored){}}
    static int removeUnchanged(Snapshot snapshot){int removed=0;for(Entry entry:snapshot.files)try{if(entry.file.getCanonicalPath().equals(entry.canonical)&&entry.file.isFile()&&entry.file.length()==entry.size&&entry.file.lastModified()==entry.modified&&entry.file.delete())removed++;}catch(java.io.IOException ignored){}return removed;}
    private ElevenCleanup(){}
}
