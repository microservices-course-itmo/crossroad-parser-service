package com.wine.to.up.crossroad.parser.service.parse.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.crossroad.parser.service.parse.serialization.CatalogResponsePojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


@Slf4j
@SpringBootTest
public class RequestsServiceTest {
    private RequestsService requestsService;

    public static final String PRODUCTS_JSON_FILE_PATH = "src/test/resources/products.json";

    @Before
    public void init() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(PRODUCTS_JSON_FILE_PATH)));
            requestsService = mock(RequestsService.class);
            doAnswer(invocation -> convertToJson(content)).when(requestsService).getJson(isA(Integer.class));
        } catch (IOException ex) {
            log.error("Can't read test file");
        }
    }

    private Optional<CatalogResponsePojo> convertToJson(String content) {
        if (content != null) {
            try {
                CatalogResponsePojo result = new ObjectMapper().readValue(content, CatalogResponsePojo.class);
                return Optional.of(result);
            } catch (JsonProcessingException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Test
    public void testDeserialization() {
        Optional<CatalogResponsePojo> pojo = requestsService.getJson(1);
        Assert.assertTrue(pojo.map(CatalogResponsePojo::getCount).orElse(0) > 0);
    }
}