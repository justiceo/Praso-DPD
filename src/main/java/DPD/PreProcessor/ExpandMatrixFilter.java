package DPD.PreProcessor;

import DPD.DependencyBrowser.JClass;

import java.util.Iterator;

import static java.lang.Thread.sleep;

/**
 * Created by Justice on 3/22/2016.
 */
public class ExpandMatrixFilter extends Filter {

    int numOfDependencyType = 0;
    String nothing = "000000000000000";

    public ExpandMatrixFilter(String dependencyLine) {
        numOfDependencyType = dependencyLine.split(",").length;
        nothing = nothing.substring(0, numOfDependencyType);
        assert nothing.length() == numOfDependencyType;
    }
    @Override
    public void run() {
        System.out.println("starting expand matrix lines filter...");
        long startTime = System.currentTimeMillis();
        while(jClasses.size() == 0) {
            try { sleep(30); }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int counter = 1;
        Iterator<JClass> iterator = jClasses.iterator();
        while (counter < matrixSize) {
            counter++;
            synchronized (jClasses) {
                JClass jClass = iterator.next();
                jClass.dependencyLine = expandLine(jClass.dependencyLine);
            }
        }
        System.out.println("exiting expand matrix line filter (" + (System.currentTimeMillis() - startTime) + "ms)");
    }

    private String expandLine(String dependencyLine) {
        // split it into pieces
        String[] deps = dependencyLine.split(" ");
        for (int i = 0; i < matrixSize; i++) {
            if (deps[i].equals("0"))
                deps[i] = nothing;
        }
        return String.join("", deps);
    }

}
