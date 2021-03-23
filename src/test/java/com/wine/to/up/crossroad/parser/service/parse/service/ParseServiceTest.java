package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author 4ound
 */
@Slf4j
@SpringBootTest
public class ParseServiceTest {
    public static final String CATALOG_FILE_PATH = "src/test/resources/catalog.html";
    public static final String WINE_FILE_PATH = "src/test/resources/wine.html";

    @Value("${site.base.url}")
    private String baseUrl;
    @InjectMocks
    private ParseService parseService = new ParseService(baseUrl);
    @Mock
    private EventLogger eventLogger;

    @Before
    public void init() {
        initMocks(this);
        doNothing().when(eventLogger).info(any());
    }

    private List<String> getTestUrls() {
        List<String> urls = new ArrayList<>();
        String testUrl = "SOME TEST URL";
        for (int i = 0; i < 30; i++) {
            urls.add(testUrl);
        }
        return urls;
    }

    @Test
    public void parseCatalogTest() throws IOException {
        ParseService parseService = mock(ParseService.class);
        doAnswer(invocation -> getTestUrls()).when(parseService).parseUrlsCatalogPage(isA(String.class));
        String content = new String(Files.readAllBytes(Paths.get(CATALOG_FILE_PATH)));
        List<String> urls = parseService.parseUrlsCatalogPage(content);
        Assert.assertEquals(30, urls.size());
    }

    @Test
    public void parseWineTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(WINE_FILE_PATH)));
        Optional<Product> productO = parseService.parseProductPage(content);
        Assert.assertTrue(productO.isPresent());
    }
}
