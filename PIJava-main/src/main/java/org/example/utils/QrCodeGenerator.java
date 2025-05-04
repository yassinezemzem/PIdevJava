package org.example.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class QrCodeGenerator {
    public static File generateQrCode(String text, int id) throws WriterException, IOException{
        int size = 200;
        String fileType = "png";
        File outputFile = new File("qrcode/Therapie_" + id + "." + fileType);
        outputFile.getParentFile().mkdirs();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);
        g.setColor(Color.BLACK);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (matrix.get(x, y)) {
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
        g.dispose();
        ImageIO.write(image, fileType, outputFile);
        return outputFile;
    }
}
