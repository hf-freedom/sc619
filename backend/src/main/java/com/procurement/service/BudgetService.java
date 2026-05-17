package com.procurement.service;

import com.procurement.model.Budget;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetService {
    @Autowired
    private InMemoryStorage storage;

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(storage.budgets.values());
    }

    public Budget getBudgetById(String id) {
        return storage.budgets.get(id);
    }

    public Budget createBudget(Budget budget) {
        budget.setId(UUID.randomUUID().toString());
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
        if (budget.getUsedAmount() == null) budget.setUsedAmount(BigDecimal.ZERO);
        if (budget.getFrozenAmount() == null) budget.setFrozenAmount(BigDecimal.ZERO);
        storage.budgets.put(budget.getId(), budget);
        return budget;
    }

    public boolean checkBudgetSufficient(String budgetId, BigDecimal amount) {
        Budget budget = storage.budgets.get(budgetId);
        if (budget == null) return false;
        return budget.getAvailableAmount().compareTo(amount) >= 0;
    }

    public void freezeBudget(String budgetId, BigDecimal amount) {
        Budget budget = storage.budgets.get(budgetId);
        if (budget != null) {
            budget.setFrozenAmount(budget.getFrozenAmount().add(amount));
            budget.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void unfreezeBudget(String budgetId, BigDecimal amount) {
        Budget budget = storage.budgets.get(budgetId);
        if (budget != null) {
            budget.setFrozenAmount(budget.getFrozenAmount().subtract(amount));
            budget.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void useBudget(String budgetId, BigDecimal amount) {
        Budget budget = storage.budgets.get(budgetId);
        if (budget != null) {
            budget.setUsedAmount(budget.getUsedAmount().add(amount));
            budget.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void releaseUsedBudget(String budgetId, BigDecimal amount) {
        Budget budget = storage.budgets.get(budgetId);
        if (budget != null) {
            budget.setUsedAmount(budget.getUsedAmount().subtract(amount));
            budget.setUpdatedAt(LocalDateTime.now());
        }
    }
}
