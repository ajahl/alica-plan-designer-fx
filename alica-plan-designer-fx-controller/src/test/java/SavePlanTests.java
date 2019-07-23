import de.unikassel.vs.alica.planDesigner.PlanDesigner;
import de.unikassel.vs.alica.planDesigner.PlanDesignerApplication;
import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.configuration.Configuration;
import de.unikassel.vs.alica.planDesigner.configuration.ConfigurationManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Extensions;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.view.I18NRepo;
import de.unikassel.vs.alica.planDesigner.view.editor.container.StateContainer;
import de.unikassel.vs.alica.planDesigner.view.editor.container.TransitionContainer;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.PointQuery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testfx.service.query.impl.NodeQueryUtils.hasText;

public class SavePlanTests extends ApplicationTest {
    private final String taskRepository = "testfxTaskRepo";
    private final String taskName = "testfxTask";
    private final String planName = "testfxPlan";
    private final String planName2 = "testfxPlan2";
    private final String planTypeName = "testfxPlanType";
    private final String behaviourName = "testfxBehaviour";
    private final String roleSetName = "testfxRoleSet";
    private final String roleName1 = "testfxRole1";
    private final String roleName2 = "testfxRole2";

    private final String taskRepositoryExtension = taskRepository + "." + Extensions.TASKREPOSITORY;
    private final String planNameExtension = planName + "." + Extensions.PLAN;
    private final String planNameExtension2 = planName2 + "." + Extensions.PLAN;
    private final String planTypeNameExtension = planTypeName + "." + Extensions.PLANTYPE;
    private final String behaviourNameExtension = behaviourName + "." + Extensions.BEHAVIOUR;
    private final String roleSetNameExtension = roleSetName + "." + Extensions.ROLESET;

    private final String configName = "testfxConfig";
    private final String rootConfigFolder = ConfigurationManager.getInstance().getPlanDesignerConfigFolder().getPath();
    private final String configFolder = rootConfigFolder + "/testfx";
    private final String configFolderPlans = configFolder + "/plans";
    private final String configFolderRoles = configFolder + "/roles";
    private final String configFolderTasks = configFolder + "/tasks";
    private final String configFolderSrc = configFolder + "/src";
    private final String configFolderPlugin = getPluginsFolder();
    private final String configFile = rootConfigFolder + "/" + configName + ".properties";

    private final String state1Name = "State1";
    private final String state2Name = "State2";
    private final String state3Name = "State3";
    private final String successStateName = "SuccessState";
    private final String precondition1Name = "Precondition1";
    private final String precondition2Name = "Precondition2";
    private final String precondition3Name = "Precondition3";
    private final String precondition4Name = "Precondition4";

    private final String entryPointContainerId = "#EntryPointContainerCircle";
    private final String stateContainerId = "#StateContainer";
    private final String successStateContainerId = "#SuccessStateContainer";
    private final String transitionContainerId = "#TransitionContainer";
    private final String propertySheetId = "#PropertySheet";
    private final String planContentId = "#PlanTabPlanContent";

    private I18NRepo i18NRepo = I18NRepo.getInstance();
    private int planElementsCounter = 0;

    private final String defaultName = i18NRepo.getString("label.state.defaultName");


    @Override
    public void start(Stage stage) throws Exception {
        // clean config
        deleteConfig();
        deleteconfigFolder();
        createConfigFolders();

        // process possible taskrepository not exists warning
        Thread thread = new Thread(() -> {
            sleep(2000);
            handleNewTaskRepositoryDialog();
        });
        thread.setDaemon(true);
        thread.start();

        startApplication(stage);
    }

    private void startApplication(Stage stage) throws IOException {
        PlanDesigner.init();
        PlanDesignerApplication planDesignerApplication = new PlanDesignerApplication();
        planDesignerApplication.start(stage);
    }

