package andpact.project.wid.model

import java.time.LocalDate

data class Diary(
    val id: Long,
//    val username: String,
    val date: LocalDate,
    val title: String,
    val content: String
)
