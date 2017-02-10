package DPD.Model;

import DPD.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 * Entity is a collection of classes, not of dependency nodes
 */
public class Entity extends HashMap<Integer, CNode> {

    private int maxPromotion;
    private HashMap<Integer, List<Integer>> pockets = new HashMap<>();

    public int getMaxScore() {
        return maxPromotion;
    }


    public boolean addAll(Collection<? extends CNode> list) {
        list.forEach(c -> add(c));
        return true;
    }

    public void add(CNode cNode) {
        if(containsKey(cNode.classId))
            System.out.println("warn: Entity.add() - overwriting class " + cNode.classId);
        put(cNode.classId, cNode);
        if(pockets.containsKey(cNode.pocket))
            pockets.get(cNode.pocket).add(cNode.classId);
        else {
            pockets.put(cNode.pocket, Util.list(cNode.classId));
        }
    }

    public void promoteAll(Collection<? extends CNode> list) {
        ++maxPromotion;
        for(CNode c: list) {
            CNode in = get(c.classId);
            in.score += 1;
        }
    }

    public void demoteAll(Collection<? extends CNode> list) {
        --maxPromotion;
        for(CNode c: list) {
            CNode in = get(c.classId);
            in.score -= 1;
        }
    }

    public boolean hasClass(int classId) {
        return keySet().contains(classId);
    }

    public void removeAll(Collection<? extends CNode> list) {
        for(CNode c: list) remove(c.classId);
    }

    public void remove(int classId) {
        // remove it from pockets
        CNode cn = get(classId);
        List<Integer> cl = pockets.get(cn.pocket);
        if(cl.contains(cn.classId))
            cl.remove(Integer.valueOf(cn.classId));

        super.remove(classId);
    }


    // returns true if one of the classes in this collection has this pocket id
    public boolean hasPocket(int pocketId) {
        return pockets.containsKey(pocketId);
    }

    public void removePocket(int pocket) {
        List<Integer> classList = pockets.get(pockets);
        classList.forEach(this::remove);
        pockets.remove(pocket);
        // since we're removing by values, test to make sure pocket don't exist afterwards
    }

    public void resetTo(Entity x) {
        clear();
        addAll(x.toList());
    }

    public void setPocket(int original, int newPocket) {
        assertHasPocket(original);
        List<Integer> oldP = pockets.get(original);
        oldP.forEach(c -> get(c).pocket = newPocket);
        pockets.put(newPocket, oldP);
        pockets.remove(original);
    }

    public void assertHasPocket(int pocket) {
        if( !pockets.containsKey(pocket) )
            throw new IllegalArgumentException();
    }

    public Collection<CNode> toList() {
        return values();
    }


}
