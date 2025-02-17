package com.example.terextree.controller;

import com.example.terextree.dto.TreeNode;
import com.example.terextree.entity.PartsbookTerex;
import com.example.terextree.repository.PartsbookTerexRepository;
import com.example.terextree.service.TreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class TreeController {
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    private PartsbookTerexRepository repository;
    
    @GetMapping("/tree")
    public List<TreeNode> getTree() {
        log.info("开始获取树形结构数据");
        List<TreeNode> result = treeService.buildTree();
        log.info("获取到的数据条数: {}, 数据内容: {}", result.size(), result);
        return result;
    }

    @GetMapping("/test")
    public List<PartsbookTerex> testQuery() {
        log.info("开始测试数据库查询");
        List<PartsbookTerex> result = repository.findAll();
        log.info("查询到 {} 条数据", result.size());
        return result;
    }
} 