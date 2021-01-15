package com.jide.surveyapp.repository.local.utilmodel

import comjidesurveyapp.OptionEntity

data class UtilOptionAndResponse(
        val option: OptionEntity,
        val responseDetailId: String?
)
