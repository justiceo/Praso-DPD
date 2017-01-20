package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.DependencyType;
import DPD.Util;

import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class Evaluator {

    protected Environment exec;
    protected final String delimiters = ":=><=++--() ";

    public Evaluator(EasyDSMQuery dsmBrowser) {
        exec = new Environment(dsmBrowser);
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
            case PrintPocketStatement:
                evalPrintPocketStmt(line);
                break;
            case OverwriteStatement:
                evalOverwriteStmt(line);
                break;
            case Noop:
                break;
        }
    }

    private StatementType getStatementType(String line) {
        if( line.contains("Entity") ) return StatementType.EntityDeclaration;
        if( line.contains("Bucket") ) return StatementType.BucketDeclaration;
        if( line.contains("Pockets") ) return StatementType.PrintPocketStatement;
        if( line.contains("Print") ) return StatementType.PrintStatement;
        if( line.contains("<=") ) return StatementType.FillStatement;
        if( line.contains("=>") ) return StatementType.FilterStatement;
        if( line.contains("=") ) return StatementType.OverwriteStatement;
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
        PrintPocketStatement,
        OverwriteStatement,
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
        String condition = st.nextToken().toUpperCase();
        String e2 = st.nextToken();
        if(condition.startsWith("[") && condition.endsWith("]")) {
            List<DependencyType> dependencyTypes = Util.getDependencyTypes(condition);
            exec.fillBucket(bucketId, dependencyTypes, e1, e2);
        }
        else if(Util.isDependencyCondition(condition)) {
            exec.fillBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
        }
        else {
            exec.fillBucket(bucketId, condition, e1, e2);
        }
    }

    private void evalFilterStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken().toUpperCase();
        String e2 = st.nextToken();
        if(condition.startsWith("[") && condition.endsWith("]")) {
            List<DependencyType> dependencyTypes = Util.getDependencyTypes(condition);
            exec.filterBucket(bucketId, dependencyTypes, e1, e2);
        }
        else if(Util.isDependencyCondition(condition))
            exec.filterBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
        else {
            exec.filterBucket(bucketId, condition, e1, e2);
        }
    }

    private void evalOverwriteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken().toUpperCase();
        String e2 = st.nextToken();
        if(condition.startsWith("[") && condition.endsWith("]")) {
            List<DependencyType> dependencyTypes = Util.getDependencyTypes(condition);
            exec.overwriteBucket(bucketId, dependencyTypes, e1, e2);
        }
        else if(Util.isDependencyCondition(condition)) {
            exec.overwriteBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
        }
        else {
            exec.overwriteBucket(bucketId, condition, e1, e2);
        }
    }

    private void evalPromoteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken().toUpperCase();
        String e2 = st.nextToken();
        if(Util.isDependencyCondition(condition))
            exec.promoteBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalDemoteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken().toUpperCase();
        String e2 = st.nextToken();
        if(Util.isDependencyCondition(condition))
            exec.demoteBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalResolveStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat Resolve keyword
        String bucketId = st.nextToken();
        exec.resolveBucket(bucketId);
    }

    private void evalPrintStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat print keyword
        String objectId = st.nextToken();
        exec.printObject(objectId);
    }

    private void evalPrintPocketStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat print keyword
        String objectId = st.nextToken();
        exec.printByPocket(objectId);
    }
}
