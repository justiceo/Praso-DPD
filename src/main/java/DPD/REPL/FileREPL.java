package DPD.REPL;

import DPD.EasyDSMQuery;
import DPD.Enums.DependencyType;

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
public class FileREPL {

    private List<String> commandLines;
    private ExecEnv exec;
    private final String delimiters = ":=><=++--() ";

    public FileREPL(String fileName, EasyDSMQuery dsmBrowser) {
        try {
            URI uri = this.getClass().getResource(fileName).toURI();
            commandLines = Files.readAllLines(Paths.get(uri), Charset.defaultCharset());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        exec = new ExecEnv(dsmBrowser);
    }

    public FileREPL(List<String> commandLines, EasyDSMQuery dsmBrowser) {
        this.commandLines = commandLines;
        exec = new ExecEnv(dsmBrowser);
    }

    public void run() {
        String line = "";
        for (int i = 0; i < commandLines.size(); i++) {
            try {
                line = commandLines.get(i);
                if(line.startsWith("###"))  // "eof" character lol
                    break;
                execute(line);
            } catch (Exception e) {
                System.out.println("Error executing line" + i + ": " + line);
                e.printStackTrace();
                break;
            }
        }
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
        if(isDependencyCondition(condition)) {
            exec.fillBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
        }
        //exec.fillBucket(bucketId, DependencyType.AGGREGATE, e1, e2);
    }

    private void evalFilterStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(isDependencyCondition(condition))
            exec.filterBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalPromoteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(isDependencyCondition(condition))
            exec.promoteBucket(bucketId, DependencyType.valueOf(condition), e1, e2);
    }

    private void evalDemoteStmt(String line) throws Exception {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String e1 = st.nextToken();
        String condition = st.nextToken();
        String e2 = st.nextToken();
        if(isDependencyCondition(condition))
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

    private boolean isDependencyCondition(String enumStr) {
        for (DependencyType d : DependencyType.values()) {
            if (d.name().equals(enumStr)) { // todo: testto upper
                return true;
            }
        }
        return false;
    }
}
