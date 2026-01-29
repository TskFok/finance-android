package com.finance.app.data.local.dao

import androidx.room.*
import com.finance.app.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sort ASC, id ASC")
    fun getAllCategories(): Flow<List<ExpenseCategory>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): ExpenseCategory?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ExpenseCategory)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ExpenseCategory>)
    
    @Update
    suspend fun updateCategory(category: ExpenseCategory)
    
    @Delete
    suspend fun deleteCategory(category: ExpenseCategory)
    
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}
