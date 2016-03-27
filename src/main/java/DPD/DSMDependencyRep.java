package DPD;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Justice on 3/20/2016.
 */
public class DSMDependencyRep implements DependencyRep {

    private boolean isDamaged = true;
    private String dependencyLine;
    private String[] filePaths;
    private String[] matrixLines;
    private String fileName;
    private String absDir;
    private List<String> locations;

    public DSMDependencyRep() {
    }

    public DSMDependencyRep(String fileName) throws FileNotFoundException {

        this.fileName = fileName;
        Scanner in = new Scanner(new File(fileName));          /* load dsm file */
        dependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {           /* read the matrix */
            matrixLines[i] = in.nextLine();
        }

        filePaths = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {                    /* read the java files */
            filePaths[i] = fixFilePath(in.nextLine());
        }

        // test
        //


        setAbsDirFromPath();

    }

    public String getDependencyLine() {
        return dependencyLine;
    }

    public String[] getMatrixLines() {
        return matrixLines;
    }

    public String[] getFilePaths() {
        return filePaths;
    }

    public void saveAs(String fileName, String dependencyLine, String[] filePaths, String[] matrixLines) throws IOException {
        if (filePaths == null || matrixLines == null || dependencyLine == null) {
            System.out.println("the information necessary to create a dsm is incomplete");
            return;
        }

        assert filePaths.length == matrixLines.length;
        int matrixSize = filePaths.length;

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for (int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        for (String filePath : filePaths) {
            writer.write(filePath + "\n");
        }

        writer.flush();
        writer.close();
    }

    public void saveWithRelativePaths() throws IOException {
        if (filePaths == null || matrixLines == null || dependencyLine == null) {
            System.out.println("the information necessary to create a dsm is incomplete");
            return;
        }

        int matrixSize = filePaths.length;
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = getRelativePath(filePaths[i]);
        }


        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for (int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        for (String filePath : filePaths) {
            writer.write(filePath + "\n");
        }
        writer.write(locations.toString() + "\n");
        writer.flush();
        writer.close();
    }

    private void setAbsDir() throws IOException, ParseException {
        String filePath = filePaths[0];
        if (!Files.exists(Paths.get(filePath)))
            throw new IOException("This operation can only be performed on the host system - for accuracy sake.");

        CompilationUnit cu = JavaParser.parse(new File(filePath));
        String packageName = cu.getPackage().toString().replace("package ", "");
        packageName = packageName.substring(0, packageName.lastIndexOf(";"));
        String fileName = packageName + "." + cu.getTypes().get(0).getName() + "_java";
        fileName = fixFilePath(fileName);
        absDir = filePath.replace(fileName, "");
        locations = new ArrayList<>();
        locations.add(absDir);
    }

    private void setAbsDirFromPath() {
        String filePath = filePaths[0];
        int cutoff = filePath.indexOf("src");
        if(cutoff != -1) {
            absDir = filePath.substring(0, cutoff);
            locations = new ArrayList();
            locations.add(absDir);
            System.out.println("package: " + absDir);
        }
    }

    public String getRelativePath(String absolutePath) {
        return absolutePath.replace(absDir, "");
    }

    public String fixFilePath(String damagedPath) {
        try {
            int l = damagedPath.lastIndexOf("_");
            if (l == -1) return damagedPath; // it's good.
            int count = damagedPath.split("_").length;
            if (!damagedPath.split("_")[count - 1].equals("java")) {
                System.out.println("cannot fix path: " + damagedPath);
                return damagedPath;
            }
            String ext = damagedPath.substring(l).replace("_", ".");
            return damagedPath.substring(0, l).replace(".", "\\") + ext;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("err: " + damagedPath);
        }
        return null;
    }
}
