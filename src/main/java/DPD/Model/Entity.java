package DPD.Model;

import java.util.*;

/**
 * Created by Justice on 1/10/2017.
 * Entity is a collection of classes, not of dependency nodes
 */
public class Entity extends ArrayList<CNode> {

    private int maxPromotion;

    public int getMaxScore() {
        return maxPromotion;
    }

    @Override
    public boolean addAll(Collection<? extends CNode> list) {
        list.forEach(c -> add(c));
        return true;
    }

    @Override
    public boolean add(CNode cNode) {
        if(contains(cNode)) return true;
        return super.add(cNode);
    }

    public void promoteAll(List<CNode> list) {
        ++maxPromotion;
        for(CNode c: list) {
            CNode in = getByClassId(c.classId);
            in.score += 1;
        }
    }

    public void demoteAll(List<CNode> list) {
        --maxPromotion;
        for(CNode c: list) {
            CNode in = getByClassId(c.classId);
            in.score -= 1;
        }
    }

    public boolean hasClass(int classId) {
        for(int i = 0; i < size(); i++) {
            if(get(i).classId == classId)
                return true;
        }
        return false;
    }

    /* this code is erroneous as multiple classId's can exist but only first returned */
    public CNode getByClassId(int classId) {
        for(int i = 0; i < size(); i++) {
            if(get(i).classId == classId)
                return get(i);
        }
        return null;
    }

    public void removeByClassId(List<CNode> list) {
        for(CNode c: list) {
            CNode internal = getByClassId(c.classId);
            remove(internal);
        }
    }

    // returns true if one of the classes in this collection has this pocket id
    public boolean hasPocket(int pocketId) {
        for(CNode c: this) {
            if(c.pocket == pocketId)
                return true;
        }
        return false;
    }

    public void removePocket(int pocket) {
        Iterator<CNode> iter = iterator();
        while(iter.hasNext()) {
            CNode c = iter.next();
            if(c.pocket == pocket)
                iter.remove();
        }
    }

    public void resetTo(Entity x) {
        clear();
        addAll(x);
    }

    public void setPocket(int original, int newPocket) {
        for(CNode cNode: this) {
            if(cNode.pocket == original)
                cNode.pocket = newPocket;
        }
    }

    public Entity getDuplicates() {
        Entity result = new Entity();
        List<Integer> classMap = new ArrayList<>();
        for(CNode c: this) {
            if(classMap.contains(c.classId)) {
                result.add(c);
            }
            else
                classMap.add(c.classId);
        }
        return result;
    }
}
