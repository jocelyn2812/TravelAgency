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

    // Couleurs
    private static final Color ROUGE =
        new Color(233, 69, 96);
    private static final Color BLEU_FONCE =
        new Color(26, 26, 46);
    private static final Color GRIS_CLAIR =
        new Color(240, 242, 245);
    private static final Color BLANC =
        new Color(255, 255, 255);
    private static final Color VERT =
        new Color(40, 167, 69);

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererBillet(Long reservationId)
            throws Exception {

        Reservation r = reservationService
            .findById(reservationId)
            .orElseThrow(() ->
                new RuntimeException("Réservation introuvable"));

        ByteArrayOutputStream out =
            new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(doc, out);

        doc.open();

        // ===== HEADER =====
        ajouterHeader(doc, writer, r);

        // ===== INFOS CLIENT =====
        ajouterSection(doc, "INFORMATIONS CLIENT",
            ROUGE);
        ajouterInfosClient(doc, r);

        // ===== INFOS VOYAGE =====
        ajouterSection(doc, "DÉTAILS DU VOYAGE",
            BLEU_FONCE);
        ajouterInfosVoyage(doc, r);

        // ===== PARTICIPANTS =====
        ajouterSection(doc, "PARTICIPANTS", ROUGE);
        ajouterParticipants(doc, r);

        // ===== PAIEMENT =====
        ajouterSection(doc, "INFORMATIONS FINANCIÈRES",
            BLEU_FONCE);
        ajouterPaiement(doc, r);

        // ===== FOOTER =====
        ajouterFooter(doc, r);

        doc.close();
        return out.toByteArray();
    }

    private void ajouterHeader(Document doc,
            PdfWriter writer, Reservation r)
            throws Exception {

        // Fond bleu foncé header
        PdfContentByte cb = writer.getDirectContent();
        cb.setColorFill(BLEU_FONCE);
        cb.rectangle(36, 750, 523, 70);
        cb.fill();

        // Titre agence
        Font fontTitre = new Font(
            Font.HELVETICA, 22,
            Font.BOLD, BLANC);
        Font fontSub = new Font(
            Font.HELVETICA, 10,
            Font.NORMAL,
            new Color(200, 200, 200));

        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{2f, 1f});

        // Colonne gauche — Titre
        PdfPCell cellLeft = new PdfPCell();
        cellLeft.setBorder(Rectangle.NO_BORDER);
        cellLeft.setBackgroundColor(BLEU_FONCE);
        cellLeft.setPadding(12);

        Paragraph titre = new Paragraph(
            "✈  TRAVEL AGENCY", fontTitre);
        Paragraph sub = new Paragraph(
            "Billet de voyage officiel", fontSub);
        cellLeft.addElement(titre);
        cellLeft.addElement(sub);
        header.addCell(cellLeft);

        // Colonne droite — Numéro billet
        PdfPCell cellRight = new PdfPCell();
        cellRight.setBorder(Rectangle.NO_BORDER);
        cellRight.setBackgroundColor(ROUGE);
        cellRight.setPadding(12);
        cellRight.setHorizontalAlignment(
            Element.ALIGN_CENTER);
        cellRight.setVerticalAlignment(
            Element.ALIGN_MIDDLE);

        Font fontNum = new Font(
            Font.HELVETICA, 11, Font.BOLD, BLANC);
        Font fontNumSub = new Font(
            Font.HELVETICA, 9, Font.NORMAL, BLANC);

        String numBillet = String.format(
            "TRV-%d-%04d",
            java.time.LocalDate.now().getYear(),
            r.getId());

        Paragraph num = new Paragraph(
            "N° BILLET", fontNumSub);
        num.setAlignment(Element.ALIGN_CENTER);
        Paragraph numVal = new Paragraph(
            numBillet, fontNum);
        numVal.setAlignment(Element.ALIGN_CENTER);

        cellRight.addElement(num);
        cellRight.addElement(numVal);
        header.addCell(cellRight);

        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    private void ajouterSection(Document doc,
            String titre, Color couleur)
            throws Exception {

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(5);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(couleur);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);

        Font font = new Font(
            Font.HELVETICA, 11,
            Font.BOLD, BLANC);
        Paragraph p = new Paragraph(titre, font);
        cell.addElement(p);
        table.addCell(cell);
        doc.add(table);
    }

    private void ajouterInfosClient(Document doc,
            Reservation r) throws Exception {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(5);

        if (r.getClient() != null) {
            ajouterLigne(table,
                "Nom complet",
                r.getClient().getNom() + " "
                + r.getClient().getPrenom());
            ajouterLigne(table,
                "Email",
                r.getClient().getEmail() != null
                ? r.getClient().getEmail() : "-");
            ajouterLigne(table,
                "Téléphone",
                r.getClient().getTelephone() != null
                ? r.getClient().getTelephone() : "-");
            ajouterLigne(table,
                "Nationalité",
                r.getClient().getNationalite() != null
                ? r.getClient().getNationalite() : "-");
            ajouterLigne(table,
                "N° Passeport",
                r.getClient().getNumeroPasseport() != null
                ? r.getClient().getNumeroPasseport() : "-");
            ajouterLigne(table,
                "Points fidélité",
                r.getClient().getPointsFidelite()
                + " points");
        }

        doc.add(table);
    }

    private void ajouterInfosVoyage(Document doc,
            Reservation r) throws Exception {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(5);

        if (r.getVoyage() != null) {
            ajouterLigne(table,
                "Titre du voyage",
                r.getVoyage().getTitre() != null
                ? r.getVoyage().getTitre() : "-");

            if (r.getVoyage().getDestination() != null) {
                ajouterLigne(table,
                    "Destination",
                    r.getVoyage().getDestination()
                        .getVille() + ", "
                    + r.getVoyage().getDestination()
                        .getPays());
                ajouterLigne(table,
                    "Continent",
                    r.getVoyage().getDestination()
                        .getContinent() != null
                    ? r.getVoyage().getDestination()
                        .getContinent() : "-");
            }

            ajouterLigne(table,
                "Type de voyage",
                r.getVoyage().getType() != null
                ? r.getVoyage().getType().name() : "-");

            ajouterLigne(table,
                "Date de départ",
                r.getVoyage().getDateDepart() != null
                ? r.getVoyage().getDateDepart()
                    .format(FMT) : "-");

            ajouterLigne(table,
                "Date de retour",
                r.getVoyage().getDateRetour() != null
                ? r.getVoyage().getDateRetour()
                    .format(FMT) : "-");

            // Durée
            if (r.getVoyage().getDateDepart() != null
                    && r.getVoyage().getDateRetour() != null) {
                long duree = java.time.temporal.ChronoUnit
                    .DAYS.between(
                        r.getVoyage().getDateDepart(),
                        r.getVoyage().getDateRetour());
                ajouterLigne(table,
                    "Durée", duree + " nuits");
            }

            ajouterLigne(table,
                "Saison",
                r.getVoyage().getSaison() != null
                ? r.getVoyage().getSaison().name() : "-");
        }

        doc.add(table);
    }

    private void ajouterParticipants(Document doc,
            Reservation r) throws Exception {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(5);

        ajouterLigne(table,
            "Nombre d'adultes",
            r.getNombreAdultes() != null
            ? r.getNombreAdultes().toString() : "0");

        ajouterLigne(table,
            "Nombre d'enfants",
            r.getNombreEnfants() != null
            ? r.getNombreEnfants().toString() : "0");

        int total = (r.getNombreAdultes() != null
            ? r.getNombreAdultes() : 0)
            + (r.getNombreEnfants() != null
            ? r.getNombreEnfants() : 0);

        ajouterLigne(table,
            "Total participants",
            String.valueOf(total));

        if (r.getObservations() != null
                && !r.getObservations().isEmpty()) {
            ajouterLigne(table,
                "Observations",
                r.getObservations());
        }

        doc.add(table);
    }

    private void ajouterPaiement(Document doc,
            Reservation r) throws Exception {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        ajouterLigne(table,
            "Montant total",
            r.getMontantTotal() != null
            ? r.getMontantTotal() + " Ar" : "-");

        ajouterLigne(table,
            "Montant payé",
            r.getAcompteVerse() != null
            ? r.getAcompteVerse() + " Ar" : "0 Ar");

        // Reste à payer
        java.math.BigDecimal reste =
            java.math.BigDecimal.ZERO;
        if (r.getMontantTotal() != null
                && r.getAcompteVerse() != null) {
            reste = r.getMontantTotal()
                .subtract(r.getAcompteVerse());
        } else if (r.getMontantTotal() != null) {
            reste = r.getMontantTotal();
        }

        ajouterLigne(table,
            "Reste à payer",
            reste + " Ar");

        ajouterLigne(table,
            "Statut réservation",
            r.getStatut() != null
            ? r.getStatut().name() : "-");

        ajouterLigne(table,
            "Date de réservation",
            r.getDateReservation() != null
            ? r.getDateReservation().format(FMT) : "-");

        doc.add(table);

        // STATUT VISUEL
        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(100);
        statusTable.setSpacingBefore(5);
        statusTable.setSpacingAfter(10);

        PdfPCell statusCell = new PdfPCell();
        statusCell.setBorder(Rectangle.BOX);
        statusCell.setBorderColor(
            r.getStatut() != null
            && r.getStatut().name().equals("CONFIRMEE")
            ? VERT : ROUGE);
        statusCell.setBorderWidth(2);
        statusCell.setPadding(10);
        statusCell.setHorizontalAlignment(
            Element.ALIGN_CENTER);

        Color statusColor =
            r.getStatut() != null
            && r.getStatut().name().equals("CONFIRMEE")
            ? VERT : ROUGE;

        Font statusFont = new Font(
            Font.HELVETICA, 14,
            Font.BOLD, statusColor);

        String statusText =
            r.getStatut() != null
            && r.getStatut().name().equals("CONFIRMEE")
            ? "✅  BILLET CONFIRMÉ — VOYAGE AUTORISÉ"
            : "⏳  EN ATTENTE DE CONFIRMATION";

        Paragraph statusPara = new Paragraph(
            statusText, statusFont);
        statusPara.setAlignment(Element.ALIGN_CENTER);
        statusCell.addElement(statusPara);
        statusTable.addCell(statusCell);
        doc.add(statusTable);
    }

    private void ajouterFooter(Document doc,
            Reservation r) throws Exception {

        // Ligne séparatrice
        PdfPTable ligne = new PdfPTable(1);
        ligne.setWidthPercentage(100);
        ligne.setSpacingBefore(10);

        PdfPCell cellLigne = new PdfPCell();
        cellLigne.setBorder(Rectangle.TOP);
        cellLigne.setBorderColor(ROUGE);
        cellLigne.setBorderWidth(2);
        cellLigne.setFixedHeight(2);
        ligne.addCell(cellLigne);
        doc.add(ligne);

        // Footer texte
        Font fontFooter = new Font(
            Font.HELVETICA, 9,
            Font.ITALIC,
            new Color(100, 100, 100));

        Font fontFooterBold = new Font(
            Font.HELVETICA, 10,
            Font.BOLD, BLEU_FONCE);

        PdfPTable footer = new PdfPTable(3);
        footer.setWidthPercentage(100);
        footer.setSpacingBefore(8);

        // Colonne 1 — Agence
        PdfPCell c1 = new PdfPCell();
        c1.setBorder(Rectangle.NO_BORDER);
        c1.addElement(new Paragraph(
            "Travel Agency", fontFooterBold));
        c1.addElement(new Paragraph(
            "Agence de voyages officielle",
            fontFooter));
        c1.addElement(new Paragraph(
            "contact@travelagency.com",
            fontFooter));
        footer.addCell(c1);

        // Colonne 2 — Conditions
        PdfPCell c2 = new PdfPCell();
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        c2.addElement(new Paragraph(
            "Ce billet est personnel et non cessible.",
            fontFooter));
        c2.addElement(new Paragraph(
            "Présentez ce document à l'embarquement.",
            fontFooter));
        footer.addCell(c2);

        // Colonne 3 — Date émission
        PdfPCell c3 = new PdfPCell();
        c3.setBorder(Rectangle.NO_BORDER);
        c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c3.addElement(new Paragraph(
            "Émis le :", fontFooter));
        c3.addElement(new Paragraph(
            java.time.LocalDate.now().format(FMT),
            fontFooterBold));

        // Zone signature
        c3.addElement(new Paragraph(
            "\nSignature & Cachet :", fontFooter));
        c3.addElement(new Paragraph(
            "_____________________", fontFooter));
        footer.addCell(c3);

        doc.add(footer);
    }

    private void ajouterLigne(PdfPTable table,
            String label, String valeur)
            throws Exception {

        Font fontLabel = new Font(
            Font.HELVETICA, 10,
            Font.BOLD, BLEU_FONCE);
        Font fontValeur = new Font(
            Font.HELVETICA, 10,
            Font.NORMAL,
            new Color(60, 60, 60));

        // Cellule label
        PdfPCell cellLabel = new PdfPCell(
            new Phrase(label, fontLabel));
        cellLabel.setBackgroundColor(GRIS_CLAIR);
        cellLabel.setPadding(7);
        cellLabel.setBorderColor(
            new Color(220, 220, 220));

        // Cellule valeur
        PdfPCell cellValeur = new PdfPCell(
            new Phrase(valeur, fontValeur));
        cellValeur.setPadding(7);
        cellValeur.setBorderColor(
            new Color(220, 220, 220));

        table.addCell(cellLabel);
        table.addCell(cellValeur);
    }
}