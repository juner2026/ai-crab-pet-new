package com.juner2026.crabpet;

public class EmotionEngine {
    private int heat;
    private long lastInteraction = System.currentTimeMillis();
    public synchronized void touch() { heat = Math.min(100, heat + 4); lastInteraction = System.currentTimeMillis(); }
    public synchronized void event(int amount) { heat = Math.max(0, Math.min(100, heat + amount)); lastInteraction = System.currentTimeMillis(); }
    public synchronized void decay() { heat = Math.max(0, heat - 1); }
    public synchronized int getHeat() { return heat; }
    public synchronized String idleLine(long now) {
        long minutes = (now - lastInteraction) / 60000L;
        if (minutes >= 30) return "我睡着了也在看你";
        if (minutes >= 20) return "你消失好久了";
        if (minutes >= 10) return "我在角落等你";
        if (minutes >= 5) return "你还在吗";
        return null;
    }
    public synchronized String hourLine(int hour) {
        if (hour >= 0 && hour < 6) return "还不睡。眼睛要坏了";
        if (hour >= 11 && hour < 14) return "该吃饭了";
        if (hour >= 18 && hour < 21) return "今天喝水了吗";
        return null;
    }
}
