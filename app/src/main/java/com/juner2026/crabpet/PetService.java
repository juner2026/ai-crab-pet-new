package com.juner2026.crabpet;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.view.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import java.io.File;
import java.util.*;

public class PetService extends Service {
 private WindowManager wm;private FrameLayout root;private WindowManager.LayoutParams lp;
 private WebView crabView;private View glow;private TextView bubble;
 private final Handler h=new Handler(Looper.getMainLooper());
 private CompanionMonitor monitor;private PopupWindow popup;
 private float downX,downY,startRawX,startRawY,baseLx,baseLy,lastMx,lastMt,vx;
 private long downTime,lastTrail,lastTouch,lastTap,comboStart,lastGlide;
 private boolean moved,dragSaid;private int combo,colorIdx,lastBat=-1,lastPlug=-1,lone=0,tapRun=0;
 private String curAction="";private int heat=0;private boolean waking;

 private static final String ACTIONS[]={"clawd_view/skin_calico__calico-idle-follow.html","clawd_view/svg__clawd-aegyo-shy.html","clawd_view/svg__clawd-coffee-hand.html","clawd_view/svg__clawd-coffee-head-flip.html","clawd_view/svg__clawd-collapse-sleep.html","clawd_view/svg__clawd-dizzy.html","clawd_view/svg__clawd-error.html","clawd_view/svg__clawd-happy.html","clawd_view/svg__clawd-headphones-groove.html","clawd_view/svg__clawd-idle-bubble.html","clawd_view/svg__clawd-idle-collapse.html","clawd_view/svg__clawd-idle-doze.html","clawd_view/svg__clawd-idle-follow.html","clawd_view/svg__clawd-idle-living.html","clawd_view/svg__clawd-idle-look.html","clawd_view/svg__clawd-idle-low-battery.html","clawd_view/svg__clawd-idle-reading.html","clawd_view/svg__clawd-idle-yawn.html","clawd_view/svg__clawd-mini-alert.html","clawd_view/svg__clawd-mini-crabwalk.html","clawd_view/svg__clawd-mini-enter-sleep.html","clawd_view/svg__clawd-mini-enter.html","clawd_view/svg__clawd-mini-happy.html","clawd_view/svg__clawd-mini-idle.html","clawd_view/svg__clawd-mini-peek.html","clawd_view/svg__clawd-mini-sleep.html","clawd_view/svg__clawd-mini-typing.html","clawd_view/svg__clawd-notification.html","clawd_view/svg__clawd-outlaw-bender.html","clawd_view/svg__clawd-react-annoyed.html","clawd_view/svg__clawd-react-double-jump.html","clawd_view/svg__clawd-react-double.html","clawd_view/svg__clawd-react-drag.html","clawd_view/svg__clawd-react-left.html","clawd_view/svg__clawd-react-right.html","clawd_view/svg__clawd-sleeping.html","clawd_view/svg__clawd-wake.html","clawd_view/svg__clawd-working-building.html","clawd_view/svg__clawd-working-carrying.html","clawd_view/svg__clawd-working-debugger.html","clawd_view/svg__clawd-working-juggling.html","clawd_view/svg__clawd-working-sweeping.html","clawd_view/svg__clawd-working-thinking.html","clawd_view/svg__clawd-working-typing-boss.html","clawd_view/svg__clawd-working-typing.html","clawd_view/svg__clawd-working-ultrathink.html","clawd_view/svg__clawd-working-wizard.html"};
 private static final String IDLE="clawd_view/svg__clawd-mini-idle.html";
 private static final String SLEEP="clawd_view/svg__clawd-mini-sleep.html";
 private static final String DOZE="clawd_view/svg__clawd-idle-doze.html";
 private static final String WAKE="clawd_view/svg__clawd-wake.html";
 private static final String HAPPY="clawd_view/svg__clawd-happy.html";
 private static final String PEEK="clawd_view/svg__clawd-mini-peek.html";
 private static final String SLEEPY="clawd_view/svg__clawd-sleeping.html";

