package com.wine.to.up.crossroad.parser.service.db;

import com.wine.to.up.crossroad.parser.service.db.entities.Wine;
import com.wine.to.up.crossroad.parser.service.db.services.WineService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@Ignore("Integration test")
@SpringBootTest
public class WineServiceTest {
    @Autowired
    WineService wineService;

    @Test
    public void findAll() {
        Wine wine = Wine.builder()
                .name("wine")
                .color("red")
                .region(Set.of("1", "2"))
                .build();
        wineService.save(wine);
        List<Wine> wines = wineService.findAll();
        Assertions.assertEquals(1, wines.size());
        Assertions.assertEquals(wine, wines.get(0));
        // проверка hibernate lazy init
        Assertions.assertEquals(wine.getName(), wines.get(0).getName());
        Assertions.assertEquals(wine.getRegion(), wines.get(0).getRegion());
    }
}
