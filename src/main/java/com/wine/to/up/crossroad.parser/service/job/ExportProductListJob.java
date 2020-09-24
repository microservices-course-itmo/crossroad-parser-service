package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * </p>
 *
 * @author Maxim Kuznetsov
 * @since 24.09.2020
 */

@Slf4j
public class ExportProductListJob {

    private final ParseService parseService;

    public ExportProductListJob(ParseService parseService) {
        this.parseService = Objects.requireNonNull(parseService, "Can't get parseService");
    }

    /**
     * Джоб, выполняющий свои действия два раза в сутки.
     * В этом классе происходит взаимодействие получаемых с сайта данных и Кафки
     *
     * @return
     */
    @Scheduled(cron = "${job.cron.export.product.list}")
    public void runJob() {
        try {
            int page = 1;
            Optional<List<Product>> result;
            do {
                result = parseService.parseCurrentPage(page);
                //Получаем страницу и отдаём кафке
            } while (result.isPresent());
        } catch (Exception ex) {
            log.error("Can't export product list", ex);
        }
    }
}
