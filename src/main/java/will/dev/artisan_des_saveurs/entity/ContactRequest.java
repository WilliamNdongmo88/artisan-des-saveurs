package will.dev.artisan_des_saveurs.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une demande de contact
 */
@Entity
@Table(name = "contact_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @NotBlank(message = "Le sujet est obligatoire")
    @Size(min = 2, max = 100, message = "Le sujet doit contenir entre 2 et 100 caractères")
    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @NotBlank(message = "Le message est obligatoire")
    @Size(min = 10, max = 2000, message = "Le message doit contenir entre 10 et 2000 caractères")
    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Builder.Default
    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Builder.Default
    @Column(name = "whatsapp_sent", nullable = false)
    private Boolean whatsappSent = false;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @Column(name = "whatsapp_sent_at")
    private LocalDateTime whatsappSentAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * Marque l'email comme envoyé
     */
    public void markEmailSent() {
        this.emailSent = true;
        this.emailSentAt = LocalDateTime.now();
    }

    /**
     * Marque le WhatsApp comme envoyé
     */
    public void markWhatsappSent() {
        this.whatsappSent = true;
        this.whatsappSentAt = LocalDateTime.now();
    }

    /**
     * Obtient un résumé court du message
     */
    public String getMessageSummary() {
        if (message == null) return "";
        return message.length() > 100 ? message.substring(0, 97) + "..." : message;
    }
}


