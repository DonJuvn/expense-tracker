package com.expensetracker.repository;

import com.expensetracker.model.Expense;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory repository implementation for expense storage.
 * Uses singleton pattern for easy access throughout the application.
 */
public class ExpenseRepository implements ExpenseRepositoryInterface {
    private static ExpenseRepository instance;
    private final List<Expense> expenses;
    private final AtomicLong idGenerator;

    private ExpenseRepository() {
        this.expenses = new ArrayList<>();
        this.idGenerator = new AtomicLong(1);
    }

    /**
     * Get the singleton instance of ExpenseRepository
     * @return the singleton instance
     */
    public static synchronized ExpenseRepository getInstance() {
        if (instance == null) {
            instance = new ExpenseRepository();
        }
        return instance;
    }

    @Override
    public Expense addExpense(Expense expense) {
        Long newId = idGenerator.getAndIncrement();
        Expense newExpense = new Expense(
                newId,
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate() != null ? expense.getDate() : LocalDateTime.now()
        );
        expenses.add(newExpense);
        return newExpense;
    }

    @Override
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return expenses.stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean deleteExpense(Long id) {
        return expenses.removeIf(expense -> expense.getId().equals(id));
    }

    @Override
    public Optional<Expense> updateExpense(Long id, Expense expense) {
        Optional<Expense> existingOpt = findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        Expense existing = existingOpt.get();
        Expense updated = new Expense(
                existing.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                existing.getDate()
        );

        int index = expenses.indexOf(existing);
        if (index >= 0) {
            expenses.set(index, updated);
            return Optional.of(updated);
        }

        return Optional.empty();
    }

    /**
     * Get the total number of expenses
     * @return the count of expenses
     */
    public int count() {
        return expenses.size();
    }

    /**
     * Clear all expenses (useful for testing)
     */
    public void clear() {
        expenses.clear();
        idGenerator.set(1);
    }
}
