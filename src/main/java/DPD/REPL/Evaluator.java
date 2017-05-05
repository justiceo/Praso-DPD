package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.BucketResult;
import DPD.Model.DependencyType;
import DPD.Util;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Justice on 1/10/2017.
 */
public class Evaluator {

    protected Environment exec;
    protected final String delimiters = ":=><=++--() ";
    private Logger log = Logger.getGlobal();
    public Evaluator(EasyDSMQuery dsmBrowser) {
        exec = new Environment(dsmBrowser);
        log.setLevel(Level.FINE);
    }

    public void execute(String line) throws Exception {
        //System.out.println("Executing: " + line);
        StatementType st = getStatementType(line);
        switch (st) {
            case EntityDeclaration:
                evalEntityDeclaration(line);
                break;
            case BucketDeclaration:
                evalBucketDeclaration(line);
                break;
            case FillStatement:
                evalBucketStatement(line, StatementType.FillStatement);
                break;
            case FilterStatement:
                evalBucketStatement(line, StatementType.FilterStatement);
                break;
            case PromoteStatement:
                evalBucketStatement(line, StatementType.PromoteStatement);
                break;
            case DemoteStatement:
                evalBucketStatement(line, StatementType.DemoteStatement);
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
                evalBucketStatement(line, StatementType.OverwriteStatement);
                break;
            case SubDsmStatement:
                evalSubDsmStatement(line);
                break;
            case FindInterfaceStatement:
                evalFindInterfaces(line);
                break;
            case DsmStatement:
                evalDsmStatement(line);
                break;
            case ClassId:
                evalClassId(line);
                break;
            case Export:
                evalExport(line);
                break;
            case PatternStatement:
                evalPattern(line);
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
        if( line.startsWith("Resolve") ) return StatementType.ResolveStatement;
        if( line.startsWith("SubDsm") ) return StatementType.SubDsmStatement;
        if( line.startsWith("Dsm") ) return StatementType.DsmStatement;
        if( line.startsWith("Find") ) return StatementType.FindInterfaceStatement;
        if (line.startsWith("ClassId")) return StatementType.ClassId;
        if (line.startsWith("Export")) return StatementType.Export;
        if (line.startsWith("Pattern")) return StatementType.PatternStatement;
        return StatementType.Noop;
    }

    public enum StatementType {
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
        SubDsmStatement,
        DsmStatement,
        FindInterfaceStatement,
        ClassId,
        Export,
        PatternStatement
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

    private void evalBucketStatement(String line, StatementType type) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String pivot = "";
        String e1 = st.nextToken();

        String condition = st.nextToken().toUpperCase();
        String e2 = st.nextToken();

        // set pivot
        if(e1.startsWith("*")) {
            e1 = e1.substring(1);
            pivot = e1;
        }
        else if(e2.startsWith("*")) {
            e2 = e2.substring(1);
            pivot = e2;
        }

        if(Util.isArray(condition)) {
            List<DependencyType> dependencyTypes = Util.getDependencyTypes(condition);
            BucketResult r = exec.evalDependency(dependencyTypes, e1, e2);
            //System.out.println("Bucket result: " + r);
            exec.evalBucketStatement(bucketId, type, r, pivot);
        }
        else if(Util.isDependencyCondition(condition)) {
            BucketResult r = exec.evalDependency(DependencyType.valueOf(condition), e1, e2);
            //System.out.println("Bucket result: " + r);
            exec.evalBucketStatement(bucketId, type, r, pivot);
        }
        else {
            exec.evalBucketStatement(bucketId, type, exec.evalFunction(bucketId, condition, e1, e2), pivot);
        }
    }

    private void evalResolveStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat Resolve keyword
        String bucketId = st.nextToken();
        exec.resolveBucket(bucketId);
    }

    private void evalSubDsmStatement(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat SubDsm keyword
        String[] args = st.rest();
        exec.getSubDsm(args);
    }

    private void evalDsmStatement(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat Dsm keyword
        exec.printDsm();
    }

    private void evalFindInterfaces(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat Find keyword
        String query = st.nextToken();
        exec.findTypes(query);
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

    private void evalClassId(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat classId keyword
        String query = st.nextToken();
        exec.getClassId(query);
    }

    private void evalExport(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat export keyword
        String[] query = st.rest();
        exec.export(query);
    }

    private void evalPattern(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat pattern keyword
        String[] args = st.rest();
        File f = new File("files\\rules\\" + args[0] + ".dpd");
        EasyDSMQuery dsmQuery = EasyDSMQuery.getInstace();
        System.out.println("Currently executing: " + f.getName());
        FileREPL reader = new FileREPL(f.getAbsolutePath(), dsmQuery);
        reader.run();
    }
}
