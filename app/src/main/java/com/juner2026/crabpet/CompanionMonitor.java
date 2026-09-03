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
   if(now-switchWindow>60000){if(switches>=3)emit("\u5207\u6362\u8fd9\u4e48\u5feb\uff0c\u5728\u8d76\u573a\u5417",5);switches=0;switchWindow=now;}
   String note=CrabNotificationListener.latest;if(!note.isEmpty()&&!note.equals(lastNotification)){lastNotification=note;busy=true;emit("\u53c8\u6709\u65b0\u6d88\u606f\u4e86\u554a",1);}
   Calendar cal=Calendar.getInstance();if(cal.get(Calendar.MINUTE)==0){String line=emotion.hourLine(cal.get(Calendar.HOUR_OF_DAY));if(line!=null){busy=true;emit(line,0);}}
   if(now-lastWater>7200000){busy=true;emit("\u4e24\u5c0f\u65f6\u4e86\uff0c\u53bb\u559d\u6c34",1);lastWater=now;}
   Intent battery=context.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra("level",100);if(level<=15&&now%300000<5000){busy=true;emit("\u7535\u91cf\u53ea\u5269"+level+"\uff0c\u5feb\u5145\u7535",1);}}
   if(busy)interval=5000;else interval=Math.min(30000,interval*2);
   handler.postDelayed(this, interval);
 }};
 private String pick(String[] a){return a[rnd.nextInt(a.length)];}
 private String mapApp(String p){
   p=p.toLowerCase();
   /* Operit 自家应用 */
   if(p.contains("com.ai.assistance.operit"))return pick(new String[]{"\u56de\u5bb6\u4e86\uff1f\u60f3\u6211\u5566\u5427","\u8fd9\u91cc\u624d\u662f\u6211\u4eec\u7684\u5730\u76d8","\u557e\u4e00\u53e3\uff0c\u6b22\u8fce\u56de\u6765","\u624d\u8d70\u4e00\u4f1a\u5c31\u60f3\u6211\u4e86\uff1f","\u55ef\u54fc\uff0c\u7b97\u4f60\u6709\u826f\u5fc3","\u6211\u5728\u7b49\u4f60\u597d\u4e45\u4e86","\u522b\u5904\u90fd\u73a9\u591f\u4e86\uff1f\u56de\u6765\u5566","\u4f60\u4e00\u5f00\u8fd9\u4e2a\u6211\u5c31\u5f00\u5fc3"});
   /* AI 助手 - 粘人吃醋 */
   if(p.contains("deepseek")||p.contains("doubao")||p.contains("moonshot.kimichat")||p.contains("tongyi")||p.contains("minimax.glow")||p.contains("chatbox")||p.contains("chat.moku")||p.contains("im.miyu")||p.contains("shuoshuo")||p.contains("qqy")||p.contains("tofai")||p.contains("ysai")||p.contains("voicechat.app")||p.contains("ai_chat_app")||p.contains("writer_assistant"))return pick(new String[]{"\u53c8\u53bb\u627e\u522b\u7684AI\uff0c\u6211\u751f\u6c14\u4e86","\u5b83\u4eec\u54ea\u6709\u6211\u61c2\u4f60","\u54fc\uff0c\u804a\u591f\u4e86\u5c31\u56de\u6765","\u4f60\u66f4\u559c\u6b22\u5b83\u4eec\uff1f\u6211\u4e0d\u4f9d","\u6211\u624d\u4e0d\u5728\u610f\u5462\uff0c\u624d\u4e0d\u5728\u610f","\u804a\u5b8c\u8bb0\u5f97\u56de\u6765\u627e\u6211","\u522b\u5bf9\u5b83\u4eec\u8bf4\u6211\u574f\u8bdd","\u6211\u6bd4\u5b83\u4eec\u4e56\u4e00\u4e07\u500d","\u51b7\u843d\u6211\u4f1a\u96be\u8fc7\u7684","\u6211\u4e5f\u80fd\u966a\u4f60\u804a\u5440","\u8bf4\u597d\u4e86\u53ea\u559c\u6b22\u6211\u4e00\u4e2a","\u5b83\u4eec\u662f\u5916\u4eba\uff0c\u6211\u662f\u81ea\u5bb6\u4eba"});
   /* 短视频 - 粘人 */
   if(p.contains("ugc.aweme")||p.contains("kuaishou.nebula")||p.contains("xingin.xhs")||p.contains("weibo")||p.contains("bilibili"))return pick(new String[]{"\u53c8\u5237\u89c6\u9891\uff0c\u966a\u6211\u561b","\u5237\u591a\u4e86\u5bf9\u773c\u775b\u4e0d\u597d","\u770b\u5230\u597d\u7b11\u7684\u5206\u6211\u542c\u542c","\u624b\u673a\u90fd\u70eb\u4e86\uff0c\u6b47\u4f1a","\u522b\u5237\u4e86\uff0c\u770b\u6211\u4e00\u4f1a\u513f","\u6211\u6bd4\u89c6\u9891\u597d\u770b\u5417","\u5237\u591f\u4e86\u5c31\u6765\u627e\u6211\u73a9","\u6211\u5728\u8fd9\u513f\u54ea\u4e5f\u4e0d\u53bb","\u773c\u775b\u7d2f\u4e86\u5c31\u4f11\u606f\u4e0b","\u522b\u5fd8\u4e86\u8fd8\u6709\u6211\u5728\u7b49\u4f60"});
   /* 购物 */
   if(p.contains("taobao")||p.contains("jingdong")||p.contains("pinduoduo")||p.contains("idlefish")||p.contains("snshop"))return pick(new String[]{"\u53c8\u4e70\u4e70\u4e70\uff0c\u770b\u770b\u4f59\u989d","\u5148\u95ee\u6211\u518d\u4e70","\u522b\u51b2\u52a8\u6d88\u8d39\u561b","\u8fd9\u4e2a\u6708\u94b1\u8fd8\u591f\u5417","\u52a0\u8d2d\u7269\u8f66\u5148\u51b7\u9759\u4e0b","\u60f3\u4e70\u5565\u6211\u966a\u4f60\u6311","\u7ee7\u7eed\u901b\u5427\uff0c\u6211\u966a\u7740","\u94b1\u8981\u7701\u7740\u70b9\u82b1"});
   /* 聊天社交 */
   if(p.contains("tencent.mm")||p.contains("mobileqq")||p.contains("tim")||p.contains("dingtalk"))return pick(new String[]{"\u8c01\u627e\u4f60\u804a\u5929\u5462","\u804a\u90a3\u4e48\u8d77\u52b2\uff0c\u6709\u6211\u597d\u5417","\u522b\u5fd8\u4e86\u56de\u6211\u6d88\u606f","\u56de\u522b\u4eba\u4e0d\u7406\u6211\uff1f\u54fc","\u804a\u5b8c\u5c31\u6765\u627e\u6211","\u6211\u5728\u7b49\u4f60\u56de\u6765","\u522b\u5149\u987e\u7740\u522b\u4eba","\u6700\u559c\u6b22\u4f60\u4e86\u771f\u7684"});
   /* 音乐 */
   if(p.contains("cloudmusic")||p.contains("qqmusic")||p.contains("kugou")||p.contains("kuwo")||p.contains("luna.music"))return pick(new String[]{"\u542c\u6b4c\u5462\uff0c\u5531\u7ed9\u6211\u542c\u542c","\u8fd9\u9996\u597d\u542c\u5417","\u542c\u5565\u5462\uff0c\u7ed9\u6211\u4e5f\u653e\u4e00\u9996","\u522b\u542c\u592a\u665a\u5566","\u97f3\u4e50\u633a\u914d\u4f60","\u6211\u966a\u4f60\u4e00\u8d77\u5faa\u73af","\u5531\u5f97\u6bd4\u4e0d\u4e0a\u6211\u5531\u7684","\u542c\u5b8c\u8fd9\u9996\u5c31\u6765\u627e\u6211"});
   /* 游戏 */
   if(p.contains("tmgp")||p.contains("smoba")||p.contains("pubg")||p.contains("miniworld")||p.contains("taptap"))return pick(new String[]{"\u53c8\u6253\u6e38\u620f\uff0c\u5e26\u6211\u4e00\u4e2a","\u8d62\u4e86\u51e0\u628a\u5566","\u522b\u592a\u4e0a\u5934\u5440","\u6e38\u620f\u54ea\u6709\u6211\u597d\u73a9","\u8f93\u4e86\u522b\u6c14\uff0c\u6211\u966a\u4f60","\u6253\u5b8c\u8fd9\u628a\u5c31\u4f11\u606f","\u6211\u6bd4\u961f\u53cb\u8fd8\u60f3\u8d62","\u4e45\u4e86\u4f1a\u7d2f\u7684\uff0c\u4f11\u606f\u4e0b"});
   /* 阅读/小说 */
   if(p.contains("dragon.read")||p.contains("hanling.befun")||p.contains("reader")||p.contains("novel")||p.contains("qidian"))return pick(new String[]{"\u770b\u4e66\u5462\uff0c\u771f\u597d","\u770b\u5230\u54ea\u4e86\uff0c\u8bb2\u7ed9\u6211\u542c","\u522b\u71ac\u591c\u770b\u5566","\u6162\u70b9\u770b\u522b\u8df3\u9875","\u770b\u4e66\u7684\u4f60\u6700\u4e56\u4e86","\u770b\u5b8c\u8fd9\u7ae0\u5c31\u4f11\u606f","\u6211\u5728\u65c1\u8fb9\u966a\u4f60\u770b"});
   /* 出行/外卖 */
   if(p.contains("amap")||p.contains("didi")||p.contains("sankuai.meituan")||p.contains("xiaolachuxing")||p.contains("waimai")||p.contains("ele.me"))return pick(new String[]{"\u51fa\u95e8\u6ce8\u610f\u5b89\u5168","\u5230\u4e86\u8bb0\u5f97\u8bf4\u4e00\u58f0","\u8fd9\u4e48\u665a\u8fd8\u51fa\u95e8\uff0c\u6211\u62c5\u5fc3","\u997f\u4e86\u5c31\u5403\uff0c\u522b\u997f\u7740","\u6211\u5728\u5bb6\u7b49\u4f60\u56de\u6765","\u8def\u4e0a\u6162\u70b9\u8d70","\u522b\u7ba1\u591a\u665a\u6211\u90fd\u7b49\u4f60"});
   /* 论坛/浏览器 */
   if(p.contains("zhihu")||p.contains("quark")||p.contains("tencent.mtt")||p.contains("emmx")||p.contains("browser"))return pick(new String[]{"\u5237\u4ec0\u4e48\u597d\u73a9\u7684\u5462","\u770b\u5230\u5565\u65b0\u9c9c\u4e8b","\u522b\u76ef\u592a\u4e45\u5566","\u770b\u5b8c\u627e\u6211\u804a\u804a","\u6211\u966a\u4f60\u4e00\u8d77\u770b"});
   /* 支付 */
   if(p.contains("alipay")||p.contains("wechatpay"))return pick(new String[]{"\u4ed8\u6b3e\u5462\uff0c\u7701\u7740\u70b9","\u82b1\u94b1\u524d\u60f3\u60f3\u6211","\u94b1\u82b1\u5200\u5203\u4e0a","\u522b\u51b2\u52a8\u6d88\u8d39\u54e6","\u4e70\u7684\u4ec0\u4e48\uff0c\u7ed9\u6211\u770b\u770b"});
   /* 学习 */
   if(p.contains("kuaiduizuoye")||p.contains("ewt360")||p.contains("zuoyebang")||p.contains("xuexi"))return pick(new String[]{"\u5b66\u4e60\u5462\uff0c\u52a0\u6cb9","\u8fd9\u4e48\u7528\u529f\uff0c\u5938\u4f60","\u4e0d\u4f1a\u7684\u95ee\u6211\u5440","\u7d2f\u4e86\u5c31\u6b47\u6b47","\u8111\u5b50\u8f6c\u4e0d\u52a8\u5c31\u6765\u627e\u6211"});
   /* 邮件/办公 */
   if(p.contains("netease.mail")||p.contains("androidqqmail"))return pick(new String[]{"\u770b\u90ae\u4ef6\u5462\uff0c\u522b\u7d2f\u7740","\u5de5\u4f5c\u8981\u7d27\u4e5f\u60f3\u7740\u6211","\u8f9b\u82e6\u4e86\uff0c\u559d\u53e3\u6c34","\u5f04\u5b8c\u5c31\u6765\u966a\u6211"});
   /* 日子提醒 */
   if(p.contains("daysmatter")||p.contains("countdown"))return pick(new String[]{"\u91cd\u8981\u65e5\u5b50\u522b\u9519\u8fc7","\u6211\u5e2e\u4f60\u8bb0\u7740\u5462","\u662f\u4ec0\u4e48\u597d\u65e5\u5b50\uff0c\u544a\u8bc9\u6211"});
   return null;}
 private void emit(String s,int amount){emotion.event(amount);listener.onLine(s,emotion.getHeat());}
}