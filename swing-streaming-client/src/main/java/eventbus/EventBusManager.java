package eventbus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:06
 */
public class EventBusManager {
    private static Map<EventType, List<EventAdapter>> adapterMap = new ConcurrentHashMap<>();

    public static void fireEvent(EventType type, String param) {
        if (!EventType.LOG.equals(type) && !EventType.PROGRESS_TIME.equals(type)) {
            fireEvent(EventType.LOG, "[Event Fire] " + type + " : " + param);
        }
        /*if (EventType.PROGRESS_TIME.equals(type)) {
            fireEvent(EventType.LOG, "[Event Fire] " + type + " : " + param);
        }*/
        if (adapterMap.containsKey(type)) {
            if (adapterMap.get(type) == null) {
                return;
            }
            for (EventAdapter adapter : adapterMap.get(type)) {
                adapter.process(param);
            }
        }
    }

    public static void addEventAdapter(EventType type, EventAdapter adapter) {
        if (!adapterMap.containsKey(type)) {
            adapterMap.put(type, new ArrayList<EventAdapter>());
        }
        adapterMap.get(type).add(adapter);
    }
}
