package com.example.catalog.service;

import com.example.catalog.entity.Catalog;
import java.util.List;

public interface CatalogService {
    List<Catalog> getCatalogsByParentId(Long parentId);
} 