package com.example.catalog.controller;

import com.example.catalog.entity.Catalog;
import com.example.catalog.entity.Sku;
import com.example.catalog.service.CatalogService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@CrossOrigin
public class CatalogController {
    
    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    
    @Autowired
    private CatalogService catalogService;
    
    @GetMapping("/level")
    public List<Catalog> getCatalogsByParentId(@RequestParam(required = false) Long parentId) {
        logger.info("Received request for parentId: {}", parentId);
        List<Catalog> result = catalogService.getCatalogsByParentId(parentId == null ? 0L : parentId);
        logger.info("Returning {} catalogs", result.size());
        return result;
    }

    @GetMapping("/skus")
    public List<Sku> getSkusByCatalogId(@RequestParam Long catalogId) {
        return catalogService.getSkusByCatalogId(catalogId);
    }

    @GetMapping("/skus/count")
    public Long getSkuCountByCatalogId(@RequestParam Long catalogId) {
        return catalogService.getSkuCountByCatalogId(catalogId);
    }
} 