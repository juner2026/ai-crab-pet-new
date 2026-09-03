package com.juner2026.crabpet;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

public class ForegroundAppMonitor {
    public static String current(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long now = System.currentTimeMillis();
        // 用事件流最近一次 MOVE_TO_FOREGROUND 的包名，才是真正的当前前台
        UsageEvents events = manager.queryEvents(now - 30000, now);
        String fg = "";
        if (events != null) {
            UsageEvents.Event ev = new UsageEvents.Event();
            while (events.hasNextEvent()) {
                events.getNextEvent(ev);
                if (ev.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    fg = ev.getPackageName();
                }
            }
        }
        // 兜底：若无前景事件，退回 lastTimeUsed 最大者
        if (fg.isEmpty()) {
            java.util.List<android.app.usage.UsageStats> list = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 10000, now);
            android.app.usage.UsageStats latest = null;
            if (list != null) for (android.app.usage.UsageStats item : list) if (latest == null || item.getLastTimeUsed() > latest.getLastTimeUsed()) latest = item;
            if (latest != null) fg = latest.getPackageName();
        }
        return fg;
    }
}
