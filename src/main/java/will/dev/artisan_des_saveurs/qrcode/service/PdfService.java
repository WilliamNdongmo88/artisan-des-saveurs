package will.dev.artisan_des_saveurs.qrcode.service;

import will.dev.artisan_des_saveurs.qrcode.entity.PdfMetadata;
import will.dev.artisan_des_saveurs.qrcode.repository.PdfMetadataRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class PdfService {

    @Autowired
    private PdfMetadataRepository pdfMetadataRepository;

    @Value("${app.upload.dir:uploads/pdfs}")
    private String uploadDir;

    public PdfMetadata uploadPdf(MultipartFile file) throws IOException {
        // Validation du fichier
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Le fichier doit être un PDF");
        }

        // Génération d'un ID unique
        String uniqueId = UUID.randomUUID().toString();

        // Création du répertoire de téléchargement s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sauvegarde du fichier
        String filename = uniqueId + ".pdf";
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // Création des métadonnées
        PdfMetadata pdfMetadata = new PdfMetadata(
                uniqueId,
                file.getOriginalFilename(),
                filePath.toString(),
                file.getSize(),
                file.getContentType()
        );

        return pdfMetadataRepository.save(pdfMetadata);
    }

    public Optional<PdfMetadata> getPdfMetadata(String uniqueId) {
        return pdfMetadataRepository.findByUniqueId(uniqueId);
    }

    public File getPdfFile(String uniqueId) throws IOException {
        Optional<PdfMetadata> pdfMetadata = getPdfMetadata(uniqueId);
        if (pdfMetadata.isEmpty()) {
            throw new IllegalArgumentException("PDF non trouvé avec l'ID: " + uniqueId);
        }

        File file = new File(pdfMetadata.get().getFilePath());
        if (!file.exists()) {
            throw new IOException("Le fichier PDF n'existe pas sur le disque");
        }

        return file;
    }

    public String extractTextFromPdf(String uniqueId) throws IOException {
        File pdfFile = getPdfFile(uniqueId);
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public boolean pdfExists(String uniqueId) {
        return pdfMetadataRepository.existsByUniqueId(uniqueId);
    }

    public void deletePdf(String uniqueId) throws IOException {
        Optional<PdfMetadata> pdfMetadata = getPdfMetadata(uniqueId);
        if (pdfMetadata.isPresent()) {
            // Suppression du fichier physique
            File file = new File(pdfMetadata.get().getFilePath());
            if (file.exists()) {
                file.delete();
            }
            
            // Suppression des métadonnées
            pdfMetadataRepository.delete(pdfMetadata.get());
        }
    }
}

