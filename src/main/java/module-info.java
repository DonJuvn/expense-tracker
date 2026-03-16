module org.example.expensedemo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.expensedemo2 to javafx.fxml;
    exports org.example.expensedemo2;

    opens com.expensetracker to javafx.fxml;
    exports com.expensetracker;
    opens com.expensetracker.model to javafx.fxml;
    exports com.expensetracker.model;
    opens com.expensetracker.controller to javafx.fxml;
    exports com.expensetracker.controller;
    opens com.expensetracker.repository to javafx.fxml;
    exports com.expensetracker.repository;
}