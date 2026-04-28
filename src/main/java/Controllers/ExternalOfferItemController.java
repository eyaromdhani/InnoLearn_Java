package Controllers;

import Entities.ExternalOffer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import java.awt.Desktop;
import java.net.URI;

public class ExternalOfferItemController {

    @FXML private Label lblTitle;
    @FXML private Label lblCompany;
    @FXML private Label lblLocation;
    @FXML private Text txtDescription;
    private String url;

    public void setData(ExternalOffer offer) {
        lblTitle.setText(offer.getTitle());
        lblCompany.setText(offer.getCompany());
        lblLocation.setText(offer.getLocation());
        txtDescription.setText(offer.getDescription());
        this.url = offer.getUrl();
    }

    @FXML
    private void handleApply() {
        try {
            if (Desktop.isDesktopSupported() && url != null) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
