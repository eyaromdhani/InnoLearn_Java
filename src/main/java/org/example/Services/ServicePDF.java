package org.example.Services;

import org.example.Entities.Experience;
import org.example.Entities.StageCondidature;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;
import java.util.List;

public class ServicePDF {

    public String genererCvPdf(StageCondidature profile, List<Experience> experiences, String aiIntro, String destPath, 
                               String studentName, String studentEmail, String studentPhone, 
                               String address, String languages, String interests) throws Exception {
        
        Document document = new Document(PageSize.A4, 0, 0, 0, 0); 
        PdfWriter.getInstance(document, new FileOutputStream(destPath));
        document.open();

        // Colors
        BaseColor sidebarColor = new BaseColor(109, 40, 217); // Purple #6d28d9
        BaseColor mainTitleColor = new BaseColor(30, 27, 75); // Dark blue #1e1b4b
        BaseColor accentColor = new BaseColor(99, 102, 241); // Light Indigo #6366f1
        BaseColor grayText = new BaseColor(71, 85, 105); 
        BaseColor lightGrayBg = new BaseColor(248, 250, 252);

        // Fonts
        Font sideTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        Font sideLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new BaseColor(200, 200, 255));
        Font sideTextFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE);
        
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, mainTitleColor);
        Font domainFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, accentColor);
        
        Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, mainTitleColor);
        Font introFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, grayText);
        
        Font itemTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, mainTitleColor);
        Font itemSubFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, accentColor);
        Font itemMetaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, grayText);
        Font itemDescFont = FontFactory.getFont(FontFactory.HELVETICA, 10, grayText);

        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{32f, 68f});
        mainTable.setExtendLastRow(true);

        // --- LEFT COLUMN (SIDEBAR) ---
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBackgroundColor(sidebarColor);
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(25);
        leftCell.setPaddingTop(60);
        
        // Avatar Placeholder (Circle)
        // (Skipping actual image for now, but leaving space)
        leftCell.addElement(new Paragraph("\n\n\n\n"));

        // Contact
        addSidebarSection(leftCell, "CONTACT", sideTitleFont);
        addSidebarItem(leftCell, "TÉLÉPHONE", studentPhone != null ? studentPhone : "Non renseigné", sideLabelFont, sideTextFont);
        addSidebarItem(leftCell, "EMAIL", studentEmail != null ? studentEmail : "contact@innolearn.tn", sideLabelFont, sideTextFont);
        addSidebarItem(leftCell, "ADRESSE", address != null ? address : "Non renseignée", sideLabelFont, sideTextFont);

        // Langues
        addSidebarSection(leftCell, "LANGUES", sideTitleFont);
        if (languages != null && !languages.isEmpty()) {
            for (String l : languages.split(",")) {
                leftCell.addElement(new Paragraph("• " + l.trim(), sideTextFont));
            }
        } else {
            leftCell.addElement(new Paragraph("• Français\n• Anglais\n• Arabe", sideTextFont));
        }

        // Compétences (Badges)
        addSidebarSection(leftCell, "COMPÉTENCES", sideTitleFont);
        if (profile.getCompetences() != null && !profile.getCompetences().isEmpty()) {
            for (String s : profile.getCompetences().split(",")) {
                PdfPTable badgeTable = new PdfPTable(1);
                badgeTable.setWidthPercentage(90);
                badgeTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                PdfPCell badge = new PdfPCell(new Paragraph(s.trim(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE)));
                badge.setBackgroundColor(new BaseColor(255, 255, 255, 40)); // Semi-transparent white
                badge.setBorder(Rectangle.NO_BORDER);
                badge.setPadding(5);
                badge.setPaddingLeft(10);
                badgeTable.addCell(badge);
                leftCell.addElement(badgeTable);
                leftCell.addElement(new Chunk("\n"));
            }
        }

        mainTable.addCell(leftCell);

        // --- RIGHT COLUMN ---
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBackgroundColor(BaseColor.WHITE);
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(40);
        
        // Header
        rightCell.addElement(new Paragraph(studentName != null ? studentName.toUpperCase() : "ÉTUDIANT", nameFont));
        rightCell.addElement(new Paragraph(profile.getDomaine() != null ? profile.getDomaine().toUpperCase() : "PSYCHOLOGIE CLINIQUE", domainFont));
        rightCell.addElement(new Chunk("\n"));
        rightCell.addElement(new LineSeparator(2f, 100f, accentColor, Element.ALIGN_LEFT, -1f));
        rightCell.addElement(new Chunk("\n"));

        // AI Summary
        if (aiIntro != null && !aiIntro.isEmpty()) {
            PdfPTable introTable = new PdfPTable(1);
            introTable.setWidthPercentage(100);
            PdfPCell introCell = new PdfPCell(new Paragraph(aiIntro, introFont));
            introCell.setBackgroundColor(lightGrayBg);
            introCell.setBorder(Rectangle.NO_BORDER);
            introCell.setBorderWidthLeft(4f);
            introCell.setBorderColorLeft(accentColor);
            introCell.setPadding(15);
            introTable.addCell(introCell);
            rightCell.addElement(introTable);
            rightCell.addElement(new Chunk("\n"));
        }

        // Parcours & Expériences
        rightCell.addElement(new Paragraph("PARCOURS & EXPÉRIENCES", sectionTitleFont));
        rightCell.addElement(new LineSeparator(0.5f, 100f, new BaseColor(200, 200, 200), Element.ALIGN_LEFT, -1f));
        rightCell.addElement(new Chunk("\n"));
        
        for (Experience exp : experiences) {
             addExperienceEntry(rightCell, exp, itemTitleFont, itemSubFont, itemMetaFont, itemDescFont, accentColor);
        }

        rightCell.addElement(new Chunk("\n"));
        
        mainTable.addCell(rightCell);
        document.add(mainTable);
        document.close();
        return destPath;
    }

    private void addSidebarSection(PdfPCell cell, String title, Font font) {
        cell.addElement(new Chunk("\n"));
        cell.addElement(new LineSeparator(1f, 100f, new BaseColor(255, 255, 255, 50), Element.ALIGN_LEFT, -1f));
        cell.addElement(new Chunk("\n"));
        cell.addElement(new Paragraph(title, font));
        cell.addElement(new Chunk("\n"));
    }

    private void addSidebarItem(PdfPCell cell, String label, String value, Font labelFont, Font valueFont) {
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valueFont));
        cell.addElement(new Chunk("\n"));
    }

    private void addExperienceEntry(PdfPCell cell, Experience exp, Font tFont, Font sFont, Font mFont, Font dFont, BaseColor accent) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{80f, 20f});
        
        PdfPCell cLeft = new PdfPCell();
        cLeft.setBorder(Rectangle.NO_BORDER);
        cLeft.setBorderWidthLeft(2f);
        cLeft.setBorderColorLeft(accent);
        cLeft.setPaddingLeft(15);
        cLeft.setPaddingBottom(10);
        
        cLeft.addElement(new Paragraph(exp.getEtablissement(), tFont));
        cLeft.addElement(new Paragraph(exp.getDomaine() + (exp.getNiveau() != null && !exp.getNiveau().isEmpty() ? " — " + exp.getNiveau() : ""), sFont));
        if (exp.getDescription() != null && !exp.getDescription().isEmpty()) {
            cLeft.addElement(new Paragraph(exp.getDescription(), dFont));
        }
        
        PdfPCell cRight = new PdfPCell(new Paragraph(exp.getAnnee(), mFont));
        cRight.setBorder(Rectangle.NO_BORDER);
        cRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        table.addCell(cLeft);
        table.addCell(cRight);
        cell.addElement(table);
        cell.addElement(new Chunk("\n"));
    }
}
