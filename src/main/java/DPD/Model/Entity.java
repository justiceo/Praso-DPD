package DPD.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 * Entity is a collection of classes, not of dependency nodes
 */
public class Entity extends ArrayList<CNode> {

    private int maxPromotion;

    public void promoteAll(List<CNode> x) {
        // todo: find the node in the list promote it
        ++maxPromotion;
    }

    public void demoteAll(List<CNode> x) {
        // todo: find the node in the list demote it
        --maxPromotion;
    }

    public boolean hasClass(int classId) {
        // todo;
        return true;
    }

    public CNode getByClassId(int classId) {
        // todo:
        return null;
    }

    // returns true if one of the classes in this collection has this pocket id
    public boolean hasPocket(int pocketId) {
        // todo;
        return true;
    }

    public void removePocket(int pocket) {

    }
}
