package com.juner2026.crabpet;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
public class ForegroundAppMonitor {
    private static String lastGood="";
    public static String current(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long now = System.currentTimeMillis();
        String fg = "";
        try {
            UsageEvents events = manager.queryEvents(now - 15000, now);
            if (events != null) {
                UsageEvents.Event ev = new UsageEvents.Event();
                while (events.hasNextEvent()) {
                    events.getNextEvent(ev);
                    int t = ev.getEventType();
                    /* ACTIVITY_RESUMED(1) 在新系统等同于 MOVE_TO_FOREGROUND(1) */
                    if (t == 1) {
                        String pkg = ev.getPackageName();
                        if (pkg != null && !pkg.equals("com.juner2026.crabpet")) fg = pkg;
                    }
                }
            }
        } catch (Exception ignored) {}
        if (fg.isEmpty()) {
            try {
                java.util.List<android.app.usage.UsageStats> list = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 86400000L, now);
                android.app.usage.UsageStats latest = null;
                if (list != null) for (android.app.usage.UsageStats item : list) {
                    if (item.getPackageName().equals("com.juner2026.crabpet")) continue;
                    if (latest == null || item.getLastTimeUsed() > latest.getLastTimeUsed()) latest = item;
                }
                if (latest != null && now - latest.getLastTimeUsed() < 300000) fg = latest.getPackageName();
            } catch (Exception ignored) {}
        }
        if (!fg.isEmpty()) lastGood = fg;
        return lastGood;
    }
}