package game.duofan.common;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {
    private static EventManager instance;
    private HashMap<String, ArrayList<IEventListener>> eventMap;
    private HashMap<String, ArrayList<IEventListener>> persistEventMap;

    private EventManager() {
        eventMap = new HashMap<>();
        persistEventMap = new HashMap<>();
    }

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void registerToEvent(String eventKey, IEventListener listener) {
        ArrayList<IEventListener> list = eventMap.getOrDefault(eventKey, null);
        if (list == null) {
            list = new ArrayList<>();
            eventMap.put(eventKey, list);
        }
        list.add(listener);
    }

    public void unregisterFromEvent(String eventKey, IEventListener listener) {
        ArrayList<IEventListener> list = eventMap.getOrDefault(eventKey, null);
        if (list != null) {
            list.remove(listener);
        }
    }

    public void registerToPersistEvent(String eventKey, IEventListener listener) {
        ArrayList<IEventListener> list = persistEventMap.getOrDefault(eventKey, null);
        if (list == null) {
            list = new ArrayList<>();
            persistEventMap.put(eventKey, list);
        }
        list.add(listener);
    }

    public void removeAll_NotPersist_Event() {
        if (eventMap == null) {
            return;
        }
        eventMap.clear();
    }

    public void notifyEvent(String eventKey, Object sender, Object e) {
        ArrayList<IEventListener> list = eventMap.getOrDefault(eventKey, null);
        ArrayList<IEventListener> listeners = null;
        if (list != null) {
            // 创建副本以避免并发修改问题
            listeners = new ArrayList<>(list);
            for (IEventListener listener : listeners) {
                listener.OnEvent(sender, e);
            }
        }

        list = persistEventMap.getOrDefault(eventKey, null);
        if (list != null) {
            if (listeners == null) {
                listeners = new ArrayList<>(list);
            } else {
                listeners.clear();
                listeners.addAll(list);
            }
            for (IEventListener listener : listeners) {
                listener.OnEvent(sender, e);
            }
        }
    }
}