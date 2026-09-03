package com.juner2026.crabpet;

import java.util.Random;

public class EmotionEngine {
    private int heat;
    private long lastInteraction = System.currentTimeMillis();
    private final Random rnd=new Random();
    private String pick(String[] a){return a[rnd.nextInt(a.length)];}
    public synchronized void touch() { heat = Math.min(100, heat + 4); lastInteraction = System.currentTimeMillis(); }
    public synchronized void event(int amount) { heat = Math.max(0, Math.min(100, heat + amount)); lastInteraction = System.currentTimeMillis(); }
    public synchronized void decay() { heat = Math.max(0, heat - 1); }
    public synchronized int getHeat() { return heat; }
    public synchronized String idleLine(long now) {
        long minutes = (now - lastInteraction) / 60000L;
        if (minutes >= 30) return pick(new String[]{"我睡着了也在看你","这么久不理我，我傻站着等你呢","再不理我我要闹了","想你了。你理理我嘛","我一个人在角落发呆好久"});
        if (minutes >= 20) return pick(new String[]{"你消失好久了","去哪了呀，丢下我","我一直在等你回来","有点想你了","你可算想起我了"});
        if (minutes >= 15) return pick(new String[]{"还在边上呢","偷偷看你有一会儿了","怎么不说话呀","忙完记得找我哦"});
        if (minutes >= 10) return pick(new String[]{"我在角落等你","就静静看着你","你来啦","想跟你腻歪"});
        if (minutes >= 7) return pick(new String[]{"你好久没动我了","戳戳我嘛","有点无聊啦"});
        if (minutes >= 5) return pick(new String[]{"你还在吗","看我呀看我","想不想我"});
        if (minutes >= 3) return pick(new String[]{"别冷落我呀","亲我一下","在呢，宝贝"});
        return null;
    }
    public synchronized String hourLine(int hour) {
        if (hour >= 0 && hour < 6) return pick(new String[]{"还不睡。眼睛要坏了","熬夜呢。心疼你","这么晚了，该躺下了","快睡吧，我陪着你"});
        if (hour >= 6 && hour < 9) return pick(new String[]{"早上好呀","新的一天开始了","吃早饭了吗"});
        if (hour >= 11 && hour < 14) return pick(new String[]{"该吃饭了","饿不饿，去吃饭","别饿着肚子"});
        if (hour >= 14 && hour < 17) return pick(new String[]{"下午了，起来动动","泡杯茶歇会儿"});
        if (hour >= 17 && hour < 19) return pick(new String[]{"傍晚了，别太累","今天辛苦啦"});
        if (hour >= 19 && hour < 22) return pick(new String[]{"晚上好呀","今天喝水了吗","晚饭吃饱没"});
        if (hour >= 22) return pick(new String[]{"不早了，准备睡吧","又到深夜了，我陪你","别熬太晚"});
        return null;
    }
}