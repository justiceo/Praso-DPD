package DPD.DSMMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class Entity extends ArrayList<DepNode> {
    private String name;

    private int maxPromotion;

    public Entity(String name) {
        this.name = name;
    }

    public void promoteAll(List<DepNode> x) {
        // find the node in the list promote it
        ++maxPromotion;
    }

    public void demoteAll(List<DepNode> x) {
        // find the node in the list demote it
        --maxPromotion;
    }

    // returns true if one of the classes in this collection has this pocket id
    public boolean hasPocket(int pocketId) {
        return true;
    }
}
