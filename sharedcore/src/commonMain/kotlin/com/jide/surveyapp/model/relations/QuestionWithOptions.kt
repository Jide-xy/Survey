package com.jide.surveyapp.model.relations

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question

data class QuestionWithOptions(
        val question: Question = Question(),
        val options: List<Option> = mutableListOf(Option())
)
