package com.finance.app.data.local.dao

import androidx.room.*
import com.finance.app.data.local.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes WHERE user_id = :userId ORDER BY income_time DESC")
    fun getIncomesByUserId(userId: Int): Flow<List<Income>>
    
    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getIncomeById(id: Int): Income?
    
    @Query("SELECT * FROM incomes WHERE user_id = :userId AND income_time BETWEEN :startTime AND :endTime ORDER BY income_time DESC")
    fun getIncomesByDateRange(userId: Int, startTime: String, endTime: String): Flow<List<Income>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomes(incomes: List<Income>)
    
    @Update
    suspend fun updateIncome(income: Income)
    
    @Delete
    suspend fun deleteIncome(income: Income)
    
    @Query("DELETE FROM incomes WHERE user_id = :userId")
    suspend fun deleteAllIncomes(userId: Int)
}
