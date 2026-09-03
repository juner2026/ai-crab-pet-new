package com.juner2026.crabpet;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class PetService extends Service {
    private WindowManager wm;
    private FrameLayout root;
    private WindowManager.LayoutParams lp;
    private TextView bubble;
    private CrabView crab;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int taps = 0;

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        createChannel();
        startForeground(7, notification());
        show();
        return START_STICKY;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel("pet", "AI小螃蟹", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    private Notification notification() {
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(this, "pet") : new Notification.Builder(this);
        return b.setContentTitle("AI小螃蟹正在陪你").setContentText("点击桌宠和它互动").setSmallIcon(android.R.drawable.ic_dialog_info).setOngoing(true).build();
    }

    private void show() {
        if (root != null) return;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        root = new FrameLayout(this);
        crab = new CrabView(this);
        root.addView(crab, new FrameLayout.LayoutParams(160, 160));
        bubble = new TextView(this);
        bubble.setText("你好呀");
        bubble.setTextColor(Color.rgb(90, 65, 70));
        bubble.setTextSize(14);
        bubble.setPadding(22, 12, 22, 12);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(255, 235, 244));
        bg.setStroke(2, Color.rgb(238, 165, 192));
        bg.setCornerRadius(28);
        bubble.setBackground(bg);
        FrameLayout.LayoutParams bp = new FrameLayout.LayoutParams(-2, 70);
        bp.leftMargin = 78;
        root.addView(bubble, bp);
        lp = new WindowManager.LayoutParams(360, 170, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START; lp.x = 25; lp.y = 230;
        wm.addView(root, lp);
        say("今天也要陪你");
    }

    private void say(String s) {
        if (bubble == null) return;
        bubble.setText(s); bubble.setVisibility(View.VISIBLE);
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> { if (bubble != null) bubble.setVisibility(View.GONE); }, 3800);
    }

    @Override public void onDestroy() {
        if (root != null && wm != null) wm.removeView(root);
        super.onDestroy();
    }

    private class CrabView extends View {
        private final Paint paint = new Paint();
        private float downX, downY; private long down; private int frame;
        CrabView(Context c) { super(c); paint.setAntiAlias(false); handler.post(loop); }
        private final Runnable loop = new Runnable() { public void run() { frame++; invalidate(); handler.postDelayed(this, 140); } };
        private void box(Canvas c, int x, int y, int w, int h, int color) { paint.setColor(color); c.drawRect(x, y, x+w, y+h, paint); }
        @Override protected void onDraw(Canvas c) {
            int bob = (int)(Math.sin(frame * .55) * 3);
            int shell = Color.rgb(244, 119, 151), dark = Color.rgb(184, 67, 103), light = Color.rgb(255, 184, 199), ink = Color.rgb(65, 42, 52);
            box(c, 44, 54+bob, 72, 56, shell); box(c, 32, 68+bob, 96, 43, shell);
            box(c, 14, 76+bob, 24, 13, dark); box(c, 122, 76+bob, 24, 13, dark);
            box(c, 5, 89+bob, 28, 10, dark); box(c, 127, 89+bob, 28, 10, dark);
            box(c, 42, 43+bob, 13, 24, light); box(c, 105, 43+bob, 13, 24, light);
            box(c, 47, 68+bob, 15, 15, ink); box(c, 101, 68+bob, 15, 15, ink);
            box(c, 51, 71+bob, 5, 5, Color.WHITE); box(c, 105, 71+bob, 5, 5, Color.WHITE);
            box(c, 58, 96+bob, 44, 6, dark);
            int step = frame % 2 == 0 ? 0 : 5;
            box(c, 37, 114+bob, 15, 18, dark); box(c, 58, 115+bob+step, 13, 20, dark);
            box(c, 101, 115+bob-step, 13, 20, dark); box(c, 122, 114+bob, 15, 18, dark);
            box(c, 51, 135+bob, 22, 6, ink); box(c, 98, 135+bob, 22, 6, ink);
        }
        @Override public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) { down = System.currentTimeMillis(); downX = e.getRawX()-lp.x; downY = e.getRawY()-lp.y; return true; }
            if (e.getAction() == MotionEvent.ACTION_MOVE) { lp.x=(int)(e.getRawX()-downX); lp.y=(int)(e.getRawY()-downY); wm.updateViewLayout(root,lp); return true; }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis()-down > 700) { say("我藏一下"); root.setVisibility(INVISIBLE); handler.postDelayed(() -> root.setVisibility(VISIBLE), 2000); }
                else { taps++; say(taps > 1 ? "被你连戳啦" : "戳我干嘛"); handler.postDelayed(() -> taps=0, 700); }
                return true;
            } return true;
        }
    }
}