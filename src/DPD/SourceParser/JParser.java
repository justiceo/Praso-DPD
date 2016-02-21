package DPD.SourceParser;

import DPD.DSMMapper.IPattern;
import DPD.Enums.ASTAnalysisType;
import DPD.Enums.DependencyType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
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

    public static void main(String[] args) {
        // creates an input stream for the file to be parsed
        FileInputStream in = null;
        try {
            in = new FileInputStream("D:\\Code\\IdeaProjects\\DesignPatterns\\src\\DPD\\JavaParser\\JParser.java");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CompilationUnit cu = null;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // prints the resulting compilation unit to default system output
        for(Comment c: cu.getComments()) {
            System.out.println(c.getContent());
        }
        new MethodVisitor().visit(cu, null);
    }

    private void testDo() {
        int number = 1;
        do {
            System.out.println(number);
            number--;
        }while(number > 0);
    }

    @Override
    public String[] getAssociatedDependency(String sourceClassStr, DependencyType dependencyType) {
        return new String[0];
    }

    @Override
    public void validate(IPattern pattern) {

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

        /**
         * Temporary: print out the fields
         *
         */
        return false;
    }
/*
    private void getMethodsOfType(CompilationUnit cu, MethodType methodType) {
        List<TypeDeclaration> types = cu.getTypes();
        for (TypeDeclaration type : types) {
            List<BodyDeclaration> members = type.getMembers();
            for (BodyDeclaration member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    getForeachStatements(method);
                }
            }
        }
    }
*/

    private List<Statement> getStatementsOfType(CompilationUnit cu, Class statementClass) {
        List<TypeDeclaration> types = cu.getTypes();
        List<Statement> statements = new LinkedList<>();
        for (TypeDeclaration type : types) {
            List<BodyDeclaration> members = type.getMembers();
            for (BodyDeclaration member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    statements.addAll(getStatementTypeFromMethod(method, statementClass));
                }
            }
            //methodType.returnType()
        }
        return statements;
    }


    private List<Statement> getStatementTypeFromMethod(MethodDeclaration method, Class statementClass) {
        List<Statement> allStatements = method.getBody().getStmts();
        List<Statement> desiredStatements = new LinkedList<>();
        if(allStatements != null) {
            desiredStatements.addAll(allStatements.stream()
                    .filter(stmt -> stmt.getClass().equals(statementClass))
                    .map(stmt -> (ForeachStmt) stmt)
                    .collect(Collectors.toList()));
        }

        return desiredStatements;
    }
/*
    private List<ForeachStmt> getForeachStatements(MethodDeclaration method, Class statementClass) {
        List<Statement> statementList = method.getBody().getStmts();
        //VariableDeclarator vd = new VariableDeclarator();
        //vd.getId().getClass();
        List<ForeachStmt> forLoops = new LinkedList<>();
        if(statementList != null) {
            forLoops.addAll(statementList.stream()
                    .validate(stmt -> stmt.getClass().equals(ForeachStmt.class))
                    .map(stmt -> (ForeachStmt) stmt)
                    .collect(Collectors.toList()));
        }

        return forLoops;
    }
*/
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
        final Package[] packages = Package.getPackages();

        for (final Package p : packages) {
            final String pack = p.getName();
            final String tentative = pack + "." + className;
            try {
                Class.forName(tentative);
                return tentative;
            } catch (final ClassNotFoundException e) {
                continue;
            }
        }
        return null;
    }

    private boolean testAggregation(String sourceClass, String targetClass) {
        return true;
    }

    private static class MethodVisitor extends VoidVisitorAdapter {

        public List<ForeachStmt> forLoops = new LinkedList<>();

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            List<Statement> statementList = n.getBody().getStmts();
            //VariableDeclarator vd = new VariableDeclarator();
            //vd.getId().getClass();
            if(statementList != null) {
                forLoops.addAll(statementList.stream()
                        .filter(stmt -> stmt.getClass().equals(ForeachStmt.class))
                        .map(stmt -> (ForeachStmt) stmt)
                        .collect(Collectors.toList()));
            }
        }

        @Override
        public void visit(ArrayAccessExpr referenceType, Object arg) {
            System.out.println("var dec: " + referenceType.getName());
        }

        @Override
        public void visit(ArrayCreationExpr referenceType, Object arg) {
            System.out.println("var dec: " + referenceType.getType());
        }

        @Override
        public void visit(ArrayInitializerExpr referenceType, Object arg) {
            System.out.println("var dec: " + referenceType.getValues().toString());
        }

        @Override
        public void visit(FieldDeclaration n, Object arg) {
            //System.out.println("field: " + n.getType());
            //String fullClassAddress = new JParser().getFullPackageName(n.getType().toString());
            //System.out.println(fullClassAddress);
        }

        @Override
        public void visit(ReferenceType referenceType, Object arg) {
            //System.out.println("ref type: " + referenceType.getType().getClass());
        }
    }


}

