package com.juner2026.crabpet;

import android.app.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.os.*;import android.view.*;import android.widget.*;

public class PetService extends Service {
 private WindowManager wm; private FrameLayout root; private WindowManager.LayoutParams lp; private TextView bubble; private CrabView crab;
 private final Handler ui=new Handler(Looper.getMainLooper()); private final Handler anim=new Handler(Looper.getMainLooper()); private final Handler monitorHandler=new Handler(Looper.getMainLooper());
 private final CompanionMonitor monitor=new CompanionMonitor(this,(line,heat)->say(line)); private Runnable hideBubble;
 public IBinder onBind(Intent i){return null;}
 public int onStartCommand(Intent i,int f,int id){channel();startForeground(7,notice());show();monitor.start();return START_STICKY;}
 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI小螃蟹",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}
 private Notification notice(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI小螃蟹正在陪你").setContentText("点击桌宠互动").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}
 private void show(){if(root!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);root=new FrameLayout(this);crab=new CrabView(this);root.addView(crab,new FrameLayout.LayoutParams(190,190));
  bubble=new TextView(this);bubble.setTextColor(Color.rgb(75,45,58));bubble.setTextSize(14);bubble.setGravity(Gravity.CENTER);bubble.setPadding(22,10,22,10);GradientDrawable g=new GradientDrawable();g.setColor(Color.rgb(255,232,243));g.setStroke(2,Color.rgb(239,146,180));g.setCornerRadius(26);bubble.setBackground(g);
  FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-2,68);bp.leftMargin=88;bp.topMargin=-92;root.addView(bubble,bp);
  lp=new WindowManager.LayoutParams(410,210,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=20;lp.y=290;wm.addView(root,lp);say("我来啦");}
 private void say(String s){ui.post(()->{if(bubble==null)return;if(hideBubble!=null)ui.removeCallbacks(hideBubble);bubble.setText(s);bubble.setVisibility(View.VISIBLE);hideBubble=()->{if(bubble!=null)bubble.setVisibility(View.GONE);};ui.postDelayed(hideBubble,4200);});}
 public void onDestroy(){monitor.stop();anim.removeCallbacksAndMessages(null);monitorHandler.removeCallbacksAndMessages(null);if(root!=null&&wm!=null)wm.removeView(root);super.onDestroy();}
 private class CrabView extends View {
  Paint p=new Paint();float sx,sy;long down;int frame=0,effect=0,taps=0;
  CrabView(Context c){super(c);p.setAntiAlias(false);anim.post(tick);}
  Runnable tick=new Runnable(){public void run(){frame++;if(effect>0)effect--;invalidate();anim.postDelayed(this,140);}};
  void r(Canvas c,int x,int y,int w,int h,int col){p.setColor(col);c.drawRect(x,y,x+w,y+h,p);}
  protected void onDraw(Canvas c){int body=Color.rgb(222,136,109),shadow=Color.rgb(190,90,70),black=Color.rgb(20,20,20),dark=Color.rgb(55,55,55),gray=Color.rgb(105,105,105),white=Color.WHITE;int y=47;
   // Canonical Clawd proportions: flat rectangular body, four feet, block headphones.
   r(c,57,5,76,7,black);r(c,47,12,96,7,dark);r(c,37,20,10,27,dark);r(c,143,20,10,27,dark);r(c,29,43,30,62,dark);r(c,151,43,30,62,dark);r(c,34,54,8,40,gray);r(c,160,54,8,40,gray);
   r(c,48,y+8,94,73,body);r(c,39,y+28,112,48,body);r(c,48,y+81,94,17,shadow);r(c,38,y+62,10,23,shadow);r(c,142,y+62,10,23,shadow);
   r(c,64,y+31,15,18,black);r(c,113,y+31,15,18,black);r(c,72,y+57,48,6,Color.rgb(122,34,48));
   int step=frame%4<2?0:5;r(c,51,y+97,18,31,body);r(c,77,y+97+step,18,31,body);r(c,105,y+97-step,18,31,body);r(c,131,y+97,18,31,body);
   if(effect>0){r(c,13,24,5,5,Color.rgb(255,190,50));r(c,20,17,5,5,Color.rgb(255,190,50));r(c,171,28,5,5,Color.rgb(255,190,50));r(c,181,20,5,5,Color.rgb(255,190,50));}
   if(effect%3==1){r(c,174,70,5,15,black);r(c,180,63,5,5,black);r(c,184,76,5,15,black);}
   if(effect%3==2){r(c,174,140,5,5,Color.rgb(236,87,125));r(c,181,145,5,5,Color.rgb(236,87,125));}
   if(taps>=3){r(c,15,147,6,19,Color.rgb(220,60,60));r(c,22,153,6,6,Color.rgb(220,60,60));}
  }
  public boolean onTouchEvent(MotionEvent e){if(e.getAction()==0){down=System.currentTimeMillis();sx=e.getRawX()-lp.x;sy=e.getRawY()-lp.y;return true;}if(e.getAction()==2){lp.x=(int)(e.getRawX()-sx);lp.y=(int)(e.getRawY()-sy);wm.updateViewLayout(root,lp);return true;}if(e.getAction()==1){long d=System.currentTimeMillis()-down;if(d>700){say("我藏两秒");root.setVisibility(View.INVISIBLE);ui.postDelayed(()->root.setVisibility(View.VISIBLE),2000);}else{taps++;effect=18;monitor.touched();say(taps>=3?"连续戳我。生气了":taps==2?"双击收到":"戳我干嘛");ui.postDelayed(()->taps=0,900);}return true;}return true;}
 }
}
