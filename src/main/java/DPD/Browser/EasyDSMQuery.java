package DPD.Browser;

import DPD.Model.*;
import DPD.Util;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class EasyDSMQuery extends DSMDataStructure {

    private FileBrowser fileBrowser;

    public EasyDSMQuery(String[] matrix, String[] filePaths, List<DependencyType> dependencyCount) {
        super(matrix, filePaths, dependencyCount);
        fileBrowser = new FileBrowser(filePaths);
    }

    public void populateFromBucket(List<DependencyType> dependencies, BucketResult t, Bucket b, String leftOp, String rightOp) {

    }

    private HashMap<String, BucketResult> cache = new HashMap<>();

    public BucketResult populate(List<DependencyType> dependencies) {
        BucketResult t = new BucketResult();
        String key = dependencies.toString();
        if(cache.containsKey(key))
            return cache.get(key);

        if(dependencies.contains(DependencyType.SPECIALIZE)) { // then it definitely doesn't contain Extend or IMPLEMENT (assert this)
            dependencies.remove(DependencyType.SPECIALIZE);

            // only extend
            dependencies.add(DependencyType.EXTEND);
            DependencyType[] dt = new DependencyType[dependencies.size()];
            populate(t, dependencies.toArray(dt));

            // only implement
            dependencies.remove(DependencyType.EXTEND);
            dependencies.add(DependencyType.IMPLEMENT);
            dt = new DependencyType[dependencies.size()];
            populate(t, dependencies.toArray(dt));
        }
        else {
            DependencyType[] dt = new DependencyType[dependencies.size()];
            populate(t, dependencies.toArray(dt));
        }
        cache.put(key, t);
        return t;
    }


    private void populate(BucketResult t, DependencyType... dependencies) {
        int dependency = getDepsAsOne(dependencies);
        if(dependency == 0) return; // cause this would accept all the empty cells
        Entity xList = t.aux;
        Entity yList = t.pivot;

        for(ClassNode c: allClassNodes) {
            List<DepNode> dependents = c.column;
            for(DepNode dep: dependents) {
                if((dep.numValue & dependency) == dependency) {
                    CNode ynode;
                    if( yList.hasClass(dep.col) )
                        ynode = yList.get(dep.col);
                    else {
                        ynode = new CNode(dep.col, Bucket.nextPocket());
                        yList.add(ynode);
                    }
                    CNode xnode = new CNode(dep.row, ynode.pocket);
                    xList.add(xnode);
                }
            }
        }
    }

    public String GetType(int classId) {
        return Util.getType( Util.fixFilePath(this.allClassNodes.get(classId).filePath) );
    }

    public FileNode getFileNode(int classId) {
        return fileBrowser.getByClassId(classId);
    }
}
