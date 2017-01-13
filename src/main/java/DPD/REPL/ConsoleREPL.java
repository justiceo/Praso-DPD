package DPD.REPL;

import DPD.Browser.EasyDSMQuery;

import java.util.Scanner;

/**
 * Created by I853985 on 1/13/2017.
 */
public class ConsoleREPL extends REPL {

    private final String prompt = "DPD$ ";
    private final String exit = "exit";

    public ConsoleREPL(EasyDSMQuery dsmBrowser) {
        super(dsmBrowser);
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        String line;
        System.out.print(prompt);
        while(!(line = in.nextLine()).equals(exit)) {
            try {
                System.out.print(prompt);
                execute(line);
            } catch (Exception e) {
                System.out.println("Error executing: " + line);
                e.printStackTrace();
            }
        }
    }
}
