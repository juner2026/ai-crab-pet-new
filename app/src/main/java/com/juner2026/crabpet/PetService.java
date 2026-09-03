package com.juner2026.crabpet;

import android.app.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.GradientDrawable;import android.os.*;import android.view.*;import android.widget.*;
import java.io.IOException;

public class PetService extends Service {
 private WindowManager wm;private FrameLayout root;private WindowManager.LayoutParams lp;
 private CrabView crabView;private TextView bubble;private final Handler h=new Handler(Looper.getMainLooper());
 private CompanionMonitor monitor;
 private float downX,downY,startRawX,startRawY,baseLx,baseLy;private long downTime;private boolean moved;
 private float bStartRawX,bStartRawY;private int bStartLx,bStartTy;private boolean bubbleMoved;
 private int tapCount,effectIx,posIx;

 private static final String[] NAMES={"gaming","singing","coffee","guitar","valentine","qixi","eating","sleeping","coding","painting","reading","birthday","christmas","dragon_boat","exercise","halloween","lantern","mid_autumn","new_year","photo","shower","spring","watering"};
 private static final String[] LINES={"\u557e","\u60f3\u4f60\u4e86","\u62b1\u62b1","\u518d\u6233\u4e00\u4e0b","\u4f60\u56de\u6765\u5566","\u4e0d\u8bb8\u8d70","\u559c\u6b22\u4f60","\u8d34\u8d34","\u4e56","\u6765\u5566","\u5c31\u9ecf\u7740\u4f60","\u4eb2\u4e00\u53e3","\u6478\u6478\u5934","\u4e0d\u51c6\u8dd1","\u5728\u5462","\u60f3\u4f60","\u8981\u4eb2\u4eb2","\u62b1\u7d27\u6211","\u8e6d\u8e6d\u4f60","\u4eca\u5929\u4e5f\u8981\u5f00\u5fc3\u54e6","\u563f\u563f","\u53eb\u4f60\u5462","\u522b\u8d70\u561b","\u966a\u4f60\u5440","\u770b\u6211\u5440"};

 public IBinder onBind(Intent i){return null;}

 public int onStartCommand(Intent i,int f,int id){channel();startForeground(7,notification());show();monitor=new CompanionMonitor(this,(line,heat)->say(line));monitor.start();return START_STICKY;}

 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI\u5c0f\u871e\u87f9",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}

 private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI\u5c0f\u871e\u87f9").setContentText("Clawd \u966a\u7740\u4f60").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}

 private void show(){if(root!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);root=new FrameLayout(this);
  crabView=new CrabView(this);crabView.setAction(0);
  FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(420,420);cp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;root.addView(crabView,cp);
  bubble=new TextView(this);bubble.setText("\u565c");bubble.setTextColor(Color.rgb(80,48,62));bubble.setTextSize(12);bubble.setGravity(Gravity.CENTER);bubble.setPadding(12,5,12,5);
  GradientDrawable g=new GradientDrawable();g.setColor(Color.rgb(255,240,248));g.setStroke(1,Color.rgb(244,176,204));g.setCornerRadius(14);bubble.setBackground(g);
  FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);bp.gravity=Gravity.NO_GRAVITY;bp.leftMargin=180;bp.topMargin=34;root.addView(bubble,bp);
  bubble.post(()->{int bw=bubble.getWidth();int rw=root.getWidth();if(bw>0&&rw>0){FrameLayout.LayoutParams par=(FrameLayout.LayoutParams)bubble.getLayoutParams();par.leftMargin=Math.max(0,(rw-bw)/2);bubble.setLayoutParams(par);}});
  attachBubbleTouch();
  lp=new WindowManager.LayoutParams(460,600,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=30;lp.y=180;wm.addView(root,lp);
  h.post(loop);attachTouch();}

