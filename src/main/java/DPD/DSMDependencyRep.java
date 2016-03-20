package DPD;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public DSMDependencyRep() {
    }

    public DSMDependencyRep(String fileName) throws FileNotFoundException {

        this.fileName = fileName;
        Scanner in = new Scanner(new File(fileName));          /* load dsm file */
        dependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        for(int i=0; i<matrixSize; i++) {           /* read the matrix */
            matrixLines[i] = in.nextLine();
        }

        filePaths = new String[matrixSize];
        if(isDamaged) { // todo: put flag for damaged files
            for (int i = 0; i < matrixSize; i++) {                    /* read the java files */
                filePaths[i] = fixFilepath(in.nextLine());
            }
        }
        else {
            for (int i = 0; i < matrixSize; i++) {                    /* read the java files */
                filePaths[i] = in.nextLine();
            }
        }

        // test
        //

        try {
            for(int i=0; i<filePaths.length; i++)
                filePaths[i] = fixFilepath(filePaths[i]);
            setAbsDir();
            // for file in filePaths, set relative paths
            // save them to new file.

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        if(filePaths == null || matrixLines == null || dependencyLine == null) {
            System.out.println("the information necessary to create a dsm is incomplete");
            return;
        }

        assert filePaths.length == matrixLines.length;
        int matrixSize = filePaths.length;

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for(int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        if(true) { // todo: put flag for damaged files
            for (int i = 0; i < matrixSize; i++) {
                writer.write(filePaths[i] + "\n");
            }
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

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for (int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        if (true) { // put in flag for damaged files
            for (int i = 0; i < matrixSize; i++) {
                writer.write(filePaths[i] + "\n");
            }
        }
        writer.flush();
        writer.close();
    }

    public boolean isPathsAbsolute() {
        return new File(filePaths[0]).isAbsolute();
    }

    private void setAbsDir() throws IOException, ParseException {
        String filePath = filePaths[0];
        if(!Files.exists(Paths.get(filePath)))
            throw new IOException("This operation can only be performed on the host system - for accuracy sake.");

        CompilationUnit cu = JavaParser.parse(new File(filePath));
        String packageName = cu.getPackage().toString().replace("package ", "");
        packageName = packageName.substring(0, packageName.lastIndexOf(";"));
        String fileName = packageName + "." + cu.getTypes().get(0).getName() + "_java";
        fileName = fixFilepath(fileName);
        absDir = filePath.replace(fileName, "");

        System.out.println("abs dir is: " + absDir);
    }

    public String getRelativePath(String absolutePath) {
        return absolutePath.replace(absDir, "");
    }

    public String fixFilepath(String damagedPath) {
        try {
            int l = damagedPath.lastIndexOf("_");
            if(l == -1) return damagedPath; // it's good.
            if(!damagedPath.split("_")[1].equals("java")) {
                System.out.println("cannot fix path: " + damagedPath);
                return damagedPath;
            }
            String ext = damagedPath.substring(l).replace("_", ".");
            return damagedPath.substring(0, l).replace(".", "\\") + ext;
        }catch (IndexOutOfBoundsException e) {
            System.out.println("err: " + damagedPath);
        }
        return null;
    }
}
