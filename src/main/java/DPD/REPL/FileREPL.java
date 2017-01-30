package DPD.REPL;

import DPD.Browser.EasyDSMQuery;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by I853985 on 1/13/2017.
 */
public class FileREPL implements Runnable {

    protected List<String> commandLines;
    private Evaluator evaluator;

    public FileREPL(String fileName, EasyDSMQuery dsmBrowser) {
        try {
            commandLines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
        } catch ( IOException e) {
            e.printStackTrace();
        }
        evaluator = new Evaluator(dsmBrowser);
    }

    public void run() {
        String line = "";
        for (int i = 0; i < commandLines.size(); i++) {
            try {
                line = commandLines.get(i);
                if(line.startsWith("###"))  // "eof" character lol
                    break;
                evaluator.execute(line);
            } catch (Exception e) {
                System.out.println("Error executing line " + (i+1) + ": " + line);
                e.printStackTrace();
                break;
            }
        }
    }
}
