import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/purchase-requests',
    name: 'PurchaseRequests',
    component: () => import('../views/PurchaseRequests.vue')
  },
  {
    path: '/suppliers',
    name: 'Suppliers',
    component: () => import('../views/Suppliers.vue')
  },
  {
    path: '/purchase-orders',
    name: 'PurchaseOrders',
    component: () => import('../views/PurchaseOrders.vue')
  },
  {
    path: '/delay-risks',
    name: 'DelayRisks',
    component: () => import('../views/DelayRisks.vue')
  },
  {
    path: '/budgets',
    name: 'Budgets',
    component: () => import('../views/Budgets.vue')
  },
  {
    path: '/receipt-tasks',
    name: 'ReceiptTasks',
    component: () => import('../views/ReceiptTasks.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
