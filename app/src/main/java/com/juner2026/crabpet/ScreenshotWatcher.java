package com.juner2026.crabpet;

import android.os.FileObserver;
import java.io.File;

public class ScreenshotWatcher extends FileObserver {
    private final Runnable callback;
    public ScreenshotWatcher(String path, Runnable callback) { super(path, CREATE); this.callback = callback; }
    @Override public void onEvent(int event, String path) { if ((event & CREATE) != 0 && path != null) callback.run(); }
}
