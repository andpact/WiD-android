package andpact.project.wid.model

import java.time.LocalDate

data class YearlyWiDListMap(
    val wiDListMap: Map<LocalDate, List<WiD>>
) {
    companion object{
        fun default(): YearlyWiDListMap {
            return YearlyWiDListMap(wiDListMap = emptyMap())
        }
    }
}