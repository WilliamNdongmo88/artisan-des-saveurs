package will.dev.artisan_des_saveurs.qrcode.controller;

import will.dev.artisan_des_saveurs.qrcode.dto.PdfUploadResponse;
import will.dev.artisan_des_saveurs.qrcode.entity.PdfMetadata;
import will.dev.artisan_des_saveurs.qrcode.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            PdfMetadata pdfMetadata = pdfService.uploadPdf(file);
            
            PdfUploadResponse response = new PdfUploadResponse(
                    pdfMetadata.getUniqueId(),
                    pdfMetadata.getOriginalFilename(),
                    pdfMetadata.getFileSize(),
                    pdfMetadata.getUploadDate(),
                    "PDF téléchargé avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du téléchargement: " + e.getMessage());
        }
    }

    @GetMapping("/view/{pdfId}")
    public ResponseEntity<Resource> viewPdf(@PathVariable String pdfId) {
        try {
            File pdfFile = pdfService.getPdfFile(pdfId);
            Optional<PdfMetadata> pdfMetadata = pdfService.getPdfMetadata(pdfId);
            
            if (pdfMetadata.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(pdfFile);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + pdfMetadata.get().getOriginalFilename() + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{pdfId}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String pdfId) {
        try {
            File pdfFile = pdfService.getPdfFile(pdfId);
            Optional<PdfMetadata> pdfMetadata = pdfService.getPdfMetadata(pdfId);
            
            if (pdfMetadata.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(pdfFile);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + pdfMetadata.get().getOriginalFilename() + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/info/{pdfId}")
    public ResponseEntity<?> getPdfInfo(@PathVariable String pdfId) {
        Optional<PdfMetadata> pdfMetadata = pdfService.getPdfMetadata(pdfId);
        
        if (pdfMetadata.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(pdfMetadata.get());
    }

    @GetMapping("/text/{pdfId}")
    public ResponseEntity<?> extractTextFromPdf(@PathVariable String pdfId) {
        try {
            String text = pdfService.extractTextFromPdf(pdfId);
            return ResponseEntity.ok().body(text);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'extraction du texte: " + e.getMessage());
        }
    }

    @DeleteMapping("/{pdfId}")
    public ResponseEntity<?> deletePdf(@PathVariable String pdfId) {
        try {
            pdfService.deletePdf(pdfId);
            return ResponseEntity.ok().body("PDF supprimé avec succès");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }
}

