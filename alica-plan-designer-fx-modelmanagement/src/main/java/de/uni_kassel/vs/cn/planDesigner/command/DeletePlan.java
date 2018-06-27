package de.uni_kassel.vs.cn.planDesigner.command;

import de.uni_kassel.vs.cn.planDesigner.alicamodel.Plan;
import de.uni_kassel.vs.cn.planDesigner.modelmanagement.ModelManager;
import de.uni_kassel.vs.cn.planDesigner.modelmanagement.ModelModificationQuery;
import de.uni_kassel.vs.cn.planDesigner.modelmanagement.Types;

public class DeletePlan extends AbstractCommand {

    protected Plan plan;

    public DeletePlan(ModelManager modelManager, ModelModificationQuery mmq) {
        super(modelManager);
        plan = null;
        if (mmq.getModelElementType().equals(Types.PLAN)) {
            String relativeDirectory = modelManager.makeRelativePlansDirectory(mmq.getAbsoluteDirectory(), mmq.getName());
            for (Plan currentPlan : modelManager.getPlans()) {
                if (currentPlan.getName().equals(mmq.getName()) && currentPlan.getRelativeDirectory().equals(relativeDirectory)) {
                    plan = currentPlan;
                    break;
                }
            }
        }
    }

    @Override
    public void doCommand() {
        if (plan == null) {
            return;
        }
        modelManager.removePlanElement(plan, Types.PLAN, true);
    }

    @Override
    public void undoCommand() {
        if (plan == null) {
            return;
        }
        modelManager.addPlanElement(plan, Types.PLAN, true);
    }
}
