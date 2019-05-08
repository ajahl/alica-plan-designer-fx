package de.unikassel.vs.alica.planDesigner.view.filebrowser;

import de.unikassel.vs.alica.planDesigner.controller.ErrorWindowController;
import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.events.GuiChangeAttributeEvent;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.view.I18NRepo;
import de.unikassel.vs.alica.planDesigner.view.model.SerializableViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;

public class FileTreeCell extends TreeCell<File> {

    private static final Logger LOG = LogManager.getLogger(FileTreeCell.class);

    private TextField textField;

    private final MainWindowController controller;


    public FileTreeCell(MainWindowController controller) {
        this.controller = controller;
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }
        setText(getItem().getName());
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        Platform.runLater(() -> {
            super.cancelEdit();
            setText(getItem().getName());
            setGraphic(getTreeItem().getGraphic());
        });
    }

    @Override
    public void commitEdit(File newValue) {
        if (!isEditing()) return;

        int fileEndingPosition = newValue.getName().lastIndexOf(".");
        if (fileEndingPosition < 0) {
            getTreeView()
                    .fireEvent(new TreeView.EditEvent<>(getTreeView(),
                            TreeView.editCancelEvent(), getTreeItem(), getItem(), getItem()));
            ErrorWindowController.createErrorWindow(
                    I18NRepo.getInstance().getString("label.error.rename.illegalEnding"), null);
            return;
        }

        ViewModelElement element = ((FileTreeItem)getTreeItem()).getViewModelElement();
        GuiChangeAttributeEvent guiChangeAttributeEvent = new GuiChangeAttributeEvent(GuiEventType.CHANGE_ELEMENT, element.getType(), element.getName());

        guiChangeAttributeEvent.setElementId(element.getId());
        guiChangeAttributeEvent.setParentId(element.getId());
        guiChangeAttributeEvent.setAttributeType(String.class.getSimpleName());
        guiChangeAttributeEvent.setAttributeName("name");
        guiChangeAttributeEvent.setNewValue(newValue.getName().substring(0, fileEndingPosition));
        controller.getGuiModificationHandler().handle(guiChangeAttributeEvent);


        final TreeItem<File> treeItem = getTreeItem();
        final TreeView<File> tree = getTreeView();
        if (tree != null) {
            // Inform the TreeView of the edit being ready to be committed.
            tree.fireEvent(new TreeView.EditEvent<>(tree,
                    TreeView.<File>editCommitEvent(),
                    treeItem,
                    getItem(),
                    newValue));
        }

    }

    private boolean checkForCorrectFileEnding(File newValue, String ending) {
        if (!newValue.getName().endsWith(ending)) {
            getTreeView()
                    .fireEvent(new TreeView.EditEvent<>(getTreeView(),
                            TreeView.editCancelEvent(), getTreeItem(), getItem(), getItem()));
            ErrorWindowController.createErrorWindow(
                    I18NRepo.getInstance().getString("label.error.rename.illegalEnding"), null);
            return true;
        }
        return false;
    }

    @Override
    public void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (getItem() != null) {
            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        if (getItem().isDirectory()) {
                            getTreeItem().setExpanded(!getTreeItem().isExpanded());
                        } else {
                            controller.openFile((SerializableViewModel) ((FileTreeItem) getTreeItem()).getViewModelElement());
                        }
                    }
                }
            });
        }
        if (isEditing()) {
            if (textField != null) {
                textField.setText(getString());
            }
            setText(null);
            setGraphic(textField);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    String absolutePath = getTreeItem().getValue().getAbsolutePath();
                    File fileWrapper = null;
                    if (absolutePath != null) {
                        fileWrapper = new File(absolutePath.replace(getItem().getName(), textField.getText()));
                    }
                    commitEdit(fileWrapper);
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().getName();
    }
}
