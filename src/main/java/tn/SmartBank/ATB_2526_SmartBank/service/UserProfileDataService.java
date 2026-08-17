package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.SmartBank.ATB_2526_SmartBank.Enums.*;
import tn.SmartBank.ATB_2526_SmartBank.dto.*;
import tn.SmartBank.ATB_2526_SmartBank.entity.*;
import tn.SmartBank.ATB_2526_SmartBank.repository.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileDataService {

    private static final Path DOCUMENTS_DIR = Path.of("C:\\Users\\Houssein\\OneDrive\\Documents\\stage doc\\ATB-2526-SmartBank\\ATB-2526-SmartBank-Backend\\src\\main\\java\\tn\\SmartBank\\ATB_2526_SmartBank\\Documents");

    private final UserService userService;
    private final AddressRepository addressRepository;
    private final Bank_AccountRepository bankAccountRepository;
    private final ContratRepository contratRepository;
    private final Donner_AdministratifRepository donnerAdministratifRepository;
    private final Familly_situationRepository famillySituationRepository;
    private final Person_ChargeRepository personChargeRepository;
    private final Person_UrgentRepository personUrgentRepository;

    @Transactional(readOnly = true)
    public UserProfileDataResponse getProfileData(Long userId) {
        User user = userService.getUserById(userId);

        return UserProfileDataResponse.builder()
                .user(UserResponse.fromEntity(user))
                .addresses(addressRepository.findByUser_Id(userId).stream()
                        .sorted(Comparator.comparing(Address::getIdAddress, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .map(AddressDto::fromEntity)
                        .toList())
                .bankAccount(bankAccountRepository.findByUser_Id(userId).map(BankAccountDto::fromEntity).orElse(null))
                .contract(contratRepository.findByUser_Id(userId).map(ContratDto::fromEntity).orElse(null))
                .administrativeData(donnerAdministratifRepository.findByUser_Id(userId).stream()
                        .sorted(Comparator.comparing(Donner_Administratif::getIdAd, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .map(AdministrativeDataDto::fromEntity)
                        .toList())
                .familySituation(famillySituationRepository.findByUser_Id(userId).map(FamilySituationDto::fromEntity).orElse(null))
                .dependents(personChargeRepository.findByUser_Id(userId).stream()
                        .sorted(Comparator.comparing(Person_Charge::getIdPerson, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .map(PersonChargeDto::fromEntity)
                        .toList())
                .urgentContacts(personUrgentRepository.findByUser_Id(userId).stream()
                        .sorted(Comparator.comparing(Person_Urgent::getIdPerson, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .map(PersonUrgentDto::fromEntity)
                        .toList())
                .build();
    }

    public AddressDto saveAddress(Long userId, AddressDto dto) {
        User user = userService.getUserById(userId);
        Address address = dto.getIdAddress() == null
                ? new Address()
                : addressRepository.findById(dto.getIdAddress())
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"));

        address.setUser(user);
        address.setCountry(dto.getCountry());
        address.setVille(dto.getVille());
        address.setGovernment(dto.getGovernment());
        address.setDocumentLink(dto.getDocumentLink());

        return AddressDto.fromEntity(addressRepository.save(address));
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"));
        addressRepository.delete(address);
    }

    public BankAccountDto saveBankAccount(Long userId, BankAccountDto dto) {
        return saveBankAccount(userId, dto, null);
    }

    public BankAccountDto saveBankAccount(Long userId, BankAccountDto dto, MultipartFile documentImage) {
        User user = userService.getUserById(userId);
        Bank_Account bankAccount = bankAccountRepository.findByUser_Id(userId).orElseGet(Bank_Account::new);
        bankAccount.setUser(user);
        bankAccount.setNameBenifice(dto.getNameBenifice());
        bankAccount.setBankTitle(dto.getBankTitle());
        bankAccount.setVille(dto.getVille());
        bankAccount.setCompte(dto.getCompte());
        bankAccount.setControlleChiffre(dto.getControlleChiffre());
        bankAccount.setContry(dto.getContry());
        bankAccount.setDocumentLink(resolveDocumentLink(userId, "bank", documentImage, dto.getDocumentLink(), bankAccount.getDocumentLink()));

        return BankAccountDto.fromEntity(bankAccountRepository.save(bankAccount));
    }

    public void deleteBankAccount(Long userId) {
        bankAccountRepository.findByUser_Id(userId).ifPresent(bankAccountRepository::delete);
    }

    public ContratDto saveContract(Long userId, ContratDto dto) {
        return saveContract(userId, dto, null);
    }

    public ContratDto saveContract(Long userId, ContratDto dto, MultipartFile documentImage) {
        User user = userService.getUserById(userId);
        Contrat contract = contratRepository.findByUser_Id(userId).orElseGet(Contrat::new);
        contract.setUser(user);
        contract.setNature(parseEnum(dto.getNature(), Nature_Contrat.class));
        contract.setTypeContra(parseEnum(dto.getTypeContra(), Type_Contrat.class));
        contract.setDateStart(dto.getDateStart());
        contract.setDateEnd(dto.getDateEnd());
        contract.setTypeTemp(parseEnum(dto.getTypeTemp(), Type_Temp.class));
        contract.setDateAffectation(dto.getDateAffectation());
        contract.setPost(parseEnum(dto.getPost(), Post.class));
        contract.setEmploi(parseEnum(dto.getEmploi(), Emploi.class));
        contract.setTaux(dto.getTaux());
        contract.setLieu(dto.getLieu());
        contract.setDocumentLink(resolveDocumentLink(userId, "contract", documentImage, dto.getDocumentLink(), contract.getDocumentLink()));
        return ContratDto.fromEntity(contratRepository.save(contract));
    }

    public void deleteContract(Long userId) {
        contratRepository.findByUser_Id(userId).ifPresent(contratRepository::delete);
    }

    public FamilySituationDto saveFamilySituation(Long userId, FamilySituationDto dto) {
        return saveFamilySituation(userId, dto, null);
    }

    public FamilySituationDto saveFamilySituation(Long userId, FamilySituationDto dto, MultipartFile documentImage) {
        User user = userService.getUserById(userId);
        Familly_situation familySituation = famillySituationRepository.findByUser_Id(userId).orElseGet(Familly_situation::new);
        familySituation.setUser(user);
        familySituation.setSituation(dto.getSituation());
        familySituation.setDocumentUpload(parseEnum(dto.getDocumentUpload(), SituationFamiliale.class));
        familySituation.setDocumentLink(resolveDocumentLink(userId, "family", documentImage, dto.getDocumentLink(), familySituation.getDocumentLink()));
        return FamilySituationDto.fromEntity(famillySituationRepository.save(familySituation));
    }

    public void deleteFamilySituation(Long userId) {
        famillySituationRepository.findByUser_Id(userId).ifPresent(famillySituationRepository::delete);
    }

    public AdministrativeDataDto saveAdministrativeData(Long userId, AdministrativeDataDto dto) {
        return saveAdministrativeData(userId, dto, null);
    }

    public AdministrativeDataDto saveAdministrativeData(Long userId, AdministrativeDataDto dto, MultipartFile documentImage) {
        User user = userService.getUserById(userId);
        Donner_Administratif adminData = dto.getIdAd() == null
                ? new Donner_Administratif()
                : donnerAdministratifRepository.findById(dto.getIdAd())
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Donnée administrative introuvable"));

        adminData.setUser(user);
        adminData.setSituationEmploye(parseEnum(dto.getSituationEmploye(), SituationEmploye.class));
        adminData.setCathegorieSituation(parseEnum(dto.getCathegorieSituation(), CathegorieSituation.class));
        adminData.setClassification(parseEnum(dto.getClassification(), Classification.class));
        adminData.setQualification(parseEnum(dto.getQualification(), Qualification.class));
        adminData.setDateInscrit(dto.getDateInscrit());
        adminData.setDocumentLink(resolveDocumentLink(userId, "admin", documentImage, dto.getDocumentLink(), adminData.getDocumentLink()));

        return AdministrativeDataDto.fromEntity(donnerAdministratifRepository.save(adminData));
    }

    public void deleteAdministrativeData(Long userId, Long idAd) {
        Donner_Administratif adminData = donnerAdministratifRepository.findById(idAd)
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Donnée administrative introuvable"));
        donnerAdministratifRepository.delete(adminData);
    }

    public PersonChargeDto savePersonCharge(Long userId, PersonChargeDto dto) {
        User user = userService.getUserById(userId);
        Person_Charge charge = dto.getIdPerson() == null
                ? new Person_Charge()
                : personChargeRepository.findById(dto.getIdPerson())
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Personne à charge introuvable"));

        charge.setUser(user);
        charge.setName(dto.getName());
        charge.setLastName(dto.getLastName());
        charge.setRelation(dto.getRelation());
        charge.setNumTel(dto.getNumTel());

        return PersonChargeDto.fromEntity(personChargeRepository.save(charge));
    }

    public void deletePersonCharge(Long userId, Long idPerson) {
        Person_Charge charge = personChargeRepository.findById(idPerson)
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Personne à charge introuvable"));
        personChargeRepository.delete(charge);
    }

    public PersonUrgentDto savePersonUrgent(Long userId, PersonUrgentDto dto) {
        User user = userService.getUserById(userId);
        Person_Urgent urgent = dto.getIdPerson() == null
                ? new Person_Urgent()
                : personUrgentRepository.findById(dto.getIdPerson())
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Personne urgente introuvable"));

        urgent.setUser(user);
        urgent.setName(dto.getName());
        urgent.setLastName(dto.getLastName());
        urgent.setRelation(dto.getRelation());
        urgent.setNumTel(dto.getNumTel());

        return PersonUrgentDto.fromEntity(personUrgentRepository.save(urgent));
    }

    public void deletePersonUrgent(Long userId, Long idPerson) {
        Person_Urgent urgent = personUrgentRepository.findById(idPerson)
                .filter(existing -> existing.getUser() != null && existing.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Personne urgente introuvable"));
        personUrgentRepository.delete(urgent);
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumType) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Enum.valueOf(enumType, value);
    }

    private String resolveDocumentLink(Long userId, String prefix, MultipartFile documentImage, String requestedPath, String existingPath) {
        if (documentImage == null || documentImage.isEmpty()) {
            if (requestedPath != null && !requestedPath.isBlank()) {
                return requestedPath;
            }
            return existingPath;
        }

        try {
            Files.createDirectories(DOCUMENTS_DIR);
            String originalFilename = documentImage.getOriginalFilename();
            String extension = extractExtension(originalFilename);
            String fileName = "user_" + userId + "_" + prefix + "_" + UUID.randomUUID() + extension;
            Path target = DOCUMENTS_DIR.resolve(fileName);
            Files.copy(documentImage.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException exception) {
            throw new RuntimeException("Impossible d'enregistrer l'image du document", exception);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return ".png";
        }
        int index = originalFilename.lastIndexOf('.');
        if (index < 0) {
            return ".png";
        }
        String extension = originalFilename.substring(index).toLowerCase();
        return switch (extension) {
            case ".png", ".jpg", ".jpeg", ".webp" -> extension;
            default -> ".png";
        };
    }
}
