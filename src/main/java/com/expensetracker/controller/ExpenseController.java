package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Controller for managing expense operations in the expense tracker application.
 * This FXML-based controller is an alternative to the pure JavaFX ExpenseTrackerApp.
 * Handles adding, deleting, and displaying expenses through a TableView.
 */
public class ExpenseController {

    @FXML
    private TableView<Expense> expenseTableView;

    @FXML
    private TableColumn<Expense, Long> idColumn;

    @FXML
    private TableColumn<Expense, String> descriptionColumn;

    @FXML
    private TableColumn<Expense, BigDecimal> amountColumn;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, LocalDateTime> dateColumn;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField amountTextField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private DatePicker expenseDatePicker;

    private ExpenseRepository expenseRepository;
    private ObservableList<Expense> expenseList;

    /**
     * Initializes the controller. Sets up the repository, table columns,
     * category options, and loads existing expenses.
     */
    @FXML
    public void initialize() {
        expenseRepository = ExpenseRepository.getInstance();
        expenseList = FXCollections.observableArrayList();

        // Initialize category options
        categoryComboBox.getItems().addAll(
            "Food",
            "Transportation",
            "Entertainment",
            "Utilities",
            "Healthcare",
            "Shopping",
            "Education",
            "Other"
        );

        // Set up table columns
        setupTableColumns();

        // Load expenses from repository
        loadExpenses();

        // Set default date to today
        expenseDatePicker.setValue(LocalDate.now());

        // Enable selection in TableView
        expenseTableView.setItems(expenseList);
    }

    /**
     * Configures the table columns to display values from Expense properties.
     */
    private void setupTableColumns() {
        // Use PropertyValueFactory instead of property methods since Expense doesn't have JavaFX properties
        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));
    }

    /**
     * Handles the Add button click event.
     * Validates input, creates an Expense object, saves it to the repository,
     * and refreshes the TableView.
     */
    @FXML
    private void handleAddButton() {
        // Validate input fields
        String validationError = validateInput();
        if (validationError != null) {
            showErrorAlert("Validation Error", validationError);
            return;
        }

        try {
            // Create Expense object from input
            Expense expense = createExpenseFromInput();

            // Save to repository
            expenseRepository.addExpense(expense);

            // Refresh the TableView
            loadExpenses();

            // Clear input fields
            clearInputFields();

            // Show success message
            showSuccessAlert("Expense Added", "Expense has been successfully added!");

        } catch (Exception e) {
            showErrorAlert("Error", "Failed to add expense: " + e.getMessage());
        }
    }

    /**
     * Handles the Delete button click event.
     * Removes the selected expense from the repository and refreshes the TableView.
     */
    @FXML
    private void handleDeleteButton() {
        Expense selectedExpense = expenseTableView.getSelectionModel().getSelectedItem();

        if (selectedExpense == null) {
            showErrorAlert("No Selection", "Please select an expense to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Expense");
        confirmAlert.setContentText("Are you sure you want to delete this expense?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete from repository
                    expenseRepository.deleteExpense(selectedExpense.getId());

                    // Refresh the TableView
                    loadExpenses();

                    // Show success message
                    showSuccessAlert("Expense Deleted", "Expense has been successfully deleted!");

                } catch (Exception e) {
                    showErrorAlert("Error", "Failed to delete expense: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Loads all expenses from the repository and updates the TableView.
     */
    private void loadExpenses() {
        try {
            expenseList.clear();
            expenseList.addAll(expenseRepository.getAllExpenses());
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to load expenses: " + e.getMessage());
        }
    }

    /**
     * Validates the input fields.
     * @return error message if validation fails, null otherwise
     */
    private String validateInput() {
        String description = descriptionTextField.getText();
        String amountText = amountTextField.getText();
        String category = categoryComboBox.getValue();
        LocalDate date = expenseDatePicker.getValue();

        if (description == null || description.trim().isEmpty()) {
            return "Please enter a description.";
        }

        if (amountText == null || amountText.trim().isEmpty()) {
            return "Please enter an amount.";
        }

        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Amount must be greater than zero.";
            }
        } catch (NumberFormatException e) {
            return "Please enter a valid amount.";
        }

        if (category == null || category.trim().isEmpty()) {
            return "Please select a category.";
        }

        if (date == null) {
            return "Please select a date.";
        }

        return null;
    }

    /**
     * Creates an Expense object from the input fields.
     * @return the created Expense object
     */
    private Expense createExpenseFromInput() {
        String description = descriptionTextField.getText().trim();
        BigDecimal amount = new BigDecimal(amountTextField.getText().trim());
        String category = categoryComboBox.getValue();
        LocalDate localDate = expenseDatePicker.getValue();

        // Create expense with null ID - repository will assign one
        return new Expense(null, description, amount, category, LocalDateTime.of(localDate, java.time.LocalTime.NOON));
    }

    /**
     * Clears all input fields.
     */
    private void clearInputFields() {
        descriptionTextField.clear();
        amountTextField.clear();
        categoryComboBox.setValue(null);
        expenseDatePicker.setValue(LocalDate.now());
    }

    /**
     * Shows an error alert with the specified title and message.
     * @param title the alert title
     * @param message the error message
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a success/information alert with the specified title and message.
     * @param title the alert title
     * @param message the success message
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
