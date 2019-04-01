package de.unikassel.vs.alica.planDesigner.view.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class SynchronisationViewModel extends PlanElementViewModel {

    protected final SimpleIntegerProperty talkTimeout = new SimpleIntegerProperty(this, "talkTimeout", 0);
    protected final SimpleIntegerProperty syncTimeout = new SimpleIntegerProperty(this, "syncTimeout", 0);
    protected final SimpleBooleanProperty failOnSyncTimeout = new SimpleBooleanProperty(this, "failOnSyncTimeout", false);
    protected ObservableList<TransitionViewModel> transitions;

    public SynchronisationViewModel(long id, String name, String type) {
        super(id, name, type);
        this.transitions = FXCollections.observableArrayList(new ArrayList<>());
        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment", "relativeDirectory", "talkTimeout", "syncTimeout", "failOnSyncTimeout"));
    }

    public ObservableList<TransitionViewModel> getTransitions() {
        return transitions;
    }

    public int getTalkTimeout() {
        return talkTimeout.get();
    }
    public void setTalkTimeout(int talkTimeout) {
        this.talkTimeout.set(talkTimeout);
    }
    public SimpleIntegerProperty talkTimeoutProperty() {
        return talkTimeout;
    }

    public int getSyncTimeout() {
        return syncTimeout.get();
    }
    public void setSyncTimeout(int syncTimeout) {
        this.syncTimeout.set(syncTimeout);
    }
    public SimpleIntegerProperty syncTimeoutProperty() {
        return syncTimeout;
    }

    public void setFailOnSyncTimeout(boolean failOnSyncTimeout) {
        this.failOnSyncTimeout.set(failOnSyncTimeout);
    }
    public boolean getFailOnSyncTimeout() {
        return failOnSyncTimeout.get();
    }
    public SimpleBooleanProperty failOnSyncTimeoutProperty() {
        return failOnSyncTimeout;
    }
}
