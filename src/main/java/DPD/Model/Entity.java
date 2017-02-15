package DPD.Model;

import DPD.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 * Entity is a collection of classes, not of dependency nodes
 */
public class Entity extends HashMap<Integer, CNode> {

    private int promotionCounter;
    private int currentScore;
    protected HashMap<Integer, List<Integer>> pockets = new HashMap<>();

    public int getMaxScore() {
        return promotionCounter;
    }


    public void addAll(Collection<? extends CNode> list) {
        list.forEach(c -> add(c));
    }

    public void add(CNode cNode) {

        if(containsKey(cNode.classId) && !values().contains(cNode)) {
            System.out.println("warn: Entity.add() - overwriting class (" + cNode.classId + ", " + get(cNode.classId).pocket + ")->(" + cNode.classId + ", " + cNode.pocket + ")");
            cNode.score = get(cNode.classId).score; // but copy over score
            remove(cNode.classId); // we don't want to leave it's pocket reference, so we clear
        }
        put(cNode.classId, cNode);
        if(pockets.containsKey(cNode.pocket))
            pockets.get(cNode.pocket).add(cNode.classId);
        else {
            pockets.put(cNode.pocket, Util.list(cNode.classId));
        }
    }

    public void promoteAll(Collection<? extends CNode> list) {
        ++promotionCounter;
        for(CNode c: list) {
            CNode in = get(c.classId);
            in.score += 1;
            ++currentScore;
        }
    }

    public void demoteAll(Collection<? extends CNode> list) {
        --promotionCounter;
        for(CNode c: list) {
            CNode in = get(c.classId);
            in.score -= 1;
            ++currentScore;
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
        List<Integer> classList = pockets.get(cn.pocket);
        if(classList.contains(cn.classId))
            classList.remove(Integer.valueOf(cn.classId));

        // if pocket is empty, remove it's key
        if(classList.isEmpty())
            pockets.remove(cn.pocket);

        super.remove(classId);
    }

    public CNode remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        removeAll(new ArrayList<>(values()));
    }

    // returns true if one of the classes in this collection has this pocket id
    public boolean hasPocket(int pocketId) {
        return pockets.containsKey(pocketId);
    }

    public void removePocket(int pocket) {
        if( !pockets.containsKey(pocket)) return;

        List<Integer> classList = pockets.get(pocket);
        if(classList != null)
            for(int c: classList)
                super.remove(c);
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

    public double getScore() {
        int maxScore = promotionCounter * size();
        return (currentScore * 1.0 ) / maxScore;
    }
}
