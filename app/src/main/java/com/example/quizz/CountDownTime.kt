package com.example.quizz

import android.os.CountDownTimer
import com.example.quizz.databinding.FragmentQuestionBinding
import java.util.concurrent.TimeUnit

class CountDownTime(private val millisInFuture: Long, private val countDownInterval: Long, private val binding: FragmentQuestionBinding) :
    CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
        binding.timerText.text = "$seconds seconds remaining"
    }

    override fun onFinish() {
        binding.timerText.text = "Time's up!"
        // Perform action after countdown finishes, e.g., navigate to the next question
    }
}