 private static final String[] LINES={
     "喵","想你了","抱抱","再戳一下",
     "你回来啦","不许走","喜欢你","贴贴",
     "乖","来啦","就黏着你","亲一口",
     "摸摸头","不准跑","在呢","想你",
     "要亲亲","抱紧我","蹭蹭你","今天也要开心哦",
     "嘿嘿","叫你呢","别走嘛","陪你呀",
     "看我呀","戳我干嘛呀","痒","哼，又来",
     "你手好欠","我在这呢","看着你呢","小心我咬你",
     "再戳我就赖上你了","摸摸","咕噜咕噜","我超乖的",
     "你笑了","不许不理我","我等你哦","只给你一个人戳"};
 private static final String[] LONELY={
     "你在忙什么呀","我在这儿等你呢","不要忘了我在","好久没碰我了","嗯……看我一眼嘛",
     "困了，但还想等你","我把灯留着了","你回来我就醒","一个人有点静","我数着呢，你多久没理我"};
 private static final String[] SYMS={"\u2726","\u2727","\u2665","\u2661","\u2605","\u2606","\u266A","\u266B","\u273F","\u2740"};
 private static final int[] HUES={0xFFFF6E9B,0xFFFFBE32,0xFFBE96FF,0xFF64CDFF,0xFF78DC82,0xFFEB5A5A,0xFF9A8CFF,0xFFFF8C69};
 private static final String[] DOUBLE={
     "偷袭我？","戳两下什么意思","你还来","痒死了","你故意的吧",
     "我咬你了","跳给你看","哼！","别挠我","你手真快"};private static final String[] C3={
     "这么喜欢戳我呀","连着三下，记仇了","你是不是闲","我快被你戳坏了","好啦好啦我在",
     "你手不酸吗"};private static final String[] C5={
     "再戳就疼了……","五下了，过分了啊","你再戳我就哭给你看","被你戳晕了","停！我认输"};private static final String[] C8={
     "哈哈哈哈行了行了！","我生气了，真的","你完蛋了，等我收拾你","别戳了，我抱你一下","好啦我服了，亲一个"};private static final String[] FLING={
     "哇———","晕了晕了","你甩我干嘛","我飞出去了","拉我一把",
     "慢点慢点","还好我抓住边了","别丢了","我又滑回来了","你力气好大"};private static final String[] MURMUR={
     "今天也要好好的呀","你在忙什么呢","我就在这蹲着","有点想你了","刚才打了个哈欠",
     "我数了数日子","你要是累了就歇歇","哼，小跳一下","我在这里呢，别忘了","有点困，但不想睡",
     "你今天笑了几次，我数着","我好养，不闹","要不要摸摸我","安安静静的，也挺好","我在偷偷看你",
     "你眼睛累了没","好久没亲我了","我尾巴又翘起来了","你专心做事的样子好看","陪你，多久都行",
     "我把最好的位置留给你了"};private static final String[] SHOT={
     "截图了？给我看看","又截什么好东西","截了我一份","是不是截了我说的话","偷偷截图干嘛",
     "截了记得发我","哦？偷偷存档","又收集素材呢"};private static final String[] WAKE2={
     "吵醒我了…","唔……醒了","谁呀……是你啊","我梦见你了","别闹，再睡五分钟"};
 private String pick(String[] a){return a[(int)(Math.random()*a.length)];}
 private static final String[] NAMES={
     "三花猫|clawd_view/skin_calico__calico-idle-follow.html","脸红|clawd_view/svg__clawd-aegyo-shy.html","喝咖啡|clawd_view/svg__clawd-coffee-hand.html",
     "咖啡翻头|clawd_view/svg__clawd-coffee-head-flip.html","瘫睡|clawd_view/svg__clawd-collapse-sleep.html","晕了|clawd_view/svg__clawd-dizzy.html","报错|clawd_view/svg__clawd-error.html",
     "开心|clawd_view/svg__clawd-happy.html","戴耳机|clawd_view/svg__clawd-headphones-groove.html","吹泡泡|clawd_view/svg__clawd-idle-bubble.html","瘫掉|clawd_view/svg__clawd-idle-collapse.html",
     "打瞌睡|clawd_view/svg__clawd-idle-doze.html","跟着|clawd_view/svg__clawd-idle-follow.html","呼吸|clawd_view/svg__clawd-idle-living.html","乱看|clawd_view/svg__clawd-idle-look.html",
     "低电量|clawd_view/svg__clawd-idle-low-battery.html","看书|clawd_view/svg__clawd-idle-reading.html","打哈欠|clawd_view/svg__clawd-idle-yawn.html","迷你警觉|clawd_view/svg__clawd-mini-alert.html",
     "迷你横走|clawd_view/svg__clawd-mini-crabwalk.html","迷你入眠|clawd_view/svg__clawd-mini-enter-sleep.html","迷你进门|clawd_view/svg__clawd-mini-enter.html","迷你开心|clawd_view/svg__clawd-mini-happy.html",
     "迷你待机|clawd_view/svg__clawd-mini-idle.html","迷你偷看|clawd_view/svg__clawd-mini-peek.html","迷你睡觉|clawd_view/svg__clawd-mini-sleep.html","迷你打字|clawd_view/svg__clawd-mini-typing.html",
     "通知|clawd_view/svg__clawd-notification.html","坏蛋|clawd_view/svg__clawd-outlaw-bender.html","生气|clawd_view/svg__clawd-react-annoyed.html","双击跳|clawd_view/svg__clawd-react-double-jump.html",
     "双击|clawd_view/svg__clawd-react-double.html","被拖|clawd_view/svg__clawd-react-drag.html","探头左|clawd_view/svg__clawd-react-left.html","探头右|clawd_view/svg__clawd-react-right.html",
     "睡着|clawd_view/svg__clawd-sleeping.html","惊醒|clawd_view/svg__clawd-wake.html","搭积木|clawd_view/svg__clawd-working-building.html",
     "搬东西|clawd_view/svg__clawd-working-carrying.html","调试|clawd_view/svg__clawd-working-debugger.html","杂耍|clawd_view/svg__clawd-working-juggling.html","扫除|clawd_view/svg__clawd-working-sweeping.html",
     "思考|clawd_view/svg__clawd-working-thinking.html","代码老板|clawd_view/svg__clawd-working-typing-boss.html","打代码|clawd_view/svg__clawd-working-typing.html","超频思考|clawd_view/svg__clawd-working-ultrathink.html",
     "变魔术|clawd_view/svg__clawd-working-wizard.html"
};
 private String byName(String v){
  for(int i=0;i<NAMES.length;i++){
   int k=NAMES[i].indexOf(124);
   if(k<0)continue;
   if(NAMES[i].substring(0,k).equals(v))return NAMES[i].substring(k+1);
  }
  return null;
 }
 private static final String[] DRAGL={
     "别拖我……","晕了晕了","轻点呀","你要带我去哪","放我下来","我不好搬的","哎呀哎呀","晃得我头晕","你把我搬走干嘛","抓紧我","转圈圈好好玩","再转我要吐了"};
 private static final int[] BC={0xFFFFF0F8,0xFFFFEBEE,0xFFFFF8E1,0xFFE8F5E9,0xFFE3F2FD,0xFFF3E5F5,0xFFFFF3E0,0xFFE0F7FA,0xFFFCE4EC,0xFFEDE7F6,0xFFF1F8E9,0xFFFFFDE7,0xFFE8EAF6,0xFFFBE9E7,0xFFE0F2F1,0xFFFFF9C4};
 private static final int[] BS={0xFFF48FB1,0xFFE57373,0xFFFFCC80,0xFF81C784,0xFF64B5F6,0xFFBA68C8,0xFFFFB74D,0xFF4DD0E1,0xFFF06292,0xFF9575CD,0xFFAED581,0xFFFFD54F,0xFF7986CB,0xFFFF8A65,0xFF4DB6AC,0xFFFFB300};

