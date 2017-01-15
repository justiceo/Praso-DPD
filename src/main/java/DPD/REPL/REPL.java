package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.DependencyType;
import DPD.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class REPL {

    protected ExecEnv exec;
    protected final String delimiters = ":=><=++--() ";

    public REPL(EasyDSMQuery dsmBrowser) {
        exec = new ExecEnv(dsmBrowser);
    }

    public void execute(String line) throws Exception {
        StatementType st = getStatementType(line);
        switch (st) {
            case EntityDeclaration:
                evalEntityDeclaration(line);
                break;
            case BucketDeclaration:
                evalBucketDeclaration(line);
                break;
            case FillStatement:
                evalFillStmt(line);
                break;
            case FilterStatement:
                evalFilterStmt(line);
                break;
            case PromoteStatement:
                evalPromoteStmt(line);
                break;
            case DemoteStatement:
                evalDemoteStmt(line);
                break;
            case ResolveStatement:
                evalResolveStmt(line);
                break;
            case PrintStatement:
                evalPrintStmt(line);
                break;
            case Noop:
                System.out.println("Noop");
                break;
        }
    }

    private StatementType getStatementType(String line) {
        if( line.contains("Entity") ) return StatementType.EntityDeclaration;
        if( line.contains("Bucket") ) return StatementType.BucketDeclaration;
        if( line.contains("Print") ) return StatementType.PrintStatement;
        if( line.contains("<=") ) return StatementType.FillStatement;
        if( line.contains("=>") ) return StatementType.FilterStatement;
        if( line.contains("++") ) return StatementType.PromoteStatement;
        if( line.contains("--") ) return StatementType.DemoteStatement;
        if( line.contains("Resolve") ) return StatementType.ResolveStatement;
        return StatementType.Noop;
    }

    private enum StatementType {
        EntityDeclaration,
        BucketDeclaration,
        PrintStatement,
        FillStatement,
        FilterStatement,
        PromoteStatement,
        DemoteStatement,
        ResolveStatement,
        Noop,
    }

    private void evalEntityDeclaration(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat the "Entity" keyword
        String entityId = st.nextToken();
        String name = st.nextToken();
        exec.createEntity(entityId, name);
    }

    private void evalBucketDeclaration(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat the "Bucket" keyword
        String bucketId = st.nextToken();
        String name = st.nextToken();
        try {
            exec.createBucket(bucketId, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void evalFillStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(condition.startsWith("[") && condition.endsWith("]")) {
            List<DependencyType> dependencyTypes = Util.getDependencyTypes(condition);
            exec.fillBucket(bucketId, dependencyTypes, e1, e2);
        }
        if(Util.isDependencyCondition(condition)) {
            exec.fillBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
        }
    }

    private void evalFilterStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(Util.isDependencyCondition(condition))
            exec.filterBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalPromoteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(Util.isDependencyCondition(condition))
            exec.promoteBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalDemoteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(Util.isDependencyCondition(condition))
            exec.demoteBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalResolveStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        exec.resolveBucket(bucketId, bucketId);
    }

    private void evalPrintStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat print keyword
        String objectId = st.nextToken();
        exec.printObject(objectId);
    }
}
