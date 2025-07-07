# Holiday Keeper

최근 5 년(2020 ~ 2025) 의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service

# ER 다이어그램

# 의사 선택

Countries와 types를 ElementCollection과 복합 키 중에서 ElementCollection을 사용한 이유   
이것입니다.

- 구현이 단순: 별도의 엔티티 클래스를 만들 필요 없이, 컬렉션(Set, List) 필드만 선언하면 됩니다.
- 삭제 및 갱신이 쉬움: 값 타입 컬렉션은 부모 엔티티(Holiday)가 삭제되면 관련 컬렉션 데이터도 자동으로 삭제됩니다.
  또한, 컬렉션 값이 변경될 때마다 JPA는 해당 holiday의 기존 컬렉션 데이터를 모두 삭제하고, 새로 삽입합니다.
- 조회도 간편: Holiday 엔티티를 조회하면 types 컬렉션도 함께 조회할 수 있습니다.
  타입 기준 조회가 필요할 때는 JPQL이나 네이티브 쿼리로 쉽게 처리할 수 있습니다.# holiday-keeper
