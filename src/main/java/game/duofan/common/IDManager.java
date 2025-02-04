package game.duofan.common;

import java.util.HashMap;

public class IDManager {

    static IDManager instance;

    public static IDManager getInstance() {
        if (instance == null) {
            instance = new IDManager();
        }

        return instance;
    }

    HashMap<Class, String> ids;

    public String getID(Class c) {
        if (ids == null) {
            ids = new HashMap<>();
        }
        if (!ids.containsKey(c)) {
            String id = Utils.generateID(c.getSimpleName());
            ids.put(c, id);
            System.out.println(c.getSimpleName() + "->" + id);
            return id;
        }
        else{
            return ids.get(c);
        }
    }
}