    @Test
    public void testCreatePlan() {
        // check if configuration is already present
        createConfiguration();

        // init
        createPlan(planName);
        createBehaviour();

        closeFileThreeElements();
        createRoleSet();

        // modify roleset and roles
        openRoleSet();
        createRoles();
        saveCurrentData();

        // create task
        createTask();

        // modify plan
        openPlan();
        setMasterPlan();

        placeEntryPoint();
        setCardinality();

        placeState();
        setStateContainerName(defaultName, state1Name);

        placeState();
        setStateContainerName(defaultName, state2Name);

        placeState();
        setStateContainerName(defaultName, state3Name);

        placeSuccessState();
        setSuccessStateContainerName(defaultName, successStateName);

        placeBehaviour(getStateContainer1());

        drawInitTransition(getEntryPointContainer(), getStateContainer1());

        drawTransition(getStateContainer1(), getStateContainer2());
        drawTransition(getStateContainer1(), getStateContainer3());
        drawTransition(getStateContainer3(), getStateContainer2());
        drawTransition(getStateContainer2(), getSuccessStateContainer());

        repositionPlanElement(getEntryPointContainer(), -0.2, -0.5);
        repositionPlanElement(getStateContainer1(), -0.1, -0.5);
        repositionPlanElement(getStateContainer2(), 0, -0.5);
        repositionPlanElement(getStateContainer3(), -0.3, 0);
        repositionPlanElement(getSuccessStateContainer(), 0, -0.5);

        createPlanType();
        placePlanType(getStateContainer2());

        createPlan(planName2);
        placePlan(getStateContainer3());

        createBendpoint(getStateContainer3(), getStateContainer2());

        setPrecondition(getTransitionLine(getStateContainer1(), getStateContainer3()), precondition1Name);
        setPrecondition(getTransitionLine(getStateContainer3(), getStateContainer2()), precondition2Name);
        setPrecondition(getTransitionLine(getStateContainer1(), getStateContainer2()), precondition3Name);
        setPrecondition(getTransitionLine(getStateContainer2(), getSuccessStateContainer()), precondition4Name);

        saveCurrentData();

        // check saved data
        checkConfig();

        // clean
        deletePlan();
        deleteBehaviour();
        deleteRoleSet();
    }

    private Node getStateContainer1() {
        // sometimes the nodes lose their scenes so we can not cache them safely
        return getContainerNode(stateContainerId, state1Name);
    }

    private Node getStateContainer2() {
        return getContainerNode(stateContainerId, state2Name);
    }

    private Node getStateContainer3() {
        return getContainerNode(stateContainerId, state3Name);
    }

    private Node getEntryPointContainer() {
        return getContainerNode(entryPointContainerId);
    }

    private Node getSuccessStateContainer() {
        return getContainerNode(successStateContainerId);
    }

    private void createBendpoint(Node fromState, Node toState) {
        pickTransitionTool();
        clickOn(getTransitionLine(fromState, toState));
        dropElement();
        repositionPlanElement(getTransitionLine(fromState, toState), 0.2, 0);
        dropElement();
    }

    private Node getTransitionLine(Node fromState, Node toState) {
        return lookup(transitionContainerId).queryAll().stream()
                .filter(n -> {
                    TransitionContainer c = (TransitionContainer) n;
                    StateContainer from = c.getFromState();
                    StateContainer to = c.getToState();
                    return from.equals(fromState) && to.equals(toState);
                })
                .findAny()
                .map(n -> ((TransitionContainer) n).getVisualRepresentation())
                .get();
    }

    private void setCardinality() {
        doubleClickOn(entryPointContainerId);
        moveTo("minCardinality");
        clickOn("0");
        write("1");
        moveTo("maxCardinality");
        clickOn("0");
        write("10");
    }

    private void setMasterPlan() {
        moveTo("masterPlan");
        clickOn(".check-box");
    }

