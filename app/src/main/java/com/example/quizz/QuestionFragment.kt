package com.example.quizz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentQuestionBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var currentPageIndex = 0
    private var answerSelectedTime: MutableMap<Int, Long> = mutableMapOf() // Time when an answer was selected
    private var timer: CountDownTime? = null

    companion object {
        private const val QUESTIONS_PER_PAGE = 1
        private const val TIMER_DELAY = 10000 // 10 seconds in milliseconds
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        database = (requireActivity().application as QuizApplication).database
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageTimer()

        binding.buttonSecond.setOnClickListener {
            if (currentPageIndex > 0) {
                currentPageIndex--
                lifecycleScope.launch {
                    displayCurrentPageQuestions(
                        database.questionDao()
                            .getQuestionsByCategory(arguments?.getInt("categoryId")!!)
                    )
                }
            } else {
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            val questions = database.questionDao().getQuestionsByCategory((arguments?.getInt("categoryId")!!))
            displayCurrentPageQuestions(questions)

            binding.nextButton.setOnClickListener {
               currentPageIndex++
                navigateToNextPageOrRestartTimer()
            }
        }

        // Start a global timer after displaying questions
        Handler(Looper.getMainLooper()).postDelayed({
            currentPageIndex++
            navigateToNextPageOrRestartTimer()
        }, TIMER_DELAY.toLong())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun manageTimer() {
        timer?.cancel() // Cancel the current timer if it exists
        timer = CountDownTime(TIMER_DELAY.toLong(), 1000, binding) // Create a new timer
        timer?.start() // Start the timer
    }

    private fun navigateToNextPageOrRestartTimer() {
        lifecycleScope.launch {
            displayCurrentPageQuestions(database.questionDao().getQuestionsByCategory(arguments?.getInt("categoryId")!!))
            manageTimer() // Restart the timer for the new page
        }
    }

    private suspend fun displayCurrentPageQuestions(questions: List<Question>) {
        binding.questionContainer.removeAllViews()

        val startIndex = currentPageIndex * QUESTIONS_PER_PAGE
        val endIndex = minOf(startIndex + QUESTIONS_PER_PAGE, questions.size)

        for (i in startIndex until endIndex) {
            val question = questions[i]

            val questionTextView = TextView(requireContext()).apply {
                text = question.text
                textSize = 18f
            }
            binding.questionContainer.addView(questionTextView)

            val answers = database.answersDao().getAnswersByQuestionId(question.id)
            for ((index, answer) in answers.withIndex()) {
                val answerButton = Button(requireContext()).apply {
                    text = answer.text
                    setOnClickListener {
                        answerSelectedTime[index] = System.currentTimeMillis()
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (System.currentTimeMillis() - answerSelectedTime[index]!! >= TIMER_DELAY) {
                                currentPageIndex++
                                lifecycleScope.launch {
                                    displayCurrentPageQuestions(questions)
                                }
                            }
                        }, TIMER_DELAY.toLong())
                    }
                }
                binding.questionContainer.addView(answerButton)
            }
        }

        binding.nextButton.visibility = if (endIndex < questions.size) View.VISIBLE else View.GONE
    }
}
