package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Bank_Account;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {
    private Long idAccount;
    private String nameBenifice;
    private String bankTitle;
    private String ville;
    private String compte;
    private Float controlleChiffre;
    private String contry;
    private String documentLink;

    public static BankAccountDto fromEntity(Bank_Account bankAccount) {
        return BankAccountDto.builder()
                .idAccount(bankAccount.getIdAccount())
                .nameBenifice(bankAccount.getNameBenifice())
                .bankTitle(bankAccount.getBankTitle())
                .ville(bankAccount.getVille())
                .compte(bankAccount.getCompte())
                .controlleChiffre(bankAccount.getControlleChiffre())
                .contry(bankAccount.getContry())
                .documentLink(bankAccount.getDocumentLink())
                .build();
    }
}