    private void repositionPlanElement(Node container, double factX, double factY) {
        Node planContent = lookup("#PlanTabPlanContent").queryFirst();
        Bounds planBounds = planContent.getBoundsInLocal();
        double planWidth = planBounds.getWidth();
        double planHeight = planBounds.getHeight();

        Bounds containerBounds = container.localToScreen(container.getBoundsInLocal());
        double origX = containerBounds.getMinX() + containerBounds.getWidth() / 2;
        double origY = containerBounds.getMinY() + containerBounds.getHeight() / 2;

        double resX = origX + (planWidth * factX);
        double resY = origY + (planHeight * factY);

        drag(container).dropTo(resX, resY);
    }

    private void setPrecondition(Node transition, String name) {
        doubleClickOn(transition);

        moveTo(propertySheetId);
        clickOn(i18NRepo.getString("label.caption.preCondtions"));

        clickOn("NONE");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        moveTo("enabled");
        Node enabledCheckBox = lookup(n -> n instanceof CheckBox).queryFirst();
        clickOn(enabledCheckBox);

        Node conditionStringText = lookup(n -> n instanceof TextField).selectAt(1).queryFirst();
        clickOn(conditionStringText);
        write(name);
    }

    private void deleteConfig() {
        // delete from ConfigurationManager
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.removeConfiguration(configName);
        configurationManager.writeToDisk();
    }

    private void createConfiguration() {
        // open configuration dialog
        clickOn("#editMenu");
        clickOn("Configure");
        sleep(1000);

        // click on first free config field
        Node firstListElement = lookup("#availableWorkspacesListView").lookup("").selectAt(0).queryFirst();
        clickOn(firstListElement);

        // enter config name
        type(KeyCode.ENTER);
        write(configName);
        type(KeyCode.ENTER);
        sleep(1000);

        // enter folders
        for(String folder: Arrays.asList(configFolderPlans, configFolderRoles, configFolderTasks, configFolderSrc, configFolderPlugin)) {
            type(KeyCode.TAB);
            write(folder);
            type(KeyCode.TAB);
        }

        // plugins folder selector have to be opened to reload the default plugin drop down menu
        clickOn("#pluginsFolderFileButton");
        type(KeyCode.ESCAPE);

        // activate default plugin
        clickOn("#defaultPluginComboBox");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        // save and activate
        clickOn("#saveButton");
        clickOn("#activeButton");

        // create TaskRepo
        handleNewTaskRepositoryDialog();

        FxAssert.verifyThat("#activeConfLabel", hasText(configName));

        // exit
        clickOn("#saveAndExitButton");
    }

    private void handleNewTaskRepositoryDialog() {
        String warningMessage = i18NRepo.getString("label.error.missing.taskrepository");
        Node warning = lookup(warningMessage).queryFirst();
        if (warning != null) {
            clickOn(i18NRepo.getString("action.create.taskrepository"));
            sleep(1000);
            clickOn("#nameTextField");
            write(taskRepository);
            clickOn("#createButton");
            sleep(1000);
        }
    }

    private String getPluginsFolder() {
        Path currentRelativePath = Paths.get("");
        String projectRoot = currentRelativePath.toAbsolutePath().getParent().toString();
        String pluginsFolder = projectRoot + "/alica-plan-designer-fx-default-plugin";
        return pluginsFolder;
    }

    private void createConfigFolders() {
        for (String folder: Arrays.asList(configFolder, configFolderPlans, configFolderRoles, configFolderTasks, configFolderSrc)) {
            new File(folder).mkdir();
        }
    }

    private void deleteconfigFolder() {
        // deletes an old config folder to start clean
        Path path = Paths.get(configFolder);
        try {
            Files.walk(path)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.out.println("Config folder can not be deleted");
        }
    }

    private void createTask() {
        // open taskrepo
        openTasksView();
        doubleClickOn(taskRepositoryExtension);
        sleep(1000);

        // create task
        clickOn("#newTaskNameTextField");
        write(taskName);
        clickOn("#createTaskButton");

        saveCurrentData();
    }

    private void placeBehaviour(Node destination) {
        placeTabElementHelper("#behavioursTab", behaviourName, destination);
    }

    private void placePlanType(Node destination) {
        placeTabElementHelper("#planTypesTab", planTypeName, destination);
    }

