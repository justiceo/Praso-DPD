package DPD.PatternParser;

import DPD.DSMMapper.PatternComponent;
import DPD.DSMMapper.SimplePattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by Justice on 1/27/2016.
 */
public final class FileIO {

    public static Config loadConfig(File configFile) {
        if (!configFile.exists()) System.out.print("config file does not exist");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Config) jaxbUnmarshaller.unmarshal(configFile);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveConfig(Config config) {
        try {
            File file = new File("config-test.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(config, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static PatternComponent loadPattern(File patternFile) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SimplePattern.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (SimplePattern) jaxbUnmarshaller.unmarshal(patternFile);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void savePattern(PatternComponent pattern) {
        try {
            File file = new File(pattern.getName() + " - Hydrated.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(PatternComponent.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(pattern, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
