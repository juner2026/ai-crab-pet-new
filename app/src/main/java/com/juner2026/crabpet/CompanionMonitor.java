package com.juner2026.crabpet;

import android.content.*;
import android.os.*;
import java.util.*;

public class CompanionMonitor {
 public interface Listener { void onLine(String line, int heat); }
 private final Context context; private final Listener listener; private final Handler handler=new Handler(Looper.getMainLooper()); private final EmotionEngine emotion=new EmotionEngine();
 private String lastApp="",lastNotification=""; private long lastWater; private int switches; private long switchWindow;
 public CompanionMonitor(Context c,Listener l){context=c;listener=l;}
 public void start(){lastWater=System.currentTimeMillis();switchWindow=lastWater;handler.post(tick);handler.postDelayed(decay,30000);}
 public void stop(){handler.removeCallbacksAndMessages(null);}
 public void touched(){emotion.touch();}
 private final Runnable decay=new Runnable(){public void run(){emotion.decay();handler.postDelayed(this,30000);}};
 private final Runnable tick=new Runnable(){public void run(){
   long now=System.currentTimeMillis(); String app=ForegroundAppMonitor.current(context);
   if(!app.isEmpty()&&!app.equals(lastApp)){lastApp=app;switches++;String line=mapApp(app);if(line!=null)emit(line,2);}
   if(now-switchWindow>60000){if(switches>=3)emit("切这么快。你在赶场吗",5);switches=0;switchWindow=now;}
   String note=CrabNotificationListener.latest;if(!note.isEmpty()&&!note.equals(lastNotification)){lastNotification=note;emit("又有新消息了",1);}
   if(now-lastWater>7200000){emit("两小时了。去喝水",1);lastWater=now;}
   Calendar cal=Calendar.getInstance();if(cal.get(Calendar.MINUTE)==0){String line=emotion.hourLine(cal.get(Calendar.HOUR_OF_DAY));if(line!=null)emit(line,0);}
   String idle=emotion.idleLine(now);if(idle!=null&&now%300000<5000)emit(idle,-1);
   Intent battery=context.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra("level",100);if(level<=15&&now%300000<5000)emit("电量只剩"+level+"。快充电",1);}
   handler.postDelayed(this,5000);
 }};
 private String mapApp(String p){if(p.contains("douyin")||p.contains("kuaishou"))return "又刷短视频。抓到你了";if(p.contains("taobao")||p.contains("tmall"))return "先看看余额再买";if(p.contains("wechat")||p.contains("tencent.mm"))return "谁找你";return null;}
 private void emit(String s,int amount){emotion.event(amount);listener.onLine(s,emotion.getHeat());}
}
