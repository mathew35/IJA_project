  
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

import org.w3c.dom.Attr;

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
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.TextAlignment;
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
    private TreeView<String> ClassTree;
    
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
        //this.setClassDiagram(classDiagram);
        // updateClassTab();
    }
    public void updateClassTab(){
        //ClassTreeView
        TreeItem<String> rootItem = ClassTree.getRoot();
        rootItem.getChildren().removeAll(rootItem.getChildren());
        for(UMLClass i:classDiagram.getClasses()){
            TreeItem<String> newItem = new TreeItem<String> (i.getName());
            newItem.setExpanded(true);
            rootItem.getChildren().add(newItem);
        }
        for(UMLClass i:classDiagram.getClasses()){
            if(i.getParent() != null){
                TreeItem<String> parent = searchTreeView(i.getParent().getName(), rootItem);
                TreeItem<String> child = searchTreeView(i.getName(), rootItem);
                ChangeParent(parent,child);
            }
        }
        //ComboBoxes
        String selectedP = ChoiceParentClass.getSelectionModel().getSelectedItem();
        String selectedCH = ChoiceChildClass.getSelectionModel().getSelectedItem();
        ChoiceParentClass.getItems().removeAll(ChoiceParentClass.getItems());
        ChoiceChildClass.getItems().removeAll(ChoiceChildClass.getItems());
        for(UMLClass i:classDiagram.getClasses()){
            ChoiceParentClass.getItems().add(i.getName());
            ChoiceChildClass.getItems().add(i.getName());
        }
        drawClassDiagram();
        NewClassName.setText("");
    }
    public void setClassDiagram(ClassDiagram diag){
        classDiagram = diag;
        TreeItem<String> rootItem = new TreeItem<>(classDiagram.getName());
        for(UMLClass i:classDiagram.getClasses()){
            TreeItem<String> newClass = new TreeItem<>(i.getName());
            rootItem.getChildren().add(newClass);
        }
        ClassTree.setShowRoot(false);
        ClassTree.setRoot(rootItem);
        ClassName.setPromptText("Class Name");
        updateClassTab();
        createSnapshot(classDiagram);
    }   

    /**
    * Detekuje selekci třídy v ClassDiagram tabu a zobrazí informace o této tříde
    */
    public void selectItem(){
        TreeItem<String> item = ClassTree.getSelectionModel().getSelectedItem();
        if(item != null){
            ClassName.setText(item.getValue());
            updateAttrTree();
        }
    }
    //source: https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
    private final class TextFieldTreeCellImpl extends TreeCell<String> {
 
        private TextField textField;
        private ContextMenu addMenu = new ContextMenu();
 
        public TextFieldTreeCellImpl() {
            MenuItem addMenuItem = new MenuItem("New Item");
            addMenu.getItems().add(addMenuItem);
            addMenuItem.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    TreeItem newItem = 
                    new TreeItem<String>("New Item");
                        getTreeItem().getChildren().add(newItem);
                }
            });
        }
 
        @Override
        public void startEdit() { 
            if(getTreeItem().getValue() == "Attributes" || getTreeItem().getValue() == "Operations"){
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
                        !getTreeItem().isLeaf()&&getTreeItem().getParent()!= null
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
                attr.getChildren().add(new TreeItem<String>(i.getName()));
            }
            for(UMLOperation i:itemClass.getOperations()){
                oper.getChildren().add(new TreeItem<String>(i.getName()));
            }
            if(attr.getChildren().isEmpty()){
                attr.getChildren().add(new TreeItem<String>(""));
            }
            if(oper.getChildren().isEmpty()){
                oper.getChildren().add(new TreeItem<String>(""));
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
            UMLClass itemClass = null;
            for(UMLClass i:classDiagram.getClasses()){
                if(i.getName().equals(ClassName.getText())){
                    itemClass = i;
                }
            }
            boolean change = false;
            if(itemClass == null){
                String name = ClassTree.getSelectionModel().getSelectedItem().getValue();
                for(UMLClass i:classDiagram.getClasses()){
                    if(i.getName().equals(name)){
                        itemClass = i;
                    }
                }
                itemClass.rename(ClassName.getText());   
                change = true;             
            }
            TreeItem<String> oldRoot = AttributesTree.getRoot();
            ArrayList<String> Attributes = new ArrayList<String>();
            ArrayList<String> Operations = new ArrayList<String>();
            for(TreeItem<String>i:oldRoot.getChildren().get(0).getChildren()){
                if(!i.getValue().equals("")){
                    Attributes.add(i.getValue());
                }
            }
            for(TreeItem<String>i:oldRoot.getChildren().get(1).getChildren()){
                if(!i.getValue().equals("")){
                    Operations.add(i.getValue());
                }
            }
            ArrayList<UMLAttribute> newAttributes = new ArrayList<UMLAttribute>();
            ArrayList<UMLOperation> newOperations= new ArrayList<UMLOperation>();
            for(String i:Attributes){
                newAttributes.add(new UMLAttribute(i,null));
            }
            for(String i:Operations){
                newOperations.add(new UMLOperation(i,null));
            }
            if(Attributes.size() == itemClass.getAttributes().size()){
                for(String s:Attributes){
                    boolean found = false;
                    for(UMLAttribute a: itemClass.getAttributes()){
                        if(s.equals(a.getName())){
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        change = true;
                        break;
                    }
                }
            }
            if(Operations.size() == itemClass.getOperations().size()){
                for(String s:Operations){
                    boolean found = false;
                    for(UMLAttribute a: itemClass.getOperations()){
                        if(s.equals(a.getName())){
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        change = true;
                        break;
                    }
                }
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
            for(UMLAttribute i:newAttributes){
                itemClass.addAttribute(i);
            }
            for(UMLOperation i:newOperations){
                itemClass.addOperation(i);
            }
            updateClassTab();
            updateAttrTree();
            if(change){
                createSnapshot(classDiagram);
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
     * TODO
     */
    public TreeItem<String> searchTreeView(String find, TreeItem<String> list){
        TreeItem<String> res = null;
        for(TreeItem<String> i:list.getChildren()){
            if (i.getValue().equals(find)){
                res = i;
            }
            if (res == null){
                res = searchTreeView(find, i);
            }
        }
        return res;
    }
    /**
     * TODO
     */
    public TreeItem<String> getTreeParent(TreeItem<String> child,TreeItem<String> Origin){
        TreeItem<String> res = ClassTree.getRoot();
        for(TreeItem<String> i:Origin.getChildren()){
            if (i.getChildren().contains(child)){
                res = i;
            }
            if (res == ClassTree.getRoot()){
                res = getTreeParent(child, i);
            }
        }
        return res;
    }
    /**
     * TODO
     */
    public void removeTreeBranch(TreeItem<String> branch,TreeItem<String> Origin){
        TreeItem<String> parent = getTreeParent(branch,Origin);
        parent.getChildren().remove(parent.getChildren().indexOf(branch));
    }
    /**
     * TODO
     */
    public void removeTreeItem(TreeItem<String> rmItem,TreeItem<String> Origin){
        TreeItem<String> parent = getTreeParent(rmItem,Origin);
        for(TreeItem<String> i:rmItem.getChildren()){
            parent.getChildren().add(i);
        }
        parent.getChildren().remove(parent.getChildren().indexOf(rmItem));
    }
    /**
     * TODO
     */
    public void ChangeParent(TreeItem<String> newParent,TreeItem<String> newChild){
        TreeItem<String> chParent = getTreeParent(newChild, ClassTree.getRoot());
        removeTreeBranch(newChild, chParent);
        newParent.getChildren().add(newChild);
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
    }
    /**
    * Přidání podtřídy ke vybrané tříde v diagramu tříd
    */
    public void onAddSubclassClick(){        
        TreeItem<String> rootItem = ClassTree.getRoot();
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
    }

    /**
     * TODO
     */
    public void onRemoveClassClick(){
        TreeItem<String> rootItem = ClassTree.getRoot();
        String deleteClassName = ChoiceParentClass.getSelectionModel().getSelectedItem();
        ClassErrorLabel.setText(null);
        if (deleteClassName == null){
            ClassErrorLabel.setText("Vyber triedu na zmazanie");
            return;
        }
        TreeItem<String> deleteClass = null;
        deleteClass = searchTreeView(deleteClassName,rootItem);
        if (deleteClass == null){
            ClassErrorLabel.setText("Class not found");
            return;
        }
        TreeItem<String> parent = getTreeParent(deleteClass,rootItem);
        UMLClass parentClass = null;
        for(UMLClass i:classDiagram.getClasses()){
            if(i.getName() == parent.getValue()){
                parentClass = i;
                break;
            }
        }
        for(TreeItem<String> i:deleteClass.getChildren()){
            for(UMLClass j:classDiagram.getClasses()){
                if(i.getValue()==j.getName()){
                    j.setParent(parentClass);
                }
            }
        }
        classDiagram.removeClass(deleteClassName);
        updateClassTab();
        createSnapshot(classDiagram);
    }

    /**
     * TOOD
     */
    public void onChangeParentClick(){
        TreeItem<String> rootItem = ClassTree.getRoot();
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
            // Rectangle rec = new Rectangle();
            // rec.setX(ClassPaneDiag.getPrefWidth()/(rootCount)-100);
            // rec.setY(ClassPaneDiag.getPrefHeight()/5-50);
            // rec.setWidth(200);
            // rec.setHeight(100);
            // rec.setFill(Color.BLUE.deriveColor(1,1,1,0.3));
            // Text txt = new Text("Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text Try Text");
            // txt.setWrappingWidth(200-txt.getFont().getSize());
            // TextFlow txtflow = new TextFlow(txt);
            // // txtflow.setLayoutX(ClassPaneDiag.getPrefWidth()/(rootCount)-100);
            // // txtflow.setLayoutY(ClassPaneDiag.getPrefHeight()/5-50);
            // txtflow.setMaxWidth(160);
            // // txtflow.setMaxHeight(100);
            // ScrollPane txtScroll = new ScrollPane();
            // txtScroll.setLayoutX(ClassPaneDiag.getPrefWidth()/(rootCount)-100);
            // txtScroll.setLayoutY(ClassPaneDiag.getPrefHeight()/5-50);
            // txtScroll.setMaxWidth(200);
            // txtScroll.setPrefHeight(100);
            // txtScroll.setStyle("-fx-font-size: "+txt.getFont().getSize()+"px;");
            // txtScroll.setContent(txtflow);
            // ClassPaneText.getChildren().add(txtScroll);
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
        rect.setMaxHeight(200);
        rect.setPrefWidth(150);
        rect.setLayoutX(posX-rect.getPrefWidth()/2);
        rect.setLayoutY(posY);
        rect.setStyle("-fx-border-width:1px; -fx-border-color:black;");
        rect.getChildren().addAll(name,sep,contentScroll);
        rect.setAlignment(Pos.CENTER);
        name.setText(uclass.getName());
        sep.setStyle("-fx-background-color:BLACK;");
        contentScroll.setContent(contentBox);
        contentScroll.setPrefWidth(150);
        contentScroll.setMaxHeight(170);
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
            str = str + i.getName();
        }
        return str;
    }
    public String formatO(List<UMLOperation> list){
        String str = "";
        for(UMLOperation i: list){
            str = str + i.getName() +"\n";
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

        tab.setOnCloseRequest(e -> {
            
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save sequence diagram?",  ButtonType.NO, ButtonType.YES);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                ObjectMapper objectMapper = new ObjectMapper();
                controller.exportSequence(objectMapper);
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