package de.uni_kassel.vs.cn.planDesigner.view.editor.container;

import de.uni_kassel.vs.cn.planDesigner.alicamodel.State;
import de.uni_kassel.vs.cn.planDesigner.command.CommandStack;
import de.uni_kassel.vs.cn.planDesigner.pmlextension.uiextensionmodel.PmlUiExtension;
import javafx.scene.paint.Color;

/**
 * Created by marci on 24.02.17.
 */
public class FailureStateContainer extends TerminalStateContainer {
    public FailureStateContainer(PmlUiExtension pmlUiExtension, State state, CommandStack commandStack) {
        super(pmlUiExtension, state, commandStack);
    }

    @Override
    public Color getVisualisationColor() {
        return Color.RED;
    }
}
