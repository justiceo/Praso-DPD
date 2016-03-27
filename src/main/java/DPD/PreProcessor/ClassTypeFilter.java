package DPD.PreProcessor;

import DPD.Enums.ClassType;
import DPD.JClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Justice on 3/17/2016.
 */
public class ClassTypeFilter extends Filter {


    @Override
    public void run() {
        System.out.println("starting class type filter...");
        long startTime = System.currentTimeMillis();

        synchronized (jClasses) {
            int counter = 0;
            Iterator<JClass> iterator = jClasses.iterator();
            while (counter < matrixSize) {
                JClass jClass = null;
                counter++;
                try {
                    jClass = iterator.next();
                    if (!Files.exists(Paths.get(jClass.filePath))) {
                        jClass.classType = ClassType.Unknown;
                    }

                    CompilationUnit cu = JavaParser.parse(new File(jClass.filePath));
                    jClass.classType = getClassType(cu);
                    jClass.typeId = getClassId(cu, jClass.filePath);

                } catch (NoSuchElementException | ParseException | IOException | ClassCastException | NullPointerException e) {
                    // todo: log which exceptions are thrown most, so we can optimize those segments
                    System.out.println("class type filter err - counter (" + counter + "): " + e.toString());
                }
            }
        }
        System.out.println("exiting class type filter (" + (System.currentTimeMillis() - startTime) + "ms)");

    }

    private String getClassId(CompilationUnit cu, String filePath) {
        PackageDeclaration pd = cu.getPackage();
        String x = pd.toString().replace("package ", "");
        x = x.substring(0, x.indexOf(";")) + ".";

        return x + filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf("."));
    }

    private ClassType getClassType(CompilationUnit cu) {
        TypeDeclaration typeDec = cu.getTypes().get(0);
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) typeDec; // most likely the case

        if (cd.getModifiers() == 1025) {
            return ClassType.Abstract;
        } else if (cd.isInterface()) {
            return ClassType.Interface;
        } else if (cd.getModifiers() == 0) { // protected class
            return ClassType.Class;
        } else if (cd.getModifiers() == 1) {
            return ClassType.Class;
        } else return ClassType.Unknown;

    }
}
