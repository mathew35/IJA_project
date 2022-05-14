  
/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 *
 * @author Adam Bazel (xbazel00)
 * @author Matůš Vráblik (xvrabl05)
 * @since  2022-04-12
 */

package project;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.geometry.VPos;
import javafx.geometry.Pos;

import uml.*;

/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 */
public class EditorController extends MenuBarController implements Initializable
{
    public SequenceDiagram sequenceDiagram = new SequenceDiagram();
    GridPane seqGrid = new GridPane();
    GridPane seqGridMsgs = new GridPane();
    double msgWidth;

    ArrayList<SequenceController> sequenceControllers = new ArrayList<SequenceController>();
    
    @FXML
    private Pane ClassPaneDiag;

    @FXML
    private Pane ClassPaneText;

    @FXML
    private TreeView<String> AttributesTree;

    @FXML
    private TextField ClassName;
    
    @FXML
    private TextField NewClassName;

    @FXML
    private Label ClassErrorLabel;

    @FXML
    private ComboBox<String> ChoiceParentClass;

    @FXML
    private ComboBox<String> ChoiceChildClass;
    
    @FXML
    TabPane tabPane;

    @FXML
    private VBox addSeqObjFirst;

    @FXML
    private Button addSeqObjButton;

    @FXML
    private Button messageCreatorButton;

    @FXML
    private StackPane seqEditorBox;

    @FXML
    private VBox seqMsgBox;

