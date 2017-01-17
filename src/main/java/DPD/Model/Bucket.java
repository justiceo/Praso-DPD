package DPD.Model;

import java.util.HashMap;

/**
 * Created by Justice on 1/12/2017.
 */
public class Bucket extends HashMap<String, Entity> {

    private static int pocket;

    public static int nextPocket() {
        return ++pocket;
    }

    public void addIfNotExists(String... entityIds) {
        for(String entityId: entityIds) {
            putIfAbsent(entityId, new Entity());
        }
    }

    public static int getPocket() {
        return pocket;
    }
}
