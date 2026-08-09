package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationRequest {
    private String title;
    private String offreFormation;
    private String domain;
    private String theme;
    private Integer duree;
    private String lieu;
    private String unite;
}
