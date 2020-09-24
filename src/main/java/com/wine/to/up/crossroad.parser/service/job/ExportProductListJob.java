package com.wine.to.up.crossroad.parser.service.job;

import com.wine.to.up.crossroad.parser.service.parse.service.ParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

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

    @Scheduled(cron = "${job.cron.export.product.list}")
    public void runJob() {
        try {
            parseService.parseDocument();
        } catch (Exception ex) {
            log.error("Can't export product list", ex);
        }
    }
}
