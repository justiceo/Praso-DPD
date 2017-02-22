package DPD.REPL;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by Justice on 1/19/2017.
 */
public class SourceVisitor {
    private boolean hasName;
    private String[] methodNames;

    // observer loop
    private boolean hasObserverLoop;
    private String targetClass;

    public boolean hasMethodName(CompilationUnit cu, String[] mnames) {
        this.methodNames = mnames;
        hasName = false;
        MethodName methodName = new MethodName();
        methodName.visit(cu, null);
        return hasName;
    }

    public boolean hasObserverLoop(CompilationUnit cu, String targetClass) {
        hasObserverLoop = false;
        this.targetClass = targetClass;
        ObserverLoopVisitor obs = new ObserverLoopVisitor();
        obs.visit(cu, null);
        return hasObserverLoop;
    }


    class ObserverLoopVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ForeachStmt stmt, Object args) {
            Type varType = stmt.getVariable().getType();
            if(varType.toString().equals(targetClass)) {
                hasObserverLoop = true;
                return;
            }
        }
    }

    class MethodName extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration md, Object args) {
            for(String method: methodNames)
                if(md.getName().contains(method))
                    hasName = true;
        }
    }

    private String[] typeNames;
    private boolean hasTypeName;
    public boolean hasTypeName(CompilationUnit cu, String[] types) {
        this.typeNames = types;
        TypeName tn = new TypeName();
        tn.visit(cu, null);
        return hasTypeName;
    }
    class TypeName extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration cd, Object args) {
            for(String type: typeNames)
                if(cd.getName().contains(type))
                    hasTypeName = true;

        }
    }
}
