<template>
  <div>
    <h2 style="margin-bottom: 20px;">系统概览</h2>
    
    <h3 style="margin: 25px 0 15px; color: #303133; font-size: 18px;">📊 核心指标</h3>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 36px; color: #409EFF; font-weight: bold;">
              {{ statistics.totalPurchaseOrders }}
            </div>
            <div style="color: #606266; margin-top: 10px; font-size: 14px;">采购订单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 36px; color: #67C23A; font-weight: bold;">
              {{ statistics.completedOrders }}
            </div>
            <div style="color: #606266; margin-top: 10px; font-size: 14px;">已完成订单</div>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;">
              完成率 {{ completionRate }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 36px; color: #E6A23C; font-weight: bold;">
              ¥{{ formatAmount(statistics.totalSavedAmount) }}
            </div>
            <div style="color: #606266; margin-top: 10px; font-size: 14px;">累计节省金额</div>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;">
              <el-icon color="#67C23A"><TrendCharts /></el-icon> 平均每单节省 ¥{{ formatAmount(averageSaving) }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 36px; color: #F56C6C; font-weight: bold;">
              {{ statistics.totalDelayCount }}
            </div>
            <div style="color: #606266; margin-top: 10px; font-size: 14px;">延期次数</div>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;" v-if="statistics.totalPurchaseOrders > 0">
              延期率 {{ delayRate.toFixed(1) }}%
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <h3 style="margin: 30px 0 15px; color: #303133; font-size: 18px;">⭐ 供应商评分统计</h3>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div style="text-align: center; padding: 10px 0;">
            <div style="font-size: 48px; color: #E6A23C; font-weight: bold; margin-bottom: 10px;">
              {{ statistics.averageSupplierRating.toFixed(1) }}
            </div>
            <el-rate 
              v-model="statistics.averageSupplierRating" 
              disabled 
              show-score 
              text-color="#E6A23C"
              size="large"
            />
            <div style="color: #606266; margin-top: 15px; font-size: 14px;">平均供应商评分</div>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;">
              共 {{ suppliers.length }} 家供应商参与评分
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>供应商评分分布</span>
          </template>
          <div style="padding: 10px 0;">
            <div v-for="(group, level) in ratingGroups" :key="level" style="margin-bottom: 15px;">
              <div style="display: flex; align-items: center; margin-bottom: 5px;">
                <span style="width: 80px; font-size: 13px; color: #606266;">{{ level }}星</span>
                <el-progress 
                  :percentage="group.percentage" 
                  :color="getRatingColor(Number(level))"
                  :stroke-width="12"
                  style="flex: 1; margin: 0 15px;"
                />
                <span style="width: 50px; text-align: right; font-size: 13px; color: #909399;">
                  {{ group.count }}家
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <h3 style="margin: 30px 0 15px; color: #303133; font-size: 18px;">💰 预算使用概览</h3>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover">
          <el-table :data="budgets" border style="width: 100%">
            <el-table-column prop="name" label="预算科目" width="200"></el-table-column>
            <el-table-column prop="totalAmount" label="总预算" width="150">
              <template #default="scope">¥{{ formatAmount(scope.row.totalAmount) }}</template>
            </el-table-column>
            <el-table-column prop="usedAmount" label="已使用" width="150">
              <template #default="scope">
                <span style="color: #409EFF; font-weight: 500;">¥{{ formatAmount(scope.row.usedAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="frozenAmount" label="已冻结" width="150">
              <template #default="scope">
                <span style="color: #E6A23C;">¥{{ formatAmount(scope.row.frozenAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="使用率" width="200">
              <template #default="scope">
                <el-progress 
                  :percentage="getBudgetUsage(scope.row)" 
                  :color="getBudgetColor(scope.row)"
                  :stroke-width="10"
                />
              </template>
            </el-table-column>
            <el-table-column label="可用余额" width="150">
              <template #default="scope">
                <span :style="{color: getAvailableAmount(scope.row) >= 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold'}">
                  ¥{{ formatAmount(getAvailableAmount(scope.row)) }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'
import { TrendCharts } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: { TrendCharts },
  data() {
    return {
      statistics: {
        totalPurchaseOrders: 0,
        completedOrders: 0,
        totalSavedAmount: 0,
        totalDelayCount: 0,
        averageSupplierRating: 0
      },
      suppliers: [],
      budgets: []
    }
  },
  computed: {
    completionRate() {
      if (this.statistics.totalPurchaseOrders === 0) return 0
      return Math.round((this.statistics.completedOrders / this.statistics.totalPurchaseOrders) * 100)
    },
    averageSaving() {
      if (this.statistics.totalPurchaseOrders === 0) return 0
      return this.statistics.totalSavedAmount / this.statistics.totalPurchaseOrders
    },
    delayRate() {
      if (this.statistics.totalPurchaseOrders === 0) return 0
      return (this.statistics.totalDelayCount / this.statistics.totalPurchaseOrders) * 100
    },
    ratingGroups() {
      const groups = {}
      for (let i = 1; i <= 5; i++) {
        const count = this.suppliers.filter(s => Math.floor(s.rating) === i).length
        const percentage = this.suppliers.length > 0 ? Math.round((count / this.suppliers.length) * 100) : 0
        groups[i] = { count, percentage }
      }
      return groups
    }
  },
  mounted() {
    this.loadStatistics()
    this.loadSuppliers()
    this.loadBudgets()
  },
  methods: {
    async loadStatistics() {
      try {
        const response = await axios.get('/api/statistics')
        this.statistics = response.data
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    async loadSuppliers() {
      try {
        const response = await axios.get('/api/suppliers')
        this.suppliers = response.data
      } catch (error) {
        console.error('加载供应商数据失败:', error)
      }
    },
    async loadBudgets() {
      try {
        const response = await axios.get('/api/budgets')
        this.budgets = response.data
      } catch (error) {
        console.error('加载预算数据失败:', error)
      }
    },
    formatAmount(amount) {
      if (!amount && amount !== 0) return '0.00'
      return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    getRatingColor(level) {
      const colors = ['#F56C6C', '#E6A23C', '#F2D74D', '#95D47C', '#67C23A']
      return colors[level - 1] || '#909399'
    },
    getBudgetUsage(budget) {
      if (!budget || budget.totalAmount === 0) return 0
      const usage = ((budget.usedAmount + budget.frozenAmount) / budget.totalAmount) * 100
      return Math.min(Math.round(usage), 100)
    },
    getBudgetColor(budget) {
      const usage = this.getBudgetUsage(budget)
      if (usage >= 90) return '#F56C6C'
      if (usage >= 70) return '#E6A23C'
      return '#67C23A'
    },
    getAvailableAmount(budget) {
      if (!budget) return 0
      return budget.totalAmount - budget.usedAmount - budget.frozenAmount
    }
  }
}
</script>
