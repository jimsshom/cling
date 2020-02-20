package upnp;

import eventbus.EventBusManager;
import eventbus.EventType;

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
