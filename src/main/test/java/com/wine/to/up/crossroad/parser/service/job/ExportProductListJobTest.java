package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.configuration.JobConfiguration;
import com.wine.to.up.crossroad.parser.service.job.ExportProductListJob;
import com.wine.to.up.crossroad.parser.service.parse.client.ParseClient;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
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
//@SpringBootTest(classes = ExportProductListJob.class)
public class ExportProductListJobTest {

    private ExportProductListJob export;
    private ParseClient parseClient;
    private ParseService parseService;
    private String baseUrl = "https://www.perekrestok.ru/catalog/alkogol/vino";
    private String userAgent = "Chrome/4.0.249.0 Safari/532.5";
    private int timeout = 60000;
    private String region = "2";

    @Before
    public void init() {
//        ApplicationContext context = new AnnotationConfigApplicationContext(JobConfiguration.class);
//        export = (ExportProductListJob) context.getBean("exportProductListJob");
        parseClient = new ParseClient(baseUrl, userAgent, timeout, region);
        parseService = new ParseService(parseClient);
        export = new ExportProductListJob(parseService);
    }

    @Test
    public void should_true_becauseTestRun() {
        export.runJob();
    }
}