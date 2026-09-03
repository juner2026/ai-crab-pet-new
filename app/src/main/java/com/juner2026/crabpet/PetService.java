package com.juner2026.crabpet;

import android.app.*;import android.content.*;import android.graphics.Color;import android.graphics.PixelFormat;import android.os.*;import android.view.*;import android.webkit.*;import android.widget.*;

public class PetService extends Service {
 private WindowManager wm;private WebView web;private WindowManager.LayoutParams lp;private float sx,sy;private long down;private final Handler h=new Handler(Looper.getMainLooper());
 public IBinder onBind(Intent i){return null;}
 public int onStartCommand(Intent i,int f,int id){channel();startForeground(7,notification());show();return START_STICKY;}
 private void channel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("pet","AI小螃蟹",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}
 private Notification notification(){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"pet"):new Notification.Builder(this);return b.setContentTitle("AI小螃蟹正在陪你").setContentText("Clawd 动画桌宠").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();}
 private void show(){if(web!=null)return;web=new WebView(this);web.setBackgroundColor(Color.TRANSPARENT);web.setLayerType(View.LAYER_TYPE_SOFTWARE,null);WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(true);web.loadUrl("file:///android_asset/clawd.html");
  lp=new WindowManager.LayoutParams(390,300,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.START;lp.x=20;lp.y=210;wm=(WindowManager)getSystemService(WINDOW_SERVICE);wm.addView(web,lp);
  web.setOnTouchListener((v,e)->{if(e.getAction()==0){down=System.currentTimeMillis();sx=e.getRawX()-lp.x;sy=e.getRawY()-lp.y;web.evaluateJavascript("say('我来啦')",null);return true;}if(e.getAction()==2){lp.x=(int)(e.getRawX()-sx);lp.y=(int)(e.getRawY()-sy);wm.updateViewLayout(web,lp);return true;}if(e.getAction()==1){long d=System.currentTimeMillis()-down;if(d>700)web.evaluateJavascript("say('长按收到')",null);else web.evaluateJavascript("effect();say('戳我干嘛')",null);return true;}return true;});}
 public void onDestroy(){if(web!=null&&wm!=null)wm.removeView(web);super.onDestroy();}
}
