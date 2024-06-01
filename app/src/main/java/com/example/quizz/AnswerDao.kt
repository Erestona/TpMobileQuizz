package com.example.quizz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AnswersDao {
    @Insert
    suspend fun insert(answer: Answers): Long

    @Query("SELECT COUNT(*) FROM answers WHERE EXISTS ( SELECT 1 FROM answers LIMIT 1)")
    suspend fun getNumberAnswers(): Int

    @Query("SELECT * FROM answers WHERE questionId = :questionId")
    suspend fun getAnswersByQuestionId(questionId: Int): List<Answers>

    @Query("SELECT * FROM answers WHERE id = :id")
    suspend fun getAnswerById(id: Int): Answers?
    @Query("DELETE FROM answers")
    suspend fun nukeTable()
}
