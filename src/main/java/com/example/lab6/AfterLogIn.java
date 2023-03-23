package com.example.lab6;

import domain.Account;
import domain.Friendship;
import domain.Request;
import domain.User;
import domain.validator.FriendshipValidator;
import domain.validator.RequestValidator;
import domain.validator.UserValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import repository.database.FriendshipDBRepo;
import repository.database.JDBCUtils;
import repository.database.RequestDBRepo;
import repository.database.UserDBRepo;
import service.NetworkDB;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static utils.Constants.DATE_TIME_FORMATTER;

public class AfterLogIn implements Initializable {
// anchor pane
    @FXML
    private AnchorPane afterLogIn;
    @FXML
    private Label centerLabel;
//  choice box
    @FXML
    private ChoiceBox<String> choiceBox;
//  friends table
    @FXML
    private Tab friendsTab;
    @FXML
    private TableView<Pair<User,LocalDateTime>> friendsTable;
    @FXML
    private TableColumn<Pair<User, LocalDateTime>, String> friendsFromColumn;
    @FXML
    private TableColumn<Pair<User, LocalDateTime>, String> nameColumn;
    @FXML
    private Button deleteFriendship;
//  requests table
    @FXML
    private Tab requestsTab;
    @FXML
    private TableView<Pair<User, String>> requestsTable;
    @FXML
    private TableColumn<Pair<User, String>, String> reqNameColumn;
    @FXML
    private TableColumn<Pair<User, String>, String> reqStatusColumn;
    @FXML
    private Button acceptRequest;
    @FXML
    private Button deleteRequest;
//  search table
    @FXML
    private TableView<User> searchTable;
    @FXML
    private Button addFriendBtn;
    @FXML
    private Tab searchTab;
    @FXML
    private Button searchBtn;
    @FXML
    private TableColumn<User, String> srchNameColumn;
    @FXML
    private TextField srchNameField;

// observable lists
    ObservableList<Pair<User, LocalDateTime>> friendsModel = FXCollections.observableArrayList();
    ObservableList<Pair<User, String>> requestsModel = FXCollections.observableArrayList();
    ObservableList<User> searchModel = FXCollections.observableArrayList();

// network and utils
    private final String[] options = {"Home", "Log out"};
    private final JDBCUtils jdbcUtils = new JDBCUtils();
    private final UserValidator userValidator = new UserValidator();
    private final UserDBRepo userDBRepo = new UserDBRepo(userValidator);
    private final FriendshipValidator friendshipValidator = new FriendshipValidator(userDBRepo);
    private final FriendshipDBRepo friendshipDBRepo = new FriendshipDBRepo(friendshipValidator);
    private final RequestValidator requestValidator = new RequestValidator();
    private final RequestDBRepo requestDBRepo = new RequestDBRepo(requestValidator);
    private final NetworkDB networkDB = new NetworkDB(friendshipDBRepo, userDBRepo, requestDBRepo);
    private final HelloController helloController = new HelloController();

//  account
    private Account loggedAccount;
    public void setAccount(Account loggedAccount) {
        this.loggedAccount = loggedAccount;
        centerLabel.setText("Welcome, " + loggedAccount.getUsername() + "!");
        populateTables();
    }

//  populating tables
    private void populateTables(){
        var myFriends = networkDB.getFriendsFor(loggedAccount.getId()).stream()
                .map(friendship -> new Pair<>(userDBRepo.findOne((friendship.getIdUser1().equals(loggedAccount.getId()))? friendship.getIdUser2(): friendship.getIdUser1()), friendship.getFriendsFrom()))
                .toList();
        friendsModel.setAll(myFriends);
        var myRequests = networkDB.getRequestsFor(loggedAccount.getId()).stream()
                .map(request ->new Pair<>(userDBRepo.findOne((request.getIdUser1().equals(loggedAccount.getId()))? request.getIdUser2(): request.getIdUser1()), request.getStatus()))
                .toList();
        requestsModel.setAll(myRequests);
        populateUsers();
    }
    public void populateUsers(){
        var requesting = networkDB.getRequests().stream()
                .filter(e -> e.getIdUser1().equals(loggedAccount.getId()))
                .map(e -> networkDB.getUserById(e.getIdUser2())).toList();
        var myFriends = networkDB.getFriendsFor(loggedAccount.getId()).stream()
                .map(friendship -> new Pair<>(userDBRepo.findOne((friendship.getIdUser1().equals(loggedAccount.getId()))? friendship.getIdUser2(): friendship.getIdUser1()), friendship.getFriendsFrom()))
                .toList();
        Predicate<User> contains = user -> user.getFirstName().concat(user.getLastName()).contains(srchNameField.getText());
        Predicate<User> notMe = user -> !user.getId().equals(loggedAccount.getId());
        Predicate<User> notFriend = user -> !friendsModel.stream().map(user1 -> user1.getKey().getId()).toList().contains(user.getId());
        Predicate<User> notTheyRequested = user -> !requestsModel.stream().map(user1 -> user1.getKey().getId()).toList().contains(user.getId());
        Predicate<User> notMeRequesting = user -> !requesting.contains(user);
        searchModel.setAll(networkDB.getUsers().stream()
                .filter(notMe.and(contains).and(notTheyRequested).and(notMeRequesting).and(notFriend))
                .toList());
    }

//  initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choiceBox.getItems().addAll(options);
        choiceBox.setOnAction(this::getChoice);

        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey().getFirstName()));
        friendsFromColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().format(DATE_TIME_FORMATTER)));

        reqNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey().getFirstName()));
        reqStatusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));

        srchNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        srchNameField.textProperty().addListener(o -> populateUsers());

        friendsTable.setItems(friendsModel);
        requestsTable.setItems(requestsModel);
        searchTable.setItems(searchModel);
    }

