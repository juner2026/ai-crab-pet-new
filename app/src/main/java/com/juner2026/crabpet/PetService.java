package com.juner2026.crabpet;

import android.app.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.GradientDrawable;import android.os.*;import android.view.*;import android.widget.*;
import java.io.IOException;

public class PetService extends Service {
 private WindowManager wm;private FrameLayout root;private WindowManager.LayoutParams lp;
 private CrabView crabView;private TextView bubble;private final Handler h=new Handler(Looper.getMainLooper());
 private CompanionMonitor monitor;
 private float downX,downY,startRawX,startRawY,baseLx,baseLy;private long downTime;private boolean moved;
 private int tapCount,effectIx,posIx;

 private static final String[] NAMES={"listening","singing","coffee","guitar","valentine","qixi"};
 private static final String[] LINES={"啾","想你了","抱抱","再戳一下","你回来啦","不许走","喜欢你","贴贴"};

 public IBinder onBind(Intent i){return null;}

 public int onStartCommand(Intent i,int f,int id){channel();startForeground(7,notification());show();monitor=new CompanionMonitor(this,(line,heat)->say(line));monitor.start();return START_STICKY;}

 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI小螃蟹",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}

 private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI小螃蟹").setContentText("Clawd 陪着你").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}

 private void show(){if(root!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);root=new FrameLayout(this);
  crabView=new CrabView(this);crabView.setAction(0);
  FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(360,360);cp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;root.addView(crabView,cp);
  bubble=new TextView(this);bubble.setText("嘘");bubble.setTextColor(Color.rgb(80,48,62));bubble.setTextSize(12);bubble.setGravity(Gravity.CENTER);bubble.setPadding(12,5,12,5);
  GradientDrawable g=new GradientDrawable();g.setColor(Color.rgb(255,240,248));g.setStroke(1,Color.rgb(244,176,204));g.setCornerRadius(14);bubble.setBackground(g);
  FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);bp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;bp.topMargin=10;root.addView(bubble,bp);
  lp=new WindowManager.LayoutParams(400,520,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=30;lp.y=180;wm.addView(root,lp);
  h.post(loop);attachTouch();}

 Runnable loop=new Runnable(){public void run(){crabView.nextFrame();h.postDelayed(this,100);}};

 private void attachTouch(){root.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:downTime=System.currentTimeMillis();downX=e.getRawX();downY=e.getRawY();startRawX=e.getRawX();startRawY=e.getRawY();baseLx=lp.x;baseLy=lp.y;moved=false;return true;
   case MotionEvent.ACTION_MOVE:float dx=e.getRawX()-downX,dy=e.getRawY()-downY;if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;if(moved){lp.x=(int)(baseLx+(e.getRawX()-startRawX));lp.y=(int)(baseLy+(e.getRawY()-startRawY));wm.updateViewLayout(root,lp);}return true;
   case MotionEvent.ACTION_UP:long dur=System.currentTimeMillis()-downTime;if(!moved){if(dur>650){say("藏两秒");root.setVisibility(View.INVISIBLE);h.postDelayed(()->root.setVisibility(View.VISIBLE),2200);}else{tapCount++;posIx=(posIx+1)%NAMES.length;crabView.setAction(posIx);effect();say(LINES[(tapCount-1)%LINES.length]);if(monitor!=null)monitor.touched();h.postDelayed(()->tapCount=0,900);}}return true;
  }return true;});}

 private void say(String s){bubble.setText(s);bubble.setVisibility(View.VISIBLE);h.removeCallbacks(hide);h.postDelayed(hide,3600);}
 Runnable hide=new Runnable(){public void run(){bubble.setVisibility(View.GONE);}};

 private void effect(){effectIx++;TextView e=new TextView(this);String[] pool={"\u2726","\u2665","\u266A","\u266B","\u2601","!"};e.setText(pool[effectIx%pool.length]);e.setTextSize(24);e.setTextColor(new int[]{Color.rgb(255,190,50),Color.rgb(255,110,155),Color.rgb(190,150,255),Color.rgb(255,110,155),Color.rgb(100,205,255),Color.rgb(225,70,70)}[effectIx%6]);root.addView(e,new FrameLayout.LayoutParams(-2,-2));e.setX((float)(150+Math.random()*60));e.setY(70);e.animate().translationY(-45).alpha(0).setDuration(1500).withEndAction(()->root.removeView(e)).start();}

 public void onDestroy(){if(monitor!=null)monitor.stop();if(root!=null&&wm!=null)wm.removeView(root);super.onDestroy();}

 private static class CrabView extends View{
  private static final int FRAME_COUNT=10;
  private Bitmap[] staticFrames;private Bitmap[] sheets;private Bitmap cur;
  private int action=0,frame=0;private float baseY=0,sheetFrameWidth;
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
  CrabView(Context c){super(c);staticFrames=new Bitmap[NAMES.length];sheets=new Bitmap[NAMES.length];for(int i=0;i<NAMES.length;i++){staticFrames[i]=load(c,"clawd/"+NAMES[i]+".png");sheets[i]=load(c,"clawd_sheet/"+NAMES[i]+".png");}cur=staticFrames[0];}
  private Bitmap load(Context c,String path){try{java.io.InputStream in=c.getAssets().open(path);Bitmap b=BitmapFactory.decodeStream(in);in.close();return b;}catch(IOException e){return null;}}
  void setAction(int a){action=a;frame=0;if(a>=0&&a<sheets.length&&sheets[a]!=null){cur=sheets[a];sheetFrameWidth=sheets[a].getWidth()/(float)FRAME_COUNT;}else if(a>=0&&a<staticFrames.length)cur=staticFrames[a];invalidate();}
  void nextFrame(){if(action>=0&&action<sheets.length&&sheets[action]!=null){frame=(frame+1)%FRAME_COUNT;invalidate();}}
  void setBaseY(float y){baseY=y;invalidate();}
  @Override protected void onDraw(Canvas c){super.onDraw(c);c.save();c.translate(0,baseY);float s=Math.min(getWidth(),getHeight());if(cur!=null){Rect src;if(sheets[action]!=null){int left=Math.round(frame*sheetFrameWidth);int right=Math.min(sheets[action].getWidth(),Math.round((frame+1)*sheetFrameWidth));src=new Rect(left,0,right,sheets[action].getHeight());}else src=new Rect(0,0,cur.getWidth(),cur.getHeight());Rect dst=new Rect((int)(getWidth()-s)/2,(int)(getHeight()-s)/2,(int)(getWidth()+s)/2,(int)(getHeight()+s)/2);c.drawBitmap(cur,src,dst,p);}c.restore();}
 }
}