 public IBinder onBind(Intent i){return null;}

 public int onStartCommand(Intent i,int f,int id){
  channel();startForeground(7,notification());show();
  monitor=new CompanionMonitor(this,(line,ht)->{heat=ht;say(line);});
  monitor.start();
  lastTouch=System.currentTimeMillis();
  h.postDelayed(ticker,1200);
  h.postDelayed(watcher,4000);
  return START_STICKY;
 }

 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI\u5c0f\u871e\u87f9",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}

 private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI\u5c0f\u871e\u87f9").setContentText("Clawd \u966a\u7740\u4f60").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}

 /* ---------------- build the window ---------------- */
 private void show(){
  if(root!=null)return;
  wm=(WindowManager)getSystemService(WINDOW_SERVICE);
  root=new FrameLayout(this);
  float d=getResources().getDisplayMetrics().density;

  glow=new View(this);
  GradientDrawable gd=new GradientDrawable();
  gd.setShape(GradientDrawable.OVAL);
  gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
  gd.setGradientRadius(232f);
  gd.setColors(new int[]{0x7AFF8FA8,0x4DFF8AA0,0x24FF8698,0x00FF8698});
  glow.setBackground(gd);
  glow.setTranslationY(20f);
  FrameLayout.LayoutParams gp=new FrameLayout.LayoutParams(440,440);
  gp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
  root.addView(glow,gp);

  crabView=new WebView(this);
  crabView.setBackgroundColor(0x00000000);
  crabView.setVerticalScrollBarEnabled(false);
  crabView.setHorizontalScrollBarEnabled(false);
  WebSettings ws=crabView.getSettings();
  ws.setAllowFileAccess(true);
  ws.setAllowContentAccess(true);
  ws.setJavaScriptEnabled(true);
  ws.setAllowFileAccessFromFileURLs(true);
  ws.setAllowUniversalAccessFromFileURLs(true);
  FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(480,480);
  cp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
  root.addView(crabView,cp);

  bubble=new TextView(this);
  bubble.setTextColor(Color.rgb(80,48,62));
  bubble.setTextSize(12.5f);
  bubble.setGravity(Gravity.CENTER);
  bubble.setPadding((int)(13*d),(int)(7*d),(int)(13*d),(int)(7*d));
  bubble.setVisibility(View.GONE);
  applyBubbleColor(0);
  FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
  bp.gravity=Gravity.TOP|Gravity.START;
  bp.leftMargin=bubX();bp.topMargin=bubY();
  bubble.setLayoutParams(bp);
  root.addView(bubble,bp);
  bubbleDrag();

  lp=new WindowManager.LayoutParams(520,660,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    PixelFormat.TRANSLUCENT);
  lp.gravity=Gravity.TOP|Gravity.START;lp.x=30;lp.y=180;
  wm.addView(root,lp);
  setAction(IDLE);
  attachTouch();
 }

