package DPD.PreProcessor;

import DPD.DependencyBrowser.Flag;
import DPD.DependencyBrowser.JClass;
import DPD.Enums.ClassType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.Thread.sleep;

/**
 * Created by Justice on 3/17/2016.
 */
public class ClassTypeFilter extends Filter {


    @Override
    public void run()  {
        if(jClasses.size() == 0) {
            try { sleep(100); }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int counter = 0;
        synchronized (jClasses) {
            Iterator<JClass> iterator = jClasses.iterator();
            while (counter < matrixSize) {
                JClass jClass = null;
                try {
                    jClass = iterator.next();
                    jClass.classType = getClassType(jClass.classPath);
                }catch (NoSuchElementException | ParseException | IOException e) {
                    continue;
                }
                counter++;
            }
        }

    }

    private ClassType getClassType(String filePath) throws IOException, ParseException {
        if(!Files.exists(Paths.get(filePath))) return ClassType.Unknown;

        CompilationUnit cu = JavaParser.parse(new File(filePath));
        List<TypeDeclaration> typeDecs = cu.getTypes();
        for(TypeDeclaration t: typeDecs) {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) t;
            if(cd.getModifiers() == 1025) {
                return ClassType.Abstract;
            }
            else if(cd.isInterface()) {
                return ClassType.Interface;
            }
            else if(cd.getModifiers() == 0) { // protected class
                return ClassType.Class;
            }
            else if(cd.getModifiers() == 1) {
                return ClassType.Class;
            }
            else return ClassType.Unknown;
        }
        return null;
    }

}
