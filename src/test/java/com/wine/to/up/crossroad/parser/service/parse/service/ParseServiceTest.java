package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.configuration.ParserConfiguration;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author 4ound
 */
@SpringBootTest(classes = ParseService.class)
@PropertySource("classpath:crossroad-site.properties")
public class ParseServiceTest {
    public static final String CATALOG_FILE_PATH = "src/test/resources/catalog.html";
    public static final String WINE_FILE_PATH = "src/test/resources/wine.html";

    private ParseService parseService;

    @Before
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ParserConfiguration.class);
        parseService = (ParseService) context.getBean("parseService");
    }

    @Test
    public void parseCatalogTest() throws IOException {
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
