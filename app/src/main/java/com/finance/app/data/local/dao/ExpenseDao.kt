package com.finance.app.data.local.dao

import androidx.room.*
import com.finance.app.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE user_id = :userId ORDER BY expense_time DESC")
    fun getExpensesByUserId(userId: Int): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?
    
    @Query("SELECT * FROM expenses WHERE user_id = :userId AND expense_time BETWEEN :startTime AND :endTime ORDER BY expense_time DESC")
    fun getExpensesByDateRange(userId: Int, startTime: String, endTime: String): Flow<List<Expense>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<Expense>)
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
    
    @Query("DELETE FROM expenses WHERE user_id = :userId")
    suspend fun deleteAllExpenses(userId: Int)
}
