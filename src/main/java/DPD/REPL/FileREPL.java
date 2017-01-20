package DPD.REPL;

import DPD.Browser.EasyDSMQuery;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by I853985 on 1/13/2017.
 */
public class FileREPL extends Evaluator implements Runnable {

    protected List<String> commandLines;

    public FileREPL(String fileName, EasyDSMQuery dsmBrowser) {
        super(dsmBrowser);
        try {
            URI uri = this.getClass().getResource(fileName).toURI();
            commandLines = Files.readAllLines(Paths.get(uri), Charset.defaultCharset());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public FileREPL(List<String> commandLines, EasyDSMQuery dsmBrowser) {
        super(dsmBrowser);
        this.commandLines = commandLines;
        exec = new Environment(dsmBrowser);
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
}
