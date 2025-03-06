package andpact.project.wid.model

enum class SubTitle(
    val kr: String,
//    val en: String,
    val title: Title,
) {
    UNSELECTED_UNTITLED(kr = "선택 안됨", title = Title.UNTITLED),

    // TODO: 같은 소제목이라도 다른 제목에 포함될 수도? 
    
    /** 1. 노동 */
    UNSELECTED_WORK(kr = "선택 안함", title = Title.WORK),

    // 🏢 사무 및 관리직
    OFFICE_WORK(kr = "사무직", title = Title.WORK),
    ACCOUNTING(kr = "회계 및 재무", title = Title.WORK),
    HUMAN_RESOURCES(kr = "인사 및 채용", title = Title.WORK),
    CUSTOMER_SERVICE(kr = "고객 서비스", title = Title.WORK),
    MARKETING(kr = "마케팅 및 광고", title = Title.WORK),

    // 💻 IT 및 기술직
    PROGRAMMING(kr = "프로그래밍", title = Title.WORK),
    IT_SUPPORT(kr = "IT 지원", title = Title.WORK),
    CYBER_SECURITY(kr = "사이버 보안", title = Title.WORK),
    NETWORK_ADMIN(kr = "네트워크 및 서버 관리", title = Title.WORK),
    DATA_SCIENCE(kr = "데이터 과학", title = Title.WORK),
    UX_UI_DESIGN(kr = "UX/UI 디자인", title = Title.WORK),
    GAME_DEVELOPMENT(kr = "게임 개발", title = Title.WORK),

    // 🛒 서비스 및 판매직
    SERVING(kr = "서빙", title = Title.WORK),
    COUNTER(kr = "카운터", title = Title.WORK),
    STORE_MANAGEMENT(kr = "매장 관리", title = Title.WORK),
    SALES(kr = "영업 및 판매", title = Title.WORK),
    REAL_ESTATE(kr = "부동산 중개", title = Title.WORK),
    TOUR_GUIDE(kr = "관광 가이드", title = Title.WORK),

    // 🏭 생산 및 제조업
    FACTORY_WORK(kr = "공장 노동", title = Title.WORK),
    MACHINE_OPERATION(kr = "기계 조작", title = Title.WORK),
    ASSEMBLY_LINE(kr = "조립 생산", title = Title.WORK),
    WELDING(kr = "용접", title = Title.WORK),
    CARPENTRY(kr = "목공", title = Title.WORK),

    // 🚚 운송 및 물류
    TRADING(kr = "트레이딩", title = Title.WORK),
    ERRAND(kr = "심부름", title = Title.WORK),
    DELIVERY(kr = "배달", title = Title.WORK),
    LOGISTICS(kr = "물류 및 창고 관리", title = Title.WORK),
    TAXI_DRIVER(kr = "택시 기사", title = Title.WORK),
    TRUCK_DRIVER(kr = "화물 운송", title = Title.WORK),

    // 🎓 교육 및 연구
    EDUCATION(kr = "교육", title = Title.WORK),
    TUTORING(kr = "과외", title = Title.WORK),
    TRANSLATION(kr = "번역 및 통역", title = Title.WORK),
    RESEARCH(kr = "연구 및 실험", title = Title.WORK),
    LIBRARIAN(kr = "사서", title = Title.WORK),

    // 🏥 의료 및 사회복지
    DOCTOR(kr = "의사", title = Title.WORK),
    NURSE(kr = "간호사", title = Title.WORK),
    PHARMACIST(kr = "약사", title = Title.WORK),
    DENTIST(kr = "치과 의사", title = Title.WORK),
    PHYSICAL_THERAPIST(kr = "물리치료사", title = Title.WORK),
    SOCIAL_WORKER(kr = "사회복지사", title = Title.WORK),
    CHILD_CARE(kr = "보육", title = Title.WORK),

    // 🎭 예술 및 창작
    ACTOR(kr = "배우", title = Title.WORK),
    MUSICIAN(kr = "음악가", title = Title.WORK),
    PAINTER(kr = "화가", title = Title.WORK),
    PHOTOGRAPHER(kr = "사진작가", title = Title.WORK),
    WRITER(kr = "작가", title = Title.WORK),

    // 🌾 농업 및 어업
    FARMING(kr = "농업", title = Title.WORK),
    FISHING(kr = "어업", title = Title.WORK),
    FORESTRY(kr = "임업", title = Title.WORK),

    // 🛠️ 기타
    ETC_WORK(kr = "기타", title = Title.WORK),

    /** 2. 공부 */
    UNSELECTED_STUDY(kr = "선택 안함", title = Title.STUDY),

    // 📚 초·중·고 공통 과목
    MATH(kr = "수학", title = Title.STUDY),
    KOREAN(kr = "국어", title = Title.STUDY),
    ENGLISH(kr = "영어", title = Title.STUDY),
    SOCIAL_STUDIES(kr = "사회", title = Title.STUDY),
    SCIENCE(kr = "과학", title = Title.STUDY),
    HISTORY(kr = "역사", title = Title.STUDY),
    ETHICS(kr = "도덕", title = Title.STUDY),
    MUSIC(kr = "음악", title = Title.STUDY),
    ART(kr = "미술", title = Title.STUDY),
    PHYSICAL_EDUCATION(kr = "체육", title = Title.STUDY),
    TECHNOLOGY_HOME_ECONOMICS(kr = "기술·가정", title = Title.STUDY),
    COMPUTER_SCIENCE(kr = "컴퓨터", title = Title.STUDY),

    // 📖 고등학교 선택 과목
    WORLD_HISTORY(kr = "세계사", title = Title.STUDY),
    GEOGRAPHY(kr = "지리", title = Title.STUDY),
    POLITICAL_SCIENCE(kr = "정치와 법", title = Title.STUDY),
    ENVIRONMENTAL_SCIENCE(kr = "환경 과학", title = Title.STUDY),
    LOGIC_CRITICAL_THINKING(kr = "논리와 비판적 사고", title = Title.STUDY),
    PSYCHOLOGY(kr = "심리학", title = Title.STUDY),

    // 🎓 대학 및 고등 교육 과정
    PHYSICS(kr = "물리학", title = Title.STUDY),
    CHEMISTRY(kr = "화학", title = Title.STUDY),
    BIOLOGY(kr = "생물학", title = Title.STUDY),
    ASTRONOMY(kr = "천문학", title = Title.STUDY),
    EARTH_SCIENCE(kr = "지구과학", title = Title.STUDY),
    PHILOSOPHY(kr = "철학", title = Title.STUDY),
    ECONOMICS(kr = "경제학", title = Title.STUDY),
    BUSINESS_ADMINISTRATION(kr = "경영학", title = Title.STUDY),
    LAW(kr = "법학", title = Title.STUDY),
    MEDICINE(kr = "의학", title = Title.STUDY),
    PHARMACY(kr = "약학", title = Title.STUDY),
    DENTISTRY(kr = "치의학", title = Title.STUDY),
    NURSING(kr = "간호학", title = Title.STUDY),
    VETERINARY_MEDICINE(kr = "수의학", title = Title.STUDY),
    ARCHITECTURE(kr = "건축학", title = Title.STUDY),
    ENGINEERING(kr = "공학", title = Title.STUDY),
    COMPUTER_ENGINEERING(kr = "컴퓨터공학", title = Title.STUDY),
    AI_MACHINE_LEARNING(kr = "인공지능 및 머신러닝", title = Title.STUDY),
//    DATA_SCIENCE(kr = "데이터 과학", title = Title.STUDY),
    STATISTICS(kr = "통계학", title = Title.STUDY),
    SOCIOLOGY(kr = "사회학", title = Title.STUDY),
    CULTURAL_STUDIES(kr = "문화학", title = Title.STUDY),
    MEDIA_COMMUNICATION(kr = "미디어 및 커뮤니케이션", title = Title.STUDY),
//    EDUCATION(kr = "교육학", title = Title.STUDY),
    LANGUAGES_LINGUISTICS(kr = "언어학", title = Title.STUDY),
    ART_HISTORY(kr = "예술사", title = Title.STUDY),
    MUSIC_THEORY(kr = "음악 이론", title = Title.STUDY),
    FILM_STUDIES(kr = "영화학", title = Title.STUDY),
    THEATER_DRAMA(kr = "연극 및 드라마", title = Title.STUDY),

    // 🛠 실용 학문 및 기타
    FINANCE(kr = "금융 및 투자", title = Title.STUDY),
    ENTREPRENEURSHIP(kr = "창업", title = Title.STUDY),
    PROJECT_MANAGEMENT(kr = "프로젝트 관리", title = Title.STUDY),
    GRAPHIC_DESIGN(kr = "그래픽 디자인", title = Title.STUDY),
//    UX_UI_DESIGN(kr = "UX/UI 디자인", title = Title.STUDY),
    GAME_DESIGN(kr = "게임 디자인", title = Title.STUDY),
    FILM_PRODUCTION(kr = "영상 제작", title = Title.STUDY),
    CULINARY_ARTS(kr = "요리학", title = Title.STUDY),
    SPORTS_SCIENCE(kr = "스포츠 과학", title = Title.STUDY),
    FASHION_DESIGN(kr = "패션 디자인", title = Title.STUDY),
    INTERIOR_DESIGN(kr = "인테리어 디자인", title = Title.STUDY),
    ETC_STUDY(kr = "기타", title = Title.STUDY),

    /** 3. 운동 */
    UNSELECTED_EXERCISE(kr = "선택 안함", title = Title.EXERCISE),
    MARATHON(kr = "마라톤", title = Title.EXERCISE),
    WEIGHT_TRAINING(kr = "웨이트 트레이닝", title = Title.EXERCISE),
    YOGA(kr = "요가", title = Title.EXERCISE),
    PILATES(kr = "필라테스", title = Title.EXERCISE),
    HIKING(kr = "등산", title = Title.EXERCISE),
    TRACKING(kr = "트래킹", title = Title.EXERCISE),
    ROCK_CLIMBING(kr = "암벽 등반", title = Title.EXERCISE),
    STRETCHING(kr = "기지개", title = Title.EXERCISE),
    JOGGING(kr = "조깅", title = Title.EXERCISE),
    BADMINTON(kr = "배드민턴", title = Title.EXERCISE),
    SOCCER(kr = "축구", title = Title.EXERCISE),
    BASEBALL(kr = "야구", title = Title.EXERCISE),
    BASKETBALL(kr = "농구", title = Title.EXERCISE),
    ROWING(kr = "조정", title = Title.EXERCISE),
    GOLF(kr = "골프", title = Title.EXERCISE),
    TENNIS(kr = "테니스", title = Title.EXERCISE),
    TABLE_TENNIS(kr = "탁구", title = Title.EXERCISE),
    BOXING(kr = "복싱", title = Title.EXERCISE),
    KICKBOXING(kr = "킥복싱", title = Title.EXERCISE),
    JIU_JITSU(kr = "주짓수", title = Title.EXERCISE),
    JUDO(kr = "유도", title = Title.EXERCISE),
    MMA(kr = "MMA", title = Title.EXERCISE),
    TAEKWONDO(kr = "태권도", title = Title.EXERCISE),
    KENDO(kr = "검도", title = Title.EXERCISE),
    MUAY_THAI(kr = "무에타이", title = Title.EXERCISE),
    CYCLING(kr = "싸이클", title = Title.EXERCISE),
    SWIMMING(kr = "수영", title = Title.EXERCISE),
    ETC_EXERCISE(kr = "기타", title = Title.EXERCISE),

    /** 4. 취미 */
    UNSELECTED_HOBBY(kr = "선택 안함", title = Title.HOBBY),
    GAME(kr = "게임", title = Title.HOBBY),
    DIARY_WRITING(kr = "다이어리 작성", title = Title.HOBBY),
    BOARD_GAMES(kr = "보드게임", title = Title.HOBBY),
    CARD_GAMES(kr = "카드게임", title = Title.HOBBY),
    CHESS(kr = "체스", title = Title.HOBBY),
    GO(kr = "바둑", title = Title.HOBBY),
    GOMOKU(kr = "오목", title = Title.HOBBY),
    // TODO: 드라이브
    // TODO: 드론날리기
    DRAWING(kr = "그림", title = Title.HOBBY),
    CALLIGRAPHY(kr = "캘리그래피", title = Title.HOBBY),
    MUSICAL(kr = "뮤지컬 관람", title = Title.HOBBY),
    SCUBA_DIVING(kr = "스쿠버 다이빙", title = Title.HOBBY),
//    FISHING(kr = "낚시", title = Title.HOBBY),
    MOVIE(kr = "영화 감상", title = Title.HOBBY),
    DRAMA(kr = "드라마 감상", title = Title.HOBBY),
    ANIME(kr = "애니메이션 감상", title = Title.HOBBY),
    DOCUMENTARY(kr = "다큐멘터리 감상", title = Title.HOBBY),
    SOCIAL_NETWORK(kr = "소셜 네트워크", title = Title.HOBBY),
    YOUTUBE(kr = "유튜브", title = Title.HOBBY),
    CAMPING(kr = "캠핑", title = Title.HOBBY),
    SKATEBOARDING(kr = "스케이트보드", title = Title.HOBBY),
    SURFING(kr = "서핑", title = Title.HOBBY),
    KNITTING(kr = "뜨개질", title = Title.HOBBY),
    SEWING(kr = "재봉", title = Title.HOBBY),
    POTTERY(kr = "도자기 만들기", title = Title.HOBBY),
    WOODWORKING(kr = "목공예", title = Title.HOBBY),
    GARDENING(kr = "정원 가꾸기", title = Title.HOBBY),
    MEDITATION(kr = "명상", title = Title.HOBBY),
    SINGING(kr = "노래 부르기", title = Title.HOBBY),
    DANCING(kr = "댄스", title = Title.HOBBY),
    LISTENING_TO_MUSIC(kr = "음악 감상", title = Title.HOBBY),
    READING(kr = "독서", title = Title.HOBBY),
    PHOTOGRAPHY(kr = "사진 촬영", title = Title.HOBBY),
    VIDEOGRAPHY(kr = "영상 제작", title = Title.HOBBY),
    BLOGGING(kr = "블로그 운영", title = Title.HOBBY),
    CROSS_STITCH(kr = "십자수", title = Title.HOBBY),
    PLAYING_INSTRUMENT(kr = "악기연주", title = Title.HOBBY),
    SHOPPING(kr = "쇼핑", title = Title.HOBBY),
    ETC_HOBBY(kr = "기타", title = Title.HOBBY),

    /** 5. 일상 */
    UNSELECTED_DAILY(kr = "선택 안함", title = Title.DAILY),
    SMOKING(kr = "흡연", title = Title.DAILY),
    MAKEUP(kr = "메이크업", title = Title.DAILY),
    SKINCARE(kr = "피부 관리", title = Title.DAILY),
    COOKING(kr = "요리", title = Title.DAILY),
    BAKING(kr = "베이킹", title = Title.DAILY),
    MASTURBATION(kr = "자위", title = Title.DAILY),
    COMMUTE(kr = "출근", title = Title.DAILY),
    LEAVING_WORK(kr = "퇴근", title = Title.DAILY),
    GOING_TO_SCHOOL(kr = "등교", title = Title.DAILY),
    LEAVING_SCHOOL(kr = "하교", title = Title.DAILY),
    MOVING(kr = "이동", title = Title.DAILY),
    TOURISM(kr = "관광", title = Title.DAILY),
    HOMECOMING(kr = "귀성", title = Title.DAILY),
    RETURNING(kr = "귀경", title = Title.DAILY),
    GOING_HOME(kr = "귀가", title = Title.DAILY),
    DRIVING(kr = "운전", title = Title.DAILY),
    ORGANIZING(kr = "정리정돈", title = Title.DAILY),
    HOME_MAINTENANCE(kr = "집 수리", title = Title.DAILY),
    HAIRCUT(kr = "이발", title = Title.DAILY),
    GROCERY_SHOPPING(kr = "장보기", title = Title.DAILY),
    LOVE(kr = "사랑", title = Title.DAILY),
    TEA_TIME(kr = "티타임", title = Title.DAILY),
    DATING(kr = "데이트", title = Title.DAILY),
    FAMILY_TIME(kr = "가족과 시간 보내기", title = Title.DAILY),
    FRIENDS_MEETUP(kr = "친구 만나기", title = Title.DAILY),
    HOSPITAL_VISIT(kr = "병원 방문", title = Title.DAILY),
    NAIL_ART(kr = "네일아트", title = Title.DAILY),
    MASSAGE(kr = "마사지", title = Title.DAILY),
    BANKING(kr = "은행 업무", title = Title.DAILY),
    HAIR_REMOVAL(kr = "제모", title = Title.DAILY),
    PARENTING(kr = "육아", title = Title.DAILY),
    ETC_DAILY(kr = "기타", title = Title.DAILY),

    /** 6. 필수 */
    UNSELECTED_ESSENTIAL(kr = "선택 안함", title = Title.ESSENTIAL),
    NAP(kr = "낮잠", title = Title.ESSENTIAL),
    SLEEP(kr = "수면", title = Title.ESSENTIAL),
    FLOOR_CLEANING(kr = "바닥 청소", title = Title.ESSENTIAL),
    LAUNDRY(kr = "빨래", title = Title.ESSENTIAL),
    WASHING_DISH(kr = "설거지", title = Title.ESSENTIAL),
    BREAKFAST(kr = "아침 식사", title = Title.ESSENTIAL),
    LUNCH(kr = "점심 식사", title = Title.ESSENTIAL),
    DINNER(kr = "저녁 식사", title = Title.ESSENTIAL),
    TOILET(kr = "화장실 이용", title = Title.ESSENTIAL),
    LATE_NIGHT_SNACK(kr = "야식", title = Title.ESSENTIAL),
    SNACK(kr = "간식", title = Title.ESSENTIAL),
    DINING_OUT(kr = "외식", title = Title.ESSENTIAL),
    DELIVERY_FOOD(kr = "배달 음식", title = Title.ESSENTIAL),
    WASHING_FACE(kr = "세수", title = Title.ESSENTIAL),
    RECYCLING(kr = "분리 수거", title = Title.ESSENTIAL),
    WASHING_FEET(kr = "세족", title = Title.ESSENTIAL),
    BATHING(kr = "목욕", title = Title.ESSENTIAL),
    IRONING(kr = "다림질", title = Title.ESSENTIAL),
    WASHING_HAIR(kr = "머리 감기", title = Title.ESSENTIAL),
    SHOWER(kr = "샤워", title = Title.ESSENTIAL),
    ETC_ESSENTIAL(kr = "기타", title = Title.ESSENTIAL),

    /** 7. 기타 */
    UNSELECTED_OTHER(kr = "선택 안함", title = Title.OTHER),
    ETC_OTHER(kr = "기타", title = Title.OTHER);

    companion object {
        fun filterSubTitlesByTitle(targetTitle: Title): List<SubTitle> {
            return values().filter { it.title == targetTitle }
        }

        fun getSubTitleCountByTitle(targetTitle: Title): Int {
            return filterSubTitlesByTitle(targetTitle).size
        }
    }
}