package ui;

import eventbus.EventBusManager;
import eventbus.EventType;

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
