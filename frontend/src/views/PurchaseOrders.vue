<template>
  <div>
    <h2 style="margin-bottom: 20px;">采购订单管理</h2>
    <el-table :data="purchaseOrders" border stripe style="width: 100%">
      <el-table-column prop="supplierName" label="供应商" width="180"></el-table-column>
      <el-table-column prop="totalAmount" label="订单金额" width="120">
        <template #default="scope">¥{{ scope.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="deliveryDays" label="约定天数" width="100"></el-table-column>
      <el-table-column prop="expectedDeliveryDate" label="预计交货" width="180"></el-table-column>
      <el-table-column prop="actualDeliveryDate" label="实际交货" width="180">
        <template #default="scope">
          {{ scope.row.actualDeliveryDate || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="fulfillmentStatus" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.fulfillmentStatus)" size="small">
            {{ getStatusText(scope.row.fulfillmentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" type="success" @click="confirmDelivery(scope.row)" v-if="scope.row.fulfillmentStatus === 'PENDING_DELIVERY' || scope.row.fulfillmentStatus === 'DELAYED'">
            确认收货
          </el-button>
          <el-button size="small" type="danger" @click="cancelOrder(scope.row)" v-if="scope.row.fulfillmentStatus !== 'CANCELLED' && scope.row.fulfillmentStatus !== 'FULLY_DELIVERED'">
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'PurchaseOrders',
  data() {
    return {
      purchaseOrders: []
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
    async cancelOrder(order) {
      try {
        await axios.post(`/api/purchase-orders/${order.id}/cancel`)
        this.$message.success('订单已取消')
        this.loadPurchaseOrders()
      } catch (error) {
        this.$message.error('操作失败')
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
