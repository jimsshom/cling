package mediaplayer;

import eventbus.EventBusManager;
import eventbus.EventType;

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
