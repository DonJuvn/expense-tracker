package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepositoryInterface {
    /**
     * Add a new expense to the repository
     * @param expense the expense to add (id will be auto-generated)
     * @return the added expense with generated id
     */
    Expense addExpense(Expense expense);

    /**
     * Get all expenses
     * @return list of all expenses
     */
    List<Expense> getAllExpenses();

    /**
     * Find an expense by id
     * @param id the expense id
     * @return Optional containing the expense if found
     */
    Optional<Expense> findById(Long id);

    /**
     * Delete an expense by id
     * @param id the expense id to delete
     * @return true if deleted, false if not found
     */
    boolean deleteExpense(Long id);

    /**
     * Update an existing expense
     * @param id the id of the expense to update
     * @param expense the updated expense data
     * @return Optional containing the updated expense if found
     */
    Optional<Expense> updateExpense(Long id, Expense expense);
}
