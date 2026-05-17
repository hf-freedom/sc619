<template>
  <div>
    <h2 style="margin-bottom: 20px;">供应商管理</h2>
    <el-button type="primary" @click="showAddDialog" style="margin-bottom: 15px;">
      新增供应商
    </el-button>
    <el-table :data="suppliers" border stripe style="width: 100%">
      <el-table-column prop="name" label="供应商名称" width="200"></el-table-column>
      <el-table-column prop="contactPerson" label="联系人" width="120"></el-table-column>
      <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
      <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column prop="rating" label="评分" width="100">
        <template #default="scope">
          <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900" />
        </template>
      </el-table-column>
      <el-table-column prop="totalOrders" label="订单数" width="80"></el-table-column>
      <el-table-column prop="delayCount" label="延期次数" width="80">
        <template #default="scope">
          <span :style="{color: scope.row.delayCount > 0 ? '#F56C6C' : '#67C23A'}">
            {{ scope.row.delayCount }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="addDialogVisible" title="新增供应商" width="500px">
      <el-form :model="newSupplier" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="newSupplier.name"></el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="newSupplier.contactPerson"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="newSupplier.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="newSupplier.email"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="newSupplier.address"></el-input>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="newSupplier.rating" show-score text-color="#ff9900"></el-rate>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addSupplier">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Suppliers',
  data() {
    return {
      suppliers: [],
      addDialogVisible: false,
      newSupplier: {
        name: '',
        contactPerson: '',
        phone: '',
        email: '',
        address: '',
        rating: 5
      }
    }
  },
  mounted() {
    this.loadSuppliers()
  },
  methods: {
    showAddDialog() {
      this.addDialogVisible = true
    },
    async loadSuppliers() {
      try {
        const response = await axios.get('/api/suppliers')
        this.suppliers = response.data
      } catch (error) {
        console.error('加载供应商失败:', error)
      }
    },
    async addSupplier() {
      try {
        await axios.post('/api/suppliers', this.newSupplier)
        this.$message.success('添加成功')
        this.addDialogVisible = false
        this.loadSuppliers()
        this.newSupplier = {
          name: '',
          contactPerson: '',
          phone: '',
          email: '',
          address: '',
          rating: 5
        }
      } catch (error) {
        this.$message.error('添加失败')
      }
    }
  }
}
</script>
