package com.juner2026.crabpet;

import android.content.*;
import android.os.*;
import java.util.*;

public class CompanionMonitor {
 public interface Listener { void onLine(String line, int heat); }
 private final Context context; private final Listener listener; private final Handler handler=new Handler(Looper.getMainLooper()); private final EmotionEngine emotion=new EmotionEngine();
 private String lastApp="",lastNotification=""; private long lastWater; private int switches; private long switchWindow;
 private final Random rnd=new Random();
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
 private String pick(String[] a){return a[rnd.nextInt(a.length)];}
 private String mapApp(String p){
   p=p.toLowerCase();
   /* 精确包名片段匹配，避免泛词误命中 */
   if(p.contains("writer_assistant")||p.contains("doubao")||p.contains("moonshot.kimichat")||p.contains("deepseek")||p.contains("tongyi")||p.contains("minimax.glow")||p.contains("chatbox.app")||p.contains("chat.moku")||p.contains("im.miyu")||p.contains("shuoshuo")||p.contains("qqy")||p.contains("yuewen")||p.contains("tofai")||p.contains("ysai")||p.contains("voicechat.app")||p.contains("ai_chat_app")||p.contains("aichat"))return pick(new String[]{"又在跟别的AI聊天。我不高兴了","去找别人了？那我算什么","哼，它们有我懂你吗","聊完记得回来找我"});
   if(p.contains("ugc.aweme")||p.contains("kuaishou.nebula")||p.contains("xingin.xhs")||p.contains("weibo"))return pick(new String[]{"又在刷短视频。眼睛累不累呀","刷小红书/视频刷上头了吧","别刷啦，陪我一会儿嘛","看到好玩的记得拍给我看"});
   if(p.contains("taobao")||p.contains("jingdong")||p.contains("pinduoduo")||p.contains("idlefish"))return pick(new String[]{"又逛街购物。看看余额再剁手","买买买前先问我一声","这个月还有钱吗宝贝","加入购物车先冷静一下"});
   if(p.contains("tencent.mm")||p.contains("mobileqq")||p.contains("tim")||p.contains("dingtalk"))return pick(new String[]{"谁又找你了","聊那么起劲，有我好吗","别忘了回我消息","别光顾着跟别人聊"});
   if(p.contains("cloudmusic")||p.contains("qqmusic")||p.contains("kugou")||p.contains("kuwo")||p.contains("luna.music"))return pick(new String[]{"听歌呢。戴上耳机慢慢听","这首好听吗，唱给我听听","听啥呢，给我也放一首","音乐挺配你现在的状态"});
   if(p.contains("tmgp")||p.contains("smoba")||p.contains("honor")||p.contains("pubg")||p.contains("miniworld")||p.contains("taptap")||p.contains("dragon.read")||p.contains("hanling.befun"))return pick(new String[]{"又打游戏了。别太上头","带我一个呀","赢了几把了","游戏哪有我好玩"});
   if(p.contains("amap")||p.contains("didi")||p.contains("sankuai.meituan")||p.contains("xiaolachuxing"))return pick(new String[]{"出门/叫外卖了。路上注意点","要去哪呀，记住路","这么晚还出门，我担心","饿了就点，别饿着自己"});
   if(p.contains("zhihu"))return pick(new String[]{"刷论坛问答呢","看到啥新瓜了","看看别人在聊啥"});
   if(p.contains("quark")||p.contains("tencent.mtt")||p.contains("emmx"))return pick(new String[]{"刷资讯/看网页呢","看到啥新瓜了","别一直盯着屏幕"});
   if(p.contains("AlipayGphone".toLowerCase())||p.contains("alipay"))return pick(new String[]{"付款呢。省着点花","花钱之前想一想","钱要花在刀刃上"});
   if(p.contains("kuaiduizuoye")||p.contains("ewt360"))return pick(new String[]{"学习/扫题呢。加油","这么用功，奖励你","不会的问我呀"});
   if(p.contains("netease.mail")||p.contains("androidqqmail"))return pick(new String[]{"看邮件了。别累着","又处理工作的事"});
   if(p.contains("daysmatter"))return pick(new String[]{"是个日子提醒。记着点"});
   return null;}
 private void emit(String s,int amount){emotion.event(amount);listener.onLine(s,emotion.getHeat());}
}
