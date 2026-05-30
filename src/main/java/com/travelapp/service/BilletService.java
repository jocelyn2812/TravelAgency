package com.travelapp.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.travelapp.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BilletService {

    private final ReservationService reservationService;

    private static final Color ROUGE =
        new Color(233, 69, 96);
    private static final Color BLEU =
        new Color(26, 26, 46);
    private static final Color GRIS =
        new Color(248, 249, 250);
    private static final Color BLANC =
        Color.WHITE;
    private static final Color VERT =
        new Color(40, 167, 69);
    private static final Color TEXTE =
        new Color(60, 60, 60);

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererBillet(Long reservationId)
            throws Exception {

        Reservation r = reservationService
            .findById(reservationId)
            .orElseThrow(() ->
                new RuntimeException(
                    "Réservation introuvable"));

        ByteArrayOutputStream out =
            new ByteArrayOutputStream();

        // Format A5 paysage — compact comme un billet
        Document doc = new Document(
            new Rectangle(595, 350));

        PdfWriter writer =
            PdfWriter.getInstance(doc, out);
        doc.setMargins(0, 0, 0, 0);
        doc.open();

        PdfContentByte cb =
            writer.getDirectContent();

        // ===== FOND GLOBAL =====
        cb.setColorFill(BLANC);
        cb.rectangle(0, 0, 595, 350);
        cb.fill();

        // ===== BANDE GAUCHE ROUGE =====
        cb.setColorFill(ROUGE);
        cb.rectangle(0, 0, 160, 350);
        cb.fill();

        // ===== BANDE BLEUE EN HAUT =====
        cb.setColorFill(BLEU);
        cb.rectangle(160, 310, 435, 40);
        cb.fill();

        // ===== LIGNE POINTILLÉE =====
        cb.setColorStroke(new Color(200, 200, 200));
        cb.setLineDash(4, 4);
        cb.moveTo(160, 0);
        cb.lineTo(160, 350);
        cb.stroke();

        // ===== CONTENU PARTIE GAUCHE =====
        // Titre agence
        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(10, 200, 150, 340);

        Font fAgence = new Font(
            Font.HELVETICA, 14, Font.BOLD, BLANC);
        Font fAgenceSub = new Font(
            Font.HELVETICA, 8, Font.NORMAL,
            new Color(255, 200, 200));

        Paragraph agence =
            new Paragraph("TRAVEL\nAGENCY", fAgence);
        agence.setAlignment(Element.ALIGN_CENTER);
        ct.addElement(agence);

        Paragraph agenceSub =
            new Paragraph("✈ Agence de voyages",
                fAgenceSub);
        agenceSub.setAlignment(Element.ALIGN_CENTER);
        ct.addElement(agenceSub);
        ct.go();

        // Numéro billet
        String numBillet = String.format(
            "TRV-%d-%04d",
            java.time.LocalDate.now().getYear(),
            r.getId());

        ColumnText ctNum =
            new ColumnText(cb);
        ctNum.setSimpleColumn(10, 80, 150, 200);

        Font fNum = new Font(
            Font.HELVETICA, 9, Font.BOLD, BLANC);
        Font fNumVal = new Font(
            Font.HELVETICA, 11, Font.BOLD,
            new Color(255, 220, 100));

        Paragraph pNum =
            new Paragraph("N° BILLET", fNum);
        pNum.setAlignment(Element.ALIGN_CENTER);
        ctNum.addElement(pNum);

        Paragraph pNumVal =
            new Paragraph(numBillet, fNumVal);
        pNumVal.setAlignment(Element.ALIGN_CENTER);
        ctNum.addElement(pNumVal);
        ctNum.go();

        // Date émission
        ColumnText ctDate =
            new ColumnText(cb);
        ctDate.setSimpleColumn(10, 10, 150, 75);

        Font fDate = new Font(
            Font.HELVETICA, 8, Font.NORMAL,
            new Color(255, 200, 200));
        Font fDateVal = new Font(
            Font.HELVETICA, 9, Font.BOLD, BLANC);

        Paragraph pDate =
            new Paragraph("Émis le", fDate);
        pDate.setAlignment(Element.ALIGN_CENTER);
        ctDate.addElement(pDate);

        Paragraph pDateVal = new Paragraph(
            java.time.LocalDate.now().format(FMT),
            fDateVal);
        pDateVal.setAlignment(Element.ALIGN_CENTER);
        ctDate.addElement(pDateVal);
        ctDate.go();

        // ===== TITRE HEADER DROITE =====
        ColumnText ctHeader =
            new ColumnText(cb);
        ctHeader.setSimpleColumn(170, 315, 590, 348);

        Font fHeader = new Font(
            Font.HELVETICA, 13, Font.BOLD, BLANC);
        String titreVoyage = r.getVoyage() != null
            && r.getVoyage().getTitre() != null
            ? r.getVoyage().getTitre().toUpperCase()
            : "VOYAGE";

        Paragraph pHeader =
            new Paragraph(titreVoyage, fHeader);
        pHeader.setAlignment(Element.ALIGN_LEFT);
        ctHeader.addElement(pHeader);
        ctHeader.go();

        // ===== CONTENU PRINCIPAL =====
        // Colonne 1 — Client
        ColumnText ctClient =
            new ColumnText(cb);
        ctClient.setSimpleColumn(170, 150, 340, 305);

        Font fSection = new Font(
            Font.HELVETICA, 8, Font.BOLD, ROUGE);
        Font fLabel = new Font(
            Font.HELVETICA, 8, Font.BOLD, BLEU);
        Font fValue = new Font(
            Font.HELVETICA, 9, Font.NORMAL, TEXTE);
        Font fValueBold = new Font(
            Font.HELVETICA, 10, Font.BOLD, BLEU);

        Paragraph pClientSec =
            new Paragraph("PASSAGER", fSection);
        ctClient.addElement(pClientSec);
        ctClient.addElement(new Paragraph(" "));

        if (r.getClient() != null) {
            ctClient.addElement(new Paragraph(
                r.getClient().getNom().toUpperCase()
                + " "
                + r.getClient().getPrenom()
                    .toUpperCase(),
                fValueBold));

            if (r.getClient().getTelephone() != null) {
                ctClient.addElement(new Paragraph(
                    "📞 "
                    + r.getClient().getTelephone(),
                    fValue));
            }

            if (r.getClient().getNumeroPasseport()
                    != null) {
                ctClient.addElement(new Paragraph(
                    "🪪 "
                    + r.getClient()
                        .getNumeroPasseport(),
                    fValue));
            }

            if (r.getClient().getNationalite()
                    != null) {
                ctClient.addElement(new Paragraph(
                    "🌍 "
                    + r.getClient().getNationalite(),
                    fValue));
            }
        }

        ctClient.addElement(new Paragraph(" "));
        Paragraph pPartSec = new Paragraph(
            "PARTICIPANTS", fSection);
        ctClient.addElement(pPartSec);

        ctClient.addElement(new Paragraph(
            "Adultes : "
            + (r.getNombreAdultes() != null
               ? r.getNombreAdultes() : 0),
            fValue));
        ctClient.addElement(new Paragraph(
            "Enfants : "
            + (r.getNombreEnfants() != null
               ? r.getNombreEnfants() : 0),
            fValue));
        ctClient.go();

        // Colonne 2 — Voyage
        ColumnText ctVoyage =
            new ColumnText(cb);
        ctVoyage.setSimpleColumn(340, 150, 510, 305);

        Paragraph pVoyageSec =
            new Paragraph("VOYAGE", fSection);
        ctVoyage.addElement(pVoyageSec);
        ctVoyage.addElement(new Paragraph(" "));

        if (r.getVoyage() != null) {
            if (r.getVoyage().getDestination()
                    != null) {
                ctVoyage.addElement(
                    new Paragraph(
                        "📍 "
                        + r.getVoyage()
                            .getDestination()
                            .getVille()
                        + ", "
                        + r.getVoyage()
                            .getDestination()
                            .getPays(),
                        fValueBold));
            }

            if (r.getVoyage().getDateDepart()
                    != null) {
                ctVoyage.addElement(
                    new Paragraph(
                        "🛫 Départ : "
                        + r.getVoyage()
                            .getDateDepart()
                            .format(FMT),
                        fValue));
            }

            if (r.getVoyage().getDateRetour()
                    != null) {
                ctVoyage.addElement(
                    new Paragraph(
                        "🛬 Retour : "
                        + r.getVoyage()
                            .getDateRetour()
                            .format(FMT),
                        fValue));
            }

            if (r.getVoyage().getDateDepart() != null
                    && r.getVoyage().getDateRetour()
                       != null) {
                long duree =
                    java.time.temporal.ChronoUnit
                        .DAYS.between(
                            r.getVoyage()
                                .getDateDepart(),
                            r.getVoyage()
                                .getDateRetour());
                ctVoyage.addElement(
                    new Paragraph(
                        "🌙 Durée : "
                        + duree + " nuits",
                        fValue));
            }

            if (r.getVoyage().getType() != null) {
                ctVoyage.addElement(
                    new Paragraph(
                        "🏷️ "
                        + r.getVoyage()
                            .getType().name(),
                        fValue));
            }
        }
        ctVoyage.go();

        // ===== BANDE BAS — PAIEMENT =====
        cb.setColorFill(GRIS);
        cb.rectangle(160, 0, 435, 145);
        cb.fill();

        cb.setColorFill(BLEU);
        cb.rectangle(160, 0, 435, 30);
        cb.fill();

        // Montants
        ColumnText ctPay =
            new ColumnText(cb);
        ctPay.setSimpleColumn(170, 35, 510, 145);

        Paragraph pPaySec =
            new Paragraph("PAIEMENT", fSection);
        ctPay.addElement(pPaySec);
        ctPay.addElement(new Paragraph(" "));

        // Tableau paiement compact
        PdfPTable tPay = new PdfPTable(3);
        tPay.setWidthPercentage(100);
        tPay.setWidths(new float[]{1f, 1f, 1f});

        Font fColHead = new Font(
            Font.HELVETICA, 8, Font.BOLD, ROUGE);
        Font fColVal = new Font(
            Font.HELVETICA, 10, Font.BOLD, BLEU);

        // En-têtes
        PdfPCell h1 = cellPay(
            "TOTAL", fColHead, BLANC);
        PdfPCell h2 = cellPay(
            "PAYÉ", fColHead, BLANC);
        PdfPCell h3 = cellPay(
            "RESTE", fColHead, BLANC);
        tPay.addCell(h1);
        tPay.addCell(h2);
        tPay.addCell(h3);

        // Valeurs
        String total = r.getMontantTotal() != null
            ? r.getMontantTotal() + " Ar" : "0 Ar";
        String paye = r.getAcompteVerse() != null
            ? r.getAcompteVerse() + " Ar" : "0 Ar";

        java.math.BigDecimal reste =
            java.math.BigDecimal.ZERO;
        if (r.getMontantTotal() != null
                && r.getAcompteVerse() != null) {
            reste = r.getMontantTotal()
                .subtract(r.getAcompteVerse());
        }
        String resteStr = reste + " Ar";

        tPay.addCell(cellPay(total, fColVal, GRIS));
        tPay.addCell(cellPay(paye, fColVal, GRIS));
        tPay.addCell(cellPay(resteStr, fColVal, GRIS));

        ctPay.addElement(tPay);
        ctPay.go();

        // Statut en bas
        ColumnText ctStatut =
            new ColumnText(cb);
        ctStatut.setSimpleColumn(170, 5, 510, 30);

        boolean confirme = r.getStatut() != null
            && r.getStatut().name()
               .equals("CONFIRMEE");

        Font fStatut = new Font(
            Font.HELVETICA, 10, Font.BOLD,
            confirme ? VERT : new Color(255, 193, 7));

        String statutText = confirme
            ? "✅  BILLET CONFIRMÉ"
            : "⏳  EN ATTENTE DE CONFIRMATION";

        Paragraph pStatut =
            new Paragraph(statutText, fStatut);
        pStatut.setAlignment(Element.ALIGN_CENTER);
        ctStatut.addElement(pStatut);
        ctStatut.go();

        doc.close();
        return out.toByteArray();
    }

    private PdfPCell cellPay(String text,
            Font font, Color bg) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(
            Element.ALIGN_CENTER);
        cell.setPadding(6);
        cell.setBorderColor(
            new Color(220, 220, 220));
        return cell;
    }
}