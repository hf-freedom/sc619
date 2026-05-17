<template>
  <div>
    <h2 style="margin-bottom: 20px;">采购申请管理</h2>
    <el-button type="primary" @click="showAddDialog" style="margin-bottom: 15px;">
      新增采购申请
    </el-button>
    <el-table :data="purchaseRequests" border stripe style="width: 100%">
      <el-table-column prop="title" label="标题" width="180"></el-table-column>
      <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="estimatedAmount" label="预估金额" width="120">
        <template #default="scope">¥{{ scope.row.estimatedAmount }}</template>
      </el-table-column>
      <el-table-column prop="requiresComparison" label="需比价" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.requiresComparison ? 'warning' : 'success'" size="small">
            {{ scope.row.requiresComparison ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="350" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewQuotes(scope.row)" v-if="['PENDING_QUOTATION', 'QUOTATION_COMPLETED', 'APPROVED', 'ORDER_CREATED'].includes(scope.row.status)">
            {{ scope.row.status === 'PENDING_QUOTATION' ? '管理报价' : '查看报价' }}
          </el-button>
          <el-button size="small" type="success" @click="submitRequest(scope.row)" v-if="scope.row.status === 'DRAFT'">
            提交
          </el-button>
          <el-button size="small" type="success" @click="approveRequest(scope.row)" v-if="scope.row.status === 'PENDING_APPROVAL' || scope.row.status === 'QUOTATION_COMPLETED'">
            审批
          </el-button>
          <el-button size="small" type="warning" @click="rejectRequest(scope.row)" v-if="scope.row.status === 'PENDING_APPROVAL' || scope.row.status === 'QUOTATION_COMPLETED'">
            拒绝
          </el-button>
          <el-button size="small" type="danger" @click="cancelRequest(scope.row)" v-if="scope.row.status !== 'CANCELLED' && scope.row.status !== 'ORDER_CREATED'">
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="addDialogVisible" title="新增采购申请" width="600px">
      <el-form :model="newRequest" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="newRequest.title"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="newRequest.description" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="预算">
          <el-select v-model="newRequest.budgetId" style="width: 100%">
            <el-option v-for="budget in budgets" :key="budget.id" :label="budget.name" :value="budget.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预估金额">
          <el-input-number v-model="newRequest.estimatedAmount" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="申请人">
          <el-input v-model="newRequest.requesterName"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addRequest">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="quoteDialogVisible" title="供应商报价管理" width="900px">
      <template #header>
        <span>{{ isQuoteEditable ? '报价管理' : '查看报价' }} - {{ currentRequest?.title }}</span>
      </template>
      <el-button type="primary" @click="showAddQuoteDialog" size="small" style="margin-bottom: 15px;" v-if="isQuoteEditable">
        新增报价
      </el-button>
      <el-alert title="报价已按价格和交付周期排序" type="info" :closable="false" style="margin-bottom: 15px;"></el-alert>
      <el-table :data="sortedQuotes" border stripe style="width: 100%">
        <el-table-column label="排名" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.$index === 0" type="success" size="small">第1名</el-tag>
            <el-tag v-else-if="scope.$index === 1" type="warning" size="small">第2名</el-tag>
            <el-tag v-else type="info" size="small">第{{ scope.$index + 1 }}名</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="supplierName" label="供应商" width="180"></el-table-column>
        <el-table-column prop="quotedPrice" label="报价金额" width="120" sortable>
          <template #default="scope">
            <span style="color: #67C23A; font-weight: bold;">¥{{ scope.row.quotedPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryDays" label="交货天数" width="100" sortable>
          <template #default="scope">
            <span style="color: #409EFF;">{{ scope.row.deliveryDays }}天</span>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'SELECTED' ? 'success' : scope.row.status === 'REJECTED' ? 'danger' : 'info'" size="small">
              {{ scope.row.status === 'SELECTED' ? '已选中' : scope.row.status === 'REJECTED' ? '已拒绝' : '待选择' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" v-if="isQuoteEditable">
          <template #default="scope">
            <el-button size="small" type="success" @click="selectQuote(scope.row)" v-if="scope.row.status === 'SUBMITTED'">
              选中
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; text-align: right;" v-if="isQuoteEditable">
        <el-button type="primary" @click="createOrder" :disabled="!hasSelectedQuote">
          生成采购订单
        </el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="addQuoteDialogVisible" title="新增报价" width="500px">
      <el-form :model="newQuote" label-width="100px">
        <el-form-item label="供应商">
          <el-select v-model="newQuote.supplierId" style="width: 100%">
            <el-option v-for="supplier in suppliers" :key="supplier.id" :label="supplier.name" :value="supplier.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报价金额">
          <el-input-number v-model="newQuote.quotedPrice" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="交货天数">
          <el-input-number v-model="newQuote.deliveryDays" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="newQuote.remarks" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addQuoteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addQuote">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'PurchaseRequests',
  data() {
    return {
      purchaseRequests: [],
      budgets: [],
      suppliers: [],
      quotes: [],
      addDialogVisible: false,
      quoteDialogVisible: false,
      addQuoteDialogVisible: false,
      currentRequest: null,
      newRequest: {
        title: '',
        description: '',
        budgetId: '',
        estimatedAmount: 0,
        requesterName: ''
      },
      newQuote: {
        supplierId: '',
        quotedPrice: 0,
        deliveryDays: 1,
        remarks: ''
      }
    }
  },
  computed: {
    hasSelectedQuote() {
      return this.quotes.some(q => q.status === 'SELECTED')
    },
    isQuoteEditable() {
      return this.currentRequest?.status === 'PENDING_QUOTATION'
    },
    sortedQuotes() {
      return [...this.quotes].sort((a, b) => {
        if (a.quotedPrice !== b.quotedPrice) {
          return a.quotedPrice - b.quotedPrice
        }
        return a.deliveryDays - b.deliveryDays
      })
    }
  },
  mounted() {
    this.loadPurchaseRequests()
    this.loadBudgets()
    this.loadSuppliers()
  },
  methods: {
    showAddDialog() {
      this.addDialogVisible = true
    },
    async loadPurchaseRequests() {
      try {
        const response = await axios.get('/api/purchase-requests')
        this.purchaseRequests = response.data
      } catch (error) {
        console.error('加载采购申请失败:', error)
      }
    },
    async loadBudgets() {
      try {
        const response = await axios.get('/api/budgets')
        this.budgets = response.data
      } catch (error) {
        console.error('加载预算失败:', error)
      }
    },
    async loadSuppliers() {
      try {
        const response = await axios.get('/api/suppliers')
        this.suppliers = response.data
      } catch (error) {
        console.error('加载供应商失败:', error)
      }
    },
    async addRequest() {
      try {
        await axios.post('/api/purchase-requests', this.newRequest)
        this.$message.success('添加成功')
        this.addDialogVisible = false
        this.loadPurchaseRequests()
        this.newRequest = {
          title: '',
          description: '',
          budgetId: '',
          estimatedAmount: 0,
          requesterName: ''
        }
      } catch (error) {
        this.$message.error('添加失败')
      }
    },
    async submitRequest(request) {
      try {
        await axios.post(`/api/purchase-requests/${request.id}/submit`)
        this.$message.success('提交成功')
        this.loadPurchaseRequests()
      } catch (error) {
        this.$message.error('提交失败: ' + (error.response?.data?.message || '预算不足'))
      }
    },
    async approveRequest(request) {
      try {
        await axios.post(`/api/purchase-requests/${request.id}/approve`)
        this.$message.success('审批通过')
        this.loadPurchaseRequests()
      } catch (error) {
        this.$message.error('审批失败')
      }
    },
    async rejectRequest(request) {
      try {
        await axios.post(`/api/purchase-requests/${request.id}/reject`)
        this.$message.success('已拒绝')
        this.loadPurchaseRequests()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async cancelRequest(request) {
      try {
        await axios.post(`/api/purchase-requests/${request.id}/cancel`)
        this.$message.success('已取消')
        this.loadPurchaseRequests()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async viewQuotes(request) {
      this.currentRequest = request
      this.quoteDialogVisible = true
      try {
        const response = await axios.get(`/api/quotes/purchase-request/${request.id}/sorted`)
        this.quotes = response.data
      } catch (error) {
        console.error('加载报价失败:', error)
      }
    },
    showAddQuoteDialog() {
      this.addQuoteDialogVisible = true
    },
    async addQuote() {
      try {
        const quoteData = {
          ...this.newQuote,
          purchaseRequestId: this.currentRequest.id
        }
        await axios.post('/api/quotes', quoteData)
        this.$message.success('添加报价成功')
        this.addQuoteDialogVisible = false
        this.viewQuotes(this.currentRequest)
        this.newQuote = {
          supplierId: '',
          quotedPrice: 0,
          deliveryDays: 1,
          remarks: ''
        }
      } catch (error) {
        this.$message.error('添加失败')
      }
    },
    async selectQuote(quote) {
      try {
        await axios.post(`/api/quotes/${quote.id}/select`)
        this.$message.success('已选中该报价')
        this.viewQuotes(this.currentRequest)
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async createOrder() {
      try {
        const selectedQuote = this.quotes.find(q => q.status === 'SELECTED')
        await axios.post('/api/purchase-orders', {
          purchaseRequestId: this.currentRequest.id,
          selectedQuoteId: selectedQuote.id
        })
        this.$message.success('采购订单已生成')
        this.quoteDialogVisible = false
        this.loadPurchaseRequests()
      } catch (error) {
        this.$message.error('生成订单失败')
      }
    },
    getStatusType(status) {
      const map = {
        'DRAFT': 'info',
        'PENDING_APPROVAL': 'warning',
        'BUDGET_CHECK_FAILED': 'danger',
        'PENDING_QUOTATION': 'warning',
        'QUOTATION_COMPLETED': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'CANCELLED': 'info',
        'ORDER_CREATED': 'success'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'DRAFT': '草稿',
        'PENDING_APPROVAL': '待审批',
        'BUDGET_CHECK_FAILED': '预算不足',
        'PENDING_QUOTATION': '待报价',
        'QUOTATION_COMPLETED': '报价完成',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝',
        'CANCELLED': '已取消',
        'ORDER_CREATED': '已生成订单'
      }
      return map[status] || status
    }
  }
}
</script>
