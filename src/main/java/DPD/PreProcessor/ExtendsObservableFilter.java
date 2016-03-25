package DPD.PreProcessor;

import DPD.Enums.Flag;
import DPD.JClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Justice on 3/17/2016.
 */
public class ExtendsObservableFilter extends Filter {
    private Flag flag;
    private String filterStr;

    public ExtendsObservableFilter(String filterPtn, Flag signal) {
        this.filterStr = filterPtn;
        this.flag = signal;
    }

    @Override
    public void run() {
        System.out.println("starting ext. observable filter...");
        long startTime = System.currentTimeMillis();

        synchronized (jClasses) {
            int counter = 0;
            Iterator<JClass> iterator = jClasses.iterator();
            while (counter < matrixSize) {
                JClass jClass = null;
                Flag usesObservable = null;
                counter++;

                try {
                    jClass = iterator.next();
                    usesObservable = filterExtends(jClass.classPath, filterStr, flag);
                } catch (NoSuchElementException | ParseException | IOException | NullPointerException e) {
                    System.out.println("ext obs err - counter (" + counter + "):  " + e.toString());
                    continue;
                }

                if (usesObservable != null) {
                    if (jClass.flags == null) jClass.flags = new ArrayList<>();
                    jClass.flags.add(usesObservable);
                }
            }
        }
        System.out.println("exiting ext. obs. filter (" + (System.currentTimeMillis() - startTime) + "ms)");

    }

    private Flag filterExtends(String classPath, String filterStr, Flag flag) throws IOException, ParseException {
        if (classPath == null || !Files.exists(Paths.get(classPath))) return null;
        CompilationUnit cu = JavaParser.parse(new File(classPath));
        List<TypeDeclaration> typeDecs = cu.getTypes();
        for (TypeDeclaration t : typeDecs) {
            try {
                ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) t;
                if (cd.getExtends() != null) {
                    return cd.getExtends().toString().contains(filterStr) ? flag : null;
                }
            } catch (ClassCastException c) {
                System.out.println("ext. obs err: 1014" + c.toString());
            }
        }
        return null;
    }
}
