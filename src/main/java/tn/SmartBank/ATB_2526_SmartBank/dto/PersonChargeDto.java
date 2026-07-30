package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Person_Charge;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonChargeDto {
    private Long idPerson;
    private String name;
    private String lastName;
    private String relation;
    private String numTel;

    public static PersonChargeDto fromEntity(Person_Charge charge) {
        return PersonChargeDto.builder()
                .idPerson(charge.getIdPerson())
                .name(charge.getName())
                .lastName(charge.getLastName())
                .relation(charge.getRelation())
                .numTel(charge.getNumTel())
                .build();
    }
}
