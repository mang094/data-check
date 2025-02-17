package com.example.terextree.service;

import com.example.terextree.dto.TreeNode;
import com.example.terextree.entity.PartsbookTerex;
import com.example.terextree.repository.PartsbookTerexRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TreeService {
    
    @Autowired
    private PartsbookTerexRepository repository;
    
    // 添加缓存
    private List<TreeNode> cachedTree = null;
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存
    
    public List<TreeNode> buildTree() {
        // 检查缓存是否有效
        if (cachedTree != null && System.currentTimeMillis() - lastCacheTime < CACHE_DURATION) {
            log.info("返回缓存的树形结构，包含 {} 个根节点", cachedTree.size());
            return cachedTree;
        }
        
        long startTime = System.currentTimeMillis();
        List<PartsbookTerex> allRecords = repository.findAll();
        log.info("从数据库查询到 {} 条记录，耗时：{}ms", allRecords.size(), System.currentTimeMillis() - startTime);
        
        if (allRecords.isEmpty()) {
            log.warn("数据库中没有查询到数据");
            return new ArrayList<>();
        }
        
        // 预处理：创建所有节点的Map
        Map<String, TreeNode> nodeMap = new LinkedHashMap<>();
        Map<String, Set<String>> parentChildMap = new HashMap<>();
        
        // 第一次遍历：收集所有唯一的节点和父子关系
        for (PartsbookTerex record : allRecords) {
            String familyName = record.getFamilyName();
            if (familyName == null || record.getHieDir() == null) continue;
            
            String[] paths = record.getHieDir().split(">");
            if (paths.length == 0) continue;
            
            // 创建或获取根节点
            nodeMap.putIfAbsent(familyName, new TreeNode(familyName));
            
            // 记录父子关系
            String parentPath = familyName;
            for (int i = 1; i < paths.length; i++) {
                String currentPath = paths[i].trim();
                if (currentPath.isEmpty()) continue;
                
                // 创建节点
                nodeMap.putIfAbsent(currentPath, new TreeNode(currentPath));
                
                // 记录父子关系
                parentChildMap.computeIfAbsent(parentPath, k -> new LinkedHashSet<>())
                             .add(currentPath);
                
                parentPath = currentPath;
            }
        }
        
        // 第二次遍历：构建树结构
        List<TreeNode> rootNodes = new ArrayList<>();
        for (PartsbookTerex record : allRecords) {
            String familyName = record.getFamilyName();
            if (familyName == null) continue;
            
            TreeNode rootNode = nodeMap.get(familyName);
            if (rootNode != null && !rootNodes.contains(rootNode)) {
                rootNodes.add(rootNode);
                buildChildren(rootNode, nodeMap, parentChildMap);
            }
        }
        
        // 更新缓存
        cachedTree = rootNodes;
        lastCacheTime = System.currentTimeMillis();
        
        log.info("构建树形结构完成，共 {} 个根节点，总耗时：{}ms", 
                rootNodes.size(), System.currentTimeMillis() - startTime);
        
        // 添加详细的根节点信息日志
        if (log.isDebugEnabled()) {
            rootNodes.forEach(node -> 
                log.debug("根节点: {}, 子节点数量: {}", 
                    node.getName(), 
                    node.getChildren().size())
            );
        }
        
        return rootNodes;
    }
    
    private void buildChildren(TreeNode parent, 
                             Map<String, TreeNode> nodeMap,
                             Map<String, Set<String>> parentChildMap) {
        Set<String> childrenNames = parentChildMap.get(parent.getName());
        if (childrenNames == null) return;
        
        for (String childName : childrenNames) {
            TreeNode childNode = nodeMap.get(childName);
            if (childNode != null && !parent.getChildren().contains(childNode)) {
                parent.getChildren().add(childNode);
                // 添加调试日志
                if (log.isDebugEnabled()) {
                    log.debug("添加子节点: {} -> {}", parent.getName(), childNode.getName());
                }
                buildChildren(childNode, nodeMap, parentChildMap);
            }
        }
    }
} 