package de.uni_kassel.vs.cn.planDesigner.view.filebrowser;

import de.uni_kassel.vs.cn.planDesigner.controller.ErrorWindowController;
import de.uni_kassel.vs.cn.planDesigner.controller.MainWindowController;
import de.uni_kassel.vs.cn.planDesigner.view.I18NRepo;
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
        super.cancelEdit();
        setText(getItem().getName());
        setGraphic(getTreeItem().getGraphic());
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
        String name = newValue.getName().substring(0, fileEndingPosition);
//        if (AlicaModelUtils.containsIllegalCharacter(name)) {
//            final TreeItem<FileWrapper> treeItem = getTreeItem();
//            final TreeView<FileWrapper> tree = getTreeView();
//            if (tree != null) {
//                // Inform the TreeView of the edit being ready to be committed.
//                tree.fireEvent(new TreeView.EditEvent<>(tree,
//                        TreeView.<FileWrapper>editCommitEvent(),
//                        treeItem,
//                        getItem(),
//                        treeItem.getValue()));
//            }
//            LOG.warn("User tried to set illegal name: " + newValue.unwrap().getKey());
//            ErrorWindowController.createErrorWindow("This name is not allowed! These characters are forbidden: "
//                    + AlicaModelUtils.forbiddenCharacters, null);
//
//            return;
//        }
        boolean isPlanElement = false;
        File unwrappedFile = getTreeItem().getValue();
//        AbstractPlan objectToChange = null;
        if (unwrappedFile.getName().endsWith(".pml") ||
                unwrappedFile.getName().endsWith(".pty") || unwrappedFile.getName().endsWith("beh")) {

//            Resource resource = AlicaResourceSet.getInstance()
//                    .getResources()
//                    .stream()
//                    .filter(e -> e.getURI().toFileString().contains(unwrappedFile.getKey()))
//                    .filter(e -> e.getURI().toFileString().contains("pmlex") == false)
//                    .findFirst()
//                    .get();
//            objectToChange = (AbstractPlan) resource.getContents().get(0);
//
//            boolean hasSameName = false;
//            if (objectToChange instanceof Plan) {
//                if (checkForCorrectFileEnding(newValue, ".pml")) return;
//                hasSameName = RepositoryViewModel.getInstance().getPlans()
//                        .stream()
//                        .anyMatch(planPathPair -> planPathPair.getKey().getKey().equals(name));
//            }
//
//            if (objectToChange instanceof Behaviour) {
//                if (checkForCorrectFileEnding(newValue, ".beh")) return;
//                hasSameName = RepositoryViewModel.getInstance().getBehaviours()
//                        .stream()
//                        .anyMatch(behaviourPathPair -> behaviourPathPair.getKey().getKey().equals(name));
//            }
//
//            if (objectToChange instanceof PlanType) {
//                if (checkForCorrectFileEnding(newValue, ".pty")) return;
//                hasSameName = RepositoryViewModel.getInstance().getPlanTypes()
//                        .stream()
//                        .anyMatch(planTypePathPair -> planTypePathPair.getKey().getKey().equals(name));
//            }
//
//            if (hasSameName) {
//                getTreeView()
//                        .fireEvent(new TreeView.EditEvent<>(getTreeView(),
//                                TreeView.editCancelEvent(), getTreeItem(), getItem(), getItem()));
//                ErrorWindowController.createErrorWindow(I18NRepo.getInstance().getString("label.error.rename"), null);
//                return;
//            }
//            controller.getCommandStack()
//                    .storeAndExecute(new ChangeAttributeValue(objectToChange, "name", name, objectToChange));
            isPlanElement = true;
        }

        unwrappedFile.renameTo(newValue);

//        if (isPlanElement && objectToChange != null) {
//            try {
//                EMFModelUtils.saveAlicaFile(objectToChange);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

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
        if (newValue.getName().endsWith(ending) == false) {
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
                            controller.openFile(((FileTreeItem) getTreeItem()).getViewModelElement());
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
