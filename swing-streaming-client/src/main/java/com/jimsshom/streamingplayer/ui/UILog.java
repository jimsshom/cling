package com.jimsshom.streamingplayer.ui;

import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 17:52
 */
public class UILog {
    public static void log(String text) {
        EventBusManager.fireEvent(EventType.LOG, "[UI] " + text);
    }
}
