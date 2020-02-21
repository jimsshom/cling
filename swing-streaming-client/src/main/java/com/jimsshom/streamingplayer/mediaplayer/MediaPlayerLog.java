package com.jimsshom.streamingplayer.mediaplayer;

import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 17:53
 */
public class MediaPlayerLog {
    public static void log(String text) {
        EventBusManager.fireEvent(EventType.LOG, "[player] " + text);
    }
}
