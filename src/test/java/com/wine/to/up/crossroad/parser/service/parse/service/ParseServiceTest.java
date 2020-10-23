package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.configuration.ParserConfiguration;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author 4ound
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ParseServiceTest {
    public static final String CATALOG_FILE_PATH = "src/test/resources/catalog.html";
    public static final String WINE_FILE_PATH = "src/test/resources/wine.html";

    @Autowired
    private ParseService parseService;

    @Ignore
    @Test
    public void parseCatalogTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(CATALOG_FILE_PATH)));
        List<String> urls = parseService.parseUrlsCatalogPage(content);
        Assert.assertEquals(30, urls.size());
    }

    @Ignore
    @Test
    public void parseWineTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(WINE_FILE_PATH)));
        Optional<Product> productO = parseService.parseProductPage(content);
        Assert.assertTrue(productO.isPresent());
    }
}
