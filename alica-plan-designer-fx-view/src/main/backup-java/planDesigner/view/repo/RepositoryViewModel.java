package de.uni_kassel.vs.cn.planDesigner.view.repo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * This class functions as backend for the repository view.
 * <p>
 * This class containsPlan Lists of all Plans, PlanTypes, Behaviours and Tasks as ViewModelElement
 */
public final class RepositoryViewModel {

    // SINGLETON
    private static volatile RepositoryViewModel instance;

    public static RepositoryViewModel getInstance() {
        if (instance == null) {
            synchronized (RepositoryViewModel.class) {
                if (instance == null) {
                    instance = new RepositoryViewModel();
                }
            }
        }
        return instance;
    }

    private ObservableList<ViewModelElement> plans;
    private ObservableList<ViewModelElement> planTypes;
    private ObservableList<ViewModelElement> behaviours;
    private ObservableList<ViewModelElement> tasks;

    private RepositoryTabPane repositoryTabPane;

    public RepositoryViewModel() {
        plans = FXCollections.observableArrayList(new ArrayList<>());
        planTypes = FXCollections.observableArrayList(new ArrayList<>());
        behaviours = FXCollections.observableArrayList(new ArrayList<>());
        tasks = FXCollections.observableArrayList(new ArrayList<>());
    }

    public void addPlan(ViewModelElement plan) {
        this.plans.add(plan);
        if (repositoryTabPane != null) {
            this.repositoryTabPane.addPlan(plan);
        }
    }

    public void addBehaviour(ViewModelElement behaviour) {
        this.behaviours.add(behaviour);
        if (repositoryTabPane != null) {
            this.repositoryTabPane.addBehaviour(behaviour);
        }
    }

    public void addPlanType(ViewModelElement planType) {
        this.planTypes.add(planType);
        if (repositoryTabPane != null) {
            this.repositoryTabPane.addPlanType(planType);
        }
    }

    public void addTask(ViewModelElement task) {
        this.tasks.add(task);
        if (repositoryTabPane != null) {
            this.repositoryTabPane.addTask(task);
        }
    }

    public void setRepositoryTabPane (RepositoryTabPane repositoryTabPane) {
        this.repositoryTabPane = repositoryTabPane;
    }
}
