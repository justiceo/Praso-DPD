package DPD.REPL;

import DPD.DSMMapper.Entity;
import DPD.DependencyBrowser.DSMBrowser;

/**
 * Created by Justice on 1/10/2017.
 */
public class ExecEnv {



    public ExecEnv(DSMBrowser dsmBrowser) {

    }

    public void createEntity(String entityId, String name) {
        System.out.println(entityId + " " + name);
        Entity e = new Entity(name);
    }

    public void createBucket(String bucketId, String name) {
        System.out.println(bucketId + " " + name);

    }

    public void fillBucket(String bucketId, String command) {
        System.out.println(bucketId + " " + command);

    }

    public void filterBucket(String bucketId, String command) {
        System.out.println(bucketId + " " + command);

    }

    public void promoteBucket(String bucketId, String command) {
        System.out.println(bucketId + " " + command);

    }

    public void demoteBucket(String bucketId, String command) {
        System.out.println(bucketId + " " + command);

    }

    public void resolveBucket(String bucketId, String command) {
        System.out.println(bucketId + " " + command);

    }

    public void printObject(String objectId) {
        System.out.println(objectId);
    }
}