    private UMLClass FocusedClass = null;
    /**
     * Po načtení scény provede prvně tyto úkony pro správné zobrazení a pracování aplikace.
     *
     * @param arg0 TODO.
     * @param arg1 TODO.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        Tab deleted = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(deleted);
        tabPane.getTabs().get(1).setClosable(false);
        selectTab(1);
    }
    public void updateClassTab(){
        //ComboBoxes
        List<String> ChItems = new ArrayList<String>(ChoiceChildClass.getItems());
        for(String i:ChItems){
            if(!classDiagram.getNameClasses().contains(i)){
                ChoiceChildClass.getItems().remove(i);
            }
        }
        List<String> PItems = new ArrayList<String>(ChoiceParentClass.getItems());
        for(String i:PItems){
            if(!classDiagram.getNameClasses().contains(i)){
                ChoiceParentClass.getItems().remove(i);
            }
        }
        for(String i:classDiagram.getNameClasses()){
            if(!ChoiceParentClass.getItems().contains(i)){
                ChoiceParentClass.getItems().add(i);
            }
            if(!ChoiceChildClass.getItems().contains(i)){
                ChoiceChildClass.getItems().add(i);
            }
        }
        NewClassName.setPromptText("New Class Name");
        drawClassDiagram();
    }
    public void updateSequenceControllers(){
        for(SequenceController i:sequenceControllers){
            i.setClassDiagram(classDiagram);
        }
    }
    public void updateSequenceClass(UMLClass oldClass, UMLClass newClass){
        for(SequenceController i:sequenceControllers){
            i.changeInClass(oldClass,newClass);
        }
    }
    public void setClassDiagram(ClassDiagram diag){
        classDiagram = diag;
        createSnapshot(classDiagram);
        classDiagram.deepCopy(snapshots.get(snapshotPos));
        updateSequenceControllers();
        refresh();
    }   

    //source: https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
    private final class TextFieldTreeCellImpl extends TreeCell<String> {
 
        private TextField textField;
        private ContextMenu addMenu = new ContextMenu();
 
        public TextFieldTreeCellImpl() {
            MenuItem addMenuItem = new MenuItem("New");
            Menu removeMenu = new Menu("Delete");
            addMenu.getItems().addAll(addMenuItem,removeMenu);
            addMenu.setOnShowing(new EventHandler<WindowEvent>(){
                public void handle(WindowEvent e){
                            TreeItem<String> actItem = getTreeItem();
                            removeMenu.getItems().removeAll(removeMenu.getItems());
                            for(TreeItem<String> i:actItem.getChildren()){
                                removeMenu.getItems().add(new MenuItem(i.getValue()));
                            }
                            for(MenuItem i:removeMenu.getItems()){
                                i.setOnAction(new EventHandler(){
                                    public void handle(Event t){
                                        TreeItem<String> actItem = getTreeItem();
                                        for(TreeItem<String> j:actItem.getChildren()){
                                            if(j.getValue().equals(i.getText())){
                                                actItem.getChildren().remove(j);
                                                break;
                                            }
                                        }
                                    }
                                });
                            }
                            if(actItem.getValue().equals("Attributes")||actItem.getValue().equals("Operations")){
                                addMenuItem.setText("New "+ actItem.getValue().toString().substring(0,actItem.getValue().toString().length()-1));
                            }
                            else if(actItem.getValue().equals("Arguments")){
                                addMenuItem.setText("New Argument");
                            }
                            else{
                                addMenu.getItems().removeAll(addMenu.getItems());
                            }
                }
            });
            addMenuItem.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    TreeItem actItem = getTreeItem();
                    if(actItem.getValue().equals("Attributes") || actItem.getValue().equals("Arguments")){
                        TreeItem newAttr = new TreeItem<String>("New " + actItem.getValue().toString().substring(0,actItem.getValue().toString().length()-1));
                        newAttr.getChildren().add(new TreeItem<String>("Type"));
                        getTreeItem().getChildren().add(newAttr);
                    }
                    if(actItem.getValue().equals("Operations")){
                        TreeItem newOper = new TreeItem<String>("New Operation");
                        newOper.getChildren().add(new TreeItem<String>("Arguments"));
                        newOper.getChildren().add(new TreeItem<String>("Return type"));
                        newOper.setExpanded(true);
                        getTreeItem().getChildren().add(newOper);
                    }
                }
            });
        }
 
        @Override
        public void startEdit() { 
            if(getTreeItem().getValue() == "Attributes" || getTreeItem().getValue() == "Operations" || getTreeItem().getValue() == "Arguments"){
                cancelEdit();
            }
            else{
                super.startEdit();
                if (textField == null) {
                    createTextField();
                }
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
        }
 
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
 
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    if (
                        getTreeItem().getParent()!= null
                    ){
                        setContextMenu(addMenu);
                    }
                }
            }
        }
 
        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
 
                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
    //end source
    /**
     * TODO
     */
    public void updateAttrTree() {
        if(FocusedClass != null){
            ClassName.setText(FocusedClass.getName());
        }
        if (ClassName.getText() != null){
            UMLClass itemClass = null;
            for(UMLClass i:classDiagram.getClasses()){
                if(i.getName().equals(ClassName.getText())){
                    itemClass = i;
                }
            }
            if(itemClass == null){
                AttributesTree.setRoot(null);
                ClassName.setText(null);
                return;
            }
            //AttributesTree
            TreeItem<String> rootItem = new TreeItem<String>(ClassName.getText());
            TreeItem<String> attr = new TreeItem<String>("Attributes");
            TreeItem<String> oper = new TreeItem<String>("Operations");
            attr.setExpanded(true);
            oper.setExpanded(true);

            for(UMLAttribute i:itemClass.getAttributes()){
                TreeItem<String> attrib = new TreeItem<String>(i.getName());
                TreeItem<String> type = new TreeItem<String>(i.getType().getName());
                attrib.getChildren().add(type); 
                attr.getChildren().add(attrib);
            }
            for(UMLOperation i:itemClass.getOperations()){
                TreeItem<String> opr = new TreeItem<String>(i.getName());
                TreeItem<String> args = new TreeItem<String>("Arguments");
                TreeItem<String> type = new TreeItem<String>(i.getType().getName());
                opr.getChildren().addAll(args, type);
                for(UMLAttribute arg:i.getArguments()){
                    TreeItem<String> atr = new TreeItem<String>(arg.getName());
                    TreeItem<String> atrType = new TreeItem<String>(arg.getType().getName());
                    atr.getChildren().add(atrType);
                    args.getChildren().add(atr);
                }
                oper.getChildren().add(opr);
            }
            rootItem.getChildren().add(attr);
            rootItem.getChildren().add(oper);
            rootItem.setExpanded(true);

            AttributesTree.setShowRoot(false);
            AttributesTree.setRoot(rootItem);
            AttributesTree.setEditable(true);
            AttributesTree.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
                @Override
                public TreeCell<String> call(TreeView<String> p) {
                    return new TextFieldTreeCellImpl();
                }
            });
        }
    }
    /**
     * TODO
     */
    public void onResetClick() {
        updateAttrTree();
    }
    /**
     * TODO
     */
    public void onUpdateClick() {
        if (!ClassName.getText().isEmpty()){
            UMLClass oldClass = new UMLClass();
            if(FocusedClass!=null){
                oldClass.rename(FocusedClass.getName());
                oldClass.deepCopyClass(FocusedClass);
            }
            UMLClass itemClass = null;
            for(UMLClass i:classDiagram.getClasses()){
                if(i.getName().equals(ClassName.getText())){
                    itemClass = i;
                }
            }
            boolean change = false;
            if(itemClass == null){
                if(FocusedClass == null){
                    return;
                }
                FocusedClass.rename(ClassName.getText()); 
                updateClassTab();
                updateAttrTree(); 
                updateSequenceClass(oldClass, FocusedClass);
                createSnapshot(classDiagram);
                updateSequenceControllers();
                change = true;             
                return;
            }
            TreeItem<String> oldRoot = AttributesTree.getRoot();
            TreeItem<String> TreeAttributes = oldRoot.getChildren().get(0);
            TreeItem<String> TreeOperations = oldRoot.getChildren().get(1);
            ArrayList<UMLAttribute> Attributes = new ArrayList<UMLAttribute>();
            ArrayList<UMLOperation> Operations = new ArrayList<UMLOperation>();
            for(TreeItem<String> i:TreeAttributes.getChildren()){
                Attributes.add(new UMLAttribute(i.getValue(),new UMLClassifier(i.getChildren().get(0).getValue())));
            }
            for(TreeItem<String> i:TreeOperations.getChildren()){
                UMLOperation newOper = new UMLOperation(i.getValue(),new UMLClassifier(i.getChildren().get(1).getValue()));
                for(TreeItem<String> j:i.getChildren().get(0).getChildren()){
                    UMLAttribute arg = new UMLAttribute(j.getValue(),new UMLClassifier(j.getChildren().get(0).getValue()));
                    newOper.addArgument(arg);
                }
                Operations.add(newOper);
            }
            List<UMLAttribute> toRmAttr = new ArrayList<UMLAttribute>();
            for(UMLAttribute i:itemClass.getAttributes()){
                boolean found = false;
                for(UMLAttribute j:Attributes){
                    if(j.getName().equals(i.getName()) && j.getType().getName().equals(i.getType().getName())){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    change = true;
                    toRmAttr.add(i);
                }
            }
            List<UMLOperation> toRmOper = new ArrayList<UMLOperation>();
            for(UMLOperation i:itemClass.getOperations()){
                boolean found = false;
                for(UMLOperation j:Operations){
                    if(j.getName().equals(i.getName()) && j.getType().getName().equals(i.getType().getName())){
                        found = true;
                        for(UMLAttribute k:i.getArguments()){
                            boolean foundAttr = false;
                            for(UMLAttribute l:j.getArguments()){
                                if(l.getName().equals(k.getName()) && l.getType().getName().equals(k.getType().getName())){
                                    foundAttr = true;
                                    break;
                                }
                            }
                            if(!foundAttr){
                                found = false;
                                break;
                            }
                        }
                        break;
                    }
                }
                if(!found){
                    change = true;
                    toRmAttr.add(i);
                }
            }
            if(Attributes.size() != itemClass.getAttributes().size() || Operations.size() != itemClass.getOperations().size()){
                change = true;
            }
            List<String> toRemove = new ArrayList<String>();
            for(UMLAttribute i:itemClass.getAttributes()){
                toRemove.add(i.getName());
            }
            for(String i:toRemove){
                itemClass.removeAttrOper(i);
            }
            toRemove.removeAll(toRemove);
            for(UMLOperation i:itemClass.getOperations()){
                toRemove.add(i.getName());
            }
            for(String i:toRemove){
                itemClass.removeAttrOper(i);
            }
            for(UMLAttribute i:Attributes){
                itemClass.addAttribute(i);
            }
            for(UMLOperation i:Operations){
                itemClass.addOperation(i);
            }
            updateClassTab();
            updateAttrTree();
            if(change){
                updateSequenceClass(oldClass, itemClass);
                createSnapshot(classDiagram);
                updateSequenceControllers();
            }
        }
    }
    /**
     * TODO
     */
    public void SelectClass() {
    }
    /**
     * TODO
     */
    public void refresh(){
        updateClassTab();
        updateAttrTree();
    }
    /**
    * Přidání třídy do diagramu tříd
    */
    public void onAddClassClick(){
        String text = NewClassName.getText();
        ClassErrorLabel.setText(null);
        if (text.isEmpty()) {
            ClassErrorLabel.setText("Zadaj nazov novej triedy");
            return;
        }
        UMLClass res = classDiagram.createClass(text);
        if (res == null){
            ClassErrorLabel.setText("Trieda uz existuje!");
            return;
        }
        updateClassTab();
        createSnapshot(classDiagram);
        updateSequenceControllers();
    }
    /**
    * Přidání podtřídy ke vybrané tříde v diagramu tříd
    */
    public void onAddSubclassClick(){        
        String text = NewClassName.getText();
        String parentClass = ChoiceParentClass.getSelectionModel().getSelectedItem();
        ClassErrorLabel.setText(null);
        if (text.isEmpty()) {
            ClassErrorLabel.setText("Zadaj nazov novej triedy");
            return;
        }
        if (parentClass == null){
            ClassErrorLabel.setText("Vyber rodicovsku triedu");
            return;
        }
        UMLClass res = classDiagram.createClass(text);
        if (res == null){
            ClassErrorLabel.setText("Trieda uz existuje!");
            return;
        }
        for(UMLClass i:classDiagram.getClasses()){
            if(i.getName().equals(parentClass)){
                res.setParent(i);
                break;
            }
        }
        updateClassTab();
        createSnapshot(classDiagram);
        updateSequenceControllers();
    }

    /**
     * TODO
     */
    public void onRemoveClassClick(){
        String deleteClassName = ChoiceParentClass.getSelectionModel().getSelectedItem();
        ClassErrorLabel.setText(null);
        if (deleteClassName == null){
            ClassErrorLabel.setText("Vyber triedu na zmazanie");
            return;
        }
        classDiagram.removeClass(deleteClassName);
        updateClassTab();
        createSnapshot(classDiagram);
        updateSequenceControllers();
    }

    /**
     * TOOD
     */
    public void onChangeParentClick(){
        String newParentName = ChoiceParentClass.getSelectionModel().getSelectedItem();
        String ChildName = ChoiceChildClass.getSelectionModel().getSelectedItem();
        ClassErrorLabel.setText(null);
        if (ChildName == null){
            ClassErrorLabel.setText("Vyber child class");
            return;
        }
        if (newParentName == null){
            ClassErrorLabel.setText("Vyber rodicovsku triedu");
            return;
        }
        UMLClass newParentClass = null;
        UMLClass ChildClass = null;
        for(UMLClass i:classDiagram.getClasses()){
            if(i.getName()==newParentName){
                newParentClass = i;    
            }
            if(i.getName()==ChildName){
                ChildClass = i;    
            }
        }
        if(newParentName == ChildName){
            ChildClass.setParent(null);
            updateClassTab();
            createSnapshot(classDiagram);
            updateSequenceControllers();
            return;
        }
        UMLClass commonParent = null;
        UMLClass pPar = newParentClass;
        UMLClass chPar = ChildClass;
        while(commonParent == null){
            if(chPar == null){
                if(pPar == null){
                    commonParent = chPar;
                    break;
                }
                pPar = pPar.getParent();                
                chPar = ChildClass;
            }
            if(pPar == chPar){
                commonParent = chPar;
            }
            else{
                chPar = chPar.getParent();
            }
        }        
        if(commonParent == ChildClass){
            newParentClass.setParent(ChildClass.getParent());      
        }
        ChildClass.setParent(newParentClass);
        updateClassTab();
        createSnapshot(classDiagram);
        updateSequenceControllers();
    }
    /**
     * TODO
    */
    public void drawClassDiagram(){
        ClassPaneText.getChildren().removeAll(ClassPaneText.getChildren());
        ArrayList<ArrayList<UMLClass>> LeveledClasses = new ArrayList<>();
        int rootCount = 0;
        int maxLevel = 0;
        for(UMLClass i:classDiagram.getClasses()){
            UMLClass parent = i.getParent();
            int level = 0;
            while(parent != null){
                parent = parent.getParent();
                level++;
            }
            while(level+1 > LeveledClasses.size()){
                LeveledClasses.add(new ArrayList<UMLClass>());
            }
            if(level >= 0 && level < LeveledClasses.size()){
                LeveledClasses.get(level).add(i);
            }
            if(level+1>maxLevel){
                maxLevel = level+1;
            }
        }
        int columns = 1;
        for(ArrayList<UMLClass> i:LeveledClasses){
            if(i.size()>columns){
                columns = i.size();
            }
        }
        for(ArrayList<UMLClass> i:LeveledClasses){
            for(UMLClass uclass:i){
                drawClass(uclass,i.indexOf(uclass)+1,i.size(),LeveledClasses.indexOf(i)+1,maxLevel);
            }
        }

    }
    /**
     * TODO
     */
    public void drawClass(UMLClass uclass,int column,int allcolumns,int level,int maxlevel){
        double posX = ClassPaneText.getPrefWidth()/(1+allcolumns)*column;
        double posY = ClassPaneText.getPrefHeight()/(1+maxlevel)*level;
        VBox rect = new VBox();
        Label name = new Label();
        Separator sep = new Separator();
        ScrollPane contentScroll = new ScrollPane();
        VBox contentBox = new VBox();
        rect.setMaxHeight(80);
        rect.setPrefWidth(180);
        rect.setLayoutX(posX-rect.getPrefWidth()/2);
        rect.setLayoutY(posY);
        rect.setStyle("-fx-border-width:1px; -fx-border-color:black;");
        rect.getChildren().addAll(name,sep,contentScroll);
        rect.setAlignment(Pos.CENTER);
        rect.setOnMouseClicked((e)->{
            FocusedClass = uclass;
            updateAttrTree();
        });
        name.setText(uclass.getName());
        sep.setStyle("-fx-background-color:BLACK;");
        contentScroll.setContent(contentBox);
        contentScroll.setPrefWidth(rect.getPrefWidth());
        contentScroll.setMaxHeight(170);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        TextFlow attr = new TextFlow();
        attr.setPrefWidth(contentScroll.getPrefWidth()-18);
        Text attrtxt = new Text();
        attr.getChildren().add(attrtxt);
        attrtxt.setText(formatA(uclass.getAttributes()));
        TextFlow oper = new TextFlow();
        oper.setPrefWidth(contentScroll.getPrefWidth()-18);
        Text opertxt = new Text();
        oper.getChildren().add(opertxt);
        opertxt.setText(formatO(uclass.getOperations()));
        if(!uclass.getAttributes().isEmpty()){
            contentBox.getChildren().add(attr);
        }
        if(!uclass.getOperations().isEmpty()){
            if(!contentBox.getChildren().isEmpty()){
                Separator sep2 = new Separator();
                // sep2.setPrefWidth(contentScroll.getPrefWidth());
                sep2.setStyle("-fx-background-color:BLACK;");
                contentBox.getChildren().add(sep2);
            }
            contentBox.getChildren().add(oper);
        }
        // contentBox.getChildren().add()
        ClassPaneText.getChildren().add(rect);
    }
    /**
     * TODO
     */
    public String formatA(List<UMLAttribute> list){
        String str = "";
        for(UMLAttribute i: list){
            str = str + "+" + i.getName() + ": " + i.getType().getName() + "\n";
        }
        if(!str.isEmpty()){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }
    public String formatO(List<UMLOperation> list){
        String str = "";
        for(UMLOperation i: list){
            str = str + "+" + i.getName() + "(";
            String substr = "";
            for(UMLAttribute j:i.getArguments()){
                if(!substr.isEmpty()){
                    substr = substr + ", " + j.getName();
                }
                substr = substr + j.getName()+": " + j.getType().getName();
            }
            str = str + substr + ")";
            if(!i.getType().getName().isEmpty()){
                str = str + ": " + i.getType().getName();
            }
            str = str + "\n";
        }
        if(!str.isEmpty()){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }
     /**
    * Přidání karty do panelu karet 
     * @throws IOException
    */
    @FXML 
    public void addTab() throws IOException {
        int numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Sequence Diagram "+(numTabs-1));

        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sequence.fxml"));
        tab.setContent(loader.load());

        SequenceController controller = loader.getController();

        sequenceControllers.add(controller);
        updateSequenceControllers();

        tab.setOnCloseRequest(e -> {
            
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save sequence diagram?",  ButtonType.NO, ButtonType.YES);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                ObjectMapper objectMapper = new ObjectMapper();
                controller.exportSequence(objectMapper);
                sequenceControllers.remove(tabPane.getSelectionModel().getSelectedIndex() - 2);
            }
        });

        tab.setClosable(true);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    @FXML
    public void requestSequenceSave()
    {

    }

    @FXML
    public void selectTab(int tabIndex)
    {
        tabPane.getSelectionModel().select(tabIndex);
    }
}