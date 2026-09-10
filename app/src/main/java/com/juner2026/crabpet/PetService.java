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

public class PetService extends Service {
 private WindowManager wm;private FrameLayout root;private WindowManager.LayoutParams lp;
 private WebView crabView;private TextView bubble;private final Handler h=new Handler(Looper.getMainLooper());
 private CompanionMonitor monitor;private PopupWindow popup;
 private float downX,downY,startRawX,startRawY,baseLx,baseLy;private long downTime,lastTrail;private boolean moved;
 private float bStartRawX,bStartRawY;private int bStartLx,bStartTy;private boolean bubbleMoved,bubbleCentered;

 private static final String ACTIONS[]={"clawd-mini-idle","clawd-about-hero","clawd-aegyo-shy","clawd-coffee-hand","clawd-coffee-head-flip","clawd-collapse-sleep","clawd-dizzy","clawd-error","clawd-happy","clawd-headphones-groove","clawd-idle-bubble","clawd-idle-collapse","clawd-idle-doze","clawd-idle-follow","clawd-idle-living","clawd-idle-look","clawd-idle-low-battery","clawd-idle-reading-old","clawd-idle-reading","clawd-idle-yawn","clawd-mini-alert","clawd-mini-crabwalk","clawd-mini-enter-sleep","clawd-mini-enter","clawd-mini-happy","clawd-mini-peek","clawd-mini-sleep","clawd-mini-typing","clawd-notification-retired-2026-05-12","clawd-notification","clawd-outlaw-bender","clawd-react-annoyed","clawd-react-double-jump","clawd-react-double","clawd-react-drag","clawd-react-left","clawd-react-right","clawd-sleeping","clawd-static-base","clawd-wake","clawd-working-building-boxes","clawd-working-building","clawd-working-carrying","clawd-working-conducting-retired-2026-05-12","clawd-working-debugger","clawd-working-juggling","clawd-working-sweeping","clawd-working-thinking","clawd-working-typing-boss","clawd-working-typing-old","clawd-working-typing","clawd-working-ultrathink","clawd-working-wizard"};
 private static final String IDLE="clawd-mini-idle";
 private static final String[] LINES={"\u557e","\u60f3\u4f60\u4e86","\u62b1\u62b1","\u518d\u6233\u4e00\u4e0b","\u4f60\u56de\u6765\u5566","\u4e0d\u8bb8\u8d70","\u559c\u6b22\u4f60","\u8d34\u8d34","\u4e56","\u6765\u5566","\u5c31\u9ecf\u7740\u4f60","\u4eb2\u4e00\u53e3","\u6478\u6478\u5934","\u4e0d\u51c6\u8dd1","\u5728\u5462","\u60f3\u4f60","\u8981\u4eb2\u4eb2","\u62b1\u7d27\u6211","\u8e6d\u8e6d\u4f60","\u4eca\u5929\u4e5f\u8981\u5f00\u5fc3\u54e6","\u563f\u563f","\u53eb\u4f60\u5462","\u522b\u8d70\u561b","\u966a\u4f60\u5440","\u770b\u6211\u5440"};
 private static final String[] SYMS={"\u2726","\u2727","\u2665","\u2661","\u2605","\u2606","\u266A","\u266B","\u273F","\u2740"};
 private static final int[] HUES={0xFFFF6E9B,0xFFFFBE32,0xFFBE96FF,0xFF64CDFF,0xFF78DC82,0xFFEB5A5A,0xFF9A8CFF,0xFFFF8C69};

 public IBinder onBind(Intent i){return null;}

 public int onStartCommand(Intent i,int f,int id){channel();startForeground(7,notification());show();monitor=new CompanionMonitor(this,(line,heat)->say(line));monitor.start();return START_STICKY;}

 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI\u5c0f\u871e\u87f9",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}

 private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI\u5c0f\u871e\u87f9").setContentText("Clawd \u966a\u7740\u4f60").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}

