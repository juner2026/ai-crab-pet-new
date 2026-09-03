package com.juner2026.crabpet;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.net.Uri;import android.provider.Settings;import android.widget.*;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b); LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(40,50,40,40);
 TextView t=new TextView(this);t.setText("AI小螃蟹\n\n请依次打开三项权限。权限只在本机用于桌宠互动。");t.setTextSize(20);t.setTextColor(Color.rgb(90,65,70));l.addView(t);
 add(l,"1. 授权悬浮窗",v->startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()))));
 add(l,"2. 授权使用情况访问",v->startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
 add(l,"3. 授权通知读取",v->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
 add(l,"启动桌宠",v->{if(!Settings.canDrawOverlays(this)){Toast.makeText(this,"先授权悬浮窗",Toast.LENGTH_SHORT).show();return;}Intent i=new Intent(this,PetService.class);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);});
 add(l,"停止桌宠",v->stopService(new Intent(this,PetService.class)));setContentView(l);}
 void add(LinearLayout l,String text,android.view.View.OnClickListener click){Button b=new Button(this);b.setText(text);b.setOnClickListener(click);l.addView(b);}
}
