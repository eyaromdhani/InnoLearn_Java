package org.example.Services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class PdfService {

    public String extraireTexte(String cheminFichier) {
        try {
            File fichier = new File(cheminFichier);
            if (!fichier.exists()) {
                return "Erreur : Le fichier PDF est introuvable sur votre ordinateur à l'emplacement : " + cheminFichier;
            }

            PDDocument document = PDDocument.load(fichier);
            PDFTextStripper stripper = new PDFTextStripper();
            String texte = stripper.getText(document);
            document.close();

            return texte;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la lecture du PDF.";
        }
    }
}
