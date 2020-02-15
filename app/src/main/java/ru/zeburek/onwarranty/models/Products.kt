package ru.zeburek.onwarranty.models

import com.google.firebase.database.IgnoreExtraProperties

const val WARRANTIES_TBL = "warranties"

@IgnoreExtraProperties
data class Warranty(
    val name: String? = "",
    val startedAt: String? = "",
    val endingAt: String? = ""
)
