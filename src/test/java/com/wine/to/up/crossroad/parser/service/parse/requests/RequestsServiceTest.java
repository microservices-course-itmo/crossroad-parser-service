package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@Slf4j
@RunWith(SpringRunner.class)
@Ignore("Integration test")
@SpringBootTest
public class RequestsServiceTest {
    @Autowired
    private RequestsService requestsService;

    @Test
    public void testDeserialization() {
        Optional<CatalogResponsePojo> pojo = requestsService.getJson(1);
        Assert.assertTrue(pojo.map(CatalogResponsePojo::getCount).orElse(0) > 0);
    }
}