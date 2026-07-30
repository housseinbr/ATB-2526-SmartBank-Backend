package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDataResponse {
    private UserResponse user;
    private List<AddressDto> addresses;
    private BankAccountDto bankAccount;
    private List<AdministrativeDataDto> administrativeData;
    private FamilySituationDto familySituation;
    private List<PersonChargeDto> dependents;
    private List<PersonUrgentDto> urgentContacts;
}
