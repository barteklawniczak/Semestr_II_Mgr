package com.blawniczak.fx;

import com.blawniczak.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

@Component
public class LoginController implements Initializable {

    final static ObservableList<String> protocolsList = FXCollections.observableArrayList();

    @Autowired
    private ClientService clientService;
    @Autowired
    private ConfigurableApplicationContext springContext;

    @FXML
    private TextField login;
    @FXML
    private ComboBox protocols;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        protocolsList.add("Hessian");
        protocolsList.add("Burlap");
        protocolsList.add("XML-RPC");
        this.protocols.setItems(protocolsList);
        this.protocols.getSelectionModel().selectFirst();
    }

    public void logIn(ActionEvent event) throws IOException, XmlRpcException {
        if(!this.login.getText().isEmpty()) {
            this.clientService.applyProtocol(this.protocols.getValue().toString());

            List<String> usernames = new ArrayList<>();
            if(this.clientService.getCurrentService() != null) {
                usernames = this.clientService.getCurrentService().usernameAlreadyExists(this.login.getText());
            } else {
                Vector params = new Vector();
                params.addElement(this.login.getText());
                Object[] objects = (Object[]) this.clientService.getXmlRpcClient().execute("ChatService.usernameAlreadyExists", params);
                for (Object object : objects) {
                    usernames.add(object.toString());
                }
            }

            if(usernames.isEmpty()) {
                this.login.setText("Username " + this.login.getText() + " already exists!");
            } else {
                this.clientService.setUsername(this.login.getText());
                this.clientService.setUsernames(usernames);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/chat.fxml"));
                fxmlLoader.setControllerFactory(springContext::getBean);
                Parent root = fxmlLoader.load();
                stage.setTitle("Chat");
                stage.setScene(new Scene(root, 700, 400));
                stage.show();
            }
        } else {
            this.login.setText("You have to enter your name");
        }
    }
}
