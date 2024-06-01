package com.example.quizz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuestionDao {
    @Insert
    suspend fun insert(question: Question): Long

    @Query("SELECT COUNT(*) FROM questions WHERE EXISTS ( SELECT 1 FROM questions LIMIT 1)")
    suspend fun getNumberQuestions(): Int

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    suspend fun getQuestionsByCategory(categoryId: Int): List<Question>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Int): Question?

    @Query("DELETE FROM questions")
    suspend fun nukeTable()
}
