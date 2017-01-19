package DPD.REPL;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by Justice on 1/19/2017.
 */
public class MethodNameVisitor extends VoidVisitorAdapter {
    private boolean hasName;
    private String name;

    public boolean hasMethodName(CompilationUnit cu, String name) {
        this.name = name;
        visit(cu, null);
        return hasName;
    }


    @Override
    public void visit(MethodDeclaration md, Object args) {
        if(md.getName().contains(name))
            hasName = true;
    }
}
