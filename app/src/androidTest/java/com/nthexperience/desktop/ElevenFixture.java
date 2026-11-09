package com.nthexperience.desktop;
import android.app.Instrumentation;import android.content.Intent;import android.net.Uri;import android.os.*;import android.provider.DocumentsContract;import java.io.*;import java.util.UUID;

/** Wait for the separate fixture UID to finish rebuilding before using an existing grant. */
final class ElevenFixture {
 static Uri grant(Instrumentation test)throws Exception{String token=UUID.randomUUID().toString();shell(test,"am start -n com.nthexperience.desktop.fixture/.GrantActivity --ez reset true --es receipt "+token);long end=SystemClock.uptimeMillis()+12000;boolean complete=false;while(SystemClock.uptimeMillis()<end){if(token.equals(shell(test,"run-as com.nthexperience.desktop.fixture cat files/grant-receipt.txt"))){complete=true;break;}Thread.sleep(80);}if(!complete)throw new AssertionError("Separate fixture did not finish rebuilding");Uri tree=DocumentsContract.buildTreeDocumentUri("com.nthexperience.desktop.fixture.documents","root:");test.getTargetContext().getContentResolver().takePersistableUriPermission(tree,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);return tree;}
 private static String shell(Instrumentation test,String command)throws Exception{try(ParcelFileDescriptor fd=test.getUiAutomation().executeShellCommand(command);InputStream in=new ParcelFileDescriptor.AutoCloseInputStream(fd);ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[1024];int n;while((n=in.read(b))!=-1)out.write(b,0,n);return out.toString("UTF-8");}}
 private ElevenFixture(){}
}
