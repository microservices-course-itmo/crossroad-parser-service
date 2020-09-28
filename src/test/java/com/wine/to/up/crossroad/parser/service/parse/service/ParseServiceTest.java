package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author 4ound
 */
public class ParseServiceTest {
    public static final String CATALOG_FILE_PATH
            = "src/main/test/resources/catalog.html";

    public static final String WINE_FILE_PATH
            = "src/main/test/resources/wine.html";

    @Test
    public void parseCatalogTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(CATALOG_FILE_PATH)));
        List<String> urls = ParseService.parseUrlsCatalogPage(content);
        Assert.assertEquals(30, urls.size());
    }

    @Test
    public void parseWineTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(WINE_FILE_PATH)));
        Optional<Product> productO = ParseService.parseProductPage(content);
        Assert.assertTrue(productO.isPresent());
    }
}
