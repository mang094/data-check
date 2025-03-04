package com.example.catalog.service;

import com.example.catalog.entity.Catalog;
import com.example.catalog.entity.Sku;


import java.util.List;

public interface CatalogService {
    List<Catalog> getCatalogsByParentId(Long parentId);
    
    // 添加 SKU 相关方法
    List<Sku> getSkusByCatalogId(Long catalogId);
    Long getSkuCountByCatalogId(Long catalogId);
} 