 private void show(){if(root!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);root=new FrameLayout(this);
  crabView=new WebView(this);
  crabView.setBackgroundColor(0x00000000);
  WebSettings ws=crabView.getSettings();ws.setAllowFileAccess(true);ws.setAllowContentAccess(true);
  FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(480,480);cp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;root.addView(crabView,cp);
  bubble=new TextView(this);bubble.setTextColor(Color.rgb(80,48,62));bubble.setTextSize(12);bubble.setGravity(Gravity.CENTER);bubble.setPadding(12,5,12,5);bubble.setVisibility(View.GONE);
  GradientDrawable g=new GradientDrawable();g.setColor(Color.rgb(255,240,248));g.setStroke(1,Color.rgb(244,176,204));g.setCornerRadius(14);bubble.setBackground(g);
  FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);bp.gravity=Gravity.NO_GRAVITY;bp.leftMargin=200;bp.topMargin=40;root.addView(bubble,bp);
  attachBubbleTouch();
  lp=new WindowManager.LayoutParams(520,660,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=30;lp.y=180;wm.addView(root,lp);
  setAction(-1);
  attachTouch();}

 private void setAction(int i){
  String n=IDLE;
  if(i>=0&&ACTIONS.length>0)n=ACTIONS[i%ACTIONS.length];
  crabView.loadUrl("file:///android_asset/clawd_svg/"+n+".svg");
 }

