package com.wine.to.up.crossroad.parser.service.db.services;

import com.wine.to.up.crossroad.parser.service.db.dto.Product;
import com.wine.to.up.crossroad.parser.service.db.entities.Wine;
import com.wine.to.up.crossroad.parser.service.db.repositories.WineRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WineService {
    private final WineRepository wineRepository;

    public WineService(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    public List<Wine> findAll() {
        return (List<Wine>) wineRepository.findAll();
    }

    public List<Product> findAllProducts() {
        return findAll().stream().map(Wine::toProduct).collect(Collectors.toList());
    }

    public void save(Wine wine) {
        wineRepository.save(wine);
    }

    public void saveAll(Collection<Product> products) {
        wineRepository.saveAll(products.stream().map(Wine::fromProduct).collect(Collectors.toList()));
    }

    public void deleteAll() {
        wineRepository.deleteAll();
    }
}
