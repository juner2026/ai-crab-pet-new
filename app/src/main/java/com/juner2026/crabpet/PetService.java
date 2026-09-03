package com.juner2026.crabpet;

import android.app.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.GradientDrawable;import android.graphics.drawable.ColorDrawable;import android.os.*;import android.view.*;import android.widget.*;
import java.io.IOException;

public class PetService extends Service {
 private WindowManager wm;private FrameLayout root;private WindowManager.LayoutParams lp;
 private CrabView crabView;private TextView bubble;private final Handler h=new Handler(Looper.getMainLooper());
 private CompanionMonitor monitor;private PopupWindow popup;
 private float downX,downY,startRawX,startRawY,baseLx,baseLy;private long downTime,lastTrail;private boolean moved;private int lastSide;private float lastMoveX;private long lastMoveT;private boolean sliding;
 private float bStartRawX,bStartRawY;private int bStartLx,bStartTy;private boolean bubbleMoved,bubbleCentered;

 private static final String[] NAMES={"gaming","singing","coffee","guitar","valentine","qixi","eating","sleeping","coding","painting","reading","birthday","christmas","dragon_boat","exercise","halloween","lantern","mid_autumn","new_year","photo","shower","spring","watering","edge_walk","edge_jump","wall_idle","wall_happy"};
 private static final String[] LINES={"\u557e","\u60f3\u4f60\u4e86","\u62b1\u62b1","\u518d\u6233\u4e00\u4e0b","\u4f60\u56de\u6765\u5566","\u4e0d\u8bb8\u8d70","\u559c\u6b22\u4f60","\u8d34\u8d34","\u4e56","\u6765\u5566","\u5c31\u9ecf\u7740\u4f60","\u4eb2\u4e00\u53e3","\u6478\u6478\u5934","\u4e0d\u51c6\u8dd1","\u5728\u5462","\u60f3\u4f60","\u8981\u4eb2\u4eb2","\u62b1\u7d27\u6211","\u8e6d\u8e6d\u4f60","\u4eca\u5929\u4e5f\u8981\u5f00\u5fc3\u54e6","\u563f\u563f","\u53eb\u4f60\u5462","\u522b\u8d70\u561b","\u966a\u4f60\u5440","\u770b\u6211\u5440"};
 private static final String[] SYMS={"\u2726","\u2727","\u2665","\u2661","\u2605","\u2606","\u266A","\u266B","\u273F","\u2740"};
 private static final int[] HUES={0xFFFF6E9B,0xFFFFBE32,0xFFBE96FF,0xFF64CDFF,0xFF78DC82,0xFFEB5A5A,0xFF9A8CFF,0xFFFF8C69};

 public IBinder onBind(Intent i){return null;}

 public int onStartCommand(Intent i,int f,int id){channel();startForeground(7,notification());show();monitor=new CompanionMonitor(this,(line,heat)->say(line));monitor.start();return START_STICKY;}

 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI\u5c0f\u871e\u87f9",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}

 private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI\u5c0f\u871e\u87f9").setContentText("Clawd \u966a\u7740\u4f60").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}

 private void show(){if(root!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);root=new FrameLayout(this);
  crabView=new CrabView(this);crabView.setAction(0);
  FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(340,340);cp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;root.addView(crabView,cp);
  bubble=new TextView(this);bubble.setTextColor(Color.rgb(80,48,62));bubble.setTextSize(12);bubble.setGravity(Gravity.CENTER);bubble.setPadding(12,5,12,5);bubble.setVisibility(View.GONE);
  GradientDrawable g=new GradientDrawable();g.setColor(Color.rgb(255,240,248));g.setStroke(1,Color.rgb(244,176,204));g.setCornerRadius(14);bubble.setBackground(g);
  FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);bp.gravity=Gravity.NO_GRAVITY;bp.leftMargin=180;bp.topMargin=34;root.addView(bubble,bp);
  attachBubbleTouch();
  lp=new WindowManager.LayoutParams(380,560,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=30;lp.y=180;wm.addView(root,lp);
  h.post(loop);attachTouch();}

