package com.example.babajidemustapha.survey.shared.room.entities

import androidx.room.*
import java.util.*

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */
@Entity(tableName = "SURVEY")
open class Survey {
    @ColumnInfo(name = "OFFLINE_ID")
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "ONLINE_ID")
    var onlineId = 0

    @ColumnInfo(name = "NAME")
    var name: String? = null

    @ColumnInfo(name = "PRIVACY")
    var isPrivacy = false

    @ColumnInfo(name = "SYNCED")
    var isSynced = true

    @ColumnInfo(name = "USERNAME")
    var username: String? = null

    @ColumnInfo(name = "DATE_CREATED")
    var date: Long = 0

    @Ignore
    var noOfQues = 0
        private set

    @ColumnInfo(name = "DESCRIPTION")
    var desc: String? = null

    @Ignore
    private var questions: MutableList<Question>? = null

    @Ignore
    var deviceToken: String? = null

    constructor()
    constructor(id: Int, name: String?, privacy: Boolean, date: Long, no_of_ques: Int, desc: String?) {
        this.id = id
        this.name = name
        isPrivacy = privacy
        this.date = date
        noOfQues = no_of_ques
        this.desc = desc
        questions = ArrayList()
    }

    constructor(id: Int, name: String?, date: Long, desc: String?, username: String?, device_token: String?) {
        this.id = id
        this.name = name
        this.username = username
        this.deviceToken = device_token
        this.date = date
        this.desc = desc
    }

    fun setNoOFQues(ques: Int) {
        noOfQues = ques
    }
}

data class SurveyWithResponseHeader(
        @Embedded
        val survey: Survey,
        @Relation(parentColumn = "OFFLINE_ID", entityColumn = "OFFLINE_SURVEY_ID")
        val responses: List<ResponseHeader>
) {
    val responseCount: Int
        get() = responses.size
}