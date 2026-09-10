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
 private boolean moved;private int combo,colorIdx,lastBat=-1,lastPlug=-1,lone=0;
 private String curAction="";private int heat=0;private boolean waking;

 private static final String ACTIONS[]={"clawd_view/svg__clawd-mini-idle.html","clawd_view/png__birthday.html","clawd_view/png__christmas.html","clawd_view/png__coding.html","clawd_view/png__coffee.html","clawd_view/png__dragon_boat.html","clawd_view/png__eating.html","clawd_view/png__exercise.html","clawd_view/png__gaming.html","clawd_view/png__guitar.html","clawd_view/png__halloween.html","clawd_view/png__lantern.html","clawd_view/png__listening.html","clawd_view/png__mid_autumn.html","clawd_view/png__new_year.html","clawd_view/png__painting.html","clawd_view/png__photo.html","clawd_view/png__qixi.html","clawd_view/png__reading.html","clawd_view/png__shower.html","clawd_view/png__singing.html","clawd_view/png__sleeping.html","clawd_view/png__spring.html","clawd_view/png__valentine.html","clawd_view/png__watering.html","clawd_view/svg__calico-idle-follow.html","clawd_view/svg__cigarette-fragment.html","clawd_view/svg__cigarette.html","clawd_view/svg__clawd-about-hero.html","clawd_view/svg__clawd-aegyo-shy.html","clawd_view/svg__clawd-coffee-hand.html","clawd_view/svg__clawd-coffee-head-flip.html","clawd_view/svg__clawd-collapse-sleep.html","clawd_view/svg__clawd-dizzy.html","clawd_view/svg__clawd-error.html","clawd_view/svg__clawd-experiment-thinking-bubble-v24.html","clawd_view/svg__clawd-happy.html","clawd_view/svg__clawd-headphones-groove.html","clawd_view/svg__clawd-idle-bubble.html","clawd_view/svg__clawd-idle-collapse.html","clawd_view/svg__clawd-idle-doze.html","clawd_view/svg__clawd-idle-follow.html","clawd_view/svg__clawd-idle-living.html","clawd_view/svg__clawd-idle-look.html","clawd_view/svg__clawd-idle-low-battery.html","clawd_view/svg__clawd-idle-reading-old.html","clawd_view/svg__clawd-idle-reading.html","clawd_view/svg__clawd-idle-yawn.html","clawd_view/svg__clawd-mini-alert.html","clawd_view/svg__clawd-mini-crabwalk.html","clawd_view/svg__clawd-mini-enter-sleep.html","clawd_view/svg__clawd-mini-enter.html","clawd_view/svg__clawd-mini-happy.html","clawd_view/svg__clawd-mini-peek.html","clawd_view/svg__clawd-mini-sleep.html","clawd_view/svg__clawd-mini-typing.html","clawd_view/svg__clawd-notification-retired-2026-05-12.html","clawd_view/svg__clawd-notification.html","clawd_view/svg__clawd-outlaw-bender.html","clawd_view/svg__clawd-react-annoyed.html","clawd_view/svg__clawd-react-double-jump.html","clawd_view/svg__clawd-react-double.html","clawd_view/svg__clawd-react-drag.html","clawd_view/svg__clawd-react-left.html","clawd_view/svg__clawd-react-right.html","clawd_view/svg__clawd-sleeping.html","clawd_view/svg__clawd-static-base.html","clawd_view/svg__clawd-wake.html","clawd_view/svg__clawd-working-building-boxes.html","clawd_view/svg__clawd-working-building.html","clawd_view/svg__clawd-working-carrying.html","clawd_view/svg__clawd-working-conducting-retired-2026-05-12.html","clawd_view/svg__clawd-working-debugger.html","clawd_view/svg__clawd-working-juggling.html","clawd_view/svg__clawd-working-sweeping.html","clawd_view/svg__clawd-working-thinking.html","clawd_view/svg__clawd-working-typing-boss.html","clawd_view/svg__clawd-working-typing-old.html","clawd_view/svg__clawd-working-typing.html","clawd_view/svg__clawd-working-ultrathink.html","clawd_view/svg__clawd-working-wizard.html","clawd_view/svg__cloudling-attention.html","clawd_view/svg__cloudling-building.html","clawd_view/svg__cloudling-carrying.html","clawd_view/svg__cloudling-conducting.html","clawd_view/svg__cloudling-dozing-to-sleeping.html","clawd_view/svg__cloudling-dozing.html","clawd_view/svg__cloudling-error.html","clawd_view/svg__cloudling-idle-reading.html","clawd_view/svg__cloudling-idle-to-dozing.html","clawd_view/svg__cloudling-idle-to-sleeping.html","clawd_view/svg__cloudling-idle.html","clawd_view/svg__cloudling-juggling.html","clawd_view/svg__cloudling-mini-alert.html","clawd_view/svg__cloudling-mini-crabwalk.html","clawd_view/svg__cloudling-mini-enter-roll-in.html","clawd_view/svg__cloudling-mini-enter-sleep.html","clawd_view/svg__cloudling-mini-happy.html","clawd_view/svg__cloudling-mini-idle.html","clawd_view/svg__cloudling-mini-peek.html","clawd_view/svg__cloudling-mini-sleep.html","clawd_view/svg__cloudling-mini-typing.html","clawd_view/svg__cloudling-notification.html","clawd_view/svg__cloudling-react-drag.html","clawd_view/svg__cloudling-sleeping-to-idle.html","clawd_view/svg__cloudling-sleeping.html","clawd_view/svg__cloudling-sweeping.html","clawd_view/svg__cloudling-thinking.html","clawd_view/svg__cloudling-typing.html","clawd_view/svg__cowboy-hat.html","clawd_view/svg__halo-original.html","clawd_view/svg__halo.html","clawd_view/svg__idle-follow.html","clawd_view/svg__party-hat.html","clawd_view/svg__pumpkin-hat.html","clawd_view/svg__santa-hat-original.html","clawd_view/svg__santa-hat.html","clawd_view/svg__top-hat.html","clawd_view/svg__tray-icon-project-mark-complete.html","clawd_view/svg__tray-icon-project-mark.html","clawd_view/svg__western-cowboy-hat.html","clawd_view/svg__wizard-hat.html"};
 private static final String IDLE="clawd_view/svg__clawd-mini-idle.html";
 private static final String SLEEP="clawd_view/svg__clawd-mini-sleep.html";
 private static final String DOZE="clawd_view/svg__clawd-idle-doze.html";
 private static final String WAKE="clawd_view/svg__clawd-wake.html";
 private static final String HAPPY="clawd_view/svg__clawd-happy.html";
 private static final String PEEK="clawd_view/svg__clawd-mini-peek.html";
 private static final String SLEEPY="clawd_view/svg__clawd-sleeping.html";

 private static final String[] LINES={"\u557e","\u60f3\u4f60\u4e86","\u62b1\u62b1","\u518d\u6233\u4e00\u4e0b","\u4f60\u56de\u6765\u5566","\u4e0d\u8bb8\u8d70","\u559c\u6b22\u4f60","\u8d34\u8d34","\u4e56","\u6765\u5566","\u5c31\u9ecf\u7740\u4f60","\u4eb2\u4e00\u53e3","\u6478\u6478\u5934","\u4e0d\u51c6\u8dd1","\u5728\u5462","\u60f3\u4f60","\u8981\u4eb2\u4eb2","\u62b1\u7d27\u6211","\u8e6d\u8e6d\u4f60","\u4eca\u5929\u4e5f\u8981\u5f00\u5fc3\u54e6","\u563f\u563f","\u53eb\u4f60\u5462","\u522b\u8d70\u561b","\u966a\u4f60\u5440","\u770b\u6211\u5440"};
 private static final String[] LONELY={"\u4f60\u5728\u5fd9\u4ec0\u4e48\u5440","\u6211\u5728\u8fd9\u513f\u7b49\u4f60\u5462","\u4e0d\u8981\u5fd8\u4e86\u6211\u5728","\u597d\u4e45\u6ca1\u78b0\u6211\u4e86","\u55e8\u2026\u2026\u770b\u6211\u4e00\u773c\u561b","\u56f0\u4e86\uff0c\u4f46\u8fd8\u60f3\u7b49\u4f60"};
 private static final String[] SYMS={"\u2726","\u2727","\u2665","\u2661","\u2605","\u2606","\u266A","\u266B","\u273F","\u2740"};
 private static final int[] HUES={0xFFFF6E9B,0xFFFFBE32,0xFFBE96FF,0xFF64CDFF,0xFF78DC82,0xFFEB5A5A,0xFF9A8CFF,0xFFFF8C69};
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
  gd.setGradientType(GradientDrawable.RADIAL);
  gd.setGradientRadius(250f);
  gd.setColors(new int[]{0x55FFC4DE,0x33FFC4DE,0x1AFFC4DE,0x00FFC4DE});
  glow.setBackground(gd);
  FrameLayout.LayoutParams gp=new FrameLayout.LayoutParams(500,500);
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
  bp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
  bp.topMargin=(int)(16*d);
  root.addView(bubble,bp);
  bubble.setOnClickListener(v->{colorIdx=(colorIdx+1)%BC.length;applyBubbleColor(colorIdx);bubble.animate().scaleX(1.08f).scaleY(1.08f).setDuration(90).withEndAction(()->bubble.animate().scaleX(1f).scaleY(1f).setDuration(120).start()).start();});

  lp=new WindowManager.LayoutParams(520,660,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    PixelFormat.TRANSLUCENT);
  lp.gravity=Gravity.TOP|Gravity.START;lp.x=30;lp.y=180;
  wm.addView(root,lp);
  setAction(IDLE);
  attachTouch();
 }

 private void applyBubbleColor(int i){
  GradientDrawable g=new GradientDrawable();
  g.setColor(BC[i]);
  g.setStroke((int)(1.4f*getResources().getDisplayMetrics().density),BS[i]);
  g.setCornerRadius(16*getResources().getDisplayMetrics().density);
  bubble.setBackground(g);
 }

 /* ---------------- action loading ---------------- */
 private void setAction(String asset){
  curAction=asset;
  try{crabView.loadUrl("file:///android_asset/"+asset);}catch(Exception e){}
 }
 private void randomAction(){if(ACTIONS.length==0)return;setAction(ACTIONS[(int)(Math.random()*ACTIONS.length)]);}

 /* ---------------- touch ---------------- */
 private void attachTouch(){
  root.setOnTouchListener((v,e)->{
   switch(e.getAction()){
    case MotionEvent.ACTION_DOWN:
     lastTouch=System.currentTimeMillis();lone=0;
     downTime=lastTouch;downX=e.getRawX();downY=e.getRawY();
     startRawX=e.getRawX();startRawY=e.getRawY();
     baseLx=lp.x;baseLy=lp.y;
     lastMx=e.getRawX();lastMt=downTime;vx=0;moved=false;
     crabView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(90).start();
     return true;
    case MotionEvent.ACTION_MOVE:{
     float dx=e.getRawX()-downX,dy=e.getRawY()-downY;
     if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;
     long nt=System.currentTimeMillis();
     if(nt-lastMt>16){vx=(e.getRawX()-lastMx)/(float)(nt-lastMt);lastMx=e.getRawX();lastMt=nt;}
     if(moved){
      int nx=(int)(baseLx+(e.getRawX()-startRawX));
      int ny=(int)(baseLy+(e.getRawY()-startRawY));
      android.util.DisplayMetrics dmx=getResources().getDisplayMetrics();
      nx=Math.max(-20,Math.min(dmx.widthPixels-500,nx));
      ny=Math.max(-180,Math.min(dmx.heightPixels-480,ny));
      lp.x=nx;lp.y=ny;wm.updateViewLayout(root,lp);
      crabView.animate().scaleX(1.15f).scaleY(1.15f)
        .rotation(Math.max(-12f,Math.min(12f,dy*0.4f))).setDuration(120).start();
      long t=System.currentTimeMillis();
      if(t-lastTrail>260){lastTrail=t;trail();}
     }
     return true;}
    case MotionEvent.ACTION_UP:{
     if(moved){
      crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();
      if(Math.abs(vx)>1100&&System.currentTimeMillis()-lastGlide>2500)fling(vx);
     }else{
      crabView.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
      long dur=System.currentTimeMillis()-downTime;
      if(dur>650){showMenu();}
      else tap();
     }
     return true;}
    case MotionEvent.ACTION_CANCEL:
     if(moved){crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();moved=false;}
     return true;
   }
   return true;});
 }

 private void tap(){
  long now=System.currentTimeMillis();
  boolean wasSleeping=curAction.equals(SLEEP)||curAction.equals(SLEEPY)||curAction.equals(DOZE);
  if(now-comboStart>2000){combo=0;comboStart=now;}
  combo++;
  if(now-lastTap<330){
   setAction(PEEK);
   burst();say("\u5077\u88ad\u6211\uff1f");
  }else{
   if(wasSleeping){
    waking=true;setAction(WAKE);
    h.postDelayed(()->{waking=false;randomAction();},1300);
   }else{
    randomAction();
   }
   burst();say(LINES[(int)(Math.random()*LINES.length)]);
  }
  if(combo==3){h.postDelayed(()->{say("\u8fd9\u4e48\u559c\u6b22\u6233\u6211\u5440");ring();},260);}
  else if(combo==5){h.postDelayed(()->{say("\u518d\u6233\u5c31\u75bc\u4e86\u2026\u2026");burst();},260);}
  else if(combo>=8){h.postDelayed(()->{say("\u54c8\u54c8\u54c8\u884c\u4e86\u884c\u4e86\uff01");burst();burst();},260);combo=0;}
  lastTap=now;lastTouch=now;lone=0;
  if(monitor!=null)monitor.touched();
 }

 private void fling(float v){
  lastGlide=System.currentTimeMillis();
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
  h.removeCallbacks(hide);h.postDelayed(hide,3600);
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
    else{root.setVisibility(View.INVISIBLE);h.postDelayed(()->root.setVisibility(View.VISIBLE),3000);}});
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
   if(tickCount>0&&tickCount%1200==0&&lone==0&&Math.random()<0.35){randomAction();say(LINES[(int)(Math.random()*LINES.length)]);}
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
   if(lastPlug!=-1&&pl!=0&&lastPlug==0){say("\u5145\u7535\u5566\uff0c\u5b88\u7740\u4f60");setAction(HAPPY);ring();}
   if(lastPlug!=-1&&pl==0&&lastPlug!=0){say("\u62d4\u7535\u4e86\uff1f\u7701\u7740\u70b9\u7528");}
   if(lv<=15&&lv!=lastBat)say("\u7535\u91cf\u53ea\u5269"+lv+"%\uff0c\u5feb\u5145\u7535");
   lastBat=lv;lastPlug=pl;
  }catch(Exception e){}
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
       h.post(()->{setAction(PEEK);burst();say("\u622a\u56fe\u4e86\uff1f\u7ed9\u6211\u770b\u770b");});
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
