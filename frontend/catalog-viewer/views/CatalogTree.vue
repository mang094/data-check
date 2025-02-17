<template>
  <div class="catalog-container">
    <div class="tree-wrapper">
      <el-tree
        ref="tree"
        :data="treeData"
        :props="defaultProps"
        @node-click="handleNodeClick"
        :load="loadNode"
        lazy
        :default-expand-all="false"
        node-key="id"
      >
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <span class="node-name">{{ data.catalogName }}</span>
            <el-tag size="small" :type="getLevelType(data.catalogLevel)">
              Level {{ data.catalogLevel }}
            </el-tag>
          </div>
        </template>
      </el-tree>
    </div>
    
    <el-drawer
      v-model="drawerVisible"
      title="目录详情"
      size="30%"
      :with-header="true"
    >
      <div v-if="selectedNode" class="node-details">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="目录名称">{{ selectedNode.catalogName }}</el-descriptions-item>
          <el-descriptions-item label="目录层级">{{ selectedNode.catalogLevel }}</el-descriptions-item>
          <el-descriptions-item label="是否末级">{{ selectedNode.catalogIsLast ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="父级目录">{{ selectedNode.parentCatalogName || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const treeData = ref([])
const selectedNode = ref(null)
const drawerVisible = ref(false)

const defaultProps = {
  children: 'children',
  label: 'catalogName',
  isLeaf: (data) => data.catalogIsLast === 1
}

const getLevelType = (level) => {
  const types = ['', 'success', 'warning', 'danger', 'info']
  return types[level % types.length]
}

const loadNode = async (node, resolve) => {
  try {
    if (node.level === 0) {
      const response = await axios.get('/api/catalog/level?parentId=0')
      console.log('一级目录数据:', response.data)
      resolve(response.data)
    } else {
      const response = await axios.get(`/api/catalog/level?parentId=${node.data.id}`)
      console.log(`${node.level}级目录数据:`, response.data)
      resolve(response.data)
    }
  } catch (error) {
    console.error('加载目录失败:', error)
    ElMessage.error('加载目录失败')
    resolve([])
  }
}

const handleNodeClick = (data) => {
  console.log('点击节点:', data)
  selectedNode.value = data
  drawerVisible.value = true
}
</script>

<style scoped>
.catalog-container {
  height: calc(100vh - 200px);
}

.tree-wrapper {
  height: 100%;
  overflow: auto;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 8px;
}

.node-name {
  font-size: 14px;
}

.node-details {
  padding: 20px;
}

:deep(.el-tree-node__content) {
  height: 40px;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}
</style> 