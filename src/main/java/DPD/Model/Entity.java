package DPD.Model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 * Entity is a collection of classes, not of dependency nodes
 */
public class Entity extends ArrayList<CNode> {

    private int maxPromotion;

    public void promoteAll(List<CNode> list) {
        for(int i = 0; i < list.size(); i++){
            if( hasClass( list.get(i).classId ) ) {
                ++getByClassId( list.get(i).classId ).score;
            }
        }
        ++maxPromotion;
    }

    public void demoteAll(List<CNode> list) {
        for(int i = 0; i < list.size(); i++){
            if( hasClass( list.get(i).classId ) ) {
                --getByClassId( list.get(i).classId ).score;
            }
        }
        --maxPromotion;
    }

    public boolean hasClass(int classId) {
        for(int i = 0; i < size(); i++) {
            if(get(i).classId == classId)
                return true;
        }
        return false;
    }

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
        throw new NotImplementedException();
    }

    public void removePocket(int pocket) {
        throw new NotImplementedException();
    }
}
