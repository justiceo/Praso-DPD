package DPD;

import DPD.REPL.FileREPL;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class REPLTest {

    FileREPL repl;
    EasyDSMQuery dsmBrowser;
    List<String> commands;

    @Before
    public void setup() {
        commands = new ArrayList<>();
        commands.add("Entity e1: 'Ada is a girl'");
        commands.add("Bucket b1: 'simple pattern bucket'");
        dsmBrowser = null;
        repl = new FileREPL(commands, dsmBrowser);
    }

    @Test
    public void executeTest() throws Exception {
        repl.execute(commands.get(0));
        repl.execute(commands.get(1));
    }
}
