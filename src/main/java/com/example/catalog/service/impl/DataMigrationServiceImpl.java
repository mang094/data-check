package com.example.catalog.service.impl;

import com.example.catalog.service.DataMigrationService;

import com.example.catalog.utils.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DataMigrationServiceImpl implements DataMigrationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    @Transactional
    public void migratePartsbookData() {
        String selectSql = "SELECT id, family_name, uuid, oss_image_path, hie_dir FROM hw_spider.partsbook_terex";
        
        jdbcTemplate.query(selectSql, rs -> {
            String hieDir = rs.getString("hie_dir");
            String ossImagePath = rs.getString("oss_image_path");
            String uuid = rs.getString("uuid");
            String[] levels = hieDir.split(">");
            
            Long previousLevelId = null;
            String previousLevelName = null;
            Map<Integer, CatalogLevel> catalogLevels = new HashMap<>();
            
            for (int i = 0; i < levels.length; i++) {
                String currentLevel = levels[i].trim();
                Long currentId = snowflakeIdGenerator.nextId();
                
                // 存储每一级目录信息，用于后续SKU关联
                catalogLevels.put(i + 1, new CatalogLevel(currentId, currentLevel));
                
                Map<String, Object> params = buildCatalogParams(
                    currentId, 
                    currentLevel, 
                    i + 1, 
                    previousLevelId, 
                    previousLevelName, 
                    i == levels.length - 1,
                    catalogLevels
                );
                
                insertCatalog(params);
                
                // 如果是末级目录，处理图片和SKU
                if (i == levels.length - 1) {
                    Long imageId = null;
                    if (StringUtils.hasText(ossImagePath)) {
                        imageId = handleImage(currentId, ossImagePath);
                    }
                    // 处理SKU数据
                    handleSkus(uuid, currentId, imageId, catalogLevels);
                }
                
                previousLevelId = currentId;
                previousLevelName = currentLevel;
            }
            return null;
        });
    }

    private Map<String, Object> buildCatalogParams(
            Long currentId,
            String currentLevel,
            int level,
            Long previousLevelId,
            String previousLevelName,
            boolean isLast,
            Map<Integer, CatalogLevel> catalogLevels) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", currentId);
        params.put("brand_id", 1L);
        params.put("catalog_name", currentLevel);
        params.put("catalog_level", level);
        params.put("parent_catalog_id", previousLevelId);
        params.put("parent_catalog_name", previousLevelName);
        params.put("catalog_is_last", isLast ? 1 : 0);
        params.put("type", determineType(level - 1));
        
        setLevelFields(params, level, currentId, currentLevel);
        
        return params;
    }

    private int determineType(int level) {
        if (level < 0 || level > 7) {
            return -1;
        }
        return level;
    }

    private void setLevelFields(Map<String, Object> params, int level, Long catalogId, String catalogName) {
        String levelPrefix;
        switch (level) {
            case 1: levelPrefix = "first"; break;
            case 2: levelPrefix = "second"; break;
            case 3: levelPrefix = "third"; break;
            case 4: levelPrefix = "fourth"; break;
            case 5: levelPrefix = "fifth"; break;
            case 6: levelPrefix = "sixth"; break;
            case 7: levelPrefix = "seventh"; break;
            case 8: levelPrefix = "eighth"; break;
            case 9: levelPrefix = "ninth"; break;
            default: return;
        }
        
        params.put(levelPrefix + "_catalog_id", catalogId);
        params.put(levelPrefix + "_catalog_name", catalogName);
    }

    private void insertCatalog(Map<String, Object> params) {
        String insertSql = 
            "INSERT INTO record_gene_catalog (" +
            "    id, brand_id, catalog_name, catalog_level, parent_catalog_id, " +
            "    parent_catalog_name, catalog_is_last, type, " +
            "    first_catalog_id, first_catalog_name, " +
            "    second_catalog_id, second_catalog_name, " +
            "    third_catalog_id, third_catalog_name, " +
            "    fourth_catalog_id, fourth_catalog_name, " +
            "    fifth_catalog_id, fifth_catalog_name, " +
            "    sixth_catalog_id, sixth_catalog_name, " +
            "    seventh_catalog_id, seventh_catalog_name, " +
            "    eighth_catalog_id, eighth_catalog_name, " +
            "    ninth_catalog_id, ninth_catalog_name, " +
            "    created_time, updated_time" +
            ") VALUES (" +
            "    :id, :brand_id, :catalog_name, :catalog_level, :parent_catalog_id, " +
            "    :parent_catalog_name, :catalog_is_last, :type, " +
            "    :first_catalog_id, :first_catalog_name, " +
            "    :second_catalog_id, :second_catalog_name, " +
            "    :third_catalog_id, :third_catalog_name, " +
            "    :fourth_catalog_id, :fourth_catalog_name, " +
            "    :fifth_catalog_id, :fifth_catalog_name, " +
            "    :sixth_catalog_id, :sixth_catalog_name, " +
            "    :seventh_catalog_id, :seventh_catalog_name, " +
            "    :eighth_catalog_id, :eighth_catalog_name, " +
            "    :ninth_catalog_id, :ninth_catalog_name, " +
            "    NOW(), NOW()" +
            ")";

        try {
            NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedJdbcTemplate.update(insertSql, params);
            log.info("Successfully inserted catalog with ID: {}", params.get("id"));
        } catch (Exception e) {
            log.error("Error inserting catalog with ID: {}", params.get("id"), e);
            throw e;
        }
    }

    private void handleSkus(String uuid, Long catalogId, Long imageId, Map<Integer, CatalogLevel> catalogLevels) {
        String selectSkuSql = 
            "SELECT id, sku_uuid, pos, part_no, description, qty, uom, comments, coordinate " +
            "FROM hw_spider.partsbook_terex_info " +
            "WHERE uuid = ?";
        
        jdbcTemplate.query(selectSkuSql, rs -> {
            Long skuId = snowflakeIdGenerator.nextId();
            Map<String, Object> params = new HashMap<>();
            
            // 基本信息
            params.put("id", skuId);
            params.put("brand_id", 1L);
            params.put("catalog_id", catalogId);
            params.put("catalog_image_id", imageId);
            params.put("sku_no", rs.getString("part_no"));
            params.put("sku_name", rs.getString("description"));
            params.put("sku_description", rs.getString("comments"));
            params.put("quantity", rs.getString("qty"));
            params.put("flight", rs.getString("pos")); // 使用pos作为flight标记
            
            // 设置目录层级信息
            setSkuLevelFields(params, catalogLevels);
            
            insertSku(params);
            return null;
        }, uuid);
    }

    private void setSkuLevelFields(Map<String, Object> params, Map<Integer, CatalogLevel> catalogLevels) {
        for (Map.Entry<Integer, CatalogLevel> entry : catalogLevels.entrySet()) {
            int level = entry.getKey();
            CatalogLevel catalogLevel = entry.getValue();
            
            String prefix = getLevelPrefix(level);
            if (prefix != null) {
                params.put(prefix + "_catalog_id", catalogLevel.getId());
                params.put(prefix + "_catalog_name", catalogLevel.getName());
            }
        }
    }

    private void insertSku(Map<String, Object> params) {
        String insertSkuSql = 
            "INSERT INTO record_gene_sku (" +
            "    id, brand_id, catalog_id, catalog_image_id, " +
            "    sku_no, sku_name, sku_description, quantity, flight, " +
            "    first_catalog_id, first_catalog_name, " +
            "    second_catalog_id, second_catalog_name, " +
            "    third_catalog_id, third_catalog_name, " +
            "    fourth_catalog_id, fourth_catalog_name, " +
            "    fifth_catalog_id, fifth_catalog_name, " +
            "    sixth_catalog_id, sixth_catalog_name, " +
            "    seventh_catalog_id, seventh_catalog_name, " +
            "    eighth_catalog_id, eighth_catalog_name, " +
            "    ninth_catalog_id, ninth_catalog_name, " +
            "    created_time, updated_time" +
            ") VALUES (" +
            "    :id, :brand_id, :catalog_id, :catalog_image_id, " +
            "    :sku_no, :sku_name, :sku_description, :quantity, :flight, " +
            "    :first_catalog_id, :first_catalog_name, " +
            "    :second_catalog_id, :second_catalog_name, " +
            "    :third_catalog_id, :third_catalog_name, " +
            "    :fourth_catalog_id, :fourth_catalog_name, " +
            "    :fifth_catalog_id, :fifth_catalog_name, " +
            "    :sixth_catalog_id, :sixth_catalog_name, " +
            "    :seventh_catalog_id, :seventh_catalog_name, " +
            "    :eighth_catalog_id, :eighth_catalog_name, " +
            "    :ninth_catalog_id, :ninth_catalog_name, " +
            "    NOW(), NOW()" +
            ")";

        try {
            NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedJdbcTemplate.update(insertSkuSql, params);
            log.info("Successfully inserted SKU with ID: {}", params.get("id"));
        } catch (Exception e) {
            log.error("Error inserting SKU with ID: {}", params.get("id"), e);
            throw e;
        }
    }

    // 辅助类，用于存储目录层级信息
    private static class CatalogLevel {
        private final Long id;
        private final String name;

        public CatalogLevel(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private String getLevelPrefix(int level) {
        return switch (level) {
            case 1 -> "first";
            case 2 -> "second";
            case 3 -> "third";
            case 4 -> "fourth";
            case 5 -> "fifth";
            case 6 -> "sixth";
            case 7 -> "seventh";
            case 8 -> "eighth";
            case 9 -> "ninth";
            default -> null;
        };
    }

    private void handleImage(Long catalogId, String ossImagePath) {
        try {
            // 1. 插入图片记录
            Long imageId = insertImage(ossImagePath);
            
            // 2. 更新目录的图片ID
            updateCatalogImageId(catalogId, imageId);
            
            // 3. 创建图片关联关系
            createImageRelation(catalogId, imageId);
            
        } catch (Exception e) {
            log.error("Error handling image for catalogId: {}, ossImagePath: {}", catalogId, ossImagePath, e);
            throw e;
        }
    }

    private Long insertImage(String ossImagePath) {
        Long imageId = snowflakeIdGenerator.nextId();
        
        String insertImageSql = 
            "INSERT INTO record_gene_image (" +
            "    id, image_url, oss_link, upload_status, created_time, updated_time" +
            ") VALUES (" +
            "    :id, :image_url, :oss_link, :upload_status, NOW(), NOW()" +
            ")";
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", imageId);
        params.put("image_url", ossImagePath); // 原始URL作为image_url
        params.put("oss_link", ossImagePath);  // OSS路径
        params.put("upload_status", 1);        // 假设图片已上传成功
        
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedJdbcTemplate.update(insertImageSql, params);
        
        return imageId;
    }

    private void updateCatalogImageId(Long catalogId, Long imageId) {
        String updateSql = 
            "UPDATE record_gene_catalog " +
            "SET catalog_image_id = :image_id " +
            "WHERE id = :catalog_id";
        
        Map<String, Object> params = new HashMap<>();
        params.put("catalog_id", catalogId);
        params.put("image_id", imageId);
        
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedJdbcTemplate.update(updateSql, params);
    }

    private void createImageRelation(Long catalogId, Long imageId) {
        String insertRelationSql = 
            "INSERT INTO record_gene_image_relation (" +
            "    id, catalog_id, catalog_image_id, brand_id, created_time" +
            ") VALUES (" +
            "    :id, :catalog_id, :catalog_image_id, :brand_id, NOW()" +
            ")";
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", snowflakeIdGenerator.nextId());
        params.put("catalog_id", catalogId);
        params.put("catalog_image_id", imageId);
        params.put("brand_id", 1L); // 使用固定的brand_id
        
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedJdbcTemplate.update(insertRelationSql, params);
    }
} 