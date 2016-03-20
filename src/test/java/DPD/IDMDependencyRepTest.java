package DPD;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by Justice on 3/20/2016.
 */
public class IDMDependencyRepTest {


    private final String testDsmFile = "files\\dsm\\Maze2.dsm";

    @Test
    public void TestSaveIdm() throws IOException {

        DSMDependencyRep dsmDependencyRep = new DSMDependencyRep(testDsmFile);
        IDMDependencyRep idmRep = new IDMDependencyRep();
        String targetFile = testDsmFile + ".idm";
        //assertTrue(!Files.exists(Paths.get(targetFile)));
        idmRep.saveAs(targetFile, dsmDependencyRep.getDependencyLine(), dsmDependencyRep.getFilePaths(), dsmDependencyRep.getMatrixLines());
        assertTrue(Files.exists(Paths.get(targetFile)));

        // todo: re-load and verify contents with those of dsm (works by inspection lol)
        // then discard
        Files.delete(Paths.get(targetFile));
    }

    @Test
    public void TestSaveCDSM() throws IOException {

        DSMDependencyRep dsmDependencyRep = new DSMDependencyRep(testDsmFile);
        dsmDependencyRep.saveWithRelativePaths();
    }
}
