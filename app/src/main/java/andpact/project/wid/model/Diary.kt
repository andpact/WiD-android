package andpact.project.wid.model

import java.time.LocalDate

data class Diary(
    val id: Long,
    val date: LocalDate,
    val title: String,
    val content: String
)
