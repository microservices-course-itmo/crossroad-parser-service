package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.configuration.JobConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */
@SpringBootTest(classes = ExportProductListJob.class)
public class ExportProductListJobTest {

    private ExportProductListJob export;

    @Before
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(JobConfiguration.class);
        export = (ExportProductListJob) context.getBean("exportProductListJob");
    }

    @Test
    public void should_true_becauseTestRun() {
        export.runJob();
    }
}