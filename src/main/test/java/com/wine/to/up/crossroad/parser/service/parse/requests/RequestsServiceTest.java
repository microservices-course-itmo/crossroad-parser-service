package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.parse.requests.RequestsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;


@Slf4j
@SpringBootTest(classes = RequestsService.class)
public class RequestsServiceTest {

    @Test
    public void testDeserialization() {
        log.info(RequestsService.getJson("https://www.perekrestok.ru/catalog/alkogol/vino?ajax=true").get().toString());
    }
}