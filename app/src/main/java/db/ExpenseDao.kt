package com.rohan.dailyexpensetracker.data.db

import androidx.room.*
import com.rohan.dailyexpensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE createdAt BETWEEN :start AND :end")
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?>
}