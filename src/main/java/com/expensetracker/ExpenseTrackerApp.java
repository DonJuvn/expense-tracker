package com.expensetracker;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseTrackerApp extends Application {

    private TableView<Expense> expenseTable;
    private ObservableList<Expense> expenseData;
    private ExpenseRepository expenseRepository;

    private TextField descriptionField;
    private TextField amountField;
    private ComboBox<String> categoryComboBox;
    private DatePicker datePicker;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker");

        // Initialize repository and data
        expenseRepository = ExpenseRepository.getInstance();
        expenseData = FXCollections.observableArrayList();
        loadExpenses();

        // Create main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create table
        createExpenseTable();
        root.setCenter(expenseTable);

        // Create form panel
        VBox formPanel = createFormPanel();
        root.setBottom(formPanel);

        // Create scene
        Scene scene = new Scene(root, 800, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            // CSS file not found, continue without it
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
    private void createExpenseTable() {
        expenseTable = new TableView<>();

        // ID Column
        TableColumn<Expense, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        // Description Column
        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setPrefWidth(250);

        // Amount Column
        TableColumn<Expense, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);

        // Category Column
        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);

        // Date Column
        TableColumn<Expense, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(180);

        expenseTable.getColumns().addAll(idCol, descriptionCol, amountCol, categoryCol, dateCol);
        expenseTable.setItems(expenseData);
    }

    private VBox createFormPanel() {
        VBox formPanel = new VBox(10);
        formPanel.setPadding(new Insets(10));
        formPanel.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

        // Form fields row
        HBox fieldsRow = new HBox(10);
        fieldsRow.setPadding(new Insets(5));

        // Description field
        descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefWidth(200);

        // Amount field
        amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.setPrefWidth(100);

        // Category combo box
        categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Food", "Transport", "Entertainment", "Utilities", "Other");
        categoryComboBox.setPromptText("Category");
        categoryComboBox.setPrefWidth(120);

        // Date picker
        datePicker = new DatePicker();
        datePicker.setPromptText("Date");
        datePicker.setValue(LocalDate.now());
        datePicker.setPrefWidth(120);

        fieldsRow.getChildren().addAll(descriptionField, amountField, categoryComboBox, datePicker);

        // Buttons row
        HBox buttonsRow = new HBox(10);
        buttonsRow.setPadding(new Insets(5));

        Button addButton = new Button("Add Expense");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> addExpense());

        Button deleteButton = new Button("Delete Expense");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteExpense());

        buttonsRow.getChildren().addAll(addButton, deleteButton);

        formPanel.getChildren().addAll(fieldsRow, buttonsRow);

        return formPanel;
    }

    private void loadExpenses() {
        expenseData.clear();
        expenseData.addAll(expenseRepository.getAllExpenses());
    }

    private void addExpense() {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        String category = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();

        // Validation
        if (description.isEmpty()) {
            showAlert("Error", "Please enter a description.");
            return;
        }

        if (amountText.isEmpty()) {
            showAlert("Error", "Please enter an amount.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Error", "Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for amount.");
            return;
        }

        if (category == null) {
            showAlert("Error", "Please select a category.");
            return;
        }

        if (date == null) {
            showAlert("Error", "Please select a date.");
            return;
        }

        // Create new expense - repository will assign ID
        Expense newExpense = new Expense(null, description, amount, category, LocalDateTime.of(date, java.time.LocalTime.NOON));
        expenseRepository.addExpense(newExpense);

        // Reload data
        loadExpenses();

        // Clear form
        clearForm();
    }

    private void deleteExpense() {
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedExpense == null) {
            showAlert("Error", "Please select an expense to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Expense");
        confirmAlert.setContentText("Are you sure you want to delete this expense?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                expenseRepository.deleteExpense(selectedExpense.getId());
                loadExpenses();
            }
        });
    }

    private void clearForm() {
        descriptionField.clear();
        amountField.clear();
        categoryComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
