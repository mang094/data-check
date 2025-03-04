package com.example.catalog.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Sku {
    private Long id;
    private Long brandId;
    private Long catalogId;
    private Long catalogImageId;
    private String skuNo;
    private String skuName;
    private String skuDescription;
    private String quantity;
    private String flight;
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
    private Date createdTime;
    private Date updatedTime;
    private String errorJson;
    private String originUrl;
}