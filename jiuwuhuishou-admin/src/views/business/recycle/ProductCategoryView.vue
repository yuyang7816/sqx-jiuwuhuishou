<script setup>
import { ref } from 'vue'
import SimpleCategoryPanel from './panels/SimpleCategoryPanel.vue'
import ProductSkuPanel from './panels/ProductSkuPanel.vue'
import RecycleConfigPanel from './panels/RecycleConfigPanel.vue'
import { clothesCategoryApi } from '@/api/modules/clothes-master'
import { bookCategoryApi } from '@/api/modules/book-master'

const activeTab = ref('appliance')
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <strong>回收品类主数据</strong>
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="家电" name="appliance" lazy>
        <ProductSkuPanel recycle-code="appliance" />
      </el-tab-pane>

      <el-tab-pane label="数码" name="digital" lazy>
        <ProductSkuPanel recycle-code="digital" />
      </el-tab-pane>

      <el-tab-pane label="奢侈品" name="luxury" lazy>
        <ProductSkuPanel recycle-code="luxury" />
      </el-tab-pane>

      <el-tab-pane label="旧衣" name="clothes" lazy>
        <SimpleCategoryPanel
          :api="clothesCategoryApi"
          label="旧衣分类"
        />
      </el-tab-pane>

      <el-tab-pane label="书籍" name="book" lazy>
        <SimpleCategoryPanel
          :api="bookCategoryApi"
          label="书籍分类"
        />
      </el-tab-pane>

      <el-tab-pane label="文案配置" name="config" lazy>
        <RecycleConfigPanel />
      </el-tab-pane>

      <el-tab-pane label="全部SKU" name="sku" lazy>
        <ProductSkuPanel />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}
</style>
