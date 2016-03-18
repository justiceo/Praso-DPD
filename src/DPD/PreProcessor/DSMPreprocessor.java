package DPD.PreProcessor;

import DPD.DependencyBrowser.Flag;
import DPD.DependencyBrowser.JClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    public void buildJClasses() {
        jClassList = Collections.synchronizedList(new LinkedList<>());
        ClassTypeFilter classTypeFilter = new ClassTypeFilter();
        classTypeFilter.init(jClassList, matrixSize);
        new Thread(classTypeFilter).start();
        ExtendsObservableFilter extendsObservableFilter = new ExtendsObservableFilter("Observable", Flag.ObservableFlag);
        extendsObservableFilter.init(jClassList, matrixSize);
        Thread p = new Thread(extendsObservableFilter);
        p.start();

        for(int i = 0; i < matrixSize; i++) {
            JClass jClass = new JClass();
            jClass.classId = i;
            jClass.classPath = fixClassPath(filePaths[i]);
            jClass.dependencyLine = matrixLines[i];
            jClassList.add(jClass);
        }
        System.out.println("main-thread: done writing objects");



        for(JClass jClass: jClassList) {
            if(jClass.flags != null) {
                System.out.println("\nobs " + jClass.classPath);
            }
        }

        System.out.println("took: " + (System.currentTimeMillis() - startTime ));
    }

    public void saveAsIDM() {
        if(dependencyLine == null || filePaths == null || matrixLines == null) {
            System.out.println("incomplete file structure");
            return;
        }

        String idmFilePath = currFilePath.substring(0, currFilePath.lastIndexOf(".")) + ".idm";

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(idmFilePath)));
            writer.write(dependencyLine + "\n");
            for(int i = 0; i < matrixSize; i++) {
                writer.write(filePaths[i] + " ::: " + matrixLines[i] + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String fixClassPath(String damagedPath) {
        int l = damagedPath.lastIndexOf("_");
        String ext = damagedPath.substring(l).replace("_", ".");
        return damagedPath.substring(0,l).replace(".", "\\") + ext;
    }

}
