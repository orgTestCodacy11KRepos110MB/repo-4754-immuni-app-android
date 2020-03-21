package org.ascolto.onlus.geocrowd19.android.models

import org.ascolto.onlus.geocrowd19.android.db.entity.Gender
import org.ascolto.onlus.geocrowd19.android.models.survey.TriageStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
class User(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "householder") val isMain: Boolean,
    @field:Json(name = "age") val age: Int,
    @field:Json(name = "gender") val gender: Gender,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "last_survey_at") val lastSurveyDate: Date,
    @field:Json(name = "last_survey_version") val lastSurveyVersion: String,
    @field:Json(name = "last_triage_status") val lastTriageStatus: TriageStatus,
    @field:Json(name = "next_survey_at") val nextSurveyDate: Date
)