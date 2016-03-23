package DPD.PreProcessor;

import DPD.DSMDependencyRep;
import DPD.DependencyBrowser.Flag;
import DPD.DependencyBrowser.JClass;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 3/17/2016.
 */
public class DSMPreprocessor {
    private String[] matrixLines;
    private String[] filePaths;
    private String dependencyLine;
    private String currFilePath;
    private int matrixSize;
    private List<JClass> jClassList;

    private long startTime;

    public boolean load(String dsmFilePath) throws FileNotFoundException {
        if(!Files.exists(Paths.get(dsmFilePath))) {
            System.out.println("dsm file does not exist");
            return false;
        }

        startTime = System.currentTimeMillis();

        currFilePath = dsmFilePath;
        DSMDependencyRep dsmDependencyRep = new DSMDependencyRep(dsmFilePath);
        this.matrixLines = dsmDependencyRep.getMatrixLines();
        this.filePaths = dsmDependencyRep.getFilePaths();
        this.matrixSize = matrixLines.length;
        this.dependencyLine = dsmDependencyRep.getDependencyLine();

        return true;
    }

    public void buildJClasses() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        jClassList = Collections.synchronizedList(new LinkedList<>());

        for(int i = 0; i < matrixSize; i++) {
            JClass jClass = new JClass();
            jClass.classId = i;
            jClass.classPath = filePaths[i];
            jClass.dependencyLine = matrixLines[i];
            jClass.flags = new LinkedList<>();
            jClassList.add(jClass);
        }

        Filter.init(jClassList, matrixSize);


        // create and start the threads
        ExpandMatrixFilter expandMatrixFilter = new ExpandMatrixFilter(dependencyLine);
        expandMatrixFilter.start();

        ClassTypeFilter classTypeFilter = new ClassTypeFilter();
        classTypeFilter.start();

        ExtendsObservableFilter extendsObservableFilter = new ExtendsObservableFilter("Observable", Flag.ObservableFlag);
        extendsObservableFilter.start();

        LoopsFilter loopsFilter = new LoopsFilter("IObserver", Flag.ObservableFlag);
        loopsFilter.start();


        // join all the threads
        classTypeFilter.join();
        extendsObservableFilter.join();
        loopsFilter.join();
        expandMatrixFilter.join();

        System.out.println("all filters have finished (" + (System.currentTimeMillis() - startTime) + "ms)" );
    }

    public List<JClass> getClassList() {
        return jClassList;
    }
    public String getDependencyLine() {
        return dependencyLine;
    }
}
