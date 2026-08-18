package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_Demande;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Reconnaissance;
import tn.SmartBank.ATB_2526_SmartBank.entity.Document_Reconnaissance;
import tn.SmartBank.ATB_2526_SmartBank.entity.Contrat;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.Demande_ReconnaissanceRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.Document_ReconnaissanceRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.ContratRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeReconnaissanceService {

    private static final Path DOCUMENTS_DIR = Path.of(System.getProperty("user.home"), "ATB-2526-SmartBank-Documents");

    private final Demande_ReconnaissanceRepository demandeRepository;
    private final Document_ReconnaissanceRepository documentRepository;
    private final ContratRepository contratRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Demande_Reconnaissance> getMyRequests(Long userId) {
        return demandeRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Demande_Reconnaissance> getManagedRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeRepository.findAll().stream()
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            // Le superviseur doit voir les demandes de son équipe, mais aussi ses propres
            // demandes lorsqu'un administrateur les traite.
            List<Demande_Reconnaissance> requests = new ArrayList<>(
                    demandeRepository.findByUser_Superviseur_IdOrderByDateDesc(actorId)
            );
            requests.addAll(demandeRepository.findByUser_IdOrderByDateDesc(actorId));
            return requests.stream()
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        }
        return demandeRepository.findByUser_IdOrderByDateDesc(actorId);
    }

    @Transactional(readOnly = true)
    public List<Demande_Reconnaissance> getManagedPendingRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeRepository.findAll().stream()
                    .filter(item -> item.getStatus() == Status.EN_ATTENTE)
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeRepository.findByUser_Superviseur_IdAndStatusOrderByDateDesc(actorId, Status.EN_ATTENTE);
        }
        return demandeRepository.findByUser_IdOrderByDateDesc(actorId).stream()
                .filter(item -> item.getStatus() == Status.EN_ATTENTE)
                .toList();
    }

    public Demande_Reconnaissance create(Long userId, Type_Demande type, String motif) {
        User user = userService.getUserById(userId);
        Demande_Reconnaissance demande = Demande_Reconnaissance.builder()
                .user(user)
                .superviseur(user.getSuperviseur())
                .type(type)
                .motif(motif)
                .status(Status.EN_ATTENTE)
                .date(LocalDate.now())
                .build();
        Demande_Reconnaissance saved = demandeRepository.save(demande);
        if (user.getSuperviseur() != null) {
            notificationService.create(
                    user.getSuperviseur(),
                    "Nouvelle demande de reconnaissance",
                    user.getFirstName() + " " + user.getLastName() + " a soumis une demande " + type.name().toLowerCase() + "."
            );
        }
        return saved;
    }

    public Demande_Reconnaissance decide(Long id, Long actorId, Status decision) {
        if (decision != Status.VALIDE && decision != Status.REFUSE) {
            throw new IllegalArgumentException("Décision invalide");
        }
        User actor = userService.getUserById(actorId);
        Demande_Reconnaissance demande = demandeRepository.findByIdDemandeReconnaissance(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        ensureCanManage(actor, demande.getUser());
        if (demande.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }
        demande.setStatus(decision);
        if (decision == Status.VALIDE) {
            demande.setPdfLink(generatePdf(demande));
            notificationService.create(
                    demande.getUser(),
                    "Demande validée",
                    "Votre demande de " + demande.getType().name().toLowerCase() + " a été validée et le document a été généré."
            );
        } else {
            notificationService.create(
                    demande.getUser(),
                    "Demande refusée",
                    "Votre demande de " + demande.getType().name().toLowerCase() + " a été refusée."
            );
        }
        return demandeRepository.save(demande);
    }

    @Transactional(readOnly = true)
    public Document_Reconnaissance getGeneratedDocument(Long demandeId) {
        return documentRepository.findByDemande_IdDemandeReconnaissance(demandeId)
                .orElseThrow(() -> new RuntimeException("Document généré introuvable"));
    }

    private void ensureCanManage(User actor, User target) {
        if (actor.getRole() == Role.ADMIN) return;
        if (actor.getRole() != Role.SUPERVISEUR) throw new AccessDeniedException("Accès refusé");
        if (target.getSuperviseur() == null || !target.getSuperviseur().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Vous ne pouvez traiter que les demandes de votre équipe");
        }
    }

    private String generatePdf(Demande_Reconnaissance demande) {
        try {
            Files.createDirectories(DOCUMENTS_DIR);
            String fileName = "reconnaissance_" + demande.getIdDemandeReconnaissance() + "_" + UUID.randomUUID() + ".pdf";
            Path file = DOCUMENTS_DIR.resolve(fileName);
            Files.write(file, buildPdfBytes(demande));

            Document_Reconnaissance document = Document_Reconnaissance.builder()
                    .demande(demande)
                    .pdfLink(file.toString())
                    .generatedAt(LocalDateTime.now())
                    .build();
            documentRepository.save(document);
            return file.toString();
        } catch (IOException exception) {
            throw new RuntimeException("Impossible de générer le PDF", exception);
        }
    }

    private byte[] buildPdfBytes(Demande_Reconnaissance demande) {
        String employee = demande.getUser().getFirstName() + " " + demande.getUser().getLastName();
        User supervisor = demande.getUser().getSuperviseur();
        Contrat contract = contratRepository.findByUser_Id(demande.getUser().getId()).orElse(null);
        boolean attestation = demande.getType() == Type_Demande.ATTESTATION;
        String title = attestation ? "ATTESTATION DE TRAVAIL" : "BADGE PROFESSIONNEL";
        String reference = "Reference : " + demande.getIdDemandeReconnaissance() + " / " + LocalDate.now();
        String introductoryText = attestation
                ? "La Banque ATB atteste que " + employee + " est collaborateur(trice) de notre etablissement."
                : "Ce badge identifie " + employee + " comme collaborateur(trice) de la Banque ATB.";
        String purpose = demande.getMotif() == null || demande.getMotif().isBlank()
                ? "Document genere a la demande du collaborateur."
                : "Objet : " + demande.getMotif();
        String employeeDetails = "Matricule/CIN : " + nullSafe(demande.getUser().getCin())
                + "   |   Email : " + nullSafe(demande.getUser().getEmail());
        String jobDetails = contract == null ? "Fonction : Non renseignee"
                : "Fonction : " + nullSafe(contract.getPost() == null ? null : contract.getPost().name())
                + "   |   Entite : " + nullSafe(contract.getLieu());
        String supervisorDetails = supervisor == null ? "Superviseur : Non renseigne"
                : "Superviseur : " + nullSafe(supervisor.getFirstName()) + " " + nullSafe(supervisor.getLastName())
                + "   |   " + nullSafe(supervisor.getEmail());
        List<String> objects = new ArrayList<>();
        String content = String.join("\n",
                "q 0.63 0.08 0.13 rg 0 792 595 50 re f Q",
                "BT /F2 18 Tf 50 810 Td (ATB | Arab Tunisian Bank) Tj ET",
                "q 0.63 0.08 0.13 rg 50 730 495 2 re f Q",
                "BT /F2 22 Tf 50 690 Td (" + escape(title) + ") Tj ET",
                "BT /F1 10 Tf 50 662 Td (" + escape(reference) + ") Tj ET",
                "BT /F1 12 Tf 50 590 Td (" + escape(introductoryText) + ") Tj ET",
                "BT /F1 12 Tf 50 560 Td (" + escape(purpose) + ") Tj ET",
                "BT /F2 11 Tf 50 510 Td (Informations du collaborateur) Tj ET",
                "BT /F1 10 Tf 50 490 Td (" + escape(employeeDetails) + ") Tj ET",
                "BT /F1 10 Tf 50 472 Td (" + escape(jobDetails) + ") Tj ET",
                "BT /F1 10 Tf 50 454 Td (" + escape(supervisorDetails) + ") Tj ET",
                "BT /F1 11 Tf 50 410 Td (Ce document est delivre pour servir et valoir ce que de droit.) Tj ET",
                "q 0.90 0.90 0.90 rg 50 220 495 1 re f Q",
                "BT /F1 10 Tf 50 195 Td (Fait a Tunis, le " + LocalDate.now() + ") Tj ET",
                "BT /F2 11 Tf 365 145 Td (Direction des Ressources Humaines) Tj ET",
                "BT /F1 9 Tf 50 70 Td (Document genere par SmartBank - ATB. Verification interne requise.) Tj ET");
        objects.add("1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n");
        objects.add("2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n");
        objects.add("3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> /Contents 4 0 R >>endobj\n");
        objects.add("4 0 obj<< /Length " + content.getBytes(StandardCharsets.US_ASCII).length + " >>stream\n" + content + "\nendstream\nendobj\n");
        objects.add("5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n");
        objects.add("6 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>endobj\n");
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (String object : objects) {
            offsets.add(pdf.length());
            pdf.append(object);
        }
        int xrefPos = pdf.length();
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n");
        pdf.append(String.format("%010d 65535 f \n", 0));
        for (int i = 1; i < offsets.size(); i++) {
            pdf.append(String.format("%010d 00000 n \n", offsets.get(i)));
        }
        pdf.append("trailer<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefPos).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.US_ASCII);
    }

    private String escape(String value) {
        return value.replaceAll("[^\\x20-\\x7E]", " ")
                .replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Non renseigne" : value;
    }
}
