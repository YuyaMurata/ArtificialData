/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import valid.MetaDataDefine;
import valid.MetaDataSet;

/**
 * FXML Controller class
 *
 * @author zz17807
 */
public class MetaDataFXMLController implements Initializable {

    @FXML
    private ComboBox<String> fileDropMenu;
    @FXML
    private HBox textHBox;
    @FXML
    private ScrollPane dataAreaScroll;
    
    private static Integer limit = 200;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        MetaDataSet.setFiles();
        fileDropMenu.getItems().addAll(MetaDataSet.files.keySet());
    }
    
    private void insertDataSet(MetaDataDefine meta) {
        meta.getData().keySet().stream().forEach(key ->{
            textHBox.getChildren().add(createFieldSet(key, meta.getUnique(key), meta.getData(key)));
        });
    }
    
    private VBox createFieldSet(String field, Double unique, Map<String, Double> data) {
        //System.out.println(field+","+unique+","+data);
        
        VBox box = new VBox();
        box.setMaxWidth(200);
        box.setPrefHeight(800);

        Label label = new Label(field);
        TextField txt = new TextField(unique.toString());
        TextField txt2 = new TextField(String.valueOf(data.size()));
        TextArea area = new TextArea(data.entrySet().stream().limit(limit).map(d -> d.getKey()+","+d.getValue().intValue()).reduce("", (x,y) -> x+"\n"+y));
        //area.setPrefHeight(400);
        VBox.setVgrow(area, Priority.ALWAYS);
        
        box.getChildren().add(label);
        box.getChildren().add(txt);
        box.getChildren().add(txt2);
        box.getChildren().add(area);
        
        box.prefHeightProperty().bind(MetaDataFXMain.parentStage.heightProperty().subtract(78));

        return box;
    }

    @FXML
    private void selectFile(ActionEvent event) {
        String select = fileDropMenu.getSelectionModel().getSelectedItem();
        System.out.println(select);
        textHBox.getChildren().clear();
        
        if(!select.equals("")){
            MetaDataDefine meta = new MetaDataDefine(MetaDataSet.files.get(select));
            insertDataSet(meta);
        }
    }
}
