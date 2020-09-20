package com.example.babajidemustapha.survey.shared.room.entities

import androidx.room.*

/**
 * Created by Babajide Mustapha on 9/22/2017.
 */
@Entity(tableName = "RESPONSE",
        foreignKeys = [ForeignKey(entity = Survey::class, parentColumns = ["OFFLINE_ID"], childColumns = ["OFFLINE_SURVEY_ID"])])
class ResponseHeader {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OFFLINE_ID")
    var responseId = 0

    @ColumnInfo(name = "ONLINE_ID")
    var onlineResponseId = 0

    @ColumnInfo(name = "RESPONDENT_NAME")
    var respondentName: String? = null

    @ColumnInfo(name = "RESPONSE_DATE")
    var date: Long = 0

    @Ignore
    var username: String? = null

    @ColumnInfo(name = "SYNCED")
    var isSynced = false

    @ColumnInfo(name = "OFFLINE_SURVEY_ID", index = true)
    var surveyId = 0

    @ColumnInfo(name = "ONLINE_SURVEY_ID")
    var onlineSurveyId: Int? = null

    constructor()

    @Ignore
    constructor(survey_id: Int, respondentName: String?, date: Long) {
        this.respondentName = respondentName
        this.date = date
        this.surveyId = survey_id
    }

    @Ignore
    constructor(response_id: Int, survey_id: Int, respondentName: String?, date: Long) {
        this.responseId = response_id
        this.respondentName = respondentName
        this.date = date
        this.surveyId = survey_id
    }
}