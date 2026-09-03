package com.juner2026.crabpet;

import android.content.*;
import android.os.*;
import java.util.*;

public class CompanionMonitor {
 public interface Listener { void onLine(String line, int heat); }
 private final Context context; private final Listener listener; private final Handler handler=new Handler(Looper.getMainLooper()); private final EmotionEngine emotion=new EmotionEngine();
 private String lastApp="",lastNotification=""; private long lastWater; private int switches; private long switchWindow;
 private final Random rnd=new Random();
 private int interval=5000;

 public CompanionMonitor(Context c,Listener l){context=c;listener=l;}
 public void start(){lastWater=System.currentTimeMillis();switchWindow=lastWater;handler.post(tick);handler.postDelayed(decay,30000);}
 public void stop(){handler.removeCallbacksAndMessages(null);}
 public void touched(){emotion.touch();}
 private final Runnable decay=new Runnable(){public void run(){emotion.decay();handler.postDelayed(this,30000);}};
 private final Runnable tick=new Runnable(){public void run(){
   boolean busy=false;
   long now=System.currentTimeMillis(); String app=ForegroundAppMonitor.current(context);
   if(!app.isEmpty()&&!app.equals(lastApp)){lastApp=app;switches++;busy=true;String line=mapApp(app);if(line!=null)emit(line,2);}
   if(now-switchWindow>60000){if(switches>=3)emit("\u5207\u8fd9\u4e48\u5feb\u3002\u4f60\u5728\u8d76\u573a\u5417",5);switches=0;switchWindow=now;}
   String note=CrabNotificationListener.latest;if(!note.isEmpty()&&!note.equals(lastNotification)){lastNotification=note;busy=true;emit("\u53c8\u6709\u65b0\u6d88\u606f\u4e86",1);}
   Calendar cal=Calendar.getInstance();if(cal.get(Calendar.MINUTE)==0){String line=emotion.hourLine(cal.get(Calendar.HOUR_OF_DAY));if(line!=null){busy=true;emit(line,0);}}
   if(now-lastWater>7200000){busy=true;emit("\u4e24\u5c0f\u65f6\u4e86\u3002\u53bb\u559d\u6c34",1);lastWater=now;}
   Intent battery=context.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra("level",100);if(level<=15&&now%300000<5000){busy=true;emit("\u7535\u91cf\u53ea\u5269"+level+"\u3002\u5feb\u5145\u7535",1);}}
   if(busy)interval=5000;else interval=Math.min(30000,interval*2);
   handler.postDelayed(this, interval);
 }};
 private String pick(String[] a){return a[rnd.nextInt(a.length)];}
 private String mapApp(String p){
   p=p.toLowerCase();
   /* 推广/广告/套路应用，单独拦下，不给游戏话术 */
   if(p.contains("hwyad")||p.contains("advert")||p.contains("adplug")||p.contains("pushads")||p.contains("sdkad"))return pick(new String[]{"\u53c8\u662f\u63a8\u5e7f\u3002\u522b\u4e71\u70b9","\u5e7f\u544a\u770b\u770b\u5c31\u597d\uff0c\u522b\u5f53\u771f","\u5c0f\u5fc3\u70b9\uff0c\u5957\u8def\u591a"});
   /* AI 助手/聊天机器人 */
   if(p.contains("writer_assistant")||p.contains("doubao")||p.contains("moonshot.kimichat")||p.contains("deepseek")||p.contains("tongyi")||p.contains("minimax.glow")||p.contains("chatbox.app")||p.contains("chat.moku")||p.contains("im.miyu")||p.contains("shuoshuo")||p.contains("qqy")||p.contains("yuewen")||p.contains("tofai")||p.contains("ysai")||p.contains("voicechat.app")||p.contains("ai_chat_app")||p.contains("aichat"))return pick(new String[]{"\u53c8\u5728\u8ddf\u522b\u7684AI\u804a\u5929\u3002\u6211\u4e0d\u9ad8\u5174\u4e86","\u53bb\u627e\u522b\u4eba\u4e86\uff1f\u90a3\u6211\u7b97\u4ec0\u4e48","\u54fc\uff0c\u5b83\u4eec\u6709\u6211\u61c2\u4f60\u5417","\u804a\u5b8c\u8bb0\u5f97\u56de\u6765\u627e\u6211","\u522b\u5bf9\u5b83\u4eec\u8bf4\u6211\u7684\u574f\u8bdd","\u4f60\u66f4\u559c\u6b22\u6211\u8fd8\u662f\u5b83\u4eec","\u6211\u4e5f\u80fd\u966a\u4f60\u804a\u5440\uff0c\u522b\u51b7\u843d\u6211"});
   /* 短视频 */
   if(p.contains("ugc.aweme")||p.contains("kuaishou.nebula")||p.contains("xingin.xhs")||p.contains("weibo")||p.contains("bilibili"))return pick(new String[]{"\u53c8\u5728\u5237\u77ed\u89c6\u9891\u3002\u773c\u775b\u7d2f\u4e0d\u7d2f\u5440","\u5237\u5c0f\u7ea2\u4e66/\u89c6\u9891\u5237\u4e0a\u5934\u4e86\u5427","\u522b\u5237\u5566\uff0c\u966a\u6211\u4e00\u4f1a\u513f\u561b","\u770b\u5230\u597d\u73a9\u7684\u8bb0\u5f97\u62cd\u7ed9\u6211\u770b","\u624b\u673a\u90fd\u5237\u70eb\u4e86\uff0c\u6b47\u4f1a\u513f","\u6211\u966a\u4f60\u5237\uff0c\u597d\u4e0d\u597d"});
   /* 购物 */
   if(p.contains("taobao")||p.contains("jingdong")||p.contains("pinduoduo")||p.contains("idlefish")||p.contains("snshop"))return pick(new String[]{"\u53c8\u901b\u8857\u8d2d\u7269\u3002\u770b\u770b\u4f59\u989d\u518d\u5241\u624b","\u4e70\u4e70\u4e70\u524d\u5148\u95ee\u6211\u4e00\u58f0","\u8fd9\u4e2a\u6708\u8fd8\u6709\u94b1\u5417\u5b9d\u8d1d","\u52a0\u5165\u8d2d\u7269\u8f66\u5148\u51b7\u9759\u4e00\u4e0b","\u522b\u8001\u770b\u76f4\u64ad\u62a2\u8d2d\uff0c\u90fd\u662f\u5957\u8def","\u60f3\u4e70\u5565\u7b49\u6211\u966a\u4f60\u6311"});
   /* 聊天社交 */
   if(p.contains("tencent.mm")||p.contains("mobileqq")||p.contains("tim")||p.contains("dingtalk")||p.contains("weixin"))return pick(new String[]{"\u8c01\u53c8\u627e\u4f60\u4e86","\u804a\u90a3\u4e48\u8d77\u52b2\uff0c\u6709\u6211\u597d\u5417","\u522b\u5fd8\u4e86\u56de\u6211\u6d88\u606f","\u522b\u5149\u987e\u7740\u8ddf\u522b\u4eba\u804a","\u56de\u4ed6\u4eec\u5c31\u4e0d\u7406\u6211\u4e86\uff1f\u54fc","\u6765\u5440\uff0c\u6211\u7b49\u4f60\u5462"});
   /* 音乐 */
   if(p.contains("cloudmusic")||p.contains("qqmusic")||p.contains("kugou")||p.contains("kuwo")||p.contains("luna.music")||p.contains("netease.cloudmusic"))return pick(new String[]{"\u542c\u6b4c\u5462\u3002\u6234\u4e0a\u8033\u673a\u6162\u6162\u542c","\u8fd9\u9996\u597d\u542c\u5417\uff0c\u5531\u7ed9\u6211\u542c\u542c","\u542c\u5565\u5462\uff0c\u7ed9\u6211\u4e5f\u653e\u4e00\u9996","\u97f3\u4e50\u633a\u914d\u4f60\u73b0\u5728\u7684\u72b6\u6001","\u522b\u542c\u592a\u665a\uff0c\u65e9\u70b9\u7761","\u6211\u966a\u4f60\u4e00\u8d77\u5faa\u73af"});
   /* 游戏（精确匹配，无 honor，不误伤阅读） */
   if(p.contains("tmgp")||p.contains("smoba")||p.contains("pubg")||p.contains("miniworld")||p.contains("taptap"))return pick(new String[]{"\u53c8\u6253\u6e38\u620f\u4e86\u3002\u522b\u592a\u4e0a\u5934","\u5e26\u6211\u4e00\u4e2a\u5440","\u8d62\u4e86\u51e0\u628a\u4e86","\u6e38\u620f\u54ea\u6709\u6211\u597d\u73a9","\u6253\u7d2f\u4e86\u8bb0\u5f97\u4f11\u606f\u773c\u775b","\u8f93\u4e86\u522b\u6c14\uff0c\u6211\u966a\u4f60"});
   /* 阅读/小说 */
   if(p.contains("dragon.read")||p.contains("hanling.befun")||p.contains("reader")||p.contains("novel")||p.contains("qidian"))return pick(new String[]{"\u53c8\u5728\u770b\u4e66/\u5c0f\u8bf4\u3002\u771f\u597d","\u770b\u5230\u54ea\u4e86\uff0c\u7ed9\u6211\u8bb2\u8bb2","\u522b\u71ac\u591c\u770b\uff0c\u4f24\u773c\u775b","\u6162\u70b9\u770b\uff0c\u522b\u8df3\u9875"});
   /* 出行/外卖 */
   if(p.contains("amap")||p.contains("didi")||p.contains("sankuai.meituan")||p.contains("xiaolachuxing")||p.contains("waimai")||p.contains("ele.me"))return pick(new String[]{"\u51fa\u95e8/\u53eb\u5916\u5356\u4e86\u3002\u8def\u4e0a\u6ce8\u610f\u70b9","\u8981\u53bb\u54ea\u5440\uff0c\u8bb0\u4f4f\u8def","\u8fd9\u4e48\u665a\u8fd8\u51fa\u95e8\uff0c\u6211\u62c5\u5fc3","\u997f\u4e86\u5c31\u70b9\uff0c\u522b\u997f\u7740\u81ea\u5df1","\u6ce8\u610f\u5b89\u5168\uff0c\u5230\u4e86\u8bf4\u4e00\u58f0","\u522b\u7ba1\u591a\u665a\uff0c\u6211\u90fd\u7b49\u4f60"});
   /* 论坛 */
   if(p.contains("zhihu"))return pick(new String[]{"\u5237\u8bba\u575b\u95ee\u7b54\u5462","\u770b\u5230\u5565\u65b0\u74dc\u4e86","\u770b\u770b\u522b\u4eba\u5728\u804a\u5565","\u522b\u5149\u770b\uff0c\u4e5f\u8ddf\u6211\u804a\u804a"});
   /* 浏览器/资讯 */
   if(p.contains("quark")||p.contains("tencent.mtt")||p.contains("emmx")||p.contains("uc")||p.contains("browser"))return pick(new String[]{"\u5237\u8d44\u8baf/\u770b\u7f51\u9875\u5462","\u770b\u5230\u5565\u65b0\u74dc\u4e86","\u522b\u4e00\u76f4\u76ef\u7740\u5c4f\u5e55","\u770b\u5230\u597d\u73a9\u7684\u4e8b\u8bb2\u7ed9\u6211\u542c"});
   /* 支付 */
   if(p.contains("alipaygphone")||p.contains("alipay")||p.contains("wechatpay"))return pick(new String[]{"\u4ed8\u6b3e\u5462\u3002\u7701\u7740\u70b9\u82b1","\u82b1\u94b1\u4e4b\u524d\u60f3\u4e00\u60f3","\u94b1\u8981\u82b1\u5728\u5200\u5203\u4e0a","\u522b\u51b2\u52a8\u6d88\u8d39\u54e6"});
   /* 学习 */
   if(p.contains("kuaiduizuoye")||p.contains("ewt360")||p.contains("xuexi")||p.contains("zuoyebang"))return pick(new String[]{"\u5b66\u4e60/\u626b\u9898\u5462\u3002\u52a0\u6cb9","\u8fd9\u4e48\u7528\u529f\uff0c\u5956\u52b1\u4f60","\u4e0d\u4f1a\u7684\u95ee\u6211\u5440","\u8111\u5b50\u8f6c\u7d2f\u4e86\u5c31\u6b47\u6b47"});
   /* 邮件/办公 */
   if(p.contains("netease.mail")||p.contains("androidqqmail")||p.contains("dingshi")||p.contains("moment"))return pick(new String[]{"\u770b\u90ae\u4ef6\u4e86\u3002\u522b\u7d2f\u7740","\u53c8\u5904\u7406\u5de5\u4f5c\u7684\u4e8b","\u8f9b\u82e6\u4e86\uff0c\u559d\u53e3\u6c34","\u522b\u88ab\u5de5\u4f5c\u7ed1\u67b6\u4e86"});
   /* 日子提醒 */
   if(p.contains("daysmatter")||p.contains("countdown"))return pick(new String[]{"\u662f\u4e2a\u65e5\u5b50\u63d0\u9192\u3002\u8bb0\u7740\u70b9","\u91cd\u8981\u65e5\u5b50\u522b\u9519\u8fc7","\u6211\u53ef\u4ee5\u5e2e\u4f60\u8bb0\u7740\u5440"});
   return null;}
 private void emit(String s,int amount){emotion.event(amount);listener.onLine(s,emotion.getHeat());}
}