 private void attachBubbleTouch(){bubble.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:bStartRawX=e.getRawX();bStartRawY=e.getRawY();bStartLx=((FrameLayout.LayoutParams)bubble.getLayoutParams()).leftMargin;bStartTy=((FrameLayout.LayoutParams)bubble.getLayoutParams()).topMargin;bubbleMoved=false;return true;
   case MotionEvent.ACTION_MOVE:float bdx=e.getRawX()-bStartRawX,bdy=e.getRawY()-bStartRawY;if(Math.abs(bdx)>8||Math.abs(bdy)>8)bubbleMoved=true;if(bubbleMoved){FrameLayout.LayoutParams par=(FrameLayout.LayoutParams)bubble.getLayoutParams();par.leftMargin=(int)(bStartLx+bdx);par.topMargin=(int)(bStartTy+bdy);bubble.setLayoutParams(par);}return true;
   case MotionEvent.ACTION_UP:return true;
  }return false;});}

 Runnable loop=new Runnable(){public void run(){crabView.nextFrame();h.postDelayed(this,90);}};

 private void attachTouch(){root.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:downTime=System.currentTimeMillis();downX=e.getRawX();downY=e.getRawY();startRawX=e.getRawX();startRawY=e.getRawY();baseLx=lp.x;baseLy=lp.y;moved=false;crabView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(90).start();return true;
   case MotionEvent.ACTION_MOVE:float dx=e.getRawX()-downX,dy=e.getRawY()-downY;if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;if(moved){lp.x=(int)(baseLx+(e.getRawX()-startRawX));lp.y=(int)(baseLy+(e.getRawY()-startRawY));wm.updateViewLayout(root,lp);crabView.animate().scaleX(1.15f).scaleY(1.15f).rotation(Math.max(-12f,Math.min(12f,dy*0.4f))).setDuration(120).start();effect();}return true;
   case MotionEvent.ACTION_UP:if(moved){crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();}else{crabView.animate().scaleX(1f).scaleY(1f).setDuration(120).start();}long dur=System.currentTimeMillis()-downTime;if(!moved){if(dur>650){say("\u85cf\u4e24\u79d2");root.setVisibility(View.INVISIBLE);h.postDelayed(()->root.setVisibility(View.VISIBLE),2200);}else{tapCount++;posIx=(int)(Math.random()*NAMES.length);crabView.setAction(posIx);effect();say(LINES[(int)(Math.random()*LINES.length)]);if(monitor!=null)monitor.touched();h.postDelayed(()->tapCount=0,900);}}return true;
  }return true;});}

 private void say(String s){bubble.setText(s);bubble.setVisibility(View.VISIBLE);h.removeCallbacks(hide);h.postDelayed(hide,3600);}
 Runnable hide=new Runnable(){public void run(){bubble.setVisibility(View.GONE);}};

 private void effect(){effectIx++;TextView e=new TextView(this);String[] pool={"\u2726","\u2665","\u266A","\u266B","\u2601","!"};e.setText(pool[effectIx%pool.length]);e.setTextSize(24);e.setTextColor(new int[]{Color.rgb(255,190,50),Color.rgb(255,110,155),Color.rgb(190,150,255),Color.rgb(255,110,155),Color.rgb(100,205,255),Color.rgb(225,70,70)}[effectIx%6]);root.addView(e,new FrameLayout.LayoutParams(-2,-2));e.setX((float)(170+Math.random()*70));e.setY(120);e.animate().translationY(-45).alpha(0).setDuration(1500).withEndAction(()->root.removeView(e)).start();}

 public void onDestroy(){if(monitor!=null)monitor.stop();if(root!=null&&wm!=null)wm.removeView(root);super.onDestroy();}

 private static class CrabView extends View{
  private static final int FRAME_COUNT=10;
  private Bitmap[] staticFrames;private Bitmap[] sheets;private Bitmap cur;
  private int action=0,frame=0;private float baseY=0,sheetFrameWidth,baseFrame=0;
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
  CrabView(Context c){super(c);staticFrames=new Bitmap[NAMES.length];sheets=new Bitmap[NAMES.length];for(int i=0;i<NAMES.length;i++){staticFrames[i]=load(c,"clawd/"+NAMES[i]+".png");sheets[i]=load(c,"clawd_sheet/"+NAMES[i]+".png");}cur=staticFrames[0];}
  private Bitmap load(Context c,String path){try{java.io.InputStream in=c.getAssets().open(path);Bitmap b=BitmapFactory.decodeStream(in);in.close();return b;}catch(IOException e){return null;}}
  void setAction(int a){action=a;frame=0;if(a>=0&&a<sheets.length&&sheets[a]!=null){cur=sheets[a];sheetFrameWidth=sheets[a].getWidth()/(float)FRAME_COUNT;}else if(a>=0&&a<staticFrames.length)cur=staticFrames[a];invalidate();}
  void nextFrame(){if(action>=0&&action<sheets.length&&sheets[action]!=null){frame=(frame+1)%FRAME_COUNT;invalidate();}}
  void setBaseY(float y){baseY=y;invalidate();}
  @Override protected void onDraw(Canvas c){super.onDraw(c);c.save();c.translate(0,baseY);float s=Math.min(getWidth(),getHeight());if(cur!=null){Rect src;float fx=frame;if(sheets[action]!=null){int left=Math.round(fx*sheetFrameWidth);int right=Math.min(sheets[action].getWidth(),Math.round((fx+1)*sheetFrameWidth));src=new Rect(left,0,right,sheets[action].getHeight());}else src=new Rect(0,0,cur.getWidth(),cur.getHeight());Rect dst=new Rect((int)(getWidth()-s)/2,(int)(getHeight()-s)/2,(int)(getWidth()+s)/2,(int)(getHeight()+s)/2);c.drawBitmap(cur,src,dst,p);}c.restore();}
 }
}
