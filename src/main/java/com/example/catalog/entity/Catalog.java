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
    
    // 添加各级目录信息
    private Long firstCatalogId;
    private String firstCatalogName;
    private Long secondCatalogId;
    private String secondCatalogName;
    private Long thirdCatalogId;
    private String thirdCatalogName;
    private Long fourthCatalogId;
    private String fourthCatalogName;
    private Long fifthCatalogId;
    private String fifthCatalogName;
    private Long sixthCatalogId;
    private String sixthCatalogName;
    private Long seventhCatalogId;
    private String seventhCatalogName;
    private Long eighthCatalogId;
    private String eighthCatalogName;
    private Long ninthCatalogId;
    private String ninthCatalogName;
} 