package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.wine.to.up.crossroad.parser.service.configuration.ParserConfiguration;
import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import com.wine.to.up.crossroad.parser.service.parse.serialization.ResponsePojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.util.Optional;


@Slf4j
@SpringBootTest(classes = RequestsService.class)
@PropertySource("classpath:crossroad-site.properties")
public class RequestsServiceTest {
    private RequestsService requestsService;

    @Before
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ParserConfiguration.class);
        requestsService = (RequestsService) context.getBean("requestsService");
    }


    @Test
    public void testDeserialization() {
        Optional<ResponsePojo> pojo = requestsService.getJson(1, true);
        Assert.assertTrue(pojo.isPresent());
        log.debug(String.valueOf(pojo.get().getCount()));
    }
}