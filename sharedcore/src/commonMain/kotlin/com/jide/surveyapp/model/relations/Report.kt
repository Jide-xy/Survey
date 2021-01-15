package com.jide.surveyapp.model.relations

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question

data class Report(
        val question: Question,
        val report: List<Pair<Option, Int>>
)