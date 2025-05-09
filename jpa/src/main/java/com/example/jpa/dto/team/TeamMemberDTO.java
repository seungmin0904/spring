package com.example.jpa.dto.team;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class TeamMemberDTO {
    private Long id ;
    private String userName;

    // private Team team;
    // DTO는 Entity 객체를 포함할 수 없다.
    // DTO가 엔티티에 의존하게 돼 → 계층 간 의존성 꼬임
    // 엔티티 변경 시 DTO도 영향을 받음
    // 직렬화 이슈 발생 가능 (예: JSON 변환 중 순환 참조 문제)
    // 테스트나 유지보수가 어려워짐
    private Long mno;
    private String teamName;
    private String constreact;
    private String name;
    private int sal;
    private String position;
    private String height;
    private String weight;
    private int age;


}
