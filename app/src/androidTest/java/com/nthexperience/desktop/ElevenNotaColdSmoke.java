package com.nthexperience.desktop;
import android.app.*;
import android.content.*;
import android.os.*;

/** A separate process after force-stop reopens scoped caches without a saved Activity bundle. */
public final class ElevenNotaColdSmoke extends TenCommandsSmoke {
 @Override public void onStart(){Bundle out=new Bundle();try{
  SharedPreferences fixture=getTargetContext().getSharedPreferences("nota-native-fixture",0);check(android.os.Process.myPid()!=fixture.getInt("pid",-1),"Cold check runs in a new process");
  store=new DesktopStore(getTargetContext());home=(MainActivity)startActivitySync(new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(getTargetContext(),MainActivity.class)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));root=home.getWindow().getDecorView();
  await(()->TenSmoke.description(root,"Start menu")!=null,"Shell ready");
  ui(()->{for(String id:new String[]{"a","b","private"}){desks().select(fixture.getString(id,""));home.open("@nota");TenNotaPanel p=panel();if(id.equals("private"))check(p.privateDraft&&p.input.length()==0&&p.lastReply.isEmpty(),"Private cold restore is blank");else{check(p.input.getText().toString().equals(id.equals("a")?ElevenNotaSmoke.longA():ElevenNotaSmoke.longB()),"Independent full draft survives process exit");check(p.importedDraft==id.equals("a")&&p.lastReply.equals(id.equals("a")?"Reply A":"Reply B"),"Origin/reply survives process exit");}check(p.pending.isEmpty()&&p.actionChoices.getChildCount()==0,"Cold restore never replays actions");}});
  out.putString("stream","PASS: new-process scoped long drafts, origin/reply ownership and private placeholder, without command replay\n");finish(Activity.RESULT_OK,out);
 }catch(Throwable e){out.putString("stream","FAIL: "+android.util.Log.getStackTraceString(e));finish(Activity.RESULT_CANCELED,out);}}
}
