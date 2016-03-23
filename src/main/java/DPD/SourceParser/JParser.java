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
 *
 * Passes a java file and provides valuable information about its inner working.
 * - including, method names, return values, fields and their types
 *
 * Sample use case, I know that classA.java uses classB.java,
 * I want to determine if this is a typed use, is the instance of classB is in a list or just a var
 * And if any method in classA returns classB
 *
 * I can get all fields in classA and check if any of them is of type classB
 * I can get all methods in classA and check if it returns classB, or has it as parameter
 *
 */
public class JParser implements ASTAnalyzer {

    ILogger logger;

    public JParser(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public boolean examine(String sourceClass, ASTAnalysisType astAnalysisType, String targetClass) {
        switch (astAnalysisType) {
            case Aggregates:
                return testAggregation(sourceClass, targetClass);
            case Loops:
                return testLoop(sourceClass, targetClass);
        }
        return false;
    }

    private boolean testLoop(String sourceClass, String targetClass) {
        CompilationUnit sourceCU = this.init(sourceClass.replace(".", "\\").replace("_", "."));
        CompilationUnit targetCU = this.init(targetClass.replace(".", "\\").replace("_", "."));
        String targetClassName = targetClass.substring(targetClass.lastIndexOf(".")+1, targetClass.lastIndexOf("_"));

        // get all for loops
        MethodVisitor mv = new MethodVisitor();
        mv.visit(sourceCU, null);
        List<ForeachStmt> forLoops = mv.forLoops;

        // check for ones that have targetClass
        for(ForeachStmt statement: forLoops) {
            Type varType = statement.getVariable().getType();
            System.out.println("\n ");
            if(varType.toString().equals(targetClassName)) {
                System.out.println("matching foreach statement in " + sourceClass + ": \n " + statement.toString());
                return true;
            }
        }
        return false;
    }

    private boolean testAggregation(String sourceClass, String targetClass) {
        return true;
    }

    private CompilationUnit init(String sourceClass) {
        // creates an input stream for the file to be parsed

        if(!Files.exists(Paths.get(sourceClass))) {
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
                continue;
            }
        }
        return null;
    }

    private static class MethodVisitor extends VoidVisitorAdapter {

        public List<ForeachStmt> forLoops = new LinkedList<>();

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            if(n.getBody() == null) return;
            List<Statement> statementList = n.getBody().getStmts();
            if(statementList != null) {
                forLoops.addAll(statementList.stream()
                        .filter(stmt -> stmt.getClass().equals(ForeachStmt.class))
                        .map(stmt -> (ForeachStmt) stmt)
                        .collect(Collectors.toList()));
            }
        }
    }

    private String fixClassPath(String damagedPath) {
        return damagedPath.replace(".", "\\").replace("_", ".");
    }
}
