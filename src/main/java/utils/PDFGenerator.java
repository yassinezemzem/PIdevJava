package utils;

import Entities.Evenement;
import Entities.Participation;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class PDFGenerator {

    public static int generateConfirmationPDF(Evenement ev, Participation p) {
        Document document = new Document();
        String fileName = "confirmation_" + System.currentTimeMillis() + ".pdf";
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + fileName;

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Confirmation de participation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Détails participant
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("ID Participant: " + p.getId(), contentFont));
            document.add(new Paragraph("Date: " + p.getDateParticipation(), contentFont));
            document.add(new Paragraph("Nombre personnes: " + p.getNombrePersonnes(), contentFont));
            document.add(Chunk.NEWLINE);

            // Détails événement
            document.add(new Paragraph("Événement: " + ev.getNom(), contentFont));
            document.add(new Paragraph("Lieu: " + ev.getLieu(), contentFont));
            document.add(new Paragraph("Dates: " + ev.getDateDebut() + " au " + ev.getDateFin(), contentFont));
            document.add(new Paragraph("Prix: " + ev.getPrix() + " DT", contentFont));

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Merci pour votre participation!", contentFont));

            return 1; // Succès
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Erreur
        } finally {
            document.close();
        }
    }
}