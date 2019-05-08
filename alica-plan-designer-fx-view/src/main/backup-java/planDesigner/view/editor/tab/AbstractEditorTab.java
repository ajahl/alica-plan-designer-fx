package de.uni_kassel.vs.cn.planDesigner.view.editor.tab;

import de.uni_kassel.vs.cn.planDesigner.controller.MainWindowController;
import de.uni_kassel.vs.cn.planDesigner.view.I18NRepo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEditorTab extends Tab {

    protected SimpleObjectProperty<List<Pair<Long, AbstractPlanElementContainer>>> selectedPlanElements;

    private Pair<Long, Path> editablePathPair;
    private ObservableList<Node> visualRepresentations;

    //TODO onAddElement to scene
    private final KeyCombination ctrlA = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);

    public AbstractEditorTab(Long planElementId) {
        selectedPlanElements = new SimpleObjectProperty<>(new ArrayList<>());
        selectedPlanElements.get().add(new Pair<>(planElementId, null));
    }

    // TODO: Review necessary, due to MVC pattern adaption.
    public AbstractEditorTab(Pair<Long, Path> editablePathPair) {
        // set Tab Caption to name of file, represented by this Tab
        super(editablePathPair.getValue().getFileName().toString());

        this.editablePathPair = editablePathPair;
        initSelectedPlanElements(editablePathPair);

        // onAddElement Ctrl+A handlerinterfaces to scene
        EditorTabPane editorTabPane = MainWindowController.getInstance().getEditorTabPane();
        editorTabPane.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (ctrlA.match(event)) {
                selectAllPlanElements(editorTabPane, event);
            }
        });

        // onAddElement close tab handlerinterfaces
        setClosable(true);
        setOnCloseRequest(e -> {
            // popup for trying to close dirty tab
            if (dirty) {
                IsDirtyWindowController.createIsDirtyWindow(this, e);
            }
        });
    }

    private void selectAllPlanElements(EditorTabPane editorTabPane, KeyEvent event) {
        Tab selectedTab = editorTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null && !(selectedTab instanceof PlanTab)) {
            return;
        }

        selectedPlanElements = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        visualRepresentations = FXCollections.observableArrayList();

        selectedPlanElements.get().addListener(new ListChangeListener<Pair<Long, AbstractPlanElementContainer>>() {
            @Override
            public void onChanged(Change<? extends Pair<Long, AbstractPlanElementContainer>> change) {
                while (change.next()) {
                    change.getAddedSubList().forEach(o -> {
                        o.getValue().setEffect(createSelectedEffect());
                        visualRepresentations.add(o.getValue().getVisualRepresentation());
                    });
                }
            }
        });

        PlanTab tab = (PlanTab) selectedTab;
        tab.getPlanEditorGroup().getStateContainers().forEach(stateContainer -> {
            selectedPlanElements.get()
                    .add(new Pair<Long, AbstractPlanElementContainer>(stateContainer.getModelElementId(), stateContainer));
        });
        tab.getPlanEditorGroup().getEntryPointContainers().forEach(epContainer -> {
            selectedPlanElements.get()
                    .add(new Pair<Long, AbstractPlanElementContainer>(epContainer.getModelElementId(), epContainer));
        });
        tab.getPlanEditorGroup().getTransitionContainers().forEach(transitionContainer -> {
            selectedPlanElements.get()
                    .add(new Pair<Long, AbstractPlanElementContainer>(transitionContainer.getModelElementId(), transitionContainer));
        });
        tab.getPlanEditorGroup().getSynchronisationContainers().forEach(syncContainer -> {
            selectedPlanElements.get()
                    .add(new Pair<Long, AbstractPlanElementContainer>(syncContainer.getModelElementId(), syncContainer));
        });
    }

    /**
     * initialization for the selected element property, which indicates what elements are selected.
     * specializations for specific selections of {@link AbstractPlanElementContainer}s can be found under
     * {@link AbstractPlanElementContainer#getMouseClickedEventHandler(PlanElement)}
     *
     * @param editablePathPair
     */
    // TODO: Review necessary, due to MVC pattern adaption.
    protected void initSelectedPlanElements(Pair<Long, Path> editablePathPair) {
        selectedPlanElements = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        selectedPlanElements.get().add(new Pair<>(editablePathPair.getKey(), null));
        selectedPlanElements.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // TODO: cannot return here because this avoid deleting selectedEffect on oldValue
                return;
            }
            // set selectedEffect on selected elements...

            // ... for state container and its containing elements (really special)
            DropShadow selectedEffect = createSelectedEffect();
            if (newValue.size() == 1 && newValue.get(0).getKey() instanceof AbstractPlan
                    && newValue.get(0).getValue() != null
                    && newValue.get(0).getValue() instanceof StateContainer) {
                ((StateContainer) newValue.get(0).getValue()).getStatePlans()
                        .stream()
                        .filter(abstractPlanHBox -> abstractPlanHBox.getAbstractPlan()
                                .equals(newValue.get(0).getKey()))
                        .findFirst().orElseGet(() -> new AbstractPlanHBox(newValue.get(0).getKey(),
                        (StateContainer) newValue.get(0).getValue()))
                        .setEffect(createSelectedEffect());
            } else {
                newValue.forEach(selectedPlanElementPair -> {
                    AbstractPlanElementContainer planElementContainer = selectedPlanElementPair.getValue();
                    if (planElementContainer != null) {
                        planElementContainer.setEffect(selectedEffect);
                    }
                });
            }
            // ... for transitions
            if (newValue.size() == 1 && newValue.get(0).getValue() instanceof TransitionContainer) {
                ((TransitionContainer) newValue.get(0).getValue()).setPotentialDraggableNodesVisible(true);
            }

            if ((oldValue == null)) {
                return;
            }
            oldValue.forEach(selectedPlanElementPair -> {
                AbstractPlanElementContainer planElementContainer = selectedPlanElementPair.getValue();
                if (planElementContainer != null) {
                    // this is weird! If I use planElementContainer.setEffectToStandard() nothing happens..
                    if (planElementContainer.getModelElementId() == oldValue.get(0).getKey()) {
                        planElementContainer.setEffect(null);
                    }
                    if (planElementContainer instanceof StateContainer) {
                        ((StateContainer) planElementContainer)
                                .getStatePlans()
                                .forEach(abstractPlanHBox -> {
                                    if (abstractPlanHBox.getAbstractPlan() != newValue.get(0).getKey()) {
                                        abstractPlanHBox.setEffect(null);
                                    }
                                });
                    }
                }
                if (planElementContainer instanceof StateContainer) {
                    ((StateContainer) planElementContainer)
                            .getStatePlans()
                            .forEach(abstractPlanHBox -> {
                                if (abstractPlanHBox.getAbstractPlan() != newValue.get(0).getKey()) {
                                    abstractPlanHBox.setEffect(null);
                                }
                            });
                }
            });

            if (oldValue.size() == 1 && oldValue.get(0).getValue() instanceof TransitionContainer) {
                ((TransitionContainer) oldValue.get(0).getValue()).setPotentialDraggableNodesVisible(false);
            }
        });
    }

    public void clearSelectedElements() {
        selectedPlanElements.get().forEach(element -> {
            if (element.getValue() == null) {
                return;
            }
            element.getValue().setEffect(null);
        });
        selectedPlanElements.get().clear();
        visualRepresentations.clear();
    }

    private DropShadow createSelectedEffect() {
        DropShadow value = new DropShadow(StateContainer.STATE_RADIUS, new Color(0, 0.4, 0.9, 0.9));
        value.setBlurType(BlurType.ONE_PASS_BOX);
        value.setSpread(0.45);
        return value;
    }

    public Path getFilePath() {
        return editablePathPair.getValue();
    }

    // TODO: Review necessary, due to MVC pattern adaption.
    public void save() {
        try {
            setText(getText().replace("*", ""));
            //EMFModelUtils.saveAlicaFile(getEditable());
            //getCommandStack().setSavedForAbstractPlan(editablePathPair.getKey());
        } catch (IOException e) {
            ErrorWindowController.createErrorWindow(I18NRepo.getInstance().getString("label.error.save"), e);
        }
    }

    public Long getEditable() {
        return editablePathPair.getKey();
    }

    public SimpleObjectProperty<List<Pair<Long, AbstractPlanElementContainer>>> getSelectedPlanElements() {
        return selectedPlanElements;
    }

    private void setEditablePathPair(Pair<Long, Path> editablePathPair) {
        this.editablePathPair = editablePathPair;
    }

    public Pair<Long, Path> getEditablePathPair() {
        return editablePathPair;
    }
}