//  get choice from choice box
    public void getChoice(ActionEvent event) {
        String choice = choiceBox.getValue();

        if (choice.equals(options[1])) {
            HelloApplication app = new HelloApplication();
            try {
                Node source = (Node)  event.getSource();
                Stage crtStage  = (Stage) source.getScene().getWindow();
                crtStage.close();
                FXMLLoader loader = new FXMLLoader(app.getClass().getResource("hello-view.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//  friends table delete friendship button action
    @FXML
    void deleteFriendshipClicked(ActionEvent event) {
        var selectedFriend = friendsTable.getSelectionModel().getSelectedItem();
        if(selectedFriend != null){
            for(Friendship f: friendshipDBRepo.findAll()){
                if((f.getIdUser1().equals(loggedAccount.getId()) && f.getIdUser2().equals(selectedFriend.getKey().getId())) ||
                        (f.getIdUser2().equals(loggedAccount.getId()) && f.getIdUser1().equals(selectedFriend.getKey().getId()))){
                    friendshipDBRepo.delete(f.getId());
                    break;
                }
            }
            populateTables();
        }
    }

// requests table accept request button action
    @FXML
    void acceptRequestClicked(ActionEvent event) {
        var selectedRequest = requestsTable.getSelectionModel().getSelectedItem().getKey();
        if(selectedRequest != null){
            for(Request r: requestDBRepo.findAll()){
                if((r.getIdUser1().equals(loggedAccount.getId()) && r.getIdUser2().equals(selectedRequest.getId())) ||
                        (r.getIdUser2().equals(loggedAccount.getId()) && r.getIdUser1().equals(selectedRequest.getId()))){
                    requestDBRepo.delete(r.getId());
                    break;
                }
            }
            Friendship friendship = new Friendship(loggedAccount.getId(), selectedRequest.getId(), LocalDateTime.now());
            Long id = networkDB.getNewIdFriendship();
            friendship.setId(id);
            networkDB.addFriendship(friendship);
            populateTables();
        }
    }

//  requests table decline request button action
    @FXML
    void deleteRequestClicked(ActionEvent event) {
        var selectedRequest = requestsTable.getSelectionModel().getSelectedItem().getKey();
        if(selectedRequest != null){
            for(Request r: requestDBRepo.findAll()){
                if((r.getIdUser1().equals(loggedAccount.getId()) && r.getIdUser2().equals(selectedRequest.getId())) ||
                        (r.getIdUser2().equals(loggedAccount.getId()) && r.getIdUser1().equals(selectedRequest.getId()))){
                    requestDBRepo.delete(r.getId());
                    break;
                }
            }
            populateTables();
        }
    }

//  search table send request button action
    @FXML
    void addFriendBtnClicked(ActionEvent event) {
        var selectedRequest = searchTable.getSelectionModel().getSelectedItem();
        if(selectedRequest != null){
            Request request = new Request(loggedAccount.getId(), selectedRequest.getId(), "pending");
            Long id = networkDB.getNewIdRequest();
            request.setId(id);
            requestDBRepo.save(request);
            populateTables();
        }
    }

}
