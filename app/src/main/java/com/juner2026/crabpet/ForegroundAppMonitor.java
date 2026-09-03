package com.juner2026.crabpet;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import java.util.List;

public class ForegroundAppMonitor {
    public static String current(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long now = System.currentTimeMillis();
        List<UsageStats> list = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 10000, now);
        UsageStats latest = null;
        if (list != null) for (UsageStats item : list) if (latest == null || item.getLastTimeUsed() > latest.getLastTimeUsed()) latest = item;
        return latest == null ? "" : latest.getPackageName();
    }
}
