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
          
          <template v-if="selectedNode.firstCatalogName">
            <el-descriptions-item label="一级目录">
              {{ selectedNode.firstCatalogName }} (ID: {{ selectedNode.firstCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.secondCatalogName">
            <el-descriptions-item label="二级目录">
              {{ selectedNode.secondCatalogName }} (ID: {{ selectedNode.secondCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.thirdCatalogName">
            <el-descriptions-item label="三级目录">
              {{ selectedNode.thirdCatalogName }} (ID: {{ selectedNode.thirdCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.fourthCatalogName">
            <el-descriptions-item label="四级目录">
              {{ selectedNode.fourthCatalogName }} (ID: {{ selectedNode.fourthCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.fifthCatalogName">
            <el-descriptions-item label="五级目录">
              {{ selectedNode.fifthCatalogName }} (ID: {{ selectedNode.fifthCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.sixthCatalogName">
            <el-descriptions-item label="六级目录">
              {{ selectedNode.sixthCatalogName }} (ID: {{ selectedNode.sixthCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.seventhCatalogName">
            <el-descriptions-item label="七级目录">
              {{ selectedNode.seventhCatalogName }} (ID: {{ selectedNode.seventhCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.eighthCatalogName">
            <el-descriptions-item label="八级目录">
              {{ selectedNode.eighthCatalogName }} (ID: {{ selectedNode.eighthCatalogId }})
            </el-descriptions-item>
          </template>
          <template v-if="selectedNode.ninthCatalogName">
            <el-descriptions-item label="九级目录">
              {{ selectedNode.ninthCatalogName }} (ID: {{ selectedNode.ninthCatalogId }})
            </el-descriptions-item>
          </template>
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
      const response = await axios.get('http://localhost:8080/api/catalog/level', {
        params: { parentId: 0 }
      })
      console.log('一级目录数据:', response.data)
      resolve(response.data)
    } else {
      const response = await axios.get(`http://localhost:8080/api/catalog/level`, {
        params: { parentId: node.data.id }
      })
      console.log(`${node.level}级目录数据:`, response.data)
      resolve(response.data)
    }
  } catch (error) {
    console.error('加载目录失败:', error)
    ElMessage.error('加载目录失败: ' + error.message)
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