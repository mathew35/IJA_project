/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI v úvodní scéně aplikace.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package project;

import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;


/**
 * Objekt řadiče, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class MenuController {
    private Stage stage;
    private Scene scene;

    /**
     * Metoda, která přepíná "jeviště" na editorskou scénu.
     *
     * @param event Představující typ akce, v tomto případě změna scény.
     */
    public void switchToEditor(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("class.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

}
