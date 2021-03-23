package com.wine.to.up.crossroad.parser.service.db;

import com.wine.to.up.crossroad.parser.service.db.entities.Wine;
import com.wine.to.up.crossroad.parser.service.db.services.WineService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class WineServiceTest {

    private WineService wineService;

    @Before
    public void init() {
        Wine wine = Wine.builder()
                .name("wine")
                .color("red")
                .region(Set.of("1", "2"))
                .build();
        wineService = mock(WineService.class);
        doNothing().when(wineService).save(isA(Wine.class));
        doAnswer(invocation -> Collections.singletonList(wine)).when(wineService).findAll();
    }

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