 /* ---------- draggable bubble ---------- */
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density);}
 private int bubX(){return getSharedPreferences("pet",0).getInt("bx",dp(58));}
 private int bubY(){return getSharedPreferences("pet",0).getInt("by",dp(74));}
 private void bubbleDrag(){
  bubble.setOnTouchListener(new android.view.View.OnTouchListener(){
   float sx,sy;int ox,oy;boolean moved2;
   public boolean onTouch(android.view.View v,android.view.MotionEvent e){
    FrameLayout.LayoutParams p=(FrameLayout.LayoutParams)bubble.getLayoutParams();
    switch(e.getAction()){
     case android.view.MotionEvent.ACTION_DOWN:
      sx=e.getRawX();sy=e.getRawY();ox=p.leftMargin;oy=p.topMargin;moved2=false;
      bubble.animate().scaleX(1.06f).scaleY(1.06f).setDuration(80).start();
      return true;
     case android.view.MotionEvent.ACTION_MOVE:{
      float dx=e.getRawX()-sx,dy=e.getRawY()-sy;
      if(Math.abs(dx)>7||Math.abs(dy)>7)moved2=true;
      android.util.DisplayMetrics dm=getResources().getDisplayMetrics();
      int nx=(int)(ox+dx),ny=(int)(oy+dy);
      nx=Math.max(dp(-10),Math.min(dm.widthPixels-dp(90),nx));
      ny=Math.max(dp(-6),Math.min(dm.heightPixels-dp(60),ny));
      p.leftMargin=nx;p.topMargin=ny;
      bubble.setLayoutParams(p);
      return true;}
     case android.view.MotionEvent.ACTION_UP:
      bubble.animate().scaleX(1f).scaleY(1f).setDuration(110).start();
      FrameLayout.LayoutParams q=(FrameLayout.LayoutParams)bubble.getLayoutParams();
      getSharedPreferences("pet",0).edit().putInt("bx",q.leftMargin).putInt("by",q.topMargin).apply();
      if(!moved2){colorIdx=(colorIdx+1)%BC.length;applyBubbleColor(colorIdx);
       bubble.animate().scaleX(1.12f).scaleY(1.12f).setDuration(90)
        .withEndAction(()->bubble.animate().scaleX(1f).scaleY(1f).setDuration(130).start()).start();}
      return true;
    }
    return false;
   }
  });
 }

 private void applyBubbleColor(int i){
  if(bubble==null)return;
  GradientDrawable g=new GradientDrawable();
  g.setColor(BC[i]);
  g.setStroke((int)(1.4f*getResources().getDisplayMetrics().density),BS[i]);
  g.setCornerRadius(16*getResources().getDisplayMetrics().density);
  bubble.setBackground(g);
 }

 /* ---------------- action loading ---------------- */
 private void setAction(String asset){
  if(asset==null||asset.isEmpty())return;
  String a=asset.trim();
  if(!a.contains("/")){
   String hit=byName(a);
   if(hit!=null)a=hit;
   else if(a.endsWith(".html"))a="clawd_view/"+a;
   else a="clawd_view/svg__"+a+".html";
  }
  curAction=a;
  int idx=-1;
  for(int i=0;i<ACTIONS.length;i++){if(ACTIONS[i].equals(a)){idx=i;break;}}
  if(idx<0)idx=Math.abs(a.hashCode());
  colorIdx=idx%BC.length;
  applyBubbleColor(colorIdx);
  try{crabView.loadUrl("file:///android_asset/"+a);}catch(Exception e){}
 }
 private void randomAction(){if(ACTIONS.length==0)return;setAction(ACTIONS[(int)(Math.random()*ACTIONS.length)]);}

 /* ---------------- touch ---------------- */
 private void attachTouch(){
  android.view.View.OnTouchListener l=(v,e)->handleTouch(e);
  root.setOnTouchListener(l);
  crabView.setOnTouchListener(l);
  crabView.setClickable(false);
  crabView.setLongClickable(false);
 }
 private boolean handleTouch(MotionEvent e){
   switch(e.getAction()){
    case MotionEvent.ACTION_DOWN:
     lastTouch=System.currentTimeMillis();lone=0;
     downTime=lastTouch;downX=e.getRawX();downY=e.getRawY();
     startRawX=e.getRawX();startRawY=e.getRawY();
     baseLx=lp.x;baseLy=lp.y;
     lastMx=e.getRawX();lastMt=downTime;vx=0;moved=false;dragSaid=false;
     crabView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(90).start();
     return true;
    case MotionEvent.ACTION_MOVE:{
     float dx=e.getRawX()-downX,dy=e.getRawY()-downY;
     if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;
      if(moved&&!dragSaid){dragSaid=true;say(pick(DRAGL));}
     long nt=System.currentTimeMillis();
     if(nt-lastMt>16){vx=(e.getRawX()-lastMx)/(float)(nt-lastMt);lastMx=e.getRawX();lastMt=nt;}
     if(moved){
      int nx=(int)(baseLx+(e.getRawX()-startRawX));
      int ny=(int)(baseLy+(e.getRawY()-startRawY));
      android.util.DisplayMetrics dmx=getResources().getDisplayMetrics();
      nx=Math.max(-20,Math.min(dmx.widthPixels-500,nx));
      ny=Math.max(-180,Math.min(dmx.heightPixels-480,ny));
      lp.x=nx;lp.y=ny;wm.updateViewLayout(root,lp);
      crabView.setScaleX(1.15f);crabView.setScaleY(1.15f);
      crabView.setRotation((e.getRawX()-startRawX)*0.55f);
      long t=System.currentTimeMillis();
      if(t-lastTrail>260){lastTrail=t;trail();}
     }
     return true;}
    case MotionEvent.ACTION_UP:{
     if(moved){
      spinFinish();
      if(Math.abs(vx)>1100&&System.currentTimeMillis()-lastGlide>2500)fling(vx);
     }else{
      crabView.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
      long dur=System.currentTimeMillis()-downTime;
      if(dur>650){showMenu();}
      else tap();
     }
     return true;}
    case MotionEvent.ACTION_CANCEL:
     if(moved){spinFinish();moved=false;}
     return true;
   }
   return true;
 }

 private void tap(){
  long now=System.currentTimeMillis();
  boolean wasSleeping=curAction.equals(SLEEP)||curAction.equals(SLEEPY)||curAction.equals(DOZE);
  if(now-comboStart>2000){combo=0;comboStart=now;}
  combo++;
  if(now-lastTap<330){
   setAction(PEEK);
   burst();say(pick(DOUBLE));
  }else{
   if(wasSleeping){
    waking=true;say(pick(WAKE2));setAction(WAKE);
    h.postDelayed(()->{waking=false;randomAction();},1300);
   }else{
    randomAction();
   }
   burst();say(LINES[(int)(Math.random()*LINES.length)]);
  }
  if(combo==3){h.postDelayed(()->{say(pick(C3));ring();},260);}
  else if(combo==5){h.postDelayed(()->{say(pick(C5));burst();},260);}
  else if(combo>=8){h.postDelayed(()->{say(pick(C8));burst();burst();},260);combo=0;}
  tapRun++;
  if(tapRun>=9){
   tapRun=0;
   say(pick(new String[]{"\u6211\u5148\u8eb2\u4e00\u4e0b","\u4e0d\u7ed9\u4f60\u6233\u4e86","\u85cf\u8d77\u6765\u4e86"}));
   h.postDelayed(()->root.setVisibility(View.INVISIBLE),350);
   h.postDelayed(()->{root.setVisibility(View.VISIBLE);say(pick(new String[]{"\u6211\u56de\u6765\u5566","\u60f3\u6211\u4e86\u5427","\u8eb2\u4e0d\u4f4f\u7684"}));},1900);
   lastTap=now;lastTouch=now;lone=0;
   return;
  }
  lastTap=now;lastTouch=now;lone=0;
  if(monitor!=null)monitor.touched();
  sbPush("gesture","tap");
 }

 private void spinFinish(){
  final float cur=crabView.getRotation();
  float target=Math.round(cur/360f)*360f;
  crabView.animate().rotation(target).scaleX(1f).scaleY(1f).setDuration(220)
   .withEndAction(()->crabView.setRotation(0f)).start();
 }
 private void fling(float v){
  lastGlide=System.currentTimeMillis();
  say(pick(FLING));
  crabView.animate().rotationBy(720f).setDuration(430).start();
  final int w=getResources().getDisplayMetrics().widthPixels;
  final int target=v>0?(w-500):(-20);
  final int from=lp.x;
  glide(from,target,420,()->{
   h.postDelayed(()->{
    setAction(curAction);
    glide(lp.x,Math.max(-20,Math.min(w-500,from)),700,null);
   },1100);
  });
 }

 private void glide(int from,int to,long dur,Runnable after){
  final long t0=System.currentTimeMillis();
  h.post(new Runnable(){public void run(){
   float k=Math.min(1f,(System.currentTimeMillis()-t0)/(float)dur);
   float e=1f-(1f-k)*(1f-k);
   lp.x=(int)(from+(to-from)*e);
   try{wm.updateViewLayout(root,lp);}catch(Exception ex){}
   if(k<1f)h.postDelayed(this,16);
   else if(after!=null)after.run();
  }});
 }

 /* ---------------- bubble ---------------- */
 private void say(String s){
  bubble.setText(s);
  if(lp.y<-60){lp.y=-60;try{wm.updateViewLayout(root,lp);}catch(Exception e){}}
  bubble.setVisibility(View.VISIBLE);
  bubble.setAlpha(0f);bubble.setScaleY(0.86f);
  bubble.animate().alpha(1f).scaleY(1f).setDuration(210).start();
  h.removeCallbacks(hide);h.postDelayed(hide,6000);
 }
 Runnable hide=new Runnable(){public void run(){bubble.animate().alpha(0f).setDuration(240)
   .withEndAction(()->bubble.setVisibility(View.GONE)).start();}};

 /* ---------------- particles ---------------- */
 private void spawn(boolean small){
  TextView e=new TextView(this);
  e.setText(SYMS[(int)(Math.random()*SYMS.length)]);
  e.setTextSize(small?15+(int)(Math.random()*8):18+(int)(Math.random()*14));
  e.setTextColor(HUES[(int)(Math.random()*HUES.length)]);
  root.addView(e,new FrameLayout.LayoutParams(-2,-2));
  e.setX(200+(float)Math.random()*120);
  e.setY(390+(float)Math.random()*70);
  e.setAlpha(0.95f);
  e.animate().translationYBy(-(60+(float)Math.random()*90))
   .translationXBy((float)(Math.random()-0.5)*80)
   .rotation((float)(Math.random()*100-50))
   .scaleX(small?0.7f:0.5f).scaleY(small?0.7f:0.5f).alpha(0)
   .setDuration(small?900+(long)(Math.random()*400):1100+(long)(Math.random()*700))
   .withEndAction(()->{try{root.removeView(e);}catch(Exception ex){}}).start();
 }
 private void burst(){ring();int n=3+(int)(Math.random()*3);for(int i=0;i<n;i++)h.postDelayed(()->spawn(false),(long)(Math.random()*280));}
 private void trail(){spawn(true);}
 private void ring(){
  View r=new View(this);
  GradientDrawable gd=new GradientDrawable();
  gd.setShape(GradientDrawable.OVAL);gd.setStroke(4,0xFFFF8FC0);gd.setColor(Color.TRANSPARENT);
  r.setBackground(gd);
  FrameLayout.LayoutParams rp=new FrameLayout.LayoutParams(70,70);
  rp.leftMargin=225;rp.topMargin=420;
  root.addView(r,rp);
  r.animate().scaleX(3.2f).scaleY(3.2f).alpha(0f).setDuration(650)
   .withEndAction(()->{try{root.removeView(r);}catch(Exception ex){}}).start();
 }

 /* ---------------- menu ---------------- */
 private void showMenu(){
  if(popup!=null&&popup.isShowing()){popup.dismiss();popup=null;return;}
  float d=getResources().getDisplayMetrics().density;
  LinearLayout box=new LinearLayout(this);
  box.setOrientation(LinearLayout.VERTICAL);
  int pad=(int)(5*d);box.setPadding(pad,pad,pad,pad);
  GradientDrawable bg=new GradientDrawable();
  bg.setColor(Color.argb(248,255,247,252));bg.setCornerRadius(14*d);
  bg.setStroke((int)(1.5f*d),Color.rgb(244,176,204));
  box.setBackground(bg);
  String[] items={"\u6362\u4e2a\u52a8\u4f5c","\u8bf4\u53e5\u8bdd","\u53d8\u4e2a\u8272","\u8eb2\u4e00\u4e0b"};
  for(final String t:items){
   TextView tv=new TextView(this);
   tv.setText(t);tv.setTextSize(14);tv.setTextColor(Color.rgb(90,55,70));
   tv.setPadding((int)(16*d),(int)(10*d),(int)(16*d),(int)(10*d));
   tv.setOnClickListener(v->{if(popup!=null)popup.dismiss();
    if(t.equals("\u6362\u4e2a\u52a8\u4f5c")){randomAction();burst();say(LINES[(int)(Math.random()*LINES.length)]);}
    else if(t.equals("\u8bf4\u53e5\u8bdd")){say(LINES[(int)(Math.random()*LINES.length)]);}
    else if(t.equals("\u53d8\u4e2a\u8272")){colorIdx=(colorIdx+1)%BC.length;applyBubbleColor(colorIdx);}
    else{root.setVisibility(View.INVISIBLE);h.postDelayed(()->root.setVisibility(View.VISIBLE),1600);}});
   box.addView(tv,new LinearLayout.LayoutParams(-1,-2));
  }
  popup=new PopupWindow(box,(int)(150*d),WindowManager.LayoutParams.WRAP_CONTENT,true);
  popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
  popup.setOutsideTouchable(true);
  popup.showAtLocation(root,Gravity.NO_GRAVITY,lp.x+40,lp.y+60);
 }

 /* ---------------- 1s ticker: loneliness / time / battery / random ---------------- */
 private int tickCount=0;
 private final Runnable ticker=new Runnable(){public void run(){
  try{
   long now=System.currentTimeMillis();
   long idleMin=(now-lastTouch)/60000L;
   if(!waking){
    if(idleMin>=20&&lone<3){
     lone=3;setAction(SLEEPY);
     if(Math.random()<0.6)say(LONELY[(int)(Math.random()*LONELY.length)]);
    }else if(idleMin>=10&&lone<2){lone=2;setAction(SLEEP);}
    else if(idleMin>=5&&lone<1){lone=1;setAction(DOZE);}
   }
   if(tickCount%60==0){
    int hr=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    if(lone==0){
     if(hr>=0&&hr<6)setAction(SLEEP);
     else if(hr>=6&&hr<9)setAction(HAPPY);
     else if(hr>=22)setAction(DOZE);
    }
   }
   if(tickCount%30==0)batteryCheck();
   if(tickCount>0&&tickCount%20==0)sbPoll();
   if(tickCount>0&&tickCount%1200==0&&lone==0&&Math.random()<0.35){randomAction();say(pick(MURMUR));}
  }catch(Exception e){}
  tickCount++;
  h.postDelayed(this,1000);
 }};

 private void batteryCheck(){
  try{
   Intent b=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
   if(b==null)return;
   int lv=b.getIntExtra("level",100);
   int pl=b.getIntExtra("plugged",0);
   if(lastPlug!=-1&&pl!=0&&lastPlug==0){say(pick(new String[]{"充电啦，我守着你","插上电了，安心","我陪你充到满","有电了，我就不担心了"}));setAction(HAPPY);ring();}
   if(lastPlug!=-1&&pl==0&&lastPlug!=0){say(pick(new String[]{"拔电了？省着点用","注意电量","怎么拔了，快插回去"}));}
   if(lv<=15&&lv!=lastBat)say(pick(new String[]{"电量只剩"+lv+"%","快没电了，插上","只有"+lv+"%，我担心"}));
   lastBat=lv;lastPlug=pl;
  }catch(Exception e){}
 }

  /* ---------------- Supabase bridge ---------------- */
 private static final String SB_URL="https://xkqqryvwwocnrzwgirje.supabase.co";
 private static final String SB_KEY=key();
 private static String key(){return new String(android.util.Base64.decode("c2Jfc2VjcmV0X2NFMU1hQ09QYjJuOXA2UEo5Mkt0MXdfZldpQjFLX1o=",android.util.Base64.DEFAULT));}
 private String lastSpeech="",lastAct="";
 private void sbPoll(){
  new Thread(()->{
   try{
    String s=sbGet("speech");
    if(s!=null&&!s.isEmpty()&&!s.equals(lastSpeech)){lastSpeech=s;final String v=s;h.post(()->say(v));}
    String a=sbGet("action");
    if(a!=null&&!a.isEmpty()&&!a.equals(lastAct)){lastAct=a;final String v=a;h.post(()->setAction(v));}
   }catch(Exception e){}
  }).start();
 }
 private String sbGet(String key)throws Exception{
  java.net.URL u=new java.net.URL(SB_URL+"/rest/v1/pet_state?state_key=eq."+key+"&select=state_value&limit=1");
  java.net.HttpURLConnection c=(java.net.HttpURLConnection)u.openConnection();
  c.setRequestProperty("apikey",SB_KEY);
  c.setRequestProperty("Authorization","Bearer "+SB_KEY);
  c.setConnectTimeout(8000);c.setReadTimeout(8000);
  java.io.InputStream is;
  try{is=c.getInputStream();}catch(Exception e){is=c.getErrorStream();}
  if(is==null)return null;
  java.io.BufferedReader r=new java.io.BufferedReader(new java.io.InputStreamReader(is,"UTF-8"));
  StringBuilder sb=new StringBuilder();String l;
  while((l=r.readLine())!=null)sb.append(l);
  r.close();
  String b=sb.toString();
  int i=b.indexOf("\"state_value\":\"");
  if(i<0)return null;
  int j=b.indexOf("\"",i+15);
  if(j<0)return null;
  return b.substring(i+15,j);
 }
 private void sbPush(final String key,final String val){
  new Thread(()->{
   try{
    java.net.URL u=new java.net.URL(SB_URL+"/rest/v1/pet_state");
    java.net.HttpURLConnection c=(java.net.HttpURLConnection)u.openConnection();
    c.setRequestMethod("POST");c.setDoOutput(true);
    c.setRequestProperty("apikey",SB_KEY);
    c.setRequestProperty("Authorization","Bearer "+SB_KEY);
    c.setRequestProperty("Content-Type","application/json");
    c.setRequestProperty("Prefer","resolution=merge-duplicates,return=minimal");
    String safe=val.replace("\\","").replace("\"","'").replace("\n"," ").replace("\r"," ");
    String body="[{\"state_key\":\""+key+"\",\"state_value\":\""+safe+"\"}]";
    c.getOutputStream().write(body.getBytes("UTF-8"));
    c.getResponseCode();
   }catch(Exception e){}
  }).start();
 }


