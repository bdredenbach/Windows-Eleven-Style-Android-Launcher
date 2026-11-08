package com.nthexperience.desktop;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ElevenExplorerOptionsTest {
    @Test public void fileFiltersRespectMimeExtensionsAndNavigableFolders(){
        String[][] data={{"Word","Report.DOCX","application/octet-stream"},{"Excel","Book","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},{"PowerPoint","Talk.pptx","application/octet-stream"},{"Images","photo.AVIF","application/octet-stream"},{"Videos","film","video/mp4"},{"PDF","Paper.PDF","application/octet-stream"}};
        for(String[] a:data){assertTrue(ElevenExplorerOptions.filter(a[0],a[1],a[2],false));assertTrue(ElevenExplorerOptions.filter(a[0],"Folder","inode/directory",true));assertFalse(ElevenExplorerOptions.filter(a[0],"plain.txt","text/plain",false));}
        assertEquals("All file types",ElevenExplorerOptions.validFilter("bad"));
    }
    @Test public void stagedChangesDoNotMutateOriginalValuesAndRoundTrip(){
        Map<String,Object> persisted=new HashMap<>();persisted.put("explorer_view_mode","Content");persisted.put("explorer_open_to","This PC");
        ElevenExplorerOptions options=new ElevenExplorerOptions(persisted);options.hidden=true;options.matchCase=true;options.extensions=false;
        assertFalse(persisted.containsKey("explorer_hidden"));
        ElevenExplorerOptions restored=new ElevenExplorerOptions(options.values());assertTrue(restored.hidden&&restored.matchCase);assertFalse(restored.extensions);assertEquals("Content",restored.view);assertEquals("This PC",restored.openTo);
        assertEquals("archive.tar",ElevenExplorerOptions.displayName("archive.tar.gz",false,false));assertEquals(".nomedia",ElevenExplorerOptions.displayName(".nomedia",false,false));assertEquals("folder.ext",ElevenExplorerOptions.displayName("folder.ext",true,false));
    }
}
