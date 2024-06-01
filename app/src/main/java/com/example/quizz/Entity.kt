package com.example.quizz
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["categoryId"])]
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val categoryId: Int
)

@Entity(
    tableName = "answers",
    foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("questionId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["questionId"])]
)
data class Answers(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionId: Int,
    val text: String,
    val isCorrect: Boolean
)