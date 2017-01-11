package DPD.REPL;

import DPD.DependencyBrowser.DSMBrowser;

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

    public FileREPL(String fileName, DSMBrowser dsmBrowser) {
        try {
            URI uri = this.getClass().getResource(fileName).toURI();
            commandLines = Files.readAllLines(Paths.get(uri), Charset.defaultCharset());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        exec = new ExecEnv(dsmBrowser);
    }

    public FileREPL(List<String> commandLines, DSMBrowser dsmBrowser) {
        this.commandLines = commandLines;
        exec = new ExecEnv(dsmBrowser);
    }

    public void run() {
        for(String line: commandLines) {
            execute(line);
        }
    }

    public void execute(String line) {
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

    private void evalEntityDeclaration(String line) {
        //StringTokenizer st = new StringTokenizer(line, delimiters);
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
        exec.createBucket(bucketId, name);
    }
    private void evalFillStmt(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String command = st.nextToken();
        exec.fillBucket(bucketId, command);
    }
    private void evalFilterStmt(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String command = st.nextToken();
        exec.filterBucket(bucketId, command);
    }
    private void evalPromoteStmt(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String command = st.nextToken();
        exec.promoteBucket(bucketId, command);
    }
    private void evalDemoteStmt(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String command = st.nextToken();
        exec.demoteBucket(bucketId, command);
    }
    private void evalResolveStmt(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        String bucketId = st.nextToken();
        String command = st.nextToken();
        exec.resolveBucket(bucketId, command);
    }
    private void evalPrintStmt(String line) {
        Tokenizer st = new Tokenizer(line, delimiters);
        st.nextToken(); // eat print keyword
        String objectId = st.nextToken();
        exec.printObject(objectId);
    }
}
