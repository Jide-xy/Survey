package com.jide.surveyapp.model.relations

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question
import com.jide.surveyapp.model.ResponseDetail

data class QuestionWithOptionsAndResponse(
        val question: Question,
        val options: List<Option>,
        var responses: List<ResponseDetail>
)
