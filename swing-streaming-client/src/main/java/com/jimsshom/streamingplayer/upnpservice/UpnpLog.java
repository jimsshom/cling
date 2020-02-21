package com.jimsshom.streamingplayer.upnpservice;

import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 17:51
 */
public class UpnpLog {
    public static void log(String text) {
        EventBusManager.fireEvent(EventType.LOG, "[Upnp] " + text);
    }
}
