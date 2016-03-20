package DPD.PreProcessor;

import DPD.DependencyBrowser.Flag;
import DPD.DependencyBrowser.JClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Justice on 3/17/2016.
 */
public class DSMPreprocessor {
    String[] matrixLines;
    String[] filePaths;
    String dependencyLine;
    private String currFilePath;
    private int matrixSize;
    private List<JClass> jClassList;

    private long startTime;

    public boolean load(String dsmFilePath) {
        if(!Files.exists(Paths.get(dsmFilePath))) {
            System.out.println("dsm file does not exist");
            return false;
        }

        startTime = System.currentTimeMillis();

        currFilePath = dsmFilePath;
        File dsmFile = new File(dsmFilePath);
        Scanner input = null;
        try {
            input = new Scanner(dsmFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(input.hasNext()) {
            dependencyLine = input.nextLine();

            matrixSize = Integer.parseInt(input.nextLine());
            matrixLines = new String[matrixSize];
            filePaths = new String[matrixSize];

            for (int i = 0; i < matrixSize; i++) matrixLines[i] = input.nextLine();
            for (int i = 0; i < matrixSize; i++) filePaths[i] = input.nextLine();

            input.close();
        }

        return true;
    }

    public void buildJClasses() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        jClassList = Collections.synchronizedList(new LinkedList<>());

        // create and start the threads
        ClassTypeFilter classTypeFilter = new ClassTypeFilter();
        classTypeFilter.init(jClassList, matrixSize);
        Thread classTypeFThread = new Thread(classTypeFilter);
        classTypeFThread.start();

        ExtendsObservableFilter extendsObservableFilter = new ExtendsObservableFilter("Observable", Flag.ObservableFlag);
        extendsObservableFilter.init(jClassList, matrixSize);
        Thread extObservableFThread = new Thread(extendsObservableFilter);
        extObservableFThread.start();

        LoopsFilter loopsFilter = new LoopsFilter("IObserver", Flag.ObservableFlag);
        loopsFilter.init(jClassList, matrixSize);
        Thread loopsFThread = new Thread(loopsFilter);
        loopsFThread.start();

        for(int i = 0; i < matrixSize; i++) {
            JClass jClass = new JClass();
            jClass.classId = i;
            jClass.classPath = fixClassPath(filePaths[i]);
            jClass.dependencyLine = matrixLines[i];
            jClass.flags = new LinkedList<>();
            jClassList.add(jClass);
        }

        // join all the threads
        classTypeFThread.join();
        extObservableFThread.join();
        loopsFThread.join();

        System.out.println("all filters have finished (" + (System.currentTimeMillis() - startTime) + "ms)" );
    }

    public List<JClass> getjClassList() {
        return jClassList;
    }
    public String getDependencyLine() {
        return dependencyLine;
    }

    public String fixClassPath(String damagedPath) {
        int l = damagedPath.lastIndexOf("_");
        String ext = damagedPath.substring(l).replace("_", ".");
        return damagedPath.substring(0,l).replace(".", "\\") + ext;
    }

}
