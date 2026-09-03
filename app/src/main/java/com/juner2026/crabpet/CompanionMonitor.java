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
 private String mapApp(String p){
   p=p.toLowerCase();
   if(p.contains("douyin")||p.contains("aweme")||p.contains("kuaishou")||p.contains("nebula")||p.contains("bilibili")||p.contains("bili")||p.contains("xiaohongshu")||p.contains("weibo"))return "又在刷短视频/追视频。眼睛累不累";
   if(p.contains("taobao")||p.contains("tmall")||p.contains("jingdong")||p.contains("pinduoduo")||p.contains("pdd")||p.contains("suning"))return "又逛购物。看看余额";
   if(p.contains("wechat")||p.contains("tencent.mm")||p.contains("mobileqq")||p.contains("tim")||p.contains("qzone"))return "谁找你聊天";
   if(p.contains("netease")||p.contains("cloudmusic")||p.contains("qqmusic")||p.contains("kugou")||p.contains("kuwo"))return "听歌呢。带上耳机吧";
   if(p.contains("game")||p.contains("honor")||p.contains("smoba")||p.contains("wangzhe")||p.contains("pubg")||p.contains("peace")||p.contains("genshin")||p.contains("yuanshen"))return "打游戏了。别太上头";
   if(p.contains("maps")||p.contains("amap")||p.contains("didi"))return "出门了。注意安全";
   if(p.contains("zhihu")||p.contains("tieba"))return "刷论坛问答呢";
   return null;}
 private void emit(String s,int amount){emotion.event(amount);listener.onLine(s,emotion.getHeat());}
}