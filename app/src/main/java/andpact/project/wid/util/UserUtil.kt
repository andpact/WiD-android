package andpact.project.wid.util

import kotlin.random.Random

/** 영어권위해서 0 : "부지런한 고양이" 같이 만들어야 하나? */
val tmpNicknameList: List<String> = listOf(
    "부지런한 고양이",
    "활기찬 햇살",
    "즐거운 토끼",
    "웃음 가득한 나비",
    "활동적인 다람쥐",
    "상큼한 레몬",
    "밝은 별빛",
    "행복한 새벽",
    "긍정적인 물결",
    "힘찬 파랑새",
    "활력 넘치는 해바라기"
)

fun getRandomNickname(): String {
    //    Log.d("DataUtil", "getRandomNickname executed")

    return tmpNicknameList[Random.nextInt(tmpNicknameList.size)]
}