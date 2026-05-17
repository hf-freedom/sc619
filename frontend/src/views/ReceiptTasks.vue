<template>
  <div>
    <h2 style="margin-bottom: 20px;">待收货任务</h2>
    
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 28px; color: #E6A23C; font-weight: bold;">
              {{ pendingCount }}
            </div>
            <div style="color: #909399; margin-top: 5px;">待收货</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 28px; color: #F56C6C; font-weight: bold;">
              {{ delayedCount }}
            </div>
            <div style="color: #909399; margin-top: 5px;">已延期</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 28px; color: #67C23A; font-weight: bold;">
              {{ completedCount }}
            </div>
            <div style="color: #909399; margin-top: 5px;">已完成</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="pendingOrders" border stripe style="width: 100%" v-if="pendingOrders.length > 0">
      <el-table-column prop="supplierName" label="供应商" width="180"></el-table-column>
      <el-table-column prop="totalAmount" label="订单金额" width="120">
        <template #default="scope">¥{{ scope.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="expectedDeliveryDate" label="预计交货" width="180"></el-table-column>
      <el-table-column label="剩余时间" width="120">
        <template #default="scope">
          <span :style="{color: getRemainingDays(scope.row) <= 0 ? '#F56C6C' : '#409EFF'}">
            {{ getRemainingDaysText(scope.row) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="fulfillmentStatus" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.fulfillmentStatus)" size="small">
            {{ getStatusText(scope.row.fulfillmentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button size="small" type="success" @click="confirmDelivery(scope.row)">
            确认收货
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty description="暂无待收货任务" v-else></el-empty>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ReceiptTasks',
  data() {
    return {
      purchaseOrders: []
    }
  },
  computed: {
    pendingOrders() {
      return this.purchaseOrders.filter(o => 
        o.fulfillmentStatus === 'PENDING_DELIVERY' || o.fulfillmentStatus === 'DELAYED'
      )
    },
    pendingCount() {
      return this.purchaseOrders.filter(o => o.fulfillmentStatus === 'PENDING_DELIVERY').length
    },
    delayedCount() {
      return this.purchaseOrders.filter(o => o.fulfillmentStatus === 'DELAYED').length
    },
    completedCount() {
      return this.purchaseOrders.filter(o => o.fulfillmentStatus === 'FULLY_DELIVERED').length
    }
  },
  mounted() {
    this.loadPurchaseOrders()
  },
  methods: {
    async loadPurchaseOrders() {
      try {
        const response = await axios.get('/api/purchase-orders')
        this.purchaseOrders = response.data
      } catch (error) {
        console.error('加载采购订单失败:', error)
      }
    },
    async confirmDelivery(order) {
      try {
        await axios.post(`/api/purchase-orders/${order.id}/confirm-delivery`)
        this.$message.success('收货确认成功')
        this.loadPurchaseOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    getRemainingDays(order) {
      if (!order.expectedDeliveryDate) return 0
      const expected = new Date(order.expectedDeliveryDate)
      const now = new Date()
      const diff = Math.ceil((expected - now) / (1000 * 60 * 60 * 24))
      return diff
    },
    getRemainingDaysText(order) {
      const days = this.getRemainingDays(order)
      if (days <= 0) {
        return `已逾期 ${Math.abs(days)} 天`
      } else if (days <= 3) {
        return `剩 ${days} 天`
      } else {
        return `${days} 天后`
      }
    },
    getStatusType(status) {
      const map = {
        'PENDING_DELIVERY': 'warning',
        'PARTIALLY_DELIVERED': 'info',
        'FULLY_DELIVERED': 'success',
        'DELAYED': 'danger',
        'CANCELLED': 'info'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'PENDING_DELIVERY': '待交货',
        'PARTIALLY_DELIVERED': '部分交货',
        'FULLY_DELIVERED': '全部交货',
        'DELAYED': '已延期',
        'CANCELLED': '已取消'
      }
      return map[status] || status
    }
  }
}
</script>