    private void placePlan(Node destination) {
        placeTabElementHelper("#plansTab", planName2, destination);
    }

    private void placeTabElementHelper(String tabQuery, String elemenQuery, Node destination) {
        clickOn(tabQuery);
        type(KeyCode.TAB);
        for (int i = 0; i < 5; i++) {
            type(KeyCode.PAGE_DOWN);
        }
        drag(elemenQuery).dropTo(destination);
    }

    private void saveCurrentData() {
        sleep(2000);
        press(KeyCode.CONTROL, KeyCode.S);
        release(KeyCode.CONTROL, KeyCode.S);
    }

    private void drawInitTransition(Node from, Node to) {
        clickOn("#InitTransitionToolButton");
        drawTransitionHelper(from, to);
    }


    private void drawTransition(Node from, Node to) {
        pickTransitionTool();
        drawTransitionHelper(from, to);
    }

    private void pickTransitionTool() {
        clickOn("#TransitionToolButton");
    }

    private void drawTransitionHelper(Node from, Node to) {
        clickOn(from);
        clickOn(to);
        dropElement();
    }

    private void placeEntryPoint() {
        // place entryPoint
        clickOn("#EntryPointToolButton");
        clickOn(freePlanContentPos());

        // choose created task
        clickOn("#taskComboBox");
        clickOn(taskName);
        clickOn("#confirmTaskChoiceButton");

        dropElement();
    }

    private PointQuery freePlanContentPos() {
        return offset(planContentId, ++planElementsCounter * 80, 0);
    }

    private void placeState() {
        clickOn("#StateToolButton");
        clickOn(freePlanContentPos());
        dropElement();
    }

    private void setContainerName(String containerId, String oldName, String newName) {
        Node container = getContainerNode(containerId, oldName);
        doubleClickOn(container);
        moveTo(propertySheetId);
        Node nameField = lookup(oldName).queryAll().stream()
                .filter(n -> n instanceof TextField)
                .findAny()
                .get();
        clickOn(nameField);
        write(newName);
    }

    private Node getContainerNode(String containerId) {
        return lookup(containerId).queryAll().stream()
                .findAny()
                .get();
    }

    private Node getContainerNode(String containerId, String name) {
        return lookup(containerId).queryAll().stream()
                .filter(n -> {
                    StateContainer st = (StateContainer) n;
                    String stName = st.getState().getName();
                    return stName.equals(name);
                })
                .findAny()
                .get();
    }

    private void setStateContainerName(String oldName, String newName) {
        setContainerName(stateContainerId, oldName, newName);
    }

    private void setSuccessStateContainerName(String oldName, String newName) {
        setContainerName(successStateContainerId, oldName, newName);
    }

    private void placeSuccessState() {
        clickOn("#SuccessStateToolButton");
        clickOn(freePlanContentPos());
        dropElement();
    }

    private void dropElement() {
        type(KeyCode.ESCAPE);
    }

    private void createPlan(String planName) {
        createPlansElementHelper("Plan", planName, planNameExtension);
    }

    private void createPlanType() {
        createPlansElementHelper("PlanType", planTypeName, planTypeNameExtension);
    }

    private void createBehaviour() {
        createPlansElementHelper("Behaviour", behaviourName, behaviourNameExtension);
    }

    private void createPlansElementHelper(String type, String name, String nameExtension) {
        rightClickOn("plans");
        clickOn("New");
        moveTo("Plan");  // avoid closing menu if mouse is outside of menu dialog
        clickOn(type);
        clickOn("#nameTextField");
        write(name);
        clickOn("#createButton");

        closeFileThreeElements();
        openPlansView();
        assertExists(planNameExtension);
    }

    private void createRoleSet() {
        rightClickOn("roles");
        clickOn("New");
        moveTo("Plan");  // avoid closing menu if mouse is outside of menu dialog
        clickOn("RoleSet");
        clickOn("#nameTextField");
        write(roleSetName);
        clickOn("#createButton");

        openRolesView();
        assertExists(roleSetNameExtension);
    }

