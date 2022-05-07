  
/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 *
 * @author Adam Bazel (xbazel00)
 * @author Matůš Vráblik (xvrabl05)
 * @since  2022-04-12
 */

package project;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import javafx.fxml.*;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

import uml.*;

/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 */
public class EditorController implements Initializable
{
    ClassDiagram classDiagram = new ClassDiagram("genereted");
    public ArrayList<SequenceDiagram> sequenceDiagrams = new ArrayList<SequenceDiagram>();
    GridPane seqGrid = new GridPane();
    GridPane seqGridMsgs = new GridPane();
    double msgWidth;

    @FXML
    private TreeView<String> ClassTree;

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
    private TabPane tabPane;

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
        this.setClassDiagram(classDiagram);
        Tab deleted = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(deleted);
        updateClassTab();
    }
    public void updateClassTab(){
        //TreeView
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
        ChoiceParentClass.getSelectionModel().select(selectedP);
        ChoiceChildClass.getSelectionModel().select(selectedCH);
        selectedP = ChoiceParentClass.getSelectionModel().getSelectedItem();
        if(selectedP == null){
            ChoiceParentClass.getSelectionModel().clearSelection();
        }
        System.out.println(selectedP);
        ChoiceParentClass.setPromptText("Select Parent Class");
        ChoiceChildClass.setPromptText("Select Child Class");
        ChoiceParentClass.getSelectionModel().clearSelection();
        //TextField
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
    }

    /**
    * Detekuje selekci třídy v ClassDiagram tabu a zobrazí informace o této tříde
    */
    public void selectItem(){
        TreeItem<String> item = ClassTree.getSelectionModel().getSelectedItem();
        if (item != null){
            ClassName.setText(item.getValue());
            UMLClass itemClass = null;
            for(UMLClass i:classDiagram.getClasses()){
                if(i.getName()==item.getValue()){
                    itemClass = i;
                }
            }
            TreeItem<String> rootItem = new TreeItem<String>(item.getValue());
            AttributesTree.setShowRoot(false);
            AttributesTree.setRoot(rootItem);
            TreeItem<String> attr = new TreeItem<String>("Attributes");
            TreeItem<String> func = new TreeItem<String>("Functions");
            TreeItem<String> oper = new TreeItem<String>("Operations");
            for(UMLOperation i:itemClass.getOperations()){
                oper.getChildren().add(new TreeItem<String>(i.getName()));
            }
            for(UMLAttribute i:itemClass.getAttributes()){
                attr.getChildren().add(new TreeItem<String>(i.getName()));
            }
            rootItem.getChildren().add(attr);
            rootItem.getChildren().add(func);
            rootItem.getChildren().add(oper);
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
        // if(searchTreeView(newParent.getValue(), newChild) != null){
        //     removeTreeBranch(newParent, ClassTree.getRoot());
        //     newChild = searchTreeView(newChild.getValue(), ClassTree.getRoot());
        //     removeTreeBranch(newChild, chParent);
        //     newParent.getChildren().add(newChild);
        //     chParent.getChildren().add(newParent);
        //     return;
        // }
        // if(searchTreeView(newChild.getValue(), newParent) != null){
        //     removeTreeBranch(newChild, newParent);
        //     newParent = searchTreeView(newParent.getValue(), ClassTree.getRoot());
        //     newParent.getChildren().add(newChild);
        //     return;
        // }
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
        if (newParentName == ChildName){
            newParentClass.setParent(null);
            return;
        }
        ChildClass.setParent(newParentClass);
        updateClassTab();

    }
    /**
    * Přidání karty do panelu karet 
    */
    @FXML
    private void addTab() {
        int numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Sequence Diagram "+(numTabs-1));

        sequenceDiagrams.add(new SequenceDiagram());

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    @FXML
    public void selectTab(int tabIndex)
    {
        tabPane.getSelectionModel().select(tabIndex);
    }

    @FXML
    private void addSeqObj()
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(addSeqObjFirst);
            addSeqObjFirst = null;

            initGridState();
            initGrid();
        };

        int colCount = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getNameClasses().size();
        VBox startObj = new VBox();
        
        Line startLine = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.01);
        StackPane objRectangle = new StackPane();
        Rectangle rectangle = new Rectangle(150, 50);
        Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);
        TextField objField = new TextField();

        rectangle.setOnMouseClicked((MouseEvent event) -> 
        {
            rectangle.requestFocus();
            Node clickedNode = event.getPickResult().getIntersectedNode();
            System.out.println(clickedNode);
            if (clickedNode != seqGrid) 
            {
                // click on descendant node
                Node parent = clickedNode.getParent();
                while (parent != seqGrid) 
                {
                    clickedNode = parent;
                    parent = clickedNode.getParent();
                }
                Integer colIndex = GridPane.getColumnIndex(clickedNode);
                Integer rowIndex = GridPane.getRowIndex(clickedNode);
                System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                rectangle.setStroke(Color.DODGERBLUE);
                rectangle.setEffect(rectangle.getEffect() == null ? effect : null);
            }
        });

        rectangle.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setEffect(null);
                }
            }
        });

        startObj.setAlignment(Pos.CENTER);

        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);

        objRectangle.getChildren().add(rectangle);
        addSeqObjButton.setDisable(true);

        // Multiple Event Handler https://stackoverflow.com/questions/31794167/how-to-handle-multiple-event-types-from-one-class-in-javafx
        objField.setOnKeyPressed(new EventHandler<KeyEvent>() 
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) 
                {
                    sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(objField.getText());

                    // sequenceDiagrams.get(0).getNameClasses()+ seqDisplay.getChildren().size()
                    Text objName = new Text(objField.getText());
                    objRectangle.getChildren().remove(objField);
                    objRectangle.getChildren().add(objName);
                    //seqGrid.add(objName, objsCount, 0);

                    if (seqGrid.getColumnCount() >= 5)
                    {
                        addSeqObjButton.setDisable(true);
                    }
                    else
                    {
                        addSeqObjButton.setDisable(false);
                    }
                }
            }
        });

        /*objField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    if (objField.getText() == "" && objsCount != 0)
                    {
                        seqGrid.getChildren().remove(startObj);
                        seqGrid.getColumnConstraints().remove(objsCount);
                    }
                    /*else
                    {
                        sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(objField.getText());

                        Text objName = new Text(objField.getText());
                        seqGrid.getChildren().remove(objField);
                        seqGrid.add(objName, objsCount, 0);
                    }
                    addSeqObjButton.setDisable(false);
                }
            }
        });*/

        seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        if (colCount == 0)
        {
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
        }
        
        objRectangle.getChildren().add(objField);

        startObj.getChildren().addAll(objRectangle, startLine);

        seqGrid.add(startObj, colCount, 0);
        objField.requestFocus();

        Line timeAxis = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.85);
        timeAxis.getStrokeDashArray().addAll(25d, 10d);

        seqGrid.add(timeAxis, colCount, 1);
        updateGridMsg();
    }

    @FXML
    public void updateGridMsg()
    {
        if (seqGrid.getColumnCount() > 2)
        {
            ColumnConstraints updateColumn = new ColumnConstraints(seqEditorBox.getWidth()/seqGrid.getColumnCount());
            ColumnConstraints updateSpacerColumn = new ColumnConstraints((seqEditorBox.getWidth()/seqGrid.getColumnCount())/2);

            seqGridMsgs.getColumnConstraints().add(seqGridMsgs.getColumnCount()-2, updateColumn);

            // TODO if seqGridMsgs.getColumnCount() != seqGridgetColumnCount()

            for (int i = 0; i < seqGridMsgs.getColumnCount(); i++)
            {
                if (i == 0 || i == seqGridMsgs.getColumnCount() - 1)
                {
                    seqGridMsgs.getColumnConstraints().set(i, updateSpacerColumn);
                }
                else
                {
                    seqGridMsgs.getColumnConstraints().set(i, updateColumn);
                }
            }

            for (int i = 0; i < (seqGridMsgs.getChildren().size()); i++)
            {
                Node nodeMsg = seqGridMsgs.getChildren().get(i);
                
                if (nodeMsg instanceof UMLArrow)
                {
                    if (((UMLArrow)nodeMsg).getEndX() > 0)
                    {
                        ((UMLArrow)nodeMsg).setEndX(seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    else
                    {
                        ((UMLArrow)nodeMsg).setEndX(-seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    
                }

                if (nodeMsg instanceof UMLArrowReply)
                {
                    if (((UMLArrowReply)nodeMsg).getEndX() > 0)
                    {
                        ((UMLArrowReply)nodeMsg).setEndX(seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    else
                    {
                        ((UMLArrowReply)nodeMsg).setEndX(-seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    
                }

                if (nodeMsg instanceof Line)
                {
                    ((Line)nodeMsg).setEndX(seqEditorBox.getWidth()/seqGrid.getColumnCount());
                }
            }
        }
    }

    @FXML
    public void openMessageCreator() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("message.fxml"));
        Parent root = (Parent)loader.load();

        MessageController msgController = loader.getController();
        msgController.loadData(sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2));

        Stage popUp = new Stage();

        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.initOwner(Main.stage);
        popUp.setTitle("Create New Message...");
        popUp.setScene(new Scene(root));
        popUp.setResizable(false);
        popUp.showAndWait();

        if (msgController.createdMessage != null)
        {
            createMessage(msgController.createdMessage);
        }
    }

    @FXML
    public void displaySequence(SequenceDiagram diagram)
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(addSeqObjFirst);
            addSeqObjFirst = null;

            initGrid();
            initGridState();
        }

        ArrayList<String> nameList = new ArrayList<String>();

        nameList = diagram.getNameClasses();
       
        for (int i = 0; i < nameList.size(); i++)
        {
            VBox startObj = new VBox();
            Line startLine = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.01);
            StackPane objRectangle = new StackPane();
            Rectangle rectangle = new Rectangle(150, 50);
            Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);

            rectangle.setOnMouseClicked((MouseEvent event) -> 
            {
                rectangle.requestFocus();
                Node clickedNode = event.getPickResult().getIntersectedNode();
                System.out.println(clickedNode);
                if (clickedNode != seqGrid) 
                {
                    // click on descendant node
                    Node parent = clickedNode.getParent();
                    while (parent != seqGrid) 
                    {
                        clickedNode = parent;
                        parent = clickedNode.getParent();
                    }
                    Integer colIndex = GridPane.getColumnIndex(clickedNode);
                    Integer rowIndex = GridPane.getRowIndex(clickedNode);
                    System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                    rectangle.setStroke(Color.DODGERBLUE);
                    rectangle.setEffect(rectangle.getEffect() == null ? effect : null);
                }
            });

            rectangle.focusedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                {
                    if (!newPropertyValue)
                    {
                        rectangle.setStroke(Color.BLACK);
                        rectangle.setEffect(null);
                    }
                }
            });

            startObj.setAlignment(Pos.CENTER);

            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(Color.TRANSPARENT);

            //sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(nameList.get(i));

            objRectangle.getChildren().add(rectangle);

            seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
            if (i == 0)
            {
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            }
            
            objRectangle.getChildren().add(new Text(nameList.get(i)));

            startObj.getChildren().addAll(objRectangle, startLine);

            seqGrid.add(startObj, i, 0);

            Line timeAxis = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.85);
            timeAxis.getStrokeDashArray().addAll(25d, 10d);

            seqGrid.add(timeAxis, i, 1);
            updateGridMsg();
        }
    }

    @FXML
    public void createMessage(UMLMessage message)
    {
        int msgCount = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).returnMessagesCount();
        int sendIndex = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getClasses().indexOf(message.sender);
        int recIndex = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getClasses().indexOf(message.receiver);
        Label messageLabel = new Label();

        GridPane.setValignment(messageLabel, VPos.TOP);
        GridPane.setHalignment(messageLabel, HPos.CENTER);

        if (message.operation != null)
        {
            messageLabel.setText(message.operation.toString());
        }
        else
        {
            messageLabel.setText(message.message);
        }

        double arrowWidth = seqEditorBox.getWidth() / seqGrid.getColumnCount();
                
        if (sendIndex > recIndex)
        {
            arrowWidth = -arrowWidth;
        }

        seqGridMsgs.getRowConstraints().add(new RowConstraints(30));
        
        if (sendIndex < recIndex)
        {
            if (Math.abs(sendIndex - recIndex) != 1)
            {
                for (int i = sendIndex + 1; i < recIndex; i++)
                {
                    Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                    if (message.transmition == false)
                    {
                        newLine.getStrokeDashArray().addAll(25d, 10d);
                    }
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }

            if (message.transmition == true)
            {
                UMLArrow arrow = new UMLArrow();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex, msgCount);
            }
            else
            {
                UMLArrowReply arrow = new UMLArrowReply();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex, msgCount);
            }
            
            seqGridMsgs.add(messageLabel, recIndex, msgCount);
        }
        else
        {
            if (Math.abs(sendIndex - recIndex) != 1)
            {
                for (int i = sendIndex; i > recIndex + 1; i--)
                {
                    Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }
            if (message.transmition == true)
            {
                UMLArrow arrow = new UMLArrow();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex + 1, msgCount);
            }
            else
            {
                UMLArrowReply arrow = new UMLArrowReply();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex + 1, msgCount);
            }
            seqGridMsgs.add(messageLabel, recIndex + 1, msgCount);
        }

        sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).addMessage(message);
        System.out.println("Sender index: " + sendIndex);
        System.out.println("Receiver index: " + recIndex);
    }

    @FXML
    public void initGrid()
    {
        seqGrid.setMinWidth(900);
        seqGrid.setMinHeight(460);
        seqGrid.setMaxWidth(900);
        seqGrid.setMaxHeight(460);
        seqGrid.setAlignment(Pos.CENTER);
        //seqGrid.setGridLinesVisible(true);
        seqEditorBox.getChildren().add(seqGrid);
    }

    @FXML
    public void initGridState()
    {
        //seqGridState.setMinWidth(900);
        //seqGridState.setMinHeight(460);
        seqEditorBox.setMaxWidth(900);
        seqGridMsgs.setAlignment(Pos.TOP_CENTER);
        seqGridMsgs.setGridLinesVisible(true);

        msgWidth = seqEditorBox.getWidth() / 2;

        ColumnConstraints columnSpacer = new ColumnConstraints(msgWidth / 2);
        ColumnConstraints column = new ColumnConstraints(msgWidth);
        //RowConstraints row = new RowConstraints(30);
        //seqGridMsgs.getRowConstraints().add(row);
        seqGridMsgs.getColumnConstraints().addAll(columnSpacer, column, columnSpacer);
        seqMsgBox.getChildren().add(seqGridMsgs);
    }
}