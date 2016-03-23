package DPD.PreProcessor;

import DPD.DependencyBrowser.JClass;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

        synchronized (jClasses) {
            int counter = 0;
            Iterator<JClass> iterator = jClasses.iterator();
            while (counter < matrixSize) {
                counter++;
                try {
                    JClass jClass = iterator.next();
                    jClass.dependencyLine = expandLine(jClass.dependencyLine);
                }catch (NoSuchElementException e) {
                    System.out.println("expand matrix filter err - counter (" + counter + "): " + e.toString());
                }
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
