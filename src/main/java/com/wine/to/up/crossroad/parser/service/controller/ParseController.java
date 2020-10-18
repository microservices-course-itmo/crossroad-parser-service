package com.wine.to.up.crossroad.parser.service.controller;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.job.ExportProductListJob;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 *     Контроллер для парсинга сайта перекрёстка по запросу пользователя
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 18.10.2020
 */
@RestController
@RequestMapping("/parse")
@Validated
@Slf4j
public class ParseController {

    private final ExportProductListJob job;
    private final ProductService productService;

    public ParseController(ExportProductListJob job, ProductService productService) {
        this.job = Objects.requireNonNull(job, "Can't get exportProductListJob");
        this.productService = Objects.requireNonNull(productService, "Can't get productService");
    }

    @GetMapping("/site")
    @ApiOperation(value = "Парсинг сайта по запросу пользователя",
            notes = "Возвращает результат парсинга сайта в формате JSON")
    public List<Product> parseSite() {
        Optional<List<Product>> wines = productService.getParsedProductList();
        return wines.orElseGet(ArrayList::new);
    }

    @GetMapping("/start/job")
    @ApiOperation(value = "Запуск джобы парсинга сайта по запросу пользователя",
            notes = "Запускает джоб с полным парсингом всего каталога вин и отправкой результата обработки в kafka")
    public void parse() {
        job.runJob();
    }
}