 private void attachBubbleTouch(){bubble.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:bStartRawX=e.getRawX();bStartRawY=e.getRawY();bStartLx=((FrameLayout.LayoutParams)bubble.getLayoutParams()).leftMargin;bStartTy=((FrameLayout.LayoutParams)bubble.getLayoutParams()).topMargin;bubbleMoved=false;return true;
   case MotionEvent.ACTION_MOVE:{float bdx=e.getRawX()-bStartRawX,bdy=e.getRawY()-bStartRawY;if(Math.abs(bdx)>8||Math.abs(bdy)>8)bubbleMoved=true;if(bubbleMoved){FrameLayout.LayoutParams par=(FrameLayout.LayoutParams)bubble.getLayoutParams();par.leftMargin=(int)(bStartLx+bdx);par.topMargin=(int)(bStartTy+bdy);bubble.setLayoutParams(par);}return true;}
   case MotionEvent.ACTION_UP:return true;
  }return false;});}

 Runnable loop=new Runnable(){public void run(){crabView.nextFrame();h.postDelayed(this,90);}};

 private void attachTouch(){root.setOnTouchListener((v,e)->{
  switch(e.getAction()){
   case MotionEvent.ACTION_DOWN:downTime=System.currentTimeMillis();downX=e.getRawX();downY=e.getRawY();startRawX=e.getRawX();startRawY=e.getRawY();baseLx=lp.x;baseLy=lp.y;moved=false;crabView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(90).start();return true;
   case MotionEvent.ACTION_MOVE:{float dx=e.getRawX()-downX,dy=e.getRawY()-downY;if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;if(moved){lp.x=(int)(baseLx+(e.getRawX()-startRawX));lp.y=(int)(baseLy+(e.getRawY()-startRawY));wm.updateViewLayout(root,lp);crabView.animate().scaleX(1.15f).scaleY(1.15f).rotation(Math.max(-12f,Math.min(12f,dy*0.4f))).setDuration(120).start();long t=System.currentTimeMillis();lastMoveX=e.getRawX();lastMoveT=t;if(t-lastTrail>260){lastTrail=t;trail();}android.util.DisplayMetrics dm=getResources().getDisplayMetrics();int sw=dm.widthPixels;int side=0;if(lp.x<50)side=1;else if(lp.x+380>sw-50)side=2;if(side>0){if(lastSide==0){edgeHit();lastSide=side;}}else lastSide=0;}}return true;
   case MotionEvent.ACTION_UP:{
    if(moved){crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();long dt=lastMoveT>0?System.currentTimeMillis()-lastMoveT:999;float vx=dt>0&&dt<220?(e.getRawX()-lastMoveX)/dt:0;android.util.DisplayMetrics dm2=getResources().getDisplayMetrics();int swx=dm2.widthPixels;if(Math.abs(vx)>1.2f&&Math.abs(e.getRawX()-downX)>150){slideTo(vx>0?swx-380:0);}else{int sd=0;if(lp.x<50)sd=1;else if(lp.x+380>swx-50)sd=2;if(sd>0)edgeHit();}}else{crabView.animate().scaleX(1f).scaleY(1f).setDuration(120).start();}
    if(!moved){long dur=System.currentTimeMillis()-downTime;
     if(dur>650){showMenu();}
     else{crabView.setAction((int)(Math.random()*NAMES.length));burst();say(LINES[(int)(Math.random()*LINES.length)]);if(monitor!=null)monitor.touched();}
    }
    return true;}
   case MotionEvent.ACTION_CANCEL:{if(moved){crabView.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(180).start();moved=false;}return true;}
  }return true;});}
 private void say(String s){bubble.setText(s);
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
  e.setX(140+(float)Math.random()*90);e.setY(355+(float)Math.random()*50);e.setAlpha(0.95f);
  e.animate().translationYBy(-(60+(float)Math.random()*90)).translationXBy((float)(Math.random()-0.5)*80)
   .rotation((float)(Math.random()*100-50)).scaleX(small?0.7f:0.5f).scaleY(small?0.7f:0.5f).alpha(0)
   .setDuration(small?900+(long)(Math.random()*400):1100+(long)(Math.random()*700))
   .withEndAction(()->root.removeView(e)).start();
 }
 private void burst(){ring();int n=3+(int)(Math.random()*3);for(int i=0;i<n;i++)h.postDelayed(()->spawn(false),(long)(Math.random()*280));}
 private void trail(){spawn(true);}
private void edgeHit(){if(lastSide!=0&&!sliding)return;lastSide=1;crabView.setAction(25);burst();say("\u8d34\u5899\u5566\uff5e\u8db4\u4e00\u4f1a\u513f");h.postDelayed(()->{if(lastSide!=0)crabView.setAction(26);},2000);}
private void slideTo(int target){if(sliding)return;sliding=true;final int sx=lp.x;final int dist=target-sx;final long[] st={System.currentTimeMillis()};Runnable r=new Runnable(){public void run(){float frac=Math.min(1f,(System.currentTimeMillis()-st[0])/220f);float ease=1-(1-frac)*(1-frac);lp.x=(int)(sx+dist*ease);wm.updateViewLayout(root,lp);if(frac<1f)h.postDelayed(this,16);else{sliding=false;burst();edgeHit();}}};h.post(r);}
 private void ring(){
  View r=new View(this);
  GradientDrawable gd=new GradientDrawable();gd.setShape(GradientDrawable.OVAL);gd.setStroke(4,0xFFFF8FC0);gd.setColor(Color.TRANSPARENT);
  r.setBackground(gd);
  FrameLayout.LayoutParams rp=new FrameLayout.LayoutParams(70,70);rp.leftMargin=155;rp.topMargin=365;
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
    if(t.equals("\u6362\u4e2a\u52a8\u4f5c")){crabView.setAction((int)(Math.random()*NAMES.length));burst();say(LINES[(int)(Math.random()*LINES.length)]);}
    else if(t.equals("\u8bf4\u53e5\u8bdd")){say(LINES[(int)(Math.random()*LINES.length)]);}
    else{root.setVisibility(View.INVISIBLE);h.postDelayed(()->root.setVisibility(View.VISIBLE),3000);}});
   box.addView(tv,new LinearLayout.LayoutParams(-1,-2));
  }
  popup=new PopupWindow(box,(int)(140*d),WindowManager.LayoutParams.WRAP_CONTENT,true);
  popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
  popup.setOutsideTouchable(true);
  popup.showAtLocation(root,Gravity.NO_GRAVITY,lp.x+24,lp.y+40);
 }

 public void onDestroy(){if(monitor!=null)monitor.stop();if(popup!=null&&popup.isShowing())popup.dismiss();if(root!=null&&wm!=null)wm.removeView(root);super.onDestroy();}

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
