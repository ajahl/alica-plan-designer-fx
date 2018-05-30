package de.uni_kassel.vs.cn.planDesigner.view.repo;

import de.uni_kassel.vs.cn.planDesigner.controller.MainWindowController;
import de.uni_kassel.vs.cn.planDesigner.view.editor.tools.DraggableHBox;
import de.uni_kassel.vs.cn.planDesigner.view.menu.ShowUsagesMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;

import java.nio.file.Path;

public class RepositoryHBox extends DraggableHBox {

    protected long modelElementId;
    protected String modelElementName;
    protected String modelElementType;

    public RepositoryHBox(long modelElementId , String modelElementName, String modelElementType, Path pathToObject) {
        this.modelElementId = modelElementId;
        this.modelElementName = modelElementName;
        this.modelElementType = modelElementType;
        setIcon(this.modelElementType);
        setText(this.modelElementName);

        // right click for opening context menu with option to show usage of model element
        setOnContextMenuRequested(e -> {
            ContextMenu contextMenu = new ContextMenu(new ShowUsagesMenuItem(modelElementId));
            contextMenu.show(RepositoryHBox.this, e.getScreenX(), e.getScreenY());
        });

        // double click for open the corresponding file
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                MainWindowController.getInstance().openFile(pathToObject.toFile());
                e.consume();
            }
        });
    }

    /** TODO: make this obsolete because ...
     * - dragTool should create this RepositoryHBox, so it should know the modelElementId
     * - startFullDrag and startPhase is already done in AbstractTool-event subscription
     */
//    protected void initDragSupport() {
//        setOnDragDetected(e -> {
//            startFullDrag();
//            dragTool.setModelElementId(modelElementId);
//            dragTool.startPhase();
//        });
//    }
}
