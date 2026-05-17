<template>
  <div>
    <h2 style="margin-bottom: 20px;">延期风险记录</h2>
    <el-table :data="delayRisks" border stripe style="width: 100%">
      <el-table-column prop="supplierName" label="供应商" width="180"></el-table-column>
      <el-table-column prop="riskLevel" label="风险等级" width="120">
        <template #default="scope">
          <el-tag :type="getRiskType(scope.row.riskLevel)" size="small">
            {{ getRiskText(scope.row.riskLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expectedDelayDays" label="预计延期天数" width="120"></el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
      <el-table-column prop="resolved" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.resolved ? 'success' : 'danger'" size="small">
            {{ scope.row.resolved ? '已解决' : '未解决' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button size="small" type="success" @click="resolveRisk(scope.row)" v-if="!scope.row.resolved">
            标记解决
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'DelayRisks',
  data() {
    return {
      delayRisks: []
    }
  },
  mounted() {
    this.loadDelayRisks()
  },
  methods: {
    async loadDelayRisks() {
      try {
        const response = await axios.get('/api/delay-risks')
        this.delayRisks = response.data
      } catch (error) {
        console.error('加载延期风险失败:', error)
      }
    },
    async resolveRisk(risk) {
      try {
        await axios.post(`/api/delay-risks/${risk.id}/resolve`)
        this.$message.success('已标记为已解决')
        this.loadDelayRisks()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    getRiskType(level) {
      const map = {
        'LOW': 'info',
        'MEDIUM': 'warning',
        'HIGH': 'danger',
        'CRITICAL': 'danger'
      }
      return map[level] || 'info'
    },
    getRiskText(level) {
      const map = {
        'LOW': '低',
        'MEDIUM': '中',
        'HIGH': '高',
        'CRITICAL': '严重'
      }
      return map[level] || level
    }
  }
}
</script>
