package com.example.babajidemustapha.survey.shared.room.entities

import androidx.room.*

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */
@Entity(tableName = "RESPONSE_DETAIL",
        foreignKeys = [ForeignKey(entity = Question::class, parentColumns = ["OFFLINE_ID"], childColumns = ["OFFLINE_QUESTION_ID"]),
            ForeignKey(entity = ResponseHeader::class, parentColumns = ["OFFLINE_ID"], childColumns = ["OFFLINE_RESPONSE_ID"])])
class ResponseDetail() {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OFFLINE_RES_DETAIL_ID")
    var id = 0

    @ColumnInfo(name = "ONLINE_RES_DETAIL_ID")
    var onlineId = 0

    @ColumnInfo(name = "OFFLINE_RESPONSE_ID", index = true)
    var responseId = 0

    @ColumnInfo(name = "ONLINE_RESPONSE_ID")
    var onlineResponseId = 0

    @ColumnInfo(name = "OFFLINE_QUESTION_ID", index = true)
    var questionId = 0

    @ColumnInfo(name = "ONLINE_QUESTION_ID")
    var onlineQuestionId = 0

    @ColumnInfo(name = "RESPONSE")
    var response: String? = null

    @ColumnInfo(name = "SYNCED")
    var isSynced = false

    @Ignore
    constructor(question_id: Int, response: String?) : this() {
        this.response = response
        this.questionId = question_id
    }

    @Ignore
    constructor(id: Int, question_id: Int, response: String?) : this() {
        this.id = id
        this.response = response
        this.questionId = question_id
    }
}