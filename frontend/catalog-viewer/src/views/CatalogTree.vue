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

    <!-- SKU抽屉 -->
    <el-drawer
      v-model="skuDrawerVisible"
      title="SKU列表"
      size="80%"
      :with-header="true"
      direction="ttb"
    >
      <div v-if="selectedNode && selectedNode.catalogIsLast === 1" class="sku-list">
        <div class="sku-header">
          <h3>{{ selectedNode.catalogName }} - SKU列表</h3>
          <div class="sku-summary">
            <span class="total">总数: {{ skuTotal }}</span>
          </div>
        </div>
        
        <el-table 
          :data="skuList" 
          border 
          style="width: 100%" 
          height="calc(100vh - 300px)"
          v-loading="loading"
        >
          <el-table-column type="index" label="序号" width="60" fixed />
          <el-table-column prop="skuNo" label="SKU编号" width="120" fixed />
          <el-table-column prop="skuName" label="SKU名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="skuDescription" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="flight" label="标记" width="80" />
          
          <!-- 目录路径信息 -->
          <el-table-column label="目录路径" min-width="300" show-overflow-tooltip>
            <template #default="{ row }">
              <el-tooltip :content="formatCatalogPath(row)" placement="top">
                <span>{{ formatCatalogPath(row) }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          
          <!-- 时间信息 -->
          <el-table-column label="时间信息" width="200">
            <template #default="{ row }">
              <div>创建: {{ formatDate(row.createdTime) }}</div>
              <div>更新: {{ formatDate(row.updatedTime) }}</div>
            </template>
          </el-table-column>
          
          <!-- 错误信息 -->
          <el-table-column prop="errorJson" label="错误信息" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <el-tag v-if="row.errorJson" type="danger">{{ row.errorJson }}</el-tag>
            </template>
          </el-table-column>
          
          <!-- 源地址 -->
          <el-table-column prop="originUrl" label="源地址" min-width="150">
            <template #default="{ row }">
              <el-link 
                type="primary" 
                :href="row.originUrl" 
                target="_blank"
                :underline="false"
              >
                <el-tooltip :content="row.originUrl" placement="top">
                  <span>查看源地址</span>
                </el-tooltip>
              </el-link>
            </template>
          </el-table-column>
        </el-table>
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
const loading = ref(false)
const skuList = ref([])
const skuTotal = ref(0)
const skuDrawerVisible = ref(false)

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

const handleNodeClick = async (data) => {
  console.log('点击节点:', data)
  selectedNode.value = data
  drawerVisible.value = true
  
  // 如果是末级目录，加载SKU信息
  if (data.catalogIsLast === 1) {
    try {
      loading.value = true
      skuDrawerVisible.value = true
      
      const [skusResponse, countResponse] = await Promise.all([
        axios.get(`http://localhost:8080/api/catalog/skus`, {
          params: { catalogId: data.id }
        }),
        axios.get(`http://localhost:8080/api/catalog/skus/count`, {
          params: { catalogId: data.id }
        })
      ])
      
      skuList.value = skusResponse.data
      skuTotal.value = countResponse.data
    } catch (error) {
      console.error('加载SKU失败:', error)
      ElMessage.error('加载SKU失败: ' + error.message)
    } finally {
      loading.value = false
    }
  }
}

const formatCatalogPath = (sku) => {
  const paths = []
  const levels = [
    { id: sku.firstCatalogId, name: sku.firstCatalogName },
    { id: sku.secondCatalogId, name: sku.secondCatalogName },
    { id: sku.thirdCatalogId, name: sku.thirdCatalogName },
    { id: sku.fourthCatalogId, name: sku.fourthCatalogName },
    { id: sku.fifthCatalogId, name: sku.fifthCatalogName },
    { id: sku.sixthCatalogId, name: sku.sixthCatalogName },
    { id: sku.seventhCatalogId, name: sku.seventhCatalogName },
    { id: sku.eighthCatalogId, name: sku.eighthCatalogName },
    { id: sku.ninthCatalogId, name: sku.ninthCatalogName }
  ]
  
  levels.forEach(level => {
    if (level.name) {
      paths.push(`${level.name} (${level.id})`)
    }
  })
  
  return paths.join(' > ')
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
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

.sku-list {
  padding: 20px;
}

.sku-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.sku-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.sku-summary {
  padding: 5px 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
}

.total {
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-table__header-wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-table .cell) {
  white-space: nowrap;
}
</style> 