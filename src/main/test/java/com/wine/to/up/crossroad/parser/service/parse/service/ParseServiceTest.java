package com.wine.to.up.parser.service;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author 4ound
 */
public class ParseServiceTest {
    public static final String FILE_PATH
            = "src/main/test/java/com/wine/to/up/crossroad/parser/service/parse/service/example.html";

    @Test
    public void parseTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
        List<Product> productList = ParseService.parseCatalogPage(content);
        Assert.assertEquals(30, productList.size());
    }
}
