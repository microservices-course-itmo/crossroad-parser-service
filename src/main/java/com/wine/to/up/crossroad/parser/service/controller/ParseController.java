package com.wine.to.up.crossroad.parser.service.controller;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.crossroad.parser.service.components.CrossroadParserServiceMetricsCollector;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.db.services.WineService;
import com.wine.to.up.crossroad.parser.service.job.ExportProductListJob;
import com.wine.to.up.crossroad.parser.service.parse.service.ProductService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.wine.to.up.crossroad.parser.service.logging.CrossroadParserServiceNotableEvents.E_RESPONSE_WRITING_ERROR;

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
    private final CrossroadParserServiceMetricsCollector metricsCollector;
    private final WineService wineService;

    @InjectEventLogger
    private EventLogger eventLogger;

    public ParseController(ExportProductListJob job,
                           ProductService productService,
                           CrossroadParserServiceMetricsCollector metricsCollector,
                           WineService wineService)
    {
        this.job = Objects.requireNonNull(job, "Can't get exportProductListJob");
        this.productService = Objects.requireNonNull(productService, "Can't get productService");
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
        this.wineService = Objects.requireNonNull(wineService, "Can't get metricsCollector");

    }

    @GetMapping("/site")
    @ApiOperation(value = "Парсинг сайта по запросу пользователя",
            notes = "Возвращает результат парсинга сайта в формате JSON")
    public List<Product> parseSite() {
        List<Product> wines = wineService.findAllProducts();
        if (wines.isEmpty()) {
            log.info("Db was empty, so perform parsing");
            wines = productService.performParsing().orElse(Collections.emptyList());
        }

        return wines;
    }

    @GetMapping(value = "/site/csv", produces = "text/plain;charset=UTF-8")
    @ApiOperation(value = "Парсинг сайта по запросу пользователя",
            notes = "Возвращает результат парсинга сайта в формате CSV")
    public void parseSiteCsv(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        List<Product> wines = wineService.findAllProducts();
        if (wines.isEmpty()) {
            log.info("Db was empty, so perform parsing");
            wines = productService.performParsing().orElse(Collections.emptyList());
        }
        try {
            productService.writeParsedProductListCsv(response.getWriter(), wines);
        } catch (Exception ex) {
            eventLogger.error(E_RESPONSE_WRITING_ERROR, ex);
        }
    }

    @GetMapping("/start/job")
    @ApiOperation(value = "Запуск джобы парсинга сайта по запросу пользователя",
            notes = "Запускает джоб с полным парсингом всего каталога вин и отправкой результата обработки в kafka")
    public void parse() {
        job.runJob();
    }
}
