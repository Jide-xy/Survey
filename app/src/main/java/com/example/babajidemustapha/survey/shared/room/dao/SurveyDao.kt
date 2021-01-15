package com.example.babajidemustapha.survey.shared.room.dao

//@Dao
//abstract class SurveyDao(val db: SurveyDatabase) {
//    @Transaction
//    open fun createSurveyWithQuestions(survey: Survey, questions: List<Question?>?) {
//        survey.isSynced = false
//        val offlineSurveyId = saveOfflineSurvey(survey)
//        for (question in questions!!) {
//            question!!.surveyID = offlineSurveyId.toInt()
//        }
//        insertQuestions(questions)
//    }
//
//    @get:Transaction
//    open val dataToSync: SurveySyncRequest?
//        get() {
//            val unsyncedSurveyWithEmbeddedQuestionsAndResponsesList: MutableList<UnsyncedSurveyWithEmbeddedQuestionsAndResponses> = ArrayList()
//            val responseHeaderWithEmbeddedResponseDetailsList: List<ResponseHeaderWithEmbeddedResponseDetails> = ArrayList()
//            val surveySyncRequest = SurveySyncRequest()
//            val surveys = unsyncedSurveys
//            for (survey in surveys) {
//                val unsyncedSurveyWithEmbeddedQuestionsAndResponses = UnsyncedSurveyWithEmbeddedQuestionsAndResponses()
//                unsyncedSurveyWithEmbeddedQuestionsAndResponses.survey = survey
//                unsyncedSurveyWithEmbeddedQuestionsAndResponses.questions = getQuestionsForSurveyWithOfflineId(survey.id.toLong())
//                val responseHeaderWithEmbeddedResponseDetailsInnerList: MutableList<ResponseHeaderWithEmbeddedResponseDetails> = ArrayList()
//                val responseHeaders = getResponseHeadersForSurveyWithOfflineId(survey.id.toLong())
//                for (responseHeader in responseHeaders) {
//                    val responseHeaderWithEmbeddedResponseDetails = ResponseHeaderWithEmbeddedResponseDetails()
//                    responseHeaderWithEmbeddedResponseDetails.responseHeader = responseHeader
//                    responseHeaderWithEmbeddedResponseDetails.responseDetails = getResponseDetailsForResponseHeaderWithOfflineId(
//                            responseHeader.responseId.toLong()
//                    )
//                    responseHeaderWithEmbeddedResponseDetailsInnerList.add(responseHeaderWithEmbeddedResponseDetails)
//                }
//                unsyncedSurveyWithEmbeddedQuestionsAndResponses.responses = responseHeaderWithEmbeddedResponseDetailsInnerList
//                unsyncedSurveyWithEmbeddedQuestionsAndResponsesList.add(unsyncedSurveyWithEmbeddedQuestionsAndResponses)
//            }
//            surveySyncRequest.surveys = unsyncedSurveyWithEmbeddedQuestionsAndResponsesList
//            val responseHeaderWithEmbeddedResponseDetailsInnerList: MutableList<ResponseHeaderWithEmbeddedResponseDetails> = ArrayList()
//            val responseHeaders = unsyncedResponseHeaders
//            for (responseHeader in responseHeaders) {
//                val responseHeaderWithEmbeddedResponseDetails = ResponseHeaderWithEmbeddedResponseDetails()
//                responseHeaderWithEmbeddedResponseDetails.responseHeader = responseHeader
//                responseHeaderWithEmbeddedResponseDetails.responseDetails = getResponseDetailsForResponseHeaderWithOfflineId(
//                        responseHeader.responseId.toLong()
//                )
//                responseHeaderWithEmbeddedResponseDetailsInnerList.add(responseHeaderWithEmbeddedResponseDetails)
//            }
//            surveySyncRequest.responses = responseHeaderWithEmbeddedResponseDetailsInnerList
//            return surveySyncRequest
//        }
//
//    @Transaction
//    open fun updateSyncedDataWithOnlineIds(surveySyncRequest: SurveySyncRequest) {
//        for (survey in surveySyncRequest.surveys) {
//            val mSurvey = survey.survey
//            mSurvey!!.isSynced = true
//            saveOfflineSurvey(mSurvey)
//            for (question in survey.questions) {
//                question.isSynced = true
//                question.onlineSurveyID = mSurvey.onlineId
//            }
//            insertQuestions(survey.questions)
//            for (responseHeaderWithEmbeddedResponseDetails in survey.responses) {
//                val responseHeader = responseHeaderWithEmbeddedResponseDetails.responseHeader
//                responseHeader?.isSynced = true
//                responseHeader?.onlineSurveyId = mSurvey.onlineId
//                for (responseDetail in responseHeaderWithEmbeddedResponseDetails.responseDetails) {
//                    responseDetail.isSynced = true
//                    responseDetail.onlineResponseId = responseHeader!!.onlineResponseId
//                }
//                saveResponse(responseHeader, responseHeaderWithEmbeddedResponseDetails.responseDetails)
//            }
//        }
//    }
//
//    @Transaction
//    open fun saveOnlineQuestions(questions: List<Question?>) {
//        for (question in questions) {
//            val offlineSurveyId = getSurveyWithOnlineId(question!!.onlineSurveyID).id.toLong()
//            question.surveyID = offlineSurveyId.toInt()
//            question.isSynced = true
//        }
//        insertQuestions(questions)
//    }
//
//    @Transaction
//    open fun saveOnlineResponseHeaders(responseHeaders: List<ResponseHeader>): LongArray {
//        for (responseHeader in responseHeaders) {
//            val offlineSurveyId = getSurveyWithOnlineId(responseHeader.onlineSurveyId!!).id.toLong()
//            responseHeader.surveyId = offlineSurveyId.toInt()
//            responseHeader.isSynced = true
//        }
//        return insertResponseHeaders(responseHeaders)
//    }
//
//    @Transaction
//    open fun saveOnlineResponseDetails(responseDetails: List<ResponseDetail?>) {
//        for (responseDetail in responseDetails) {
//            val offlineQuestionId = getQuestionWithOnlineId(responseDetail!!.onlineQuestionId).id.toLong()
//            val offlineResponseHeaderId = getResponseHeaderWithOnlineId(responseDetail.onlineResponseId.toLong()).responseId.toLong()
//            responseDetail.questionId = offlineQuestionId.toInt()
//            responseDetail.responseId = offlineResponseHeaderId.toInt()
//            responseDetail.isSynced = true
//        }
//        insertResponseDetails(responseDetails)
//    }
//
//    @Transaction
//    open fun saveResponse(responseHeader: ResponseHeader?, responseDetails: List<ResponseDetail?>?) {
//        val responseHeaderId = saveResponseHeader(responseHeader)
//        for (responseDetail in responseDetails!!) {
//            responseDetail!!.responseId = responseHeaderId.toInt()
//            responseDetail.onlineQuestionId = getQuestionFromOfflineId(responseDetail.questionId.toLong()).onlineId
//            responseDetail.onlineResponseId = getResponseHeaderFromOfflineId(responseHeaderId).onlineResponseId
//        }
//        insertResponseDetails(responseDetails)
//    }
//
//    @Transaction
//    open fun generateReport(survey_id: Int): Map<Question, List<ResponseDetail>> {
//        val report: MutableMap<Question, List<ResponseDetail>> = LinkedHashMap()
//        val questions = getQuestionsForSurveyWithOfflineId(survey_id.toLong())
//        for (question in questions) {
//            val responseDetails = getResponseDetailsWithQuestionId(question.id)
//            report[question] = responseDetails
//        }
//        return report
//    }
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun saveOnlineSurvey(surveys: List<Survey?>?)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun saveOfflineSurvey(survey: Survey?): Long
//
//    @get:Query("SELECT * FROM SURVEY")
//    abstract val allSurveys: List<Survey?>?
//
//    @get:Query("SELECT * FROM SURVEY")
//    @get:Transaction
//    abstract val allSurveysWithResponse: List<SurveyWithResponseHeader?>?
//
//    @Query("SELECT * FROM SURVEY WHERE ONLINE_ID = :online_id")
//    abstract fun getSurveyWithOnlineId(online_id: Int): Survey
//
//    @Query("SELECT * FROM QUESTION WHERE ONLINE_ID = :online_id")
//    abstract fun getQuestionWithOnlineId(online_id: Int): Question
//
//    @Query("SELECT * FROM RESPONSE WHERE ONLINE_ID = :online_id")
//    abstract fun getResponseHeaderWithOnlineId(online_id: Long): ResponseHeader
//
//    @Query("SELECT * FROM SURVEY WHERE NAME LIKE '%' || :query || '%' OR DESCRIPTION LIKE '%' || :query || '%' ")
//    abstract fun searchSurveys(query: String?): MutableList<Survey?>?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insertQuestions(questions: List<Question?>?)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insertResponseHeaders(responseHeaders: List<ResponseHeader>?): LongArray
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insertResponseDetails(responseDetails: List<ResponseDetail?>?)
//
//    //================================================SYNC ASPECT
//    @get:Query("SELECT * FROM SURVEY WHERE SYNCED = 0")
//    abstract val unsyncedSurveys: List<Survey>
//
//    @Query("SELECT * FROM QUESTION WHERE OFFLINE_SURVEY_ID = :id ORDER BY Q_NO ASC ")
//    abstract fun getQuestionsForSurveyWithOfflineId(id: Long): List<Question>
//
//    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = :id AND ONLINE_SURVEY_ID IS NULL")
//    abstract fun getResponseHeadersForSurveyWithOfflineId(id: Long): List<ResponseHeader>
//
//    @Query("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_RESPONSE_ID = :id")
//    abstract fun getResponseDetailsForResponseHeaderWithOfflineId(id: Long): List<ResponseDetail>
//
//    @get:Query("SELECT * FROM RESPONSE WHERE SYNCED = 0 AND ONLINE_SURVEY_ID != 0")
//    abstract val unsyncedResponseHeaders: List<ResponseHeader>
//
//    //================================================REPORT ASPECT
//    @Query("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_QUESTION_ID = :question_id")
//    abstract fun getResponseDetailsWithQuestionId(question_id: Int): List<ResponseDetail>
//
//    //================================================ON LOGIN CACHE DATA ASPECT
//    //REGION START=====================RESPONSE HEADER
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun saveResponseHeader(responseHeader: ResponseHeader?): Long
//
//    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_ID = :id")
//    abstract fun getResponseHeaderFromOfflineId(id: Long): ResponseHeader
//
//    @Query("SELECT * FROM QUESTION WHERE OFFLINE_ID = :id")
//    abstract fun getQuestionFromOfflineId(id: Long): Question
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun saveResponseDetail(responseDetails: List<ResponseDetail?>?): LongArray? //ENDREGION
//}