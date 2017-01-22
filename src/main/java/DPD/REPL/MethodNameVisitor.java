package DPD.REPL;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by Justice on 1/19/2017.
 */
public class MethodNameVisitor extends VoidVisitorAdapter {
    private boolean hasName;
    private String[] methodNames;

    public boolean hasMethodName(CompilationUnit cu, String[] mnames) {
        this.methodNames = mnames;
        visit(cu, null);
        return hasName;
    }


    @Override
    public void visit(MethodDeclaration md, Object args) {
        for(String method: methodNames)
            if(md.getName().contains(method))
                hasName = true;
    }
}
