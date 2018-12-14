package de.unikassel.vs.alica.planDesigner.view.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class ViewModelElement {
    protected SimpleLongProperty id;
    protected SimpleStringProperty name;
    protected SimpleStringProperty type;
    protected SimpleStringProperty relativeDirectory;
    protected SimpleLongProperty parentId;

    public ViewModelElement(long id, String name, String type) {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.type = new SimpleStringProperty();
        this.relativeDirectory = new SimpleStringProperty();
        this.parentId = new SimpleLongProperty();

        this.id.setValue(id);
        this.name.setValue(name);
        this.type.setValue(type);
    }

    public ViewModelElement(long id, String name, String type, String relativeDirectory) {
        this(id, name, type);
        this.relativeDirectory.setValue(relativeDirectory);
    }

    public final SimpleLongProperty idProperty() {
        return this.id;
    }
    public long getId() {
        return this.id.get();
    }

    public final SimpleStringProperty nameProperty() {
        return this.name;
    }
    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public final SimpleStringProperty typeProperty() { return this.type; }
    public String getType() {
        return type.get();
    }

    public final SimpleStringProperty relativeDirectoryProperty() {return this.relativeDirectory; }
    public void setRelativeDirectory(String relativeDirectory) {this.relativeDirectory.set(relativeDirectory);}
    public String getRelativeDirectory() {return this.relativeDirectory.get();}

    public void setParentId(long id) {
        this.parentId.setValue(id);
    }

    public long getParentId() {
        return parentId.get();
    }

    public String toString() {
        return type + ": " + name + "(" + id + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ViewModelElement) {
            ViewModelElement otherElement = (ViewModelElement) other;
            return this.getId() == otherElement.getId() && this.getName().equals(otherElement.getName());
        } else {
            return false;
        }
    }
}
