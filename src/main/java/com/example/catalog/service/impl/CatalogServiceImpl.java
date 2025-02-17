package com.example.catalog.service.impl;

import com.example.catalog.entity.Catalog;
import com.example.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.annotation.PostConstruct;

@Service
public class CatalogServiceImpl implements CatalogService {
    
    private static final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://am-bp1c47r9kj6bjg46w167330o.ads.aliyuncs.com:3306/hw_parts_image?serverTimezone=GMT%2B8&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setUsername("h20230329");
        dataSource.setPassword("lCCvGfCdfKg3iwSJ!");
        jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public List<Catalog> getCatalogsByParentId(Long parentId) {
        String sql = "SELECT id, brand_id, catalog_name, catalog_image_id, parent_catalog_id, " +
                    "parent_catalog_name, catalog_level, type, catalog_is_last " +
                    "FROM spider_avspare_case_catalog " +
                    "WHERE parent_catalog_id = ? " +
                    "ORDER BY catalog_level, id";
        
        logger.info("Executing query for parentId: {}", parentId);
        
        List<Catalog> catalogs = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Catalog catalog = new Catalog();
            catalog.setId(rs.getLong("id"));
            catalog.setBrandId(rs.getLong("brand_id"));
            catalog.setCatalogName(rs.getString("catalog_name"));
            catalog.setCatalogImageId(rs.getLong("catalog_image_id"));
            catalog.setParentCatalogId(rs.getLong("parent_catalog_id"));
            catalog.setParentCatalogName(rs.getString("parent_catalog_name"));
            catalog.setCatalogLevel(rs.getInt("catalog_level"));
            catalog.setType(rs.getInt("type"));
            catalog.setCatalogIsLast(rs.getInt("catalog_is_last"));
            return catalog;
        }, parentId);
        
        logger.info("Found {} catalogs for parentId: {}", catalogs.size(), parentId);
        return catalogs;
    }
}