package game.duofan.common;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {
    private static EventManager instance;
    private HashMap<String, ArrayList<IEventListener>> eventMap;

    private EventManager() {
        eventMap = new HashMap<>();
    }

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void registerToEvent(String eventKey, IEventListener listener) {
        if (!eventMap.containsKey(eventKey)) {
            eventMap.put(eventKey, new ArrayList<>());
        }
        eventMap.get(eventKey).add(listener);
    }

    public void unregisterFromEvent(String eventKey, IEventListener listener) {
        if (eventMap.containsKey(eventKey)) {
            eventMap.get(eventKey).remove(listener);
        }
    }

    public void removeAllEvent() {
        if(eventMap == null){
            return;
        }
        eventMap.clear();
    }

    public void notifyEvent(String eventKey, Object sender, Object e) {
        if (eventMap.containsKey(eventKey)) {
            // 创建副本以避免并发修改问题
            ArrayList<IEventListener> listeners = new ArrayList<>(eventMap.get(eventKey));
            for (IEventListener listener : listeners) {
                listener.OnEvent(sender, e);
            }
        }
    }
}