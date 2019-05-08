package de.uni_kassel.vs.cn.planDesigner.view.menu;

import de.uni_kassel.vs.cn.planDesigner.view.I18NRepo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ShowUsagesMenuItem extends MenuItem {

    private I18NRepo i18NRepo;

    public ShowUsagesMenuItem(long modelElementId) {
        i18NRepo = I18NRepo.getInstance();
        setText(i18NRepo.getString("label.menu.usage"));
        setOnAction( e -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ShowUsagesMenuItem.class.getClassLoader().getResource("usagesWindow.fxml"));
            try {
                Parent infoWindow = fxmlLoader.load();
                UsagesWindowController controller = fxmlLoader.getController();
                controller.createReferencesList(modelElementId);
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle(i18NRepo.getString("label.usage.info"));
                stage.setScene(new Scene(infoWindow));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(PlanDesigner.getPrimaryStage());
                stage.showAndWait();
            } catch (IOException ignored) {
            }
        });
    }



}
