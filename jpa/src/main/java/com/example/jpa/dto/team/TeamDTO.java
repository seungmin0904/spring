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
@Setter
@Getter

public class TeamDTO {
    private long id;
    private String teamName;   
    private String atk;
    private String mf;
    private String df;
    private String gk;
    private String coach; 
}
