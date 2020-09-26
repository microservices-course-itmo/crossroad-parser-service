package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.wine.to.up.crossroad.parser.service.parse.serialization.ResponsePojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@Slf4j
@SpringBootTest(classes = RequestsService.class)
public class RequestsServiceTest {

    @Test
    public void testDeserialization() {
        Optional<ResponsePojo> pojo = RequestsService.getJson("https://www.perekrestok.ru/catalog/alkogol/vino?ajax=true");
        assert pojo.isPresent();
        log.info(pojo.get().toString());
    }
}