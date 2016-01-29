import DPD.DSMBrowser.ClassType;
import DPD.DSMBrowser.DSMBrowser;
import DPD.DSMBrowser.IBrowser;
import DPD.DSMMapper.DSMMapper;
import DPD.DSMMapper.EntityMapper;
import DPD.DSMMapper.IPattern;
import DPD.DSMMapper.PatternEntity;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class EntityMapperTests {

    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern observerPattern;

    @Before
    public void setup() {
        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File("config.xml"));
        observerPattern = patternsParser.parse(patternsParser.getRunnableConfigs().get(0));

        File dsmFile = new File("observer-sample.dsm");
        browser = new DSMBrowser();
        browser.init(dsmFile);

        mapper = new DSMMapper();
        mapper.init(browser);
    }

    @After
    public void tearDown() {
        browser = null;
        mapper = null;
        observerPattern = null;
    }

    @Test
    public void getPatternEntitiesTest() {
        //todo: re-write this test to compare pattern with observerPattern, observerPattern shouldn't be modified
        mapper.mapPatternEntities(observerPattern);
        Assert.assertEquals(3, observerPattern.getEntities().size());
        PatternEntity observerEntity = observerPattern.getEntities().get(0);
        Assert.assertEquals(1, observerEntity.compliantClasses.size());
        PatternEntity concreteObserverEntity = observerPattern.getEntities().get(1);
        Assert.assertEquals(4, concreteObserverEntity.compliantClasses.size());
        Assert.assertEquals(ClassType.Interface, observerEntity.type);
        Assert.assertEquals("e1", observerEntity.id);
        Assert.assertEquals("Observer", observerEntity.name);
        List<String> expectedCompliantClasses = new ArrayList<>();
        expectedCompliantClasses.add("D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java");
        Assert.assertEquals(expectedCompliantClasses, observerEntity.compliantClasses);

        //todo: add exhaustive tests
    }

    @Test
    public void resolvePatternEntitiesTest() {

        mapper.mapPatternEntities(observerPattern);
        Assert.assertEquals(3, observerPattern.getEntities().size());
        PatternEntity observerEntity = observerPattern.getEntities().get(0);
        Assert.assertEquals(ClassType.Interface, observerEntity.type);
        Assert.assertEquals("e1", observerEntity.id);
        Assert.assertEquals("Observer", observerEntity.name);
        List<String> expectedCompliantClasses = new ArrayList<>();
        expectedCompliantClasses.add("D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java");
        Assert.assertEquals(expectedCompliantClasses, observerEntity.compliantClasses);

        List<IPattern> resolvedPatterns = mapper.resolvePatternEntities(observerPattern);

    }
}
