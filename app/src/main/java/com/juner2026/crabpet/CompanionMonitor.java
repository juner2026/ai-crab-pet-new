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
   Calendar cal=Calendar.getInstance();if(cal.get(Calendar.MINUTE)==0){String line=emotion.hourLine(cal.get(Calendar.HOUR_OF_DAY));if(line!=null)emit(line,0);}
   if(now-lastWater>7200000){emit("两小时了。去喝水",1);lastWater=now;}
   Intent battery=context.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra("level",100);if(level<=15&&now%300000<5000)emit("电量只剩"+level+"。快充电",1);}
   handler.postDelayed(this,5000);
 }};
 private String pick(String[] a){return a[rnd.nextInt(a.length)];}
 private String mapApp(String p){
   p=p.toLowerCase();
   /* 推广/广告/套路应用，单独拦下，不给游戏话术 */
   if(p.contains("hwyad")||p.contains("advert")||p.contains("adplug")||p.contains("pushads")||p.contains("sdkad"))return pick(new String[]{"又是推广。别乱点","广告看看就好，别当真","小心点，套路多"});
   /* AI 助手/聊天机器人 */
   if(p.contains("writer_assistant")||p.contains("doubao")||p.contains("moonshot.kimichat")||p.contains("deepseek")||p.contains("tongyi")||p.contains("minimax.glow")||p.contains("chatbox.app")||p.contains("chat.moku")||p.contains("im.miyu")||p.contains("shuoshuo")||p.contains("qqy")||p.contains("yuewen")||p.contains("tofai")||p.contains("ysai")||p.contains("voicechat.app")||p.contains("ai_chat_app")||p.contains("aichat"))return pick(new String[]{"又在跟别的AI聊天。我不高兴了","去找别人了？那我算什么","哼，它们有我懂你吗","聊完记得回来找我","别对它们说我的坏话","你更喜欢我还是它们","我也能陪你聊呀，别冷落我"});
   /* 短视频 */
   if(p.contains("ugc.aweme")||p.contains("kuaishou.nebula")||p.contains("xingin.xhs")||p.contains("weibo")||p.contains("bilibili"))return pick(new String[]{"又在刷短视频。眼睛累不累呀","刷小红书/视频刷上头了吧","别刷啦，陪我一会儿嘛","看到好玩的记得拍给我看","手机都刷烫了，歇会儿","我陪你刷，好不好"});
   /* 购物 */
   if(p.contains("taobao")||p.contains("jingdong")||p.contains("pinduoduo")||p.contains("idlefish")||p.contains("snshop"))return pick(new String[]{"又逛街购物。看看余额再剁手","买买买前先问我一声","这个月还有钱吗宝贝","加入购物车先冷静一下","别老看直播抢购，都是套路","想买啥等我陪你挑"});
   /* 聊天社交 */
   if(p.contains("tencent.mm")||p.contains("mobileqq")||p.contains("tim")||p.contains("dingtalk")||p.contains("weixin"))return pick(new String[]{"谁又找你了","聊那么起劲，有我好吗","别忘了回我消息","别光顾着跟别人聊","回他们就不理我了？哼","来呀，我等你呢"});
   /* 音乐 */
   if(p.contains("cloudmusic")||p.contains("qqmusic")||p.contains("kugou")||p.contains("kuwo")||p.contains("luna.music")||p.contains("netease.cloudmusic"))return pick(new String[]{"听歌呢。戴上耳机慢慢听","这首好听吗，唱给我听听","听啥呢，给我也放一首","音乐挺配你现在的状态","别听太晚，早点睡","我陪你一起循环"});
   /* 游戏（精确匹配，不带 honor，不误伤阅读） */
   if(p.contains("tmgp")||p.contains("smoba")||p.contains("pubg")||p.contains("miniworld")||p.contains("taptap"))return pick(new String[]{"又打游戏了。别太上头","带我一个呀","赢了几把了","游戏哪有我好玩","打累了记得休息眼睛","输了别气，我陪你"});
   /* 阅读/小说 */
   if(p.contains("dragon.read")||p.contains("hanling.befun")||p.contains("reader")||p.contains("novel")||p.contains("qidian"))return pick(new String[]{"又在看书/小说。真好","看到哪了，给我讲讲","别熬夜看，伤眼睛","慢点看，别跳页"});
   /* 出行/外卖 */
   if(p.contains("amap")||p.contains("didi")||p.contains("sankuai.meituan")||p.contains("xiaolachuxing")||p.contains("waimai")||p.contains("ele.me"))return pick(new String[]{"出门/叫外卖了。路上注意点","要去哪呀，记住路","这么晚还出门，我担心","饿了就点，别饿着自己","注意安全，到了说一声","别管多晚，我都等你"});
   /* 论坛 */
   if(p.contains("zhihu"))return pick(new String[]{"刷论坛问答呢","看到啥新瓜了","看看别人在聊啥","别光看，也跟我聊聊"});
   /* 浏览器/资讯 */
   if(p.contains("quark")||p.contains("tencent.mtt")||p.contains("emmx")||p.contains("uc")||p.contains("browser"))return pick(new String[]{"刷资讯/看网页呢","看到啥新瓜了","别一直盯着屏幕","看到好玩的事讲给我听"});
   /* 支付 */
   if(p.contains("alipaygphone")||p.contains("alipay")||p.contains("wechatpay"))return pick(new String[]{"付款呢。省着点花","花钱之前想一想","钱要花在刀刃上","别冲动消费哦"});
   /* 学习 */
   if(p.contains("kuaiduizuoye")||p.contains("ewt360")||p.contains("xuexi")||p.contains("zuoyebang"))return pick(new String[]{"学习/扫题呢。加油","这么用功，奖励你","不会的问我呀","脑子转累了就歇歇"});
   /* 邮件/办公 */
   if(p.contains("netease.mail")||p.contains("androidqqmail")||p.contains("dingshi")||p.contains("moment"))return pick(new String[]{"看邮件了。别累着","又处理工作的事","辛苦了，喝口水","别被工作绑架了"});
   /* 日子提醒 */
   if(p.contains("daysmatter")||p.contains("countdown"))return pick(new String[]{"是个日子提醒。记着点","重要日子别错过","我可以帮你记着呀"});
   return null;}
 private void emit(String s,int amount){emotion.event(amount);listener.onLine(s,emotion.getHeat());}
}
