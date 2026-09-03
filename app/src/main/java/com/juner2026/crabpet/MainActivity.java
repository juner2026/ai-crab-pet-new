package com.juner2026.crabpet;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.net.Uri;import android.provider.Settings;import android.widget.*;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b); LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(40,50,40,40);
 TextView t=new TextView(this);t.setText("AI小螃蟹\n\n权限只在本机用于桌宠互动。打开本页面会自动启动桌宠。");t.setTextSize(20);t.setTextColor(Color.rgb(90,65,70));l.addView(t);
 add(l,"1. 授权悬浮窗",v->startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()))));
 add(l,"2. 授权使用情况访问",v->startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
 add(l,"3. 授权通知读取",v->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
 add(l,"手动重启桌宠",v->{startPet();Toast.makeText(this,"已重启",Toast.LENGTH_SHORT).show();});
 add(l,"停止桌宠",v->stopService(new Intent(this,PetService.class)));
 setContentView(l);
 /* 打开页面即自动拉起服务，进程被杀后点图标即可复活 */
 if(Settings.canDrawOverlays(this))startPet();
 }
 private void startPet(){Intent i=new Intent(this,PetService.class);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);}
 void add(LinearLayout l,String text,android.view.View.OnClickListener click){Button b=new Button(this);b.setText(text);b.setOnClickListener(click);l.addView(b);}
}