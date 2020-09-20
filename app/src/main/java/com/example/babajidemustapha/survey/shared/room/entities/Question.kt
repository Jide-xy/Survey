package com.example.babajidemustapha.survey.shared.room.entities

import androidx.room.*
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.room.QuestionTypeConverter
import com.example.babajidemustapha.survey.shared.room.StringListConverter
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.io.Serializable
import java.util.*

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */
@Entity(tableName = "QUESTION",
        foreignKeys = [ForeignKey(entity = Survey::class, parentColumns = ["OFFLINE_ID"], childColumns = ["OFFLINE_SURVEY_ID"])])
open class Question() : Serializable {
    @ColumnInfo(name = "OFFLINE_ID")
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @Ignore
    val tempId = UUID.randomUUID().toString()

    @ColumnInfo(name = "ONLINE_ID")
    var onlineId = 0

    @ColumnInfo(name = "Q_NO")
    var questionNo = 0

    @TypeConverters(QuestionTypeConverter::class)
    @ColumnInfo(name = "Q_TYPE")
    var questionType: QuestionType? = null

    @ColumnInfo(name = "OFFLINE_SURVEY_ID", index = true)
    var surveyID = 0

    @ColumnInfo(name = "ONLINE_SURVEY_ID")
    var onlineSurveyID = 0

    @TypeConverters(StringListConverter::class)
    @ColumnInfo(name = "OPTIONS")
    var options: MutableList<String>? = ArrayList()
        set(value) {
            value?.let {
                field?.addAll(it)
            } ?: kotlin.run { field = value }
        }

    @ColumnInfo(name = "MANDATORY")
    var isMandatory = false

    @ColumnInfo(name = "Q_TEXT")
    var questionText: String? = null

    @ColumnInfo(name = "SYNCED")
    var isSynced = false

    @Ignore
    var questionResponse: ResponseDetail? = null


    @Ignore
    constructor(id: Int, questionNo: Int, questionType: QuestionType?, surveyID: Int, options: JSONArray?, mandatory: Boolean, questionText: String?) : this() {
        this.id = id
        this.questionNo = questionNo
        this.questionType = questionType
        this.surveyID = surveyID
        isMandatory = mandatory
        this.questionText = questionText
        try {
            if (options != null) {
                this.options = mutableListOf()
                for (i in 0 until options.length()) {
                    this.options?.add(options.getString(i))
                }
            } else {
                this.options = null
            }
        } catch (e: JSONException) {
            throw e
        }
    }

    fun addOption(option: String) {
        options!!.add(option)
    }

    val optionCount: Int
        get() = options?.size ?: 0

    val multiSelectResponseIndex: List<Int>?
        get() {
            if (questionResponse == null || questionResponse!!.response.isNullOrEmpty()) {
                return null
            }
            val responses: List<String> = ArrayList(Arrays.asList(*Gson().fromJson(questionResponse!!.response, Array<String>::class.java)))
            val indexes: MutableList<Int> = ArrayList()
            for (response in responses) {
                for (i in options!!.indices) {
                    if (options!![i].equals(response, ignoreCase = true)) {
                        indexes.add(i)
                        break
                    }
                }
            }
            return indexes
        }
    val singleOptionResponseIndex: Int
        get() {
            if (questionResponse == null || questionResponse!!.response == null || questionResponse!!.response!!.isEmpty()) {
                return -1
            }
            for (i in options!!.indices) {
                if (options!![i].equals(questionResponse!!.response, ignoreCase = true)) {
                    return i
                }
            }
            return -1
        }
}