 private void attachBubbleTouch(){bubble.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:bStartRawX=e.getRawX();bStartRawY=e.getRawY();bStartLx=((FrameLayout.LayoutParams)bubble.getLayoutParams()).leftMargin;bStartTy=((FrameLayout.LayoutParams)bubble.getLayoutParams()).topMargin;bubbleMoved=false;return true;
   case MotionEvent.ACTION_MOVE:{float bdx=e.getRawX()-bStartRawX,bdy=e.getRawY()-bStartRawY;if(Math.abs(bdx)>8||Math.abs(bdy)>8)bubbleMoved=true;if(bubbleMoved){FrameLayout.LayoutParams par=(FrameLayout.LayoutParams)bubble.getLayoutParams();par.leftMargin=(int)(bStartLx+bdx);par.topMargin=(int)(bStartTy+bdy);bubble.setLayoutParams(par);}return true;}
   case MotionEvent.ACTION_UP:return true;
  }return false;});}

 private void attachTouch(){root.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:downTime=System.currentTimeMillis();downX=e.getRawX();downY=e.getRawY();startRawX=e.getRawX();startRawY=e.getRawY();baseLx=lp.x;baseLy=lp.y;moved=false;crabView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(90).start();return true;
   case MotionEvent.ACTION_MOVE:{float dx=e.getRawX()-downX,dy=e.getRawY()-downY;if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;if(moved){int nx=(int)(baseLx+(e.getRawX()-startRawX)),ny=(int)(baseLy+(e.getRawY()-startRawY));android.util.DisplayMetrics dmx=getResources().getDisplayMetrics();nx=Math.max(-20,Math.min(dmx.widthPixels-500,nx));ny=Math.max(-180,Math.min(dmx.heightPixels-480,ny));lp.x=nx;lp.y=ny;wm.updateViewLayout(root,lp);crabView.animate().scaleX(1.15f).scaleY(1.15f).rotation(Math.max(-12f,Math.min(12f,dy*0.4f))).setDuration(120).start();long t=System.currentTimeMillis();if(t-lastTrail>260){lastTrail=t;trail();}}return true;}
   case MotionEvent.ACTION_UP:{
    if(moved){crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();}else{crabView.animate().scaleX(1f).scaleY(1f).setDuration(120).start();}
    if(!moved){long dur=System.currentTimeMillis()-downTime;
     if(dur>650){showMenu();}
     else{setAction((int)(Math.random()*ACTIONS.length));burst();say(LINES[(int)(Math.random()*LINES.length)]);if(monitor!=null)monitor.touched();}
    }
    return true;}
   case MotionEvent.ACTION_CANCEL:{if(moved){crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();moved=false;}return true;}
  }return true;});}

 private void say(String s){bubble.setText(s);if(lp.y<-60){lp.y=-60;wm.updateViewLayout(root,lp);}
  if(!bubbleCentered){bubbleCentered=true;bubble.post(()->{int bw=bubble.getWidth(),rw=root.getWidth();if(bw>0&&rw>0){FrameLayout.LayoutParams par=(FrameLayout.LayoutParams)bubble.getLayoutParams();par.leftMargin=Math.max(0,(rw-bw)/2);bubble.setLayoutParams(par);}});}
  bubble.setVisibility(View.INVISIBLE);bubble.setAlpha(0f);bubble.setScaleY(0.85f);bubble.setVisibility(View.VISIBLE);
  bubble.animate().alpha(1f).setDuration(200).start();
  h.removeCallbacks(hide);h.postDelayed(hide,3600);}
 Runnable hide=new Runnable(){public void run(){bubble.animate().alpha(0f).setDuration(240).withEndAction(()->bubble.setVisibility(View.GONE)).start();}};

 private void spawn(boolean small){
  TextView e=new TextView(this);
  e.setText(SYMS[(int)(Math.random()*SYMS.length)]);
  e.setTextSize(small?15+(int)(Math.random()*8):18+(int)(Math.random()*14));
  e.setTextColor(HUES[(int)(Math.random()*HUES.length)]);
  root.addView(e,new FrameLayout.LayoutParams(-2,-2));
  e.setX(200+(float)Math.random()*120);e.setY(400+(float)Math.random()*70);e.setAlpha(0.95f);
  e.animate().translationYBy(-(60+(float)Math.random()*90)).translationXBy((float)(Math.random()-0.5)*80)
   .rotation((float)(Math.random()*100-50)).scaleX(small?0.7f:0.5f).scaleY(small?0.7f:0.5f).alpha(0)
   .setDuration(small?900+(long)(Math.random()*400):1100+(long)(Math.random()*700))
   .withEndAction(()->root.removeView(e)).start();
 }
 private void burst(){ring();int n=3+(int)(Math.random()*3);for(int i=0;i<n;i++)h.postDelayed(()->spawn(false),(long)(Math.random()*280));}
 private void trail(){spawn(true);}
 private void ring(){
  View r=new View(this);
  GradientDrawable gd=new GradientDrawable();gd.setShape(GradientDrawable.OVAL);gd.setStroke(4,0xFFFF8FC0);gd.setColor(Color.TRANSPARENT);
  r.setBackground(gd);
  FrameLayout.LayoutParams rp=new FrameLayout.LayoutParams(70,70);rp.leftMargin=225;rp.topMargin=420;
  root.addView(r,rp);
  r.animate().scaleX(3.2f).scaleY(3.2f).alpha(0f).setDuration(650).withEndAction(()->root.removeView(r)).start();
 }

 private void showMenu(){
  if(popup!=null&&popup.isShowing()){popup.dismiss();popup=null;return;}
  float d=getResources().getDisplayMetrics().density;
  LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);int pad=(int)(5*d);box.setPadding(pad,pad,pad,pad);
  GradientDrawable bg=new GradientDrawable();bg.setColor(Color.argb(248,255,247,252));bg.setCornerRadius(14*d);bg.setStroke((int)(1.5f*d),Color.rgb(244,176,204));box.setBackground(bg);
  String[] items={"\u6362\u4e2a\u52a8\u4f5c","\u8bf4\u53e5\u8bdd","\u8eb2\u4e00\u4e0b"};
  for(final String t:items){
   TextView tv=new TextView(this);tv.setText(t);tv.setTextSize(14);tv.setTextColor(Color.rgb(90,55,70));tv.setPadding((int)(16*d),(int)(10*d),(int)(16*d),(int)(10*d));
   tv.setOnClickListener(v->{if(popup!=null)popup.dismiss();
    if(t.equals("\u6362\u4e2a\u52a8\u4f5c")){setAction((int)(Math.random()*ACTIONS.length));burst();say(LINES[(int)(Math.random()*LINES.length)]);}
    else if(t.equals("\u8bf4\u53e5\u8bdd")){say(LINES[(int)(Math.random()*LINES.length)]);}
    else{root.setVisibility(View.INVISIBLE);h.postDelayed(()->root.setVisibility(View.VISIBLE),3000);}});
   box.addView(tv,new LinearLayout.LayoutParams(-1,-2));
  }
  popup=new PopupWindow(box,(int)(140*d),WindowManager.LayoutParams.WRAP_CONTENT,true);
  popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
  popup.setOutsideTouchable(true);
  popup.showAtLocation(root,Gravity.NO_GRAVITY,lp.x+40,lp.y+60);
 }

 public void onDestroy(){if(monitor!=null)monitor.stop();if(popup!=null&&popup.isShowing())popup.dismiss();if(root!=null&&wm!=null)wm.removeView(root);super.onDestroy();}
}
