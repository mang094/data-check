package com.example.catalog.service.impl;

import com.example.catalog.entity.Catalog;
import com.example.catalog.entity.Sku;
import com.example.catalog.service.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.HashMap;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@Service
@Slf4j
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
                    "parent_catalog_name, catalog_level, type, catalog_is_last, " +
                    "first_catalog_id, first_catalog_name, " +
                    "second_catalog_id, second_catalog_name, " +
                    "third_catalog_id, third_catalog_name, " +
                    "fourth_catalog_id, fourth_catalog_name, " +
                    "fifth_catalog_id, fifth_catalog_name, " +
                    "sixth_catalog_id, sixth_catalog_name, " +
                    "seventh_catalog_id, seventh_catalog_name, " +
                    "eighth_catalog_id, eighth_catalog_name, " +
                    "ninth_catalog_id, ninth_catalog_name " +
                    "FROM record_avspare_case_catalog " +
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
            
            // 设置各级目录信息
            catalog.setFirstCatalogId(rs.getLong("first_catalog_id"));
            catalog.setFirstCatalogName(rs.getString("first_catalog_name"));
            catalog.setSecondCatalogId(rs.getLong("second_catalog_id"));
            catalog.setSecondCatalogName(rs.getString("second_catalog_name"));
            catalog.setThirdCatalogId(rs.getLong("third_catalog_id"));
            catalog.setThirdCatalogName(rs.getString("third_catalog_name"));
            catalog.setFourthCatalogId(rs.getLong("fourth_catalog_id"));
            catalog.setFourthCatalogName(rs.getString("fourth_catalog_name"));
            catalog.setFifthCatalogId(rs.getLong("fifth_catalog_id"));
            catalog.setFifthCatalogName(rs.getString("fifth_catalog_name"));
            catalog.setSixthCatalogId(rs.getLong("sixth_catalog_id"));
            catalog.setSixthCatalogName(rs.getString("sixth_catalog_name"));
            catalog.setSeventhCatalogId(rs.getLong("seventh_catalog_id"));
            catalog.setSeventhCatalogName(rs.getString("seventh_catalog_name"));
            catalog.setEighthCatalogId(rs.getLong("eighth_catalog_id"));
            catalog.setEighthCatalogName(rs.getString("eighth_catalog_name"));
            catalog.setNinthCatalogId(rs.getLong("ninth_catalog_id"));
            catalog.setNinthCatalogName(rs.getString("ninth_catalog_name"));
            
            return catalog;
        }, parentId);
        
        logger.info("Found {} catalogs for parentId: {}", catalogs.size(), parentId);
        return catalogs;
    }

    @Override
    public List<Sku> getSkusByCatalogId(Long catalogId) {
        String sql = "SELECT * FROM record_avspare_case_sku WHERE catalog_id = ? ORDER BY id";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Sku sku = new Sku();
            sku.setId(rs.getLong("id"));
            sku.setBrandId(rs.getLong("brand_id"));
            sku.setCatalogId(rs.getLong("catalog_id"));
            sku.setCatalogImageId(rs.getLong("catalog_image_id"));
            sku.setSkuNo(rs.getString("sku_no"));
            sku.setSkuName(rs.getString("sku_name"));
            sku.setSkuDescription(rs.getString("sku_description"));
            sku.setQuantity(rs.getString("quantity"));
            sku.setFlight(rs.getString("flight"));
            
            // 设置目录路径信息
            sku.setFirstCatalogId(rs.getLong("first_catalog_id"));
            sku.setFirstCatalogName(rs.getString("first_catalog_name"));
            sku.setSecondCatalogId(rs.getLong("second_catalog_id"));
            sku.setSecondCatalogName(rs.getString("second_catalog_name"));
            sku.setThirdCatalogId(rs.getLong("third_catalog_id"));
            sku.setThirdCatalogName(rs.getString("third_catalog_name"));
            sku.setFourthCatalogId(rs.getLong("fourth_catalog_id"));
            sku.setFourthCatalogName(rs.getString("fourth_catalog_name"));
            sku.setFifthCatalogId(rs.getLong("fifth_catalog_id"));
            sku.setFifthCatalogName(rs.getString("fifth_catalog_name"));
            sku.setSixthCatalogId(rs.getLong("sixth_catalog_id"));
            sku.setSixthCatalogName(rs.getString("sixth_catalog_name"));
            sku.setSeventhCatalogId(rs.getLong("seventh_catalog_id"));
            sku.setSeventhCatalogName(rs.getString("seventh_catalog_name"));
            sku.setEighthCatalogId(rs.getLong("eighth_catalog_id"));
            sku.setEighthCatalogName(rs.getString("eighth_catalog_name"));
            sku.setNinthCatalogId(rs.getLong("ninth_catalog_id"));
            sku.setNinthCatalogName(rs.getString("ninth_catalog_name"));
            
            sku.setCreatedTime(rs.getTimestamp("created_time"));
            sku.setUpdatedTime(rs.getTimestamp("updated_time"));
            sku.setErrorJson(rs.getString("error_json"));
            sku.setOriginUrl(rs.getString("origin_url"));
            
            return sku;
        }, catalogId);
    }

    @Override
    public Long getSkuCountByCatalogId(Long catalogId) {
        String sql = "SELECT COUNT(*) FROM record_avspare_case_sku WHERE catalog_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, catalogId);
    }
}