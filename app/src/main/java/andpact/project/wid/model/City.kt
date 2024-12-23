package andpact.project.wid.model

enum class City(
    val country: Country, // 국가를 나타내는 프로퍼티가 첫 번째로 옴
    val kr: String,
) {
    BUSAN(country = Country.KOREA, kr = "부산"),
    SEOUL(country = Country.KOREA, kr = "서울"),
    INCHEON(country = Country.KOREA, kr = "인천"),
    DAEGU(country = Country.KOREA, kr = "대구"),
    DAEJEON(country = Country.KOREA, kr = "대전"),
    GWANGJU(country = Country.KOREA, kr = "광주"),
    ULSAN(country = Country.KOREA, kr = "울산"),
    SUWON(country = Country.KOREA, kr = "수원"),
    CHANGWON(country = Country.KOREA, kr = "창원"),
    SEJONG(country = Country.KOREA, kr = "세종"),
    CHEONGJU(country = Country.KOREA, kr = "청주"),
    JEONJU(country = Country.KOREA, kr = "전주"),
    JEJU(country = Country.KOREA, kr = "제주"),
    POHANG(country = Country.KOREA, kr = "포항"),
    GIMHAE(country = Country.KOREA, kr = "김해"),
    YONGIN(country = Country.KOREA, kr = "용인"),
    HWASEONG(country = Country.KOREA, kr = "화성"),
    SEONGNAM(country = Country.KOREA, kr = "성남"),
    GOYANG(country = Country.KOREA, kr = "고양"),
    GWANGMYEONG(country = Country.KOREA, kr = "광명");

    companion object {
        fun filterByCountry(country: Country): List<City> {
            return values().filter { it.country == country }
        }
    }
}