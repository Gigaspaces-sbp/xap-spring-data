package org.springframework.data.xap.integration.javaconfig;

/**
 * @author Anna_Babich
 */

import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.xap.integration.BaseRepositoryTest;
import org.springframework.data.xap.repository.config.EnableXapRepositories;
import org.springframework.data.xap.utils.TestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Tests for creating Repository using JavaConfig.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class RepositoryJavaConfigTest extends BaseRepositoryTest {

    @Configuration
    @ComponentScan("org.springframework.data.xap")
    @EnableXapRepositories(value = "org.springframework.data.xap.repository", namedQueriesLocation = "classpath:named-queries.properties", gigaspace = "gigaSpace1")
    public static class ContextConfiguration {

        @Autowired
        Environment env;

        @Bean
        public GigaSpace gigaSpace() {
            return TestUtils.initSpace("space");
        }

        @Bean
        public GigaSpace gigaSpace1() {
            return TestUtils.initSpace("space1");
        }
    }

}