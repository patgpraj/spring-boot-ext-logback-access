package net.rakugakibox.springbootext.logback.access.tomcat;

import net.rakugakibox.springbootext.logback.access.AbstractGeneralConfigurationAutoDetectionTest;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The test that auto-detect general configuration on Tomcat.
 */
@SpringApplicationConfiguration
public class TomcatGeneralConfigurationAutoDetectionTest extends AbstractGeneralConfigurationAutoDetectionTest {

    /**
     * The context configuration.
     */
    @Configuration
    @Import(EmbeddedServletContainerAutoConfiguration.EmbeddedTomcat.class)
    public static class TestContextConfiguration {
    }

}
