package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Person_Urgent;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonUrgentDto {
    private Long idPerson;
    private String name;
    private String lastName;
    private String relation;
    private String numTel;

    public static PersonUrgentDto fromEntity(Person_Urgent urgent) {
        return PersonUrgentDto.builder()
                .idPerson(urgent.getIdPerson())
                .name(urgent.getName())
                .lastName(urgent.getLastName())
                .relation(urgent.getRelation())
                .numTel(urgent.getNumTel())
                .build();
    }
}
