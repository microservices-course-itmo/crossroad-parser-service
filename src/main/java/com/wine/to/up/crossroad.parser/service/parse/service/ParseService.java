package com.wine.to.up.crossroad.parser.service.parse.service;

import com.wine.to.up.crossroad.parser.service.db.constants.Currency;
import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.parse.client.ParseClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
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
public class ParseService {

    private ParseClient client;

    public ParseService(ParseClient client) {
        this.client = Objects.requireNonNull(client, "Can't get ParseClient");
    }


    /**
     * Парсинг текущей страницы. Необходимо сформировать path для добавления в url.
     * Номер страницы передаётся в параметрах запроса. Пока что можно формировать в ручную
     * Удобной библиотеки/функции для этого пока не нашёл
     *
     * @return список продуктов со страницы
     */
    public Optional<List<Product>> parseCurrentPage(int page) {
        try {
            List<Product> productList = new ArrayList<>();
            String path = String.format("/catalog/alkogol/vino?attr[rate][]=0&page=%d&sort=rate_desc", page);
            Document document = client.getDocumentByPath(path);

            Element productsList = document.getElementById("catalogItems");
            for (Element el : productsList.children()) {
                Elements links = el.getElementsByClass("xf-product__main-link");
                if (links.size() > 0) {
                    String itemPath = links.get(0).attr("href");

                    Optional<Product> product = parseProductPage(itemPath);
                    if (product.isPresent()) {
                        productList.add((product.get()));
                    }
                }
            }

            return Optional.of(productList);
        } catch (Exception ex) {
            log.error("Can't parse page = {}", page, ex);
            return Optional.empty();
        }
    }

    private Optional<Product> parseProductPage(String path) {
        Document document = client.getDocumentByPath(path);
        Elements elements = document.getElementsByClass("js-product _substrate-card");

        if (elements.size() > 0) {
            return createProduct(elements.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Преобразуем элемент, который получили со страницы в объект Product
     *
     * @return список продуктов со страницы
     */
    private Optional<Product> createProduct(Element element) {
        log.debug(element.attr("data-gtm-product-name"));

        try {
//            Product product = Product.builder()
//                    .name()
//                    .currentCost()
//                    .previousCost()
//                    .typeValue()
//                    .currency(Currency.RUB)
//                    .build();
//            return Optional.of(product);
            return Optional.empty(); //Временно, чтобы запускалось
        } catch (Exception ex) {
            log.error("Can't create product ", ex);
            return Optional.empty();
        }
    }
}
