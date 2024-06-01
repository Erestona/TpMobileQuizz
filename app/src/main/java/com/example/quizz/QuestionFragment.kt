package com.example.quizz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentQuestionBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var currentPageIndex = 0
    private var score = 0
    private var questions: List<Question> = listOf()
    private lateinit var sharedPreferences: SharedPreferences
    private var timer: CountDownTimer? = null

    companion object {
        private const val TIMER_DELAY = 10000 // 10 seconds in milliseconds
        private const val MAX_QUESTIONS = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        database = (requireActivity().application as QuizApplication).database
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getInt("categoryId") ?: return

        lifecycleScope.launch {
            questions =
                database.questionDao().getQuestionsByCategory(categoryId).take(MAX_QUESTIONS)
            displayCurrentPageQuestions()
            startTimer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
        currentPageIndex = 0
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(TIMER_DELAY.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = "Temps restant : ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                currentPageIndex++
                navigateToNextPage()
            }
        }.start()
    }

    private fun navigateToNextPage() {
        if (currentPageIndex < questions.size) {
            lifecycleScope.launch {
                displayCurrentPageQuestions()
                startTimer()
            }
        } else {
            timer?.cancel()
            saveScore()
            val username = sharedPreferences.getString("username", "Unknown") ?: "Unknown"

            val bundle = createBundle(score, username)
            findNavController().navigate(R.id.action_QuestionFragment_to_ScoreFragment, bundle)
        }
    }

    private fun createBundle(score: Int, username: String) : Bundle
    {
        val bundle = Bundle().apply {
            putInt("score", score)
            putString("username", username)
        }
        return bundle;
    }

    private suspend fun displayCurrentPageQuestions() {
        if (currentPageIndex >= questions.size) {
            return
        }

        binding.questionContainer.removeAllViews()

        val question = questions[currentPageIndex]
        val questionTextView = TextView(requireContext()).apply {
            text = question.text
            typeface = resources.getFont(R.font.montserrat_medium)
            textSize = 22f
        }
        binding.questionContainer.addView(questionTextView)

        val answers = database.answersDao().getAnswersByQuestionId(question.id)
        val marginTopInPixels = resources.getDimensionPixelSize(R.dimen.fab_margin)

        for (answer in answers) {
            val answerButton = Button(requireContext()).apply {
                text = answer.text
                typeface = resources.getFont(R.font.montserrat_bold)
                setTextColor(resources.getColor(R.color.white))
                setBackgroundResource(R.drawable.rounded_button)
                textSize = 15f

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, marginTopInPixels, 0, 0)
                }

                setOnClickListener {
                    if (answer.isCorrect) {
                        score++
                        updateScoreTextView()
                    }
                    currentPageIndex++
                    navigateToNextPage()
                }
            }
            binding.questionContainer.addView(answerButton)
        }
    }

    private fun updateScoreTextView() {
        binding.scoreTextView.text = "Score: $score/10"
    }

    private fun saveScore() {
        val categoryId = arguments?.getInt("categoryId") ?: return
        val username = sharedPreferences.getString("username", "Unknown") ?: "Unknown"
        val editor = sharedPreferences.edit()
        editor.putInt("$username-$categoryId", score)
        editor.apply()
    }
}
