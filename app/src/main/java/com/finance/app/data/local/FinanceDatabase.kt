package com.finance.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finance.app.data.local.dao.CategoryDao
import com.finance.app.data.local.dao.ExpenseDao
import com.finance.app.data.local.dao.IncomeDao
import com.finance.app.data.local.entity.Expense
import com.finance.app.data.model.ExpenseCategory
import com.finance.app.data.local.entity.Income

@Database(
    entities = [Expense::class, Income::class, ExpenseCategory::class],
    version = 2,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
}
