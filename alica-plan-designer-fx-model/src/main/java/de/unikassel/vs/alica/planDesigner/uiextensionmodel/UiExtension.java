package de.unikassel.vs.alica.planDesigner.uiextensionmodel;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class UiExtension extends PositionedElement {

    // ID of the plan, this pmlUIExtension object helps to layout its elements in the Plan Designer
    protected final SimpleLongProperty layoutedModelElementId = new SimpleLongProperty();
    protected final SimpleIntegerProperty height = new SimpleIntegerProperty();
    protected final SimpleIntegerProperty width = new SimpleIntegerProperty();
    protected final SimpleBooleanProperty collapsed = new SimpleBooleanProperty();
    protected final SimpleBooleanProperty visible = new SimpleBooleanProperty();

    protected LinkedList<BendPoint> bendPoints;

    public UiExtension() {
        bendPoints = new LinkedList<>();
    }

    public long getLayoutedModelElementId() {
        return layoutedModelElementId.get();
    }

    public void setLayoutedModelElementId(long layoutedModelElementId) {
        this.layoutedModelElementId.set(layoutedModelElementId);
    }

    public SimpleLongProperty layoutedModelElementIdProperty() {
        return layoutedModelElementId;
    }

    public int getWidth() {return this.width.get();}

    public void setWidth(int width) {this.width.set(width);}

    public SimpleIntegerProperty widthProperty() {
        return width;
    }

    public int getHeight() {return this.height.get();}

    public void setHeight(int height) {this.height.set(height);}

    public SimpleIntegerProperty heightProperty() {
        return height;
    }

    public boolean isCollapsed() {return this.collapsed.get();}

    public void setCollapsed(boolean collapsed) {this.collapsed.set(collapsed);}

    public SimpleBooleanProperty collapsedProperty() {
        return collapsed;
    }

    public LinkedList<BendPoint> getBendPoints() {return this.bendPoints;}

    public boolean isVisible() {return this.visible.get();}

    public void setVisible(boolean visible) {this.visible.set(visible);}

    public SimpleBooleanProperty visibleProperty() {
        return visible;
    }

    public void addBendpoint(BendPoint bendPoint) {
        bendPoints.add(bendPoint);
    }

    public void removeBendpoint(BendPoint bendPoint) {
        bendPoints.remove(bendPoint);
    }
}