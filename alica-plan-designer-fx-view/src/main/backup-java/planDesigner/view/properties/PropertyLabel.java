package de.uni_kassel.vs.cn.planDesigner.view.properties;

import de.uni_kassel.vs.cn.planDesigner.alicamodel.PlanElement;
import javafx.scene.control.Label;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class PropertyLabel<T extends PlanElement> extends Label {

    public PropertyLabel(T object, String propertyName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        setText(BeanUtils.getProperty(object, propertyName));
    }
}
