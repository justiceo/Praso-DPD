package DPD.PreProcessor;

import DPD.Enums.Flag;
import DPD.JClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Justice on 3/17/2016.
 */
public class LoopsFilter extends Filter {
    private String loopVariable;
    private Flag flag;

    public LoopsFilter(String variable, Flag signal) {
        this.loopVariable = variable;
        this.flag = signal;
    }

    @Override
    public void run() {
        System.out.println("starting loops filter...");
        long startTime = System.currentTimeMillis();

        int counter = 0;
        synchronized (jClasses) {
            Iterator<JClass> iterator = jClasses.iterator();
            while (counter < matrixSize) {
                JClass jClass = null;
                Flag usesObservable = null;
                try {
                    jClass = iterator.next();
                    filterLoop(jClass);
                } catch (NoSuchElementException | ParseException | IOException e) {
                    System.out.println("loop filter err - counter (" + counter + "); file (" + jClass.filePath + "):\n\t" + e.toString());
                    continue;
                }

                if (usesObservable != null) {
                    if (jClass.flags == null) jClass.flags = new ArrayList<>();
                    jClass.flags.add(usesObservable);
                }
                counter++;
            }
        }
        System.out.println("exiting loops filter (" + (System.currentTimeMillis() - startTime) + "ms)");
    }


    private void filterLoop(JClass jClass) throws IOException, ParseException {
        if (!Files.exists(Paths.get(jClass.filePath))) return;
        CompilationUnit cu = JavaParser.parse(new File(jClass.filePath));
        List<TypeDeclaration> types = cu.getTypes();

        /*
        // I'm leaving this for-loop for historic reasons. in case someone trips on the lambda (which is more than 10x faster, yes!)
        for(TypeDeclaration t: types) {
            for(BodyDeclaration member: t.getMembers()) {
                if(member instanceof MethodDeclaration ) {
                    List<Statement> statements = getStatementsFromMethod((MethodDeclaration) member);
                    if(statements == null) continue;
                    for(Statement statement: statements) {
                        processLoopStatement(statement,jClass);
                    }
                }
            }
        }
        */
        types.stream()
                .map(t -> t.getMembers())
                .forEach(bodyDeclarationList -> bodyDeclarationList.stream()
                        .filter(bodyDeclaration -> bodyDeclaration instanceof MethodDeclaration)
                        .map(bodyD -> (MethodDeclaration) bodyD)
                        .map(method -> getStatementsFromMethod(method))
                        .filter(stmts -> stmts != null)
                        .forEach(statement -> statement.stream()
                                .forEach(sc -> processLoopStatement(sc, jClass))));
    }

    private List<Statement> getStatementsFromMethod(MethodDeclaration method) {
        if (method.getBody() == null || method.getBody().getStmts() == null) return null;
        List<Statement> statementList = method.getBody().getStmts();
        List<Statement> forLoopStatements = new LinkedList<>();
        forLoopStatements.addAll(statementList.stream()
                .filter(stmt -> stmt.getClass().equals(ForeachStmt.class)
                        || stmt.getClass().equals(ForStmt.class))
                .map(stmt -> stmt)
                .collect(Collectors.toList()));

        return forLoopStatements;
    }

    private void processLoopStatement(Statement statement, JClass jClass) {
        if (statement instanceof ForeachStmt)
            processForEach((ForeachStmt) statement, jClass);
        else if (statement instanceof ForStmt)
            processForLoop((ForStmt) statement, jClass);
    }

    private void processForLoop(ForStmt statement, JClass jClass) {
        // todo: un-implemented
    }

    private void processForEach(ForeachStmt statement, JClass jClass) {
        if (statement.getVariable().getType().toString().equals(loopVariable)) {
            jClass.flags.add(flag);
        }
    }
}
