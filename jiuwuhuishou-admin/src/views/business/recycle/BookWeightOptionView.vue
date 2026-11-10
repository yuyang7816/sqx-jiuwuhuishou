<script setup>
import { ref } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { bookWeightOptionApi } from '@/api/modules/book-weight-option'
import { applianceHeroFlowApi } from '@/api/modules/appliance-hero-flow'
import { appointmentSlotApi } from '@/api/modules/appointment-slot'
import { PERMS } from '@/constants/permissions'
import { resolveFileUrl, tagMeta } from '@/utils/display'

const activeTab = ref('bookWeight')

const stateMap = {
  1: { label: '上架', type: 'success' },
  2: { label: '下架', type: 'info' },
}

const bookColumns = [
  { prop: 'title', label: '重量区间', minWidth: 120 },
  { prop: 'desc', label: '说明', minWidth: 160 },
  { prop: 'estimateAmount', label: '预估金额', width: 110 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'state', label: '状态', width: 90 },
  { prop: 'updateTime', label: '更新时间', minWidth: 170 },
]

const flowColumns = [
  { prop: 'name', label: '步骤名称', minWidth: 140 },
  { prop: 'icon', label: '图标', width: 90 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'state', label: '状态', width: 90 },
  { prop: 'updateTime', label: '更新时间', minWidth: 170 },
]

const slotColumns = [
  { prop: 'startTime', label: '开始时间', width: 120 },
  { prop: 'endTime', label: '结束时间', width: 120 },
  { prop: 'timeRange', label: '时段展示', minWidth: 140 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'state', label: '状态', width: 90 },
  { prop: 'updateTime', label: '更新时间', minWidth: 170 },
]

const TIME_OPTIONS = [
  '08:00', '08:30', '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
  '12:00', '12:30', '13:00', '13:30', '14:00', '14:30', '15:00', '15:30',
  '16:00', '16:30', '17:00', '17:30', '18:00', '18:30', '19:00', '19:30',
  '20:00', '20:30', '21:00', '21:30', '22:00',
]

function formatSlotRange(row) {
  if (!row?.startTime || !row?.endTime) return '—'
  return `${row.startTime} - ${row.endTime}`
}
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <div>
          <strong>回收配置</strong>
          <div class="desc">书籍预约重量、家电流程步骤、上门预约时间段；用户端同步读取对应接口。</div>
        </div>
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="书籍预约配置" name="bookWeight" lazy>
        <CrudPage
          title="书籍预约重量选项"
          :api="bookWeightOptionApi"
          :columns="bookColumns"
          :create-defaults="{ sort: 1, state: 1, estimateAmount: 0, desc: '' }"
          :create-perm="PERMS.bookWeightAdd"
          :edit-perm="PERMS.bookWeightUpdate"
          :show-delete="false"
        >
          <template #column-state="{ row }">
            <el-tag :type="tagMeta(stateMap, row.state).type">
              {{ tagMeta(stateMap, row.state).label }}
            </el-tag>
          </template>
          <template #form="{ form }">
            <el-form-item label="重量区间" required>
              <el-input v-model="form.title" maxlength="64" placeholder="如 5-15kg" />
            </el-form-item>
            <el-form-item label="说明">
              <el-input v-model="form.desc" maxlength="255" placeholder="如 日常整理" />
            </el-form-item>
            <el-form-item label="预估金额" required>
              <el-input-number v-model="form.estimateAmount" :min="0" :precision="2" :step="1" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="form.state">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="2">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </template>
        </CrudPage>
      </el-tab-pane>

      <el-tab-pane label="家电回收流程步骤" name="applianceFlow" lazy>
        <el-alert
          class="tab-hint"
          type="info"
          :closable="false"
          show-icon
          title="原系统配置 appliance_hero_flows（历史 id=75）已迁入本表。用户端：/app/recycle/appliance/heroFlow/list。"
        />
        <CrudPage
          title="家电回收流程步骤"
          :api="applianceHeroFlowApi"
          :columns="flowColumns"
          :create-defaults="{ sort: 1, state: 1, icon: '' }"
        >
          <template #column-icon="{ row }">
            <el-image
              v-if="row.icon"
              :src="resolveFileUrl(row.icon)"
              style="width: 36px; height: 36px"
              fit="cover"
              :preview-src-list="[resolveFileUrl(row.icon)]"
              preview-teleported
            />
            <span v-else>—</span>
          </template>
          <template #column-state="{ row }">
            <el-tag :type="tagMeta(stateMap, row.state).type">
              {{ tagMeta(stateMap, row.state).label }}
            </el-tag>
          </template>
          <template #form="{ form }">
            <el-form-item label="步骤名称" required>
              <el-input v-model="form.name" maxlength="64" placeholder="如 线上预约" />
            </el-form-item>
            <el-form-item label="图标" required>
              <ImageUpload v-model="form.icon" tip="支持 JPG/PNG，不超过 5MB" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="form.state">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="2">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </template>
        </CrudPage>
      </el-tab-pane>

      <el-tab-pane label="上门时间段配置" name="appointmentSlot" lazy>
        <el-alert
          class="tab-hint"
          type="info"
          :closable="false"
          show-icon
          title="配置用户端上门时间弹层中的时段列表（按开始时刻自动归入上午/下午）。用户端：/app/order/appointment/options。"
        />
        <CrudPage
          title="上门时间段列表"
          :api="appointmentSlotApi"
          :columns="slotColumns"
          :create-defaults="{ sort: 1, state: 1, startTime: '10:00', endTime: '12:00' }"
        >
          <template #column-timeRange="{ row }">
            {{ formatSlotRange(row) }}
          </template>
          <template #column-state="{ row }">
            <el-tag :type="tagMeta(stateMap, row.state).type">
              {{ tagMeta(stateMap, row.state).label }}
            </el-tag>
          </template>
          <template #form="{ form }">
            <el-form-item label="开始时间" required>
              <el-select v-model="form.startTime" filterable allow-create default-first-option placeholder="如 10:00" style="width: 100%">
                <el-option v-for="item in TIME_OPTIONS" :key="`s-${item}`" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="结束时间" required>
              <el-select v-model="form.endTime" filterable allow-create default-first-option placeholder="如 12:00" style="width: 100%">
                <el-option v-for="item in TIME_OPTIONS" :key="`e-${item}`" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="form.state">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="2">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </template>
        </CrudPage>
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
.desc {
  margin-top: 4px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.tab-hint {
  margin-bottom: 12px;
}
</style>
