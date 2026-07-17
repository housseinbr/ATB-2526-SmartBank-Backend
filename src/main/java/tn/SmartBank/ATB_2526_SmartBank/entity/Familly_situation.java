package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.SituationFamiliale;

@Entity
@Table(name = "familly_situation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Familly_situation {

    // One-to-one with User: Id_User acts as PK in the diagram
    @Id
    @Column(name = "id_user")
    private Long idUser;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_user")
    private User user;

    // Enum in diagram - e.g. "CELIBATAIRE" / "MARIE" / "DIVORCE"
    private String situation;

    @Column(name = "document_upload")
    @Enumerated(EnumType.STRING)
    private SituationFamiliale documentUpload;
}
