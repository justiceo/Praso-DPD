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

    public Entity getEntity(String entityId) {
        return get(entityId);
    }

    public static int getPocket() {
        return pocket;
    }

    public boolean isPocketInAllEntities(int pocket) {
        for(Entity e: values()) {
            if( !e.hasPocket(pocket) )
                return false;
        }
        return true;
    }

    public boolean isPocketInAnyEntity(int pocket) {
        for(Entity e: values()) {
            if(e.hasPocket(pocket))
                return true;
        }
        return false;
    }
}
