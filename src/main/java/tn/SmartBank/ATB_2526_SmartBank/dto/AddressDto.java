package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Address;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long idAddress;
    private String country;
    private String ville;
    private String government;
    private String documentLink;

    public static AddressDto fromEntity(Address address) {
        return AddressDto.builder()
                .idAddress(address.getIdAddress())
                .country(address.getCountry())
                .ville(address.getVille())
                .government(address.getGovernment())
                .documentLink(address.getDocumentLink())
                .build();
    }
}
