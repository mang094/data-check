package com.example.catalog.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Catalog {
    private Long id;
    private Long brandId;
    private String catalogName;
    private Long catalogImageId;
    private Long parentCatalogId;
    private String parentCatalogName;
    private Integer catalogLevel;
    private Integer type;
    private Integer catalogIsLast;
    private List<Catalog> children;
} 