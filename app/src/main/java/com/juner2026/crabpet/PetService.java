package com.juner2026.crabpet;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class PetService extends Service {
    private WindowManager wm; private FrameLayout root; private WindowManager.LayoutParams lp;
    private TextView bubble; private CrabView crab; private final Handler handler = new Handler(Looper.getMainLooper());
    private final CompanionMonitor monitor = new CompanionMonitor(this, (line, heat) -> say(line));

    @Override public IBinder onBind(Intent i){return null;}
    @Override public int onStartCommand(Intent i,int flags,int id){createChannel();startForeground(7,notification());show();monitor.start();return START_STICKY;}
    private void createChannel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI小螃蟹",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}
    private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI小螃蟹正在陪你").setContentText("点击桌宠和它互动").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}

    private void show(){if(root!=null)return;wm=(WindowManager)getSystemService(WINDOW_SERVICE);root=new FrameLayout(this);
        crab=new CrabView(this);root.addView(crab,new FrameLayout.LayoutParams(190,190));
        bubble=new TextView(this);bubble.setTextColor(Color.rgb(72,55,54));bubble.setTextSize(14);bubble.setGravity(Gravity.CENTER);bubble.setPadding(24,12,24,12);
        GradientDrawable bg=new GradientDrawable();bg.setColor(Color.rgb(255,235,244));bg.setStroke(2,Color.rgb(238,165,192));bg.setCornerRadius(30);bubble.setBackground(bg);
        FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-2,70);bp.leftMargin=70;bp.topMargin=-82;root.addView(bubble,bp);
        lp=new WindowManager.LayoutParams(390,190,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=25;lp.y=270;wm.addView(root,lp);say("今天也要陪你");}
    private void say(String s){if(bubble==null)return;bubble.setText(s);bubble.setVisibility(View.VISIBLE);handler.removeCallbacksAndMessages(null);handler.postDelayed(()->{if(bubble!=null)bubble.setVisibility(View.GONE);},4200);}
    @Override public void onDestroy(){monitor.stop();if(root!=null&&wm!=null)wm.removeView(root);super.onDestroy();}

    private class CrabView extends View {
        final Paint p=new Paint();float downX,downY;long down;int frame=0;int effect=0;
        final Runnable loop=new Runnable(){public void run(){frame++;if(effect>0)effect--;invalidate();handler.postDelayed(this,150);}};
        CrabView(Context c){super(c);p.setAntiAlias(false);handler.post(loop);}
        void box(Canvas c,int x,int y,int w,int h,int color){p.setColor(color);c.drawRect(x,y,x+w,y+h,p);}
        void text(Canvas c,String s,float x,float y,int size,int color){p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(size);p.setColor(color);c.drawText(s,x,y,p);}
        @Override protected void onDraw(Canvas c){super.onDraw(c);
            int shell=Color.rgb(224,111,79),shadow=Color.rgb(190,78,61),head=Color.rgb(55,55,55),metal=Color.rgb(105,105,105),black=Color.rgb(16,16,16),white=Color.WHITE;
            int y=40;
            // Headphones and cable. The body stays fixed during idle.
            box(c,65,5,70,10,head);box(c,52,15,96,10,black);box(c,43,25,12,25,head);box(c,145,25,12,25,head);
            box(c,35,38,28,72,head);box(c,137,38,28,72,head);box(c,28,52,12,45,metal);box(c,160,52,12,45,metal);box(c,43,45,12,58,Color.rgb(75,75,75));box(c,145,45,12,58,Color.rgb(75,75,75));
            // Orange crab from the reference image.
            box(c,58,y+10,74,24,shell);box(c,48,y+30,94,70,shell);box(c,38,y+52,114,50,shell);box(c,48,y+95,94,18,shadow);
            box(c,38,y+70,10,25,shadow);box(c,142,y+70,10,25,shadow);
            box(c,64,y+48,16,16,black);box(c,112,y+48,16,16,black);
            int step=(frame%4<2)?0:5;box(c,52,y+113,18,30,shell);box(c,77,y+113+step,18,30,shell);box(c,105,y+113-step,18,30,shell);box(c,130,y+113,18,30,shell);
            // Effects are separate from the crab, so they can be enabled without moving it.
            if(effect>0){text(c,"✦",12,35,22,Color.rgb(255,190,55));text(c,"✦",172,30,18,Color.rgb(255,190,55));}
            if(effect%3==1){text(c,"♪",175,72,24,black);text(c,"♫",183,106,20,black);}
            if(effect%3==2){text(c,"♥",175,145,18,Color.rgb(236,87,125));}
            if(taps>=3){text(c,"!",18,150,28,Color.rgb(230,75,65));}
        }
        int taps=0;
        @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){down=System.currentTimeMillis();downX=e.getRawX()-lp.x;downY=e.getRawY()-lp.y;return true;}if(e.getAction()==MotionEvent.ACTION_MOVE){lp.x=(int)(e.getRawX()-downX);lp.y=(int)(e.getRawY()-downY);wm.updateViewLayout(root,lp);return true;}if(e.getAction()==MotionEvent.ACTION_UP){long d=System.currentTimeMillis()-down;if(d>700){say("我藏两秒");root.setVisibility(INVISIBLE);handler.postDelayed(()->root.setVisibility(VISIBLE),2000);}else{taps++;effect=12;monitor.touched();say(taps>=3?"被你连戳啦":taps==2?"双击收到":"戳我干嘛");handler.postDelayed(()->taps=0,900);}return true;}return true;}
    }
}
