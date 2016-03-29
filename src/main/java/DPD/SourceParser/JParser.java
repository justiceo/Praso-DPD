package DPD.SourceParser;

import DPD.Enums.ASTAnalysisType;
import DPD.ILogger;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Justice on 1/27/2016.
 * <p>
 * Passes a java file and provides valuable information about its inner working.
 * - including, method names, return values, fields and their types
 * <p>
 * Sample use case, I know that classA.java uses classB.java,
 * I want to determine if this is a typed use, is the instance of classB is in a list or just a var
 * And if any method in classA returns classB
 * <p>
 * I can get all fields in classA and check if any of them is of type classB
 * I can get all methods in classA and check if it returns classB, or has it as parameter
 */
public class JParser {

    ILogger logger;

    public JParser(ILogger logger) {
        this.logger = logger;
    }

    public String examine(String sourceClass, ASTAnalysisType astAnalysisType, String targetType) {
        switch (astAnalysisType) {
            case HasCollectionOf:
                return testAggregation(sourceClass, targetType);
            case IteratesOver:
                return testLoop(sourceClass, targetType);
        }
        return null;
    }

    private String testLoop(String sourceClass, String targetType) {
        CompilationUnit sourceCU = this.init(sourceClass);

        String targetClassName = targetType.contains(".") ? targetType.substring(targetType.lastIndexOf(".") + 1) : targetType;

        // get all for loops
        MethodVisitor mv = new MethodVisitor();
        mv.visit(sourceCU, null);
        List<ForeachStmt> forLoops = mv.forLoops;

        // check for ones that have targetClass
        for (ForeachStmt statement : forLoops) {
            Type varType = statement.getVariable().getType();
            if (varType.toString().equals(targetClassName)) {
                return statement.toString();
            }
        }
        return null;
    }

    private String testAggregation(String sourceClass, String targetClass) {
        return null;
    }

    private CompilationUnit init(String sourceClass) {
        // creates an input stream for the file to be parsed

        if (!Files.exists(Paths.get(sourceClass))) {
            System.out.println("file: " + sourceClass + "  - doesn't exist ");
            return null;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(sourceClass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CompilationUnit cu = null;
        try {
            // parse the file
            cu = JavaParser.parse(in);
            in.close();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cu;
    }

    public String getFullPackageName(String className) {
        Package[] packages = Package.getPackages();

        for (Package p : packages) {
            String pack = p.getName();
            String tentative = pack + "." + className;
            try {
                Class.forName(tentative);
                return tentative;
            } catch (ClassNotFoundException e) {
                System.out.println("Jparser err (1015): " + e.toString());
            }
        }
        return null;
    }

    private static class MethodVisitor extends VoidVisitorAdapter {

        public List<ForeachStmt> forLoops = new LinkedList<>();

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            if (n.getBody() == null) return;
            List<Statement> statementList = n.getBody().getStmts();
            if (statementList != null) {
                forLoops.addAll(statementList.stream()
                        .filter(stmt -> stmt.getClass().equals(ForeachStmt.class))
                        .map(stmt -> (ForeachStmt) stmt)
                        .collect(Collectors.toList()));
            }
        }
    }
}

