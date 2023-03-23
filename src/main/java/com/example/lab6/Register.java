package com.example.lab6;

import domain.Account;
import domain.User;
import domain.validator.AccountValidator;
import domain.validator.FriendshipValidator;
import domain.validator.RequestValidator;
import domain.validator.UserValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import repository.database.AccountDBRepo;
import repository.database.FriendshipDBRepo;
import repository.database.RequestDBRepo;
import repository.database.UserDBRepo;
import service.NetworkDB;

import java.io.IOException;
import java.time.LocalDateTime;

public class Register {
//  register/ cancel button
    @FXML
    private Button cancelBtn;
    @FXML
    private Button registerBtn;
//  text fields
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private TextField newEmail;
    @FXML
    private TextField newFirstName;
    @FXML
    private TextField newLastName;
    @FXML
    private PasswordField newPassword;
    @FXML
    private TextField newUsername;
//  labels
    @FXML
    private Label wrongCredentials;

//  network and utils
    private final UserValidator userValidator = new UserValidator();
    private final UserDBRepo userDBRepo = new UserDBRepo(userValidator);
    private final FriendshipValidator friendshipValidator = new FriendshipValidator(userDBRepo);
    private final FriendshipDBRepo friendshipDBRepo = new FriendshipDBRepo(friendshipValidator);
    private final RequestValidator requestValidator = new RequestValidator();
    private final RequestDBRepo requestDBRepo = new RequestDBRepo(requestValidator);

    private final NetworkDB networkDB = new NetworkDB(friendshipDBRepo, userDBRepo, requestDBRepo);
    ObservableList<Pair<User, LocalDateTime>> friendsModel = FXCollections.observableArrayList();
    ObservableList<User> requestsModel = FXCollections.observableArrayList();

//  account
    private Account loggedAccount;
    public void setAccount(Account loggedAccount) {
        this.loggedAccount = loggedAccount;
        populateTables();
    }
//  populate tables
    private void populateTables(){
        var x = networkDB.getFriendsFor(loggedAccount.getId()).stream()
                .map(friendship -> new Pair<>(userDBRepo.findOne((friendship.getIdUser1().equals(loggedAccount.getId()))? friendship.getIdUser2(): friendship.getIdUser1()), friendship.getFriendsFrom()))
                .toList();
        friendsModel.setAll(x);
        var y = networkDB.getRequestsFor(loggedAccount.getId()).stream()
                .map(request -> userDBRepo.findOne((request.getIdUser1().equals(loggedAccount.getId()))? request.getIdUser2(): request.getIdUser1()))
                .toList();
        requestsModel.setAll(y);
    }
//  register button action
    @FXML
    void registerBtnClicked(ActionEvent event) throws IOException {
        HelloApplication app = new HelloApplication();

        UserValidator userValidator = new UserValidator();
        UserDBRepo userDBRepo = new UserDBRepo(userValidator);

        FriendshipValidator friendshipValidator = new FriendshipValidator(userDBRepo);
        FriendshipDBRepo friendshipDBRepo = new FriendshipDBRepo(friendshipValidator);

        AccountValidator accountValidator = new AccountValidator();
        AccountDBRepo accountDBRepo = new AccountDBRepo(accountValidator);

        RequestValidator requestValidator = new RequestValidator();
        RequestDBRepo requestDBRepo = new RequestDBRepo(requestValidator);

        Iterable<Account> allAccounts = accountDBRepo.findAll();
        Iterable<User> allUsers = userDBRepo.findAll();


        NetworkDB service = new NetworkDB(friendshipDBRepo, userDBRepo, requestDBRepo);
        if(newFirstName.getText().isEmpty() || newLastName.getText().isEmpty() || newUsername.getText().isEmpty() ||
                newPassword.getText().isEmpty() || newEmail.getText().isEmpty() || confirmPassword.getText().isEmpty()){
            wrongCredentials.setText("Please enter your data!");
        }
        else if(newFirstName.getText().contains(" ") || newLastName.getText().contains(" ") || newUsername.getText().contains(" ") ||
                newPassword.getText().contains(" ") || newEmail.getText().contains(" ") || confirmPassword.getText().contains(" ")){
            wrongCredentials.setText("Please enter your data without spaces!");
        }
        else {
            for (User user: allUsers) {
                for (Account account : allAccounts) {
                    if (newFirstName.getText().equals(user.getFirstName()) && newLastName.getText().equals(user.getLastName()))
                        wrongCredentials.setText("There is already an account with this name!");
                    else if (newUsername.getText().equals(account.getUsername()))
                        wrongCredentials.setText("There is already an account with this name!");
                    else if (newEmail.getText().equals(account.getEmail()))
                        wrongCredentials.setText("There is already an account with this email!");
                    else if (!newEmail.getText().contains("@"))
                        wrongCredentials.setText(("Please enter a valid email!"));
                    else if (!newEmail.getText().contains("."))
                        wrongCredentials.setText(("Please enter a valid email!"));
                    else if (!newPassword.getText().equals(confirmPassword.getText()))
                        wrongCredentials.setText("Passwords don't match!");
                    else{
                        User newUser = new User(newFirstName.getText(), newLastName.getText());
                        Account newAccount = new Account(newUsername.getText(), newEmail.getText(), newPassword.getText());
                        Long id = service.getNewIdUser();
                        newUser.setId(id);
                        newAccount.setId(id);
                        userValidator.validate(newUser);
                        accountValidator.validate(newAccount);

                        userDBRepo.save(newUser);
                        accountDBRepo.save(newAccount);
                        app.changeScene("hello-view.fxml");
                        break;
                    }
                    break;
                }
                break;
            }

        }
    }
//  cancel button action
    @FXML
    void cancelBtnClicked(ActionEvent event) throws IOException {
        HelloApplication app = new HelloApplication();
//        app.changeScene("hello-view.fxml");
        FXMLLoader loader = new FXMLLoader(app.getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Node source = (Node)  event.getSource();
        Stage crtStage  = (Stage) source.getScene().getWindow();
        crtStage.close();
    }

}
