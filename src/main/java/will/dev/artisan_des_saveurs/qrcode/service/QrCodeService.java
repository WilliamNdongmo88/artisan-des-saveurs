package will.dev.artisan_des_saveurs.qrcode.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import will.dev.artisan_des_saveurs.qrcode.entity.QrCodeMetadata;
import will.dev.artisan_des_saveurs.qrcode.repository.QrCodeMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrCodeService {

    @Autowired
    private QrCodeMetadataRepository qrCodeMetadataRepository;

    @Autowired
    private PdfService pdfService;

    @Value("${app.qrcode.dir:uploads/qrcodes}")
    private String qrCodeDir;

    @Value("${BASE_URL}")
    private String baseUrl;

    public QrCodeMetadata generateQrCode(String pdfId, String logoPath) throws IOException, WriterException {
        // Vérification que le PDF existe
        if (!pdfService.pdfExists(pdfId)) {
            throw new IllegalArgumentException("PDF non trouvé avec l'id: " + pdfId);
        }

        // Vérification si un QR code existe déjà pour ce PDF
        Optional<QrCodeMetadata> existingQrCode = qrCodeMetadataRepository.findByPdfId(pdfId);
        if (existingQrCode.isPresent()) {
            return existingQrCode.get();
        }

        // Génération d'un ID unique pour le QR code
        String qrCodeId = UUID.randomUUID().toString();

        // URL qui sera encodée dans le QR code (pointe vers l'endpoint de visualisation du PDF)
        String qrContent = baseUrl + "/api/pdf/view/" + pdfId;
        //String qrContent = "https://artisan-des-saveurs-production.up.railway.app/" + "api/pdf/view/" + pdfId;

        // Création du répertoire de QR codes s'il n'existe pas
        Path qrCodePath = Paths.get(qrCodeDir);
        if (!Files.exists(qrCodePath)) {
            Files.createDirectories(qrCodePath);
        }

        // Génération de l'image QR code
        String filename = qrCodeId + ".png";
        Path filePath = qrCodePath.resolve(filename);
        generateQrCodeImage(qrContent, filePath.toString(), 300, 300, logoPath);

        // Création des métadonnées
        QrCodeMetadata qrCodeMetadata = new QrCodeMetadata(
                qrCodeId,
                filePath.toString(),
                pdfId,
                qrContent
        );

        return qrCodeMetadataRepository.save(qrCodeMetadata);
    }

    private void generateQrCodeImage(String text, String filePath, int width, int height, String logoPath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLUE);

        for (int i = 0; i < bitMatrix.getWidth(); i++) {
            for (int j = 0; j < bitMatrix.getHeight(); j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        for (int i = 0; i < bitMatrix.getWidth(); i++) {
            for (int j = 0; j < bitMatrix.getHeight(); j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        if (logoPath != null && !logoPath.isEmpty()) {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                BufferedImage logoImage = ImageIO.read(logoFile);
                if (logoImage != null) {
                    int logoWidth = width / 8; // Taille du logo (ex: 1/4 de la taille du QR code)
                    int logoHeight = height / 8;
                    int x = (width - logoWidth) / 2;
                    int y = (height - logoHeight) / 2;

                    graphics.drawImage(logoImage, x, y, logoWidth, logoHeight, null);
                }
            }
        }

        ImageIO.write(image, "PNG", new File(filePath));
    }

    public Optional<QrCodeMetadata> getQrCodeMetadata(String uniqueId) {
        return qrCodeMetadataRepository.findByUniqueId(uniqueId);
    }

    public File getQrCodeFile(String uniqueId) throws IOException {
        Optional<QrCodeMetadata> qrCodeMetadata = getQrCodeMetadata(uniqueId);
        if (qrCodeMetadata.isEmpty()) {
            throw new IllegalArgumentException("QR Code non trouvé avec l'ID: " + uniqueId);
        }

        File file = new File(qrCodeMetadata.get().getFilePath());
        if (!file.exists()) {
            throw new IOException("Le fichier QR Code n'existe pas sur le disque");
        }

        return file;
    }

    public boolean qrCodeExists(String uniqueId) {
        return qrCodeMetadataRepository.existsByUniqueId(uniqueId);
    }

    public void deleteQrCode(String uniqueId) throws IOException {
        Optional<QrCodeMetadata> qrCodeMetadata = getQrCodeMetadata(uniqueId);
        if (qrCodeMetadata.isPresent()) {
            // Suppression du fichier physique
            File file = new File(qrCodeMetadata.get().getFilePath());
            if (file.exists()) {
                file.delete();
            }
            
            // Suppression des métadonnées
            qrCodeMetadataRepository.delete(qrCodeMetadata.get());
        }
    }
}

