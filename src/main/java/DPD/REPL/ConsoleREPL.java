package DPD.REPL;

import DPD.Browser.EasyDSMQuery;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Created by I853985 on 1/13/2017.
 */
public class ConsoleREPL {

    private final String prompt = "DPD$ ";
    private final String exit = "exit";
    private Evaluator evaluator;

    public ConsoleREPL(EasyDSMQuery dsmBrowser) {
        evaluator = new Evaluator(dsmBrowser);
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        String line = "";
        while( !line.equals(exit)) {
            try {
                System.out.print(prompt);
                line = in.nextLine();
                evaluator.execute(line);
            } catch (Exception e) {
                System.out.println("Error executing: " + line);
                e.printStackTrace();
            }
        }
    }

    public void redirectPrint() {
        // see http://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
        // on redirecting print stream for when things get bad
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }
}
