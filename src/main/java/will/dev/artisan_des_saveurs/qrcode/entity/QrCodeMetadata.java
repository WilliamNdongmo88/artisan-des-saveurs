package will.dev.artisan_des_saveurs.qrcode.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qrcode_metadata")
public class QrCodeMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", unique = true, nullable = false)
    private String uniqueId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "pdf_id", nullable = false)
    private String pdfId;

    @Column(name = "qr_content", nullable = false, length = 1000)
    private String qrContent;

    @Column(name = "generation_date")
    private LocalDateTime generationDate;

    @Column(name = "image_format")
    private String imageFormat;

    @Column(name = "image_size")
    private Integer imageSize;

    // Constructeurs
    public QrCodeMetadata() {
        this.generationDate = LocalDateTime.now();
        this.imageFormat = "PNG";
        this.imageSize = 300;
    }

    public QrCodeMetadata(String uniqueId, String filePath, String pdfId, String qrContent) {
        this();
        this.uniqueId = uniqueId;
        this.filePath = filePath;
        this.pdfId = pdfId;
        this.qrContent = qrContent;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPdfId() {
        return pdfId;
    }

    public void setPdfId(String pdfId) {
        this.pdfId = pdfId;
    }

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public LocalDateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(LocalDateTime generationDate) {
        this.generationDate = generationDate;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public Integer getImageSize() {
        return imageSize;
    }

    public void setImageSize(Integer imageSize) {
        this.imageSize = imageSize;
    }
}

