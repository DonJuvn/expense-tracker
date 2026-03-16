package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Expense {
    private final Long id;
    private String description;
    private final BigDecimal amount;
    private final String category;
    private final LocalDateTime date;

    public Expense(Long id, String description, BigDecimal amount, String category, LocalDateTime date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                '}';
    }
}
