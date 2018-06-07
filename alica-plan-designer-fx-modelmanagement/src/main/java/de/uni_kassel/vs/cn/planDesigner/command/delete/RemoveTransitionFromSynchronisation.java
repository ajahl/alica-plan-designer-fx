package de.uni_kassel.vs.cn.planDesigner.command.delete;

import de.uni_kassel.vs.cn.planDesigner.alicamodel.Synchronization;
import de.uni_kassel.vs.cn.planDesigner.alicamodel.Transition;
import de.uni_kassel.vs.cn.planDesigner.command.AbstractCommand;

public class RemoveTransitionFromSynchronisation extends AbstractCommand {

    private Transition toRemove;

    public RemoveTransitionFromSynchronisation(Synchronization element, Transition toRemove) {
        // TODO think about other solution for getting the plan
        super(element, toRemove.getInState().getParentPlan());
        this.toRemove = toRemove;
    }

    @Override
    public void doCommand() {
        ((Synchronization) getElementToEdit()).getSynchedTransitions().remove(toRemove);
    }

    @Override
    public void undoCommand() {
        ((Synchronization) getElementToEdit()).getSynchedTransitions().add(toRemove);
    }

    @Override
    public String getCommandString() {
        return "Remove Transition from Synchronisation";
    }
}