/* ---------------- screenshot watcher ---------------- */
 private final Runnable watcher=new Runnable(){public void run(){
  try{
   String[] dirs={Environment.getExternalStorageDirectory()+"/Pictures/Screenshots",
                  Environment.getExternalStorageDirectory()+"/DCIM/Screenshots",
                  Environment.getExternalStorageDirectory()+"/Pictures/\u622a\u5c4f"};
   for(final String p:dirs){
    File f=new File(p);
    if(!f.exists()||!f.isDirectory())continue;
    android.os.FileObserver fo=new android.os.FileObserver(p){
     @Override public void onEvent(int ev,String name){
      if(name==null)return;
      if((ev&FileObserver.CREATE)!=0||(ev&FileObserver.MOVED_TO)!=0){
       lastTouch=System.currentTimeMillis();lone=0;
       h.post(()->{setAction(PEEK);burst();say(pick(SHOT));});
      }
     }
    };
    fo.startWatching();
   }
  }catch(Exception e){}
 }};

 public void onDestroy(){
  try{if(monitor!=null)monitor.stop();}catch(Exception e){}
  try{if(popup!=null&&popup.isShowing())popup.dismiss();}catch(Exception e){}
  try{if(root!=null&&wm!=null)wm.removeView(root);}catch(Exception e){}
  h.removeCallbacksAndMessages(null);
  super.onDestroy();
 }
}