    private void openRoleSet() {
        doubleClickOn(roleSetNameExtension);
    }

    private void createRoles() {
        Node firstListElement = lookup("#RoleTableView").lookup("").selectAt(1).queryFirst();

        // create first role
        clickOn(firstListElement);
        write(roleName1);
        type(KeyCode.ENTER);

        // create second role
        type(KeyCode.ENTER);
        write(roleName2);
        type(KeyCode.ENTER);

        assertExists(roleName1);
        assertExists(roleName2);
    }

    private void openPlan() {
        openPlansView();
        doubleClickOn(planNameExtension);
    }

    private void deletePlan() {
        openPlansView();
        clickOn(planNameExtension);
        type(KeyCode.DELETE);

        assertNotExists(planNameExtension);
    }

    private void deleteBehaviour() {
        openPlansView();
        clickOn(behaviourNameExtension);
        type(KeyCode.DELETE);

        assertNotExists(behaviourNameExtension);
    }

    private void deleteRoleSet() {
        openRolesView();
        clickOn(roleSetNameExtension);
        type(KeyCode.DELETE);

        assertNotExists(roleSetNameExtension);
    }

    private void openPlansView() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_UP);
        type(KeyCode.RIGHT);
    }

    private void closeFileThreeElements() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_UP);
        for (int i = 0; i < 5; i++) {
            type(KeyCode.LEFT);
            type(KeyCode.DOWN);
        }
    }

    private void openRolesView() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_UP);
        type(KeyCode.DOWN);
        type(KeyCode.RIGHT);
    }

    private void openTasksView() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_DOWN);
        type(KeyCode.RIGHT);
    }

    private void assertExists(String query) {
        sleep(1000);
        Assert.assertEquals(1, lookup(query).queryAll().size());
    }

    private void assertNotExists(String query) {
        sleep(1000);
        Assert.assertEquals(0, lookup(query).queryAll().size());
    }

    private void checkConfig() {
        ModelManager modelManager = new ModelManager();
        Configuration activeConfiguration = ConfigurationManager.getInstance().getActiveConfiguration();
        String plansPath = activeConfiguration.getPlansPath();
        String tasksPath = activeConfiguration.getTasksPath();
        String rolesPath = activeConfiguration.getRolesPath();
        modelManager.setPlansPath(plansPath);
        modelManager.setTasksPath(tasksPath);
        modelManager.setRolesPath(rolesPath);
        modelManager.loadModelFromDisk();

        // get root elements
        ArrayList<Plan> plans = modelManager.getPlans();
        Plan plan = plans.stream().filter(p -> p.getName().equals(planName)).findFirst().get();

        List<EntryPoint> entryPoints = plan.getEntryPoints();
        EntryPoint entryPoint = entryPoints.get(0);

        List<State> states = plan.getStates();
        State terminalState = states.stream().filter(s -> s instanceof TerminalState).findFirst().get();
        State state = states.stream().filter(s -> !s.equals(terminalState)).findFirst().get();

        List<Transition> transitions = plan.getTransitions();
        Transition transition = transitions.get(0);

        ArrayList<Behaviour> behaviours = modelManager.getBehaviours();
        Behaviour behaviour = behaviours.stream().filter(b -> b.getName().equals(behaviourName)).findFirst().get();

        List<de.unikassel.vs.alica.planDesigner.alicamodel.Configuration> configurations = behaviour.getConfigurations();
        de.unikassel.vs.alica.planDesigner.alicamodel.Configuration configuration = configurations.get(0);

        List<Task> tasks = modelManager.getTasks();
        Task task = tasks.stream().filter(t -> t.getName().equals(taskName)).findFirst().get();

        // test plan
        Assert.assertNotNull(plan.getId());
        Assert.assertEquals(planName, plan.getName());
        Assert.assertEquals("", plan.getComment());
        Assert.assertEquals("", plan.getRelativeDirectory());
        Assert.assertEquals(0, plan.getVariables().size());
        Assert.assertFalse(plan.getMasterPlan());
        Assert.assertEquals(0.0, plan.getUtilityThreshold(), 0.0);
        Assert.assertNull(plan.getPreCondition());
        Assert.assertNull(plan.getRuntimeCondition());

        // test entrypoint
        Assert.assertEquals(1, entryPoints.size());
        Assert.assertNotNull(entryPoint.getId());
        Assert.assertEquals(String.valueOf(entryPoint.getId()), entryPoint.getName());
        Assert.assertEquals("", entryPoint.getComment());
        Assert.assertFalse(entryPoint.getSuccessRequired());
        Assert.assertEquals(0, entryPoint.getMinCardinality());
        Assert.assertEquals(0, entryPoint.getMaxCardinality());
        Assert.assertEquals(task.getId(), entryPoint.getTask().getId());
        Assert.assertEquals(state.getId(), entryPoint.getState().getId());
        Assert.assertEquals(plan.getId(), entryPoint.getPlan().getId());

        // test state
        // missing: type
        Assert.assertNotNull(state.getId());
        Assert.assertEquals("Default Name", state.getName());
        Assert.assertEquals("", state.getComment());
        Assert.assertEquals(entryPoint.getId(), state.getEntryPoint().getId());
        Assert.assertEquals(plan.getId(), state.getParentPlan().getId());
        Assert.assertEquals(1, state.getAbstractPlans().size());
        Assert.assertEquals(configuration.getId(), state.getAbstractPlans().get(0).getId());
        Assert.assertEquals(0, state.getVariableBindings().size());
        Assert.assertEquals(transition.getId(), state.getOutTransitions().get(0).getId());
        Assert.assertEquals(0, state.getInTransitions().size());

        // test terminalState
        // missing: type
        Assert.assertNotNull(terminalState.getId());
        Assert.assertEquals("Default Name", terminalState.getName());
        Assert.assertEquals("", terminalState.getComment());
        Assert.assertNull(terminalState.getEntryPoint());
        Assert.assertEquals(plan.getId(), terminalState.getParentPlan().getId());
        Assert.assertEquals(0, terminalState.getAbstractPlans().size());
        Assert.assertEquals(0, terminalState.getVariableBindings().size());
        Assert.assertEquals(0, terminalState.getOutTransitions().size());
        Assert.assertEquals(transition.getId(), terminalState.getInTransitions().get(0).getId());
        // missing: success
        // missing: postCondition

        // test transition
        Assert.assertNotNull(transition.getId());
        Assert.assertEquals("FromDefault NameToDefault Name", transition.getName());
        Assert.assertEquals("MISSING_COMMENT", transition.getComment());
        Assert.assertEquals(state.getId(), transition.getInState().getId());
        Assert.assertEquals(terminalState.getId(), transition.getOutState().getId());
        Assert.assertNull(transition.getPreCondition());
        Assert.assertNull(transition.getSynchronisation());

        // test synchronisations
        Assert.assertEquals(0, plan.getSynchronisations().size());

        // test behaviour
        Assert.assertNotNull(behaviour.getId());
        Assert.assertEquals(behaviourName, behaviour.getName());
        Assert.assertEquals("", behaviour.getComment());
        Assert.assertEquals("", behaviour.getRelativeDirectory());
        Assert.assertEquals(0, behaviour.getVariables().size());
        Assert.assertEquals(0, behaviour.getFrequency());
        Assert.assertEquals(0, behaviour.getDeferring());
        Assert.assertNull(behaviour.getPreCondition());
        Assert.assertNull(behaviour.getRuntimeCondition());
        Assert.assertNull(behaviour.getPostCondition());

        Assert.assertNotNull(configuration.getId());
        Assert.assertEquals("default", configuration.getName());
        Assert.assertEquals("", configuration.getComment());
        Assert.assertEquals("", configuration.getRelativeDirectory());
        Assert.assertEquals(0, configuration.getVariables().size());
        Assert.assertEquals(behaviour.getId(), configuration.getBehaviour().getId());
        Assert.assertEquals(0, configuration.getKeyValuePairs().size());
    }
}
