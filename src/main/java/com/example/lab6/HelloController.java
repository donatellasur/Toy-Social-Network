package com.example.lab6;

import domain.Account;
import domain.User;
import domain.validator.AccountValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.database.AccountDBRepo;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
//  anchor pane
    @FXML
    private AnchorPane helloView;
    @FXML
    private Label wrongLogIn;
//  text fields
    @FXML
    private TextField emailUsernameField;
    @FXML
    private PasswordField passwordField;
//  log in button
    @FXML
    private Button logInBtn;

//  register label
    @FXML
    private Label registerField;

//  log in button action
//  change scene to after log in
    @FXML
    void logInBtnClicked(ActionEvent event) throws IOException {
        User user = null;
        HelloApplication app = new HelloApplication();
        AccountValidator accountValidator = new AccountValidator();
        AccountDBRepo accountDBRepo = new AccountDBRepo(accountValidator);
        Iterable<Account> allAccounts = accountDBRepo.findAll();

        Long id = null;
        for (Account a : allAccounts) {

            if ((emailUsernameField.getText().equals(a.getUsername()) || emailUsernameField.getText().equals(a.getEmail()))
                    && passwordField.getText().equals(a.getPassword())) {
                wrongLogIn.setText("Login successful!");
//                app.changeScene("afterLogIn.fxml");
                FXMLLoader loader = new FXMLLoader(app.getClass().getResource("afterLogIn.fxml"));
                Scene scene = new Scene(loader.load());
                AfterLogIn afterLogIn = loader.getController();
                Stage stage = new Stage();
                stage.setTitle("Profile");
                stage.setScene(scene);
                afterLogIn.setAccount(a);
                stage.show();
                Node source = (Node)  event.getSource();
                Stage crtStage  = (Stage) source.getScene().getWindow();
                crtStage.close();
            } else if (emailUsernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                wrongLogIn.setText("Please enter your data!");
            } else {
                wrongLogIn.setText("Wrong credentials!");
            }
        }
    }

//  register label action
    @FXML
    void registerClicked(MouseEvent event) throws IOException {
        HelloApplication app = new HelloApplication();
//        app.changeScene("register.fxml");
        FXMLLoader loader = new FXMLLoader(app.getClass().getResource("register.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
        Node source = (Node)  event.getSource();
        Stage crtStage  = (Stage) source.getScene().getWindow();
        crtStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wrongLogIn.setText("");
    }
}
