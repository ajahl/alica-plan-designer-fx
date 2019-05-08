package de.uni_kassel.vs.cn.planDesigner.view.editor.tools.condition;

import de.uni_kassel.vs.cn.planDesigner.alicamodel.Condition;
import javafx.scene.control.TabPane;

import static de.uni_kassel.vs.cn.generator.EMFModelUtils.getAlicaFactory;

/**
 * Created by marci on 01.03.17.
 */
public class RuntimeConditionTool extends AbstractConditionTool {

    public RuntimeConditionTool(TabPane workbench) {
        super(workbench);
    }

    @Override
    public Condition createNewObject() {
        return getAlicaFactory().createRuntimeCondition();
    }
}
