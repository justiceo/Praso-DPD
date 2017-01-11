package DPD.DependencyBrowser;

import DPD.EasyDSMQuery;
import DPD.SourceParser.FileBrowser;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class MatrixBrowser {

    private EasyDSMQuery dsm;
    private FileBrowser fileBrowser;
    private static int pocketCount;

    public MatrixBrowser(EasyDSMQuery dsm, FileBrowser fileBrowser){
        this.fileBrowser = fileBrowser;
        this.dsm = dsm;
    }

    public void fill(String command, Object e1, Object e2) {
    }

    // takes care of e1 generalizes e2, that is e2 extends/implements e1
    public void generalize(Entity e1, Entity e2) {
        List<Integer> e1entries = dsm.get_abstraction_classes();
        for(int classIndex: e1entries){
            ClassNode cn = new ClassNode(classIndex, e1.nextPocket());
            e1.add(cn);

            // get corresponding e2 fellas
            List<Integer> e2entries = dsm.get_specializations_of(classIndex);
            e2entries.stream().forEach(e -> {
                e2.add(new ClassNode(e, cn.pocketId));
            });
        }
    }

    class Entity {
        HashMap<Integer, ClassNode> classList;

        // if cn already exists, it would be erased
        void add(ClassNode cn) {
            classList.put(cn.indexOfClass, cn);
        }

        public int nextPocket() {
            return ++pocketCount;
        }
    }

    class ClassNode {
        int indexOfClass;
        int pocketId;

        public ClassNode(int indexOfClass, int pocketId) {
            this.indexOfClass = indexOfClass;
            this.pocketId = pocketId;
        }
    }
}
