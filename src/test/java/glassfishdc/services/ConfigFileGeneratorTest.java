package glassfishdc.services;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class ConfigFileGeneratorTest {
    private ConfigFileGenerator configFileGenerator;

    @Before
    public void setUp() {
        InputStream templateFile = getClass().getClassLoader().getResourceAsStream("microdomain.xml");
        InputStream resourcesFile = getClass().getClassLoader().getResourceAsStream("resources.xml");
        configFileGenerator = new ConfigFileGenerator(templateFile, resourcesFile);
    }

    @Test
    public void test() throws IOException {
        System.out.println(configFileGenerator.generate());
    }
}
