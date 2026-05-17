<template>
  <el-container style="height: 100vh">
    <el-header style="background: #409EFF; color: white; font-size: 20px; display: flex; align-items: center; padding: 0 20px;">
      采购申请比价系统
    </el-header>
    <el-container>
      <el-aside width="200px" style="background: #f5f5f5; padding-top: 10px;">
        <el-menu
        :default-active="$route.path"
        router
        mode="vertical"
      >
        <el-menu-item index="/">
          <span>首页统计</span>
        </el-menu-item>
        <el-menu-item index="/purchase-requests">
          <span>采购申请</span>
        </el-menu-item>
        <el-menu-item index="/suppliers">
          <span>供应商管理</span>
        </el-menu-item>
        <el-menu-item index="/purchase-orders">
          <span>采购订单</span>
        </el-menu-item>
        <el-menu-item index="/receipt-tasks">
          <span>待收货任务</span>
        </el-menu-item>
        <el-menu-item index="/delay-risks">
          <span>延期风险</span>
        </el-menu-item>
        <el-menu-item index="/budgets">
          <span>预算管理</span>
        </el-menu-item>
      </el-menu>
      </el-aside>
      <el-main style="background: #fff; padding: 20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
