package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purana_quizzes")
data class PuranaQuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val questionTelugu: String,
    val option1: String,
    val option1Telugu: String,
    val option2: String,
    val option2Telugu: String,
    val option3: String,
    val option3Telugu: String,
    val option4: String,
    val option4Telugu: String,
    val correctOption: Int, // 1-4
    val puranam: String,
    val puranamTelugu: String,
    val chapter: String = "",
)
