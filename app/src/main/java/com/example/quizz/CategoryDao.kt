package com.example.quizz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import android.util.Log

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Query("SELECT COUNT(*) FROM categories WHERE EXISTS ( SELECT 1 FROM categories LIMIT 1)")
    suspend fun getNumberCategory(): Int

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?
    @Query("DELETE FROM categories")
    suspend fun nukeTable()
}

suspend fun insertQuestion(questionDao: QuestionDao, categoryDao: CategoryDao, question: Question) {
    val categoryExists = categoryDao.getCategoryById(question.categoryId) != null
    if (categoryExists) {
        questionDao.insert(question)
    } else {
        Log.e("Database", "Category with id ${question.categoryId} does not exist.")
    }
}
