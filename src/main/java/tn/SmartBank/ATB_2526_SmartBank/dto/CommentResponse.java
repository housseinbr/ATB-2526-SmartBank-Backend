package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.entity.Comment;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long idComment;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userRole;
    private String text;
    private LocalDate date;

    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .idComment(comment.getIdComment())
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .userFirstName(comment.getUser() != null ? comment.getUser().getFirstName() : null)
                .userLastName(comment.getUser() != null ? comment.getUser().getLastName() : null)
                .userRole(comment.getUser() != null && comment.getUser().getRole() != null ? comment.getUser().getRole().name() : null)
                .text(comment.getText())
                .date(comment.getDate())
                .build();
    }
}
