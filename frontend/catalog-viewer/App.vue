<template>
  <div class="app">
    <div class="header">
      <h1>零件目录树</h1>
    </div>
    <div class="content">
      <CatalogTree />
    </div>
  </div>
</template>

<script setup>
import CatalogTree from './views/CatalogTree.vue'
</script>

<style>
.app {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.header h1 {
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.content {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
</style> 