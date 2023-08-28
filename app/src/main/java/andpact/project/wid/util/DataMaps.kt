package andpact.project.wid.util

class DataMaps {
    companion object {
        val titleMap = mapOf(
            "STUDY" to "공부",
            "WORK" to "일",
            "READING" to "독서",
            "EXERCISE" to "운동",
            "HOBBY" to "취미",
            "TRAVEL" to "여행",
            "SLEEP" to "수면"
        )

        val colorMap = mapOf(
            "STUDY" to "#FF0000",    // 빨강
            "WORK" to "#FF7F00",     // 주황
            "READING" to "#FFFF00",  // 노랑
            "EXERCISE" to "#007F00", // 연두
            "HOBBY" to "#0000FF",    // 녹색
            "TRAVEL" to "#00007F",   // 남색
            "SLEEP" to "#7F00FF"     // 보라
        )
    }
}