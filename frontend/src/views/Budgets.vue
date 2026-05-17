<template>
  <div>
    <h2 style="margin-bottom: 20px;">预算管理</h2>
    <el-table :data="budgets" border stripe style="width: 100%">
      <el-table-column prop="name" label="预算名称" width="200"></el-table-column>
      <el-table-column prop="code" label="预算编码" width="150"></el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="scope">
          ¥{{ scope.row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="usedAmount" label="已使用" width="120">
        <template #default="scope">
          ¥{{ scope.row.usedAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="frozenAmount" label="已冻结" width="120">
        <template #default="scope">
          ¥{{ scope.row.frozenAmount }}
        </template>
      </el-table-column>
      <el-table-column label="可用余额" width="120">
        <template #default="scope">
          <span :style="{color: getAvailableAmount(scope.row) >= 0 ? '#67C23A' : '#F56C6C'}">
            ¥{{ getAvailableAmount(scope.row) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Budgets',
  data() {
    return {
      budgets: []
    }
  },
  mounted() {
    this.loadBudgets()
  },
  methods: {
    async loadBudgets() {
      try {
        const response = await axios.get('/api/budgets')
        this.budgets = response.data
      } catch (error) {
        console.error('加载预算失败:', error)
      }
    },
    getAvailableAmount(budget) {
      return budget.totalAmount - budget.usedAmount - budget.frozenAmount
    }
  }
}
</script>
