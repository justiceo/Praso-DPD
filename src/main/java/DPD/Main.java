package DPD;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private static final String configFile = "config.xml";
    private static final String testDsmFile = "files\\dsm\\Maze.dsm";

    public static void main(String[] args) throws InterruptedException {
        /* tryLoad the dsm file and on the way processing the classes
        LoadDSM loadDSM = new LoadDSM();
        try {
            if (loadDSM.load(testDsmFile)) {
                loadDSM.buildJClasses();
            }
        } catch (InterruptedException e1) {
            System.out.println("error loading " + testDsmFile + ": " + e1.toString());
            System.exit(0);
        }

        // hydrates the fields in each jClass
        loadDSM.process();

        Logger logger = Logger.getGlobal();
        DSMBrowser browser = new IDMBrowser(logger, loadDSM.getClassList(), loadDSM.getDependencyLine());*/

    }
}
