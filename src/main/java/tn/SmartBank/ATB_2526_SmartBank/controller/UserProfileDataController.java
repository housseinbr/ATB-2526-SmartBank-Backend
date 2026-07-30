package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.SmartBank.ATB_2526_SmartBank.dto.*;
import tn.SmartBank.ATB_2526_SmartBank.service.UserProfileDataService;

@RestController
@RequestMapping("/api/users/{userId}/profile-data")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR', 'EMPLOYE')")
public class UserProfileDataController {

    private final UserProfileDataService userProfileDataService;

    @GetMapping
    public ResponseEntity<UserProfileDataResponse> getProfileData(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileDataService.getProfileData(userId));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> createAddress(@PathVariable Long userId, @RequestBody AddressDto dto) {
        return ResponseEntity.ok(userProfileDataService.saveAddress(userId, dto));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressDto dto) {
        dto.setIdAddress(addressId);
        return ResponseEntity.ok(userProfileDataService.saveAddress(userId, dto));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        userProfileDataService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bank-account")
    public ResponseEntity<BankAccountDto> createBankAccount(@PathVariable Long userId, @RequestBody BankAccountDto dto) {
        return ResponseEntity.ok(userProfileDataService.saveBankAccount(userId, dto));
    }

    @PostMapping(value = "/bank-account/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BankAccountDto> createBankAccountWithImage(
            @PathVariable Long userId,
            @RequestParam(required = false) String nameBenifice,
            @RequestParam(required = false) String bankTitle,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String compte,
            @RequestParam(required = false) Float controlleChiffre,
            @RequestParam(required = false) String contry,
            @RequestParam(required = false) String documentLink,
            @RequestPart(required = false) MultipartFile documentImage) {
        BankAccountDto dto = BankAccountDto.builder()
                .nameBenifice(nameBenifice)
                .bankTitle(bankTitle)
                .ville(ville)
                .compte(compte)
                .controlleChiffre(controlleChiffre)
                .contry(contry)
                .documentLink(documentLink)
                .build();
        return ResponseEntity.ok(userProfileDataService.saveBankAccount(userId, dto, documentImage));
    }

    @PutMapping("/bank-account")
    public ResponseEntity<BankAccountDto> updateBankAccount(@PathVariable Long userId, @RequestBody BankAccountDto dto) {
        return ResponseEntity.ok(userProfileDataService.saveBankAccount(userId, dto));
    }

    @DeleteMapping("/bank-account")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long userId) {
        userProfileDataService.deleteBankAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/family-situation")
    public ResponseEntity<FamilySituationDto> createFamilySituation(@PathVariable Long userId, @RequestBody FamilySituationDto dto) {
        return ResponseEntity.ok(userProfileDataService.saveFamilySituation(userId, dto));
    }

    @PostMapping(value = "/family-situation/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FamilySituationDto> createFamilySituationWithImage(
            @PathVariable Long userId,
            @RequestParam(required = false) String situation,
            @RequestParam(required = false) String documentUpload,
            @RequestParam(required = false) String documentLink,
            @RequestPart(required = false) MultipartFile documentImage) {
        FamilySituationDto dto = FamilySituationDto.builder()
                .situation(situation)
                .documentUpload(documentUpload)
                .documentLink(documentLink)
                .build();
        return ResponseEntity.ok(userProfileDataService.saveFamilySituation(userId, dto, documentImage));
    }

    @PutMapping("/family-situation")
    public ResponseEntity<FamilySituationDto> updateFamilySituation(@PathVariable Long userId, @RequestBody FamilySituationDto dto) {
        return ResponseEntity.ok(userProfileDataService.saveFamilySituation(userId, dto));
    }

    @DeleteMapping("/family-situation")
    public ResponseEntity<Void> deleteFamilySituation(@PathVariable Long userId) {
        userProfileDataService.deleteFamilySituation(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/administrative-data")
    public ResponseEntity<AdministrativeDataDto> createAdministrativeData(@PathVariable Long userId, @RequestBody AdministrativeDataDto dto) {
        return ResponseEntity.ok(userProfileDataService.saveAdministrativeData(userId, dto));
    }

    @PostMapping(value = "/administrative-data/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdministrativeDataDto> createAdministrativeDataWithImage(
            @PathVariable Long userId,
            @RequestParam(required = false) String situationEmploye,
            @RequestParam(required = false) String cathegorieSituation,
            @RequestParam(required = false) String classification,
            @RequestParam(required = false) String qualification,
            @RequestParam(required = false) String dateInscrit,
            @RequestParam(required = false) String documentLink,
            @RequestPart(required = false) MultipartFile documentImage) {
        AdministrativeDataDto dto = AdministrativeDataDto.builder()
                .situationEmploye(situationEmploye)
                .cathegorieSituation(cathegorieSituation)
                .classification(classification)
                .qualification(qualification)
                .dateInscrit(dateInscrit == null || dateInscrit.isBlank() ? null : java.time.LocalDate.parse(dateInscrit))
                .documentLink(documentLink)
                .build();
        return ResponseEntity.ok(userProfileDataService.saveAdministrativeData(userId, dto, documentImage));
    }

    @PutMapping("/administrative-data/{idAd}")
    public ResponseEntity<AdministrativeDataDto> updateAdministrativeData(
            @PathVariable Long userId,
            @PathVariable Long idAd,
            @RequestBody AdministrativeDataDto dto) {
        dto.setIdAd(idAd);
        return ResponseEntity.ok(userProfileDataService.saveAdministrativeData(userId, dto));
    }

    @DeleteMapping("/administrative-data/{idAd}")
    public ResponseEntity<Void> deleteAdministrativeData(@PathVariable Long userId, @PathVariable Long idAd) {
        userProfileDataService.deleteAdministrativeData(userId, idAd);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dependents")
    public ResponseEntity<PersonChargeDto> createDependent(@PathVariable Long userId, @RequestBody PersonChargeDto dto) {
        return ResponseEntity.ok(userProfileDataService.savePersonCharge(userId, dto));
    }

    @PutMapping("/dependents/{idPerson}")
    public ResponseEntity<PersonChargeDto> updateDependent(
            @PathVariable Long userId,
            @PathVariable Long idPerson,
            @RequestBody PersonChargeDto dto) {
        dto.setIdPerson(idPerson);
        return ResponseEntity.ok(userProfileDataService.savePersonCharge(userId, dto));
    }

    @DeleteMapping("/dependents/{idPerson}")
    public ResponseEntity<Void> deleteDependent(@PathVariable Long userId, @PathVariable Long idPerson) {
        userProfileDataService.deletePersonCharge(userId, idPerson);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/urgent-contacts")
    public ResponseEntity<PersonUrgentDto> createUrgentContact(@PathVariable Long userId, @RequestBody PersonUrgentDto dto) {
        return ResponseEntity.ok(userProfileDataService.savePersonUrgent(userId, dto));
    }

    @PutMapping("/urgent-contacts/{idPerson}")
    public ResponseEntity<PersonUrgentDto> updateUrgentContact(
            @PathVariable Long userId,
            @PathVariable Long idPerson,
            @RequestBody PersonUrgentDto dto) {
        dto.setIdPerson(idPerson);
        return ResponseEntity.ok(userProfileDataService.savePersonUrgent(userId, dto));
    }

    @DeleteMapping("/urgent-contacts/{idPerson}")
    public ResponseEntity<Void> deleteUrgentContact(@PathVariable Long userId, @PathVariable Long idPerson) {
        userProfileDataService.deletePersonUrgent(userId, idPerson);
        return ResponseEntity.noContent().build();
    }
}
