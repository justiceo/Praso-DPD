package DPD;

import DPD.Model.CNode;
import DPD.Model.Entity;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Justice on 2/9/2017.
 */
public class EntityTest {

    @Test
    public void addTest() {
        EntityA entity = new EntityA();
        CNode cNode = new CNode(0, 2);
        CNode ref1 = cNode;
        cNode.score = 6;
        entity.add(cNode);
        assertTrue(entity.size() == 1);
        cNode = new CNode(0, 3);
        CNode ref2 = cNode;
        entity.add(cNode);
        assertTrue(entity.size() == 1);
        cNode = entity.get(0);
        System.out.println(cNode);
        assertTrue(ref1 != cNode && ref2 == cNode);
        assertTrue(cNode.classId == 0 && cNode.pocket == 3 && cNode.score == 6);
        System.out.println(entity.getPockets().keySet());
        assertTrue(entity.getPockets().keySet().size() == 1);
        assertTrue(entity.getPockets().keySet().contains(3));

        entity.removePocket(3);
        System.out.println(entity.getPockets().keySet());
        System.out.println(entity);
        assertTrue(entity.isEmpty());
        assertTrue(entity.keySet().isEmpty());

    }

    @Test
    public void removePocketTest() {
        EntityA entity = new EntityA();
        CNode c1 = new CNode(0, 8);
        CNode c2 = new CNode(1, 8);
        entity.addAll(Util.list(c1, c2));
        assertTrue(entity.size() == 2 && entity.hasClass(0) && entity.hasClass(1));
        assertTrue(entity.getPockets().size() == 1 && entity.getPockets().containsKey(8));
        assertTrue(entity.hasPocket(8));

        entity.removePocket(8);
        assertTrue(entity.isEmpty());
        assertTrue(entity.keySet().isEmpty());
        assertTrue(entity.getPockets().isEmpty());
        assertFalse(entity.hasPocket(8));
    }

    class EntityA extends Entity {
        public HashMap<Integer, List<Integer>> getPockets() {
            return pockets;
        }
    }
}
