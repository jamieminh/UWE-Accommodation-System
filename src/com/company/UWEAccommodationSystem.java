package com.company;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class UWEAccommodationSystem extends Application {
    private VBox overallVBox = new VBox();
    private TableView<Tenant> tenantTableView = new TableView<>();
    private TableView<Student> applicationTableView = new TableView<>();
    private Scene loginScene, workScene;
    private ObservableList<Student> applicationList;
    private static String staff;
    private Tab staffTab;

    public static void main(String[] args) {
        Database.getData();
        launch(args);
    }

    public void start(Stage primaryStage) {
        // -------------------------- Log In Scene --------------------------------------
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Button loginBut = new Button("Login", new ImageView("login_icon.png"));
        Text uweText = new Text("UWE Accommodation System");
        ImageView uweLogo = new ImageView(new Image("uwe_logo.png", 300, 100, true, true));

        username.setPromptText("Username");
        password.setPromptText("Password");
        uweText.setStyle("-fx-font-weight: BOLD; -fx-font-size: 14px; -fx-alignment: CENTER");

        GridPane loginGrid = new GridPane();
        loginGrid.setVgap(10);
        loginGrid.setHgap(100);
        loginGrid.setPadding(new Insets(50, 50, 50, 50));
        loginGrid.add(uweText, 0, 0);
        loginGrid.add(uweLogo, 0, 1, 1, 3);
        loginGrid.add(username, 1, 1);
        loginGrid.add(password, 1, 2);
        loginGrid.add(loginBut, 1, 3);

        loginScene = new Scene(loginGrid, 550, 250);

        // ------------------------- Logout Button ------------------------------------
        HBox logoutBox = new HBox();
        Button logoutBut = new Button("Logout", new ImageView("logout_icon.png"));
        logoutBut.getStyleClass().add("delete-button");
        logoutBut.setOnAction(e -> {
            username.clear();
            password.clear();
            primaryStage.setScene(loginScene);
        });
        logoutBox.getChildren().add(logoutBut);
        logoutBox.setAlignment(Pos.TOP_RIGHT);
        overallVBox.getChildren().add(0, logoutBox);
        overallVBox.getStyleClass().add("overall-vbox");

        // -------------------------- Create Tabs ---------------------------------------
        TabPane tabPane = new TabPane();

        staffTab = new Tab("Manager", roomTable("manager"));
        Tab tenantTab = new Tab("Current Tenants", tenantTable());
        Tab applicationTab = new Tab("Applications", applicationTable());
        Tab instructionTab = new Tab("Instructions", InstructionTab());
        Tab[] tabs = {staffTab, tenantTab, applicationTab, instructionTab};

        tabPane.getTabs().addAll(tabs);
        overallVBox.getChildren().add(1, tabPane);

        // ---------------------------------- Lease Info Area -------------------------------------------------
        for (Tab t : tabs) {
            t.setClosable(false);
            t.selectedProperty().addListener(((observable, oldValue, newValue) -> leaseInfoPrompt(t)));
            // upon selecting any new Tab, the Lease Info area will be cleared; "staffTab" also display the Lease Info instr line
        }
        applicationTab.selectedProperty().addListener(((observable, oldValue, newValue) -> overallVBox.getChildren().add(2, applicationInfo(applicationTableView, staff))));   // when application tab is selected
        leaseInfoPrompt(staffTab);

        // add tabPane into Scene
        GridPane.setMargin(tabPane, new Insets(5));
        workScene = new Scene(overallVBox, 750, 600);
        loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());   // add css file to Login Scene
        workScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());   // add css file to Work Scene


        // Set Login Scene and Switching Scenes
        loginBut.setOnAction(e -> {
            Pair<Boolean, String> checkLogin = checkLogin(username.getText(), password.getText());
            staff = checkLogin.getValue();
            if (checkLogin.getKey()) {
                primaryStage.setScene(workScene);
                switch(staff) {
//                    case "manager":
//                        staffTab.setText("Manager");
//                        staffTab.setContent(roomTable("manager"));
//                        break;
                    case "warden":
                        staffTab.setText("Warden");
                        staffTab.setContent(roomTable("warden"));
                        break;
                    case "executive manager":
                        staffTab.setText("All");
                        staffTab.setContent(roomTable("executive manager"));
                }
            }
        });
        loginBut.setDefaultButton(true);
        loginBut.getStyleClass().add("button");

        primaryStage.setResizable(false);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("UWE Bristol Accommodation System");
        primaryStage.getIcons().add(new Image("uwe_icon.png"));
        primaryStage.show();
    }

    private Pair<Boolean, String> checkLogin(String email, String password) {
        Pair<Boolean, String> staff = new Pair<>(false, "null");
        if (email.isEmpty() || password.isEmpty())
            errorPopup("All fields must be specified");
        else if (!Database.loginMap.containsKey(email))
            errorPopup("Incorrect Email or Password");
        else {
            Login curr = Database.loginMap.get(email);
            if (!password.equals(curr.getPassword()))
                errorPopup("Incorrect Email or Password");
            else
                staff = new Pair<>(true, curr.getStaff());
        }
        return staff;
    }

    private TableView<Room> roomTable(String staff) {
        TableView<Room> table = new TableView<>();
        TableColumn<Room, String> lease          = new TableColumn<>("Lease");
        TableColumn<Room, String> hall           = new TableColumn<>("Hall");
        TableColumn<Room, String> room           = new TableColumn<>("Room");
        TableColumn<Room, String> student        = new TableColumn<>("Student");
        TableColumn<Room, String> monthlyRent    = new TableColumn<>("Monthly Rent");
        TableColumn<Room, String> isOccupied     = new TableColumn<>("Occupied");
        TableColumn<Room, String> cleaningStatus = new TableColumn<>("Cleaning Status");
        TableColumn<Room, String> isAvailable    = new TableColumn<>("Availability");

        table.getColumns().addAll(lease, hall, room, student, monthlyRent, isOccupied, cleaningStatus, isAvailable);

        // Center the text (data)
        for (TableColumn col : table.getColumns()) {
            col.setMinWidth(80);
            col.setStyle("-fx-alignment: CENTER;");
        }

        lease.setCellValueFactory(c -> (new SimpleStringProperty((c.getValue().getLease() == null ? "" : c.getValue().getLeaseNumber()))));
        hall.setCellValueFactory(new PropertyValueFactory<>("hallID"));
        room.setCellValueFactory(new PropertyValueFactory<>("number"));
        student.setCellValueFactory(c -> {
            Lease hasLease = c.getValue().getLease();
            if (hasLease != null)
                return new SimpleStringProperty((hasLease.getTenant().getFullName()));
            else
                return new SimpleStringProperty("");
        });
        monthlyRent.setCellValueFactory(new PropertyValueFactory<>("monthlyRent"));
        cleaningStatus.setCellValueFactory(new PropertyValueFactory<>("cleaningStatus"));
        isOccupied.setCellValueFactory(c -> (new SimpleStringProperty((c.getValue().getIsOccupied()) ? "Yes" : "No")));
        isAvailable.setCellValueFactory(c -> (new SimpleStringProperty((c.getValue().getIsAvailable()) ? "Yes" : "No")));

        ObservableList<Room> list = FXCollections.observableArrayList(Database.roomMap.values());
        table.setItems(list);

        // upon clicking a row, Lease Information box will be displayed
        table.setRowFactory(tableView -> {
            TableRow<Room> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Room roomData = row.getItem();
                if (roomData != null)
                    leaseInfo(roomData, table, staff);
            });
            return row;
        });

        // change background color of columns that a specific staff cannot edit
        ObservableList<TableColumn<Room, ?>> columns = table.getColumns();
        if (staff.equals("manager"))
            columns.get(columns.size() - 2).getStyleClass().add("disable-column");        // get the cleaning column
        else if (staff.equals("warden"))
            for (TableColumn<Room, ?> col : columns)
                if (!col.equals(cleaningStatus))
                    col.getStyleClass().add("disable-column");

        return table;
    }

    private TableView tenantTable() {
        TableColumn<Tenant, String> id        = new TableColumn<>("ID");
        TableColumn<Tenant, String> firstName = new TableColumn<>("First Name");
        TableColumn<Tenant, String> lastName  = new TableColumn<>("Last Name");
        TableColumn<Tenant, String> lease     = new TableColumn<>("Lease");
        TableColumn<Tenant, String> hall      = new TableColumn<>("Hall");
        TableColumn<Tenant, String> room      = new TableColumn<>("Room");
        tenantTableView.getColumns().addAll(id, firstName, lastName, lease, hall, room);

        setIdName(id, firstName, lastName, tenantTableView);
        lease.setCellValueFactory(c -> (new SimpleStringProperty(c.getValue().getLease().getLeaseNo())));
        hall.setCellValueFactory(c  -> (new SimpleStringProperty(c.getValue().getLease().getRoom().getHall().getID())));
        room.setCellValueFactory(c  -> (new SimpleStringProperty(String.valueOf(c.getValue().getLease().getRoom().getNumber()))));

        ObservableList<Tenant> list = FXCollections.observableArrayList(Database.tenantMap.values());
        tenantTableView.setItems(list);

        return tenantTableView;
    }

    private TableView applicationTable() {
        TableColumn<Student, String> id = new TableColumn<>("ID");
        TableColumn<Student, String> firstName = new TableColumn<>("First Name");
        TableColumn<Student, String> lastName  = new TableColumn<>("Last Name");
        TableColumn selectCol = new TableColumn("Select");
        applicationTableView.getColumns().addAll(id, firstName, lastName, selectCol);

        setIdName(id, firstName, lastName, applicationTableView);
        selectCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        applicationList = FXCollections.observableArrayList(Database.applicationMap.values());
        applicationTableView.setItems(applicationList);

        return applicationTableView;
    }

    private GridPane applicationInfo(TableView applicationTableView, String staff) {
        GridPane gridPane = createGridPane();

        Label idLabel        = new Label("ID");
        Label firstLabel     = new Label("First Name");
        Label lastLabel      = new Label("Last Name");
        TextField idField    = new TextField();
        TextField firstField = new TextField();
        TextField lastField  = new TextField();
        Button addApp        = new Button("Add Application", new ImageView("add_app_icon.png"));
        Button deleteApp = new Button("Delete Selected", new ImageView("trash_bin_icon.png"));

        gridPane.add(idLabel, 0, 0);
        gridPane.add(firstLabel, 0, 1);
        gridPane.add(lastLabel, 0, 2);
        gridPane.add(idField, 1, 0);
        gridPane.add(firstField, 1, 1);
        gridPane.add(lastField, 1, 2);
        gridPane.add(addApp, 1, 3);
        gridPane.add(deleteApp, 1, 4);

        if (staff.equals("warden")) {
            addApp.setDisable(false);
            deleteApp.setDisable(false);
        }

        addApp.setOnMouseClicked(e -> {
            String stdID     = idField.getText().replaceAll("\\s", "");     // remove all white space
            String firstName = firstField.getText().replaceAll("\\s", "");
            String lastName  = lastField.getText().replaceAll("\\s", "");

            if (stdID.isEmpty() || firstName.isEmpty() || lastName.isEmpty())
                errorPopup("All fields must be specified");

            else if (!stdID.matches("\\d{8}") || firstName.matches("[A-Z]+") || lastName.matches("[A-Z]+")) {
                errorPopup("Check if all of these conditions are satisfied: " +
                        "\n  1. ID must be of 8 digits format (e.g. 12345678)" +
                        "\n  2. First Name and Last Name must have uppercase first letter (e.g. John)");
            }
            else if (Database.applicationMap.containsKey(stdID))        // if student already exists in Application database
                errorPopup("This Student ID is already in the list");

            else if (Database.tenantMap.containsKey(stdID))             // if student already exists in Tenant database
                errorPopup("This Student ID already has a Lease");

            else {
                Student newStudent = new Student(firstName, lastName, stdID);
                Database.applicationMap.put(stdID, newStudent);
                applicationList.add(newStudent);
                applicationTableView.setItems(FXCollections.observableArrayList(Database.applicationMap.values()));     // update Application table
            }
        });

        deleteApp.getStyleClass().add("delete-button");
        deleteApp.setOnMouseClicked(e -> {
            ObservableList<Student> removeRows = FXCollections.observableArrayList();
            for (Student std : applicationList) {
                if (std.getSelect().isSelected()) {
                    removeRows.add(std);
                    Database.applicationMap.remove(std.getStudentID());
                }
            }
            applicationList.removeAll(removeRows);
            applicationTableView.setItems(applicationList);     // update Application table
        });

        return gridPane;
    }

    private void setIdName(TableColumn id, TableColumn firstName, TableColumn lastName, TableView<?> tableView) {
        // Center the text (data)
        //tenantTableView.getStyleClass().add("student-tableView"); // can't target column header and cell at the same time
        for (TableColumn col : tableView.getColumns()) {
            col.setMinWidth(80);
            col.setStyle("-fx-alignment: CENTER;");
        }
        id.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    }

    private TextArea InstructionTab() {
        TextArea ins = new TextArea();
        ins.setWrapText(true);
        ins.setText("Instructions for Using the UWE Accommodation System\n" +
                "1. A Manager can edit/delete/create a new Lease for a room.\n" +
                "2. A Warden can only change the Cleaning Status.\n" +
                "3. An Executive Manager have full editing rights. \n" +
                "3. To prevent human error, the occupancy and availability statuses will be automatically determined and updated whenever the user press the 'Update' button.\n" +
                "4. Student of a existing Lease is fixed at the time of creating and cannot be changed.\n" +
                "5. Deleting a Lease will also delete the Student of that Lease off database." +
                "6. If both 'Lease' and 'Student' is left blank, that lease will be deleted (if there was a lease in that room)\n" +
                "7. A new Tenant can only be added from the list of application.\n" +
                "8. When a new Lease is added correctly, the application with corresponding student ID will be automatically deleted.\n");
        return ins;
    }

    private void leaseInfoPrompt(Tab currentTab) {
        if (overallVBox.getChildren().size() == 3)        // 1st, overallVBox only has 1 child (the table)
            overallVBox.getChildren().remove(2);    // remove whatever was left in the Lease Info area
        if (currentTab.equals(staffTab)) {         // if the selected tab is 'managerTab', 'wardenTab' or 'allTab'
            Text instruction = new Text("Select a row to see Lease Information of that room");
            overallVBox.getChildren().add(2, instruction);
            GridPane.setHalignment(instruction, HPos.CENTER);
        }
    }

    // create the Lease Information tab upon clicking on a row
    private void leaseInfo(Room room, TableView tableView, String staff) {
        GridPane gridPane = createGridPane();

        Text leaseInfo = new Text("Lease Information");
        gridPane.add(leaseInfo, 0, 0, 2, 1);

        Label hallNameLabel    = new Label("Hall Name");
        Label hallAddressLabel = new Label("Hall Address");
        Label roomNoLabel      = new Label("Room Number");
        Label leaseNoLabel     = new Label("Lease Number");
        Label studentIDLabel   = new Label("Student ID");
        Label studentNameLabel = new Label("Student Name");
        Label occupancyLabel   = new Label("Occupancy");
        Label cleaningLabel    = new Label("Cleaning Status");
        Label availableLabel   = new Label("Availability");

        TextField hallNameText    = new TextField(room.getHall().getName());
        TextField hallAddressText = new TextField(room.getHall().getAddress());
        TextField roomNoText      = new TextField(String.valueOf(room.getNumber()));
        TextField leaseNoText     = new TextField(String.valueOf(room.getLeaseNumber()));       // empty string if room dont have lease
        TextField studentIDText   = new TextField();
        TextField studentNameText = new TextField();


        String roomLeaseNo = room.getLeaseNumber();
        String roomStudentID;
        Lease roomLease = room.getLease();
        if (roomLease != null) {      // if room has Lease
            roomStudentID = roomLease.getTenant().getStudentID();
            studentIDText.setText(roomStudentID);
            studentNameText.setText(String.valueOf(roomLease.getTenant().getFullName()));
        } else
            roomStudentID = "";

        // Combo Box for Cleaning Status
        ComboBox cleaningBox = new ComboBox(FXCollections.observableArrayList("Clean", "Dirty", "Offline"));
        cleaningBox.getSelectionModel().select(room.getCleaningStatus());

        // Check Boxes for Occupancy and Availability
        CheckBox occupancyBox = new CheckBox();
        occupancyBox.setSelected(room.getIsOccupied());

        CheckBox availabilityBox = new CheckBox();
        availabilityBox.setSelected(room.getIsAvailable());

        // Add Control components to gridPane
        Label[] labels = {hallNameLabel, hallAddressLabel, roomNoLabel, leaseNoLabel, studentIDLabel, occupancyLabel, cleaningLabel, availableLabel};
        Control[] textFields = {hallNameText, hallAddressText, roomNoText, leaseNoText, studentIDText, occupancyBox, cleaningBox, availabilityBox};

        int k = 0, l = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 1; j < 5; j++)
                if (i % 2 == 0) {
                    gridPane.add(labels[k], i, j);
                    k++;
                } else {
                    gridPane.add(textFields[l], i, j);
                    l++;
                }
        gridPane.add(studentNameLabel, 4, 1);
        gridPane.add(studentNameText, 5, 1);

        // set NOT Editable
        hallNameText.setEditable(false);
        hallAddressText.setEditable(false);
        roomNoText.setEditable(false);
        occupancyBox.setDisable(true);
        availabilityBox.setDisable(true);
        studentNameText.setEditable(false);

        // Update Lease and Delete Lease Buttons
        Button deleteBut = new Button("Delete Lease", new ImageView("trash_bin_icon.png"));
        Button updateBut = new Button("Update", new ImageView("update_icon.png"));
        gridPane.add(updateBut, 5, 2);
        gridPane.add(deleteBut, 5, 3);

        if (staff.equals("manager"))
            cleaningBox.setDisable(true);
        else if (staff.equals("warden")) {
            leaseNoText.setEditable(false);
            studentIDText.setEditable(false);
            studentNameText.setEditable(false);
            deleteBut.setDisable(true);
        }

        updateBut.setOnMouseClicked(e -> {
            String changedStudentID = studentIDText.getText();
            String changedLease = leaseNoText.getText();
            String changedCleaning = cleaningBox.getValue().toString();

            // CHANGING THE CLEANING STATUS (warden)
            if (staff.equals("warden")) {
                if (changedCleaning.equals("Offline") && room.getLease() != null)    // if room is rented and Cleaning is set to 'Offline' -> error
                    errorPopup("This room is rented and cannot be set to Offline");
                else if (changedCleaning.equals("Offline")) {                        // if room is NOT rented and Cleaning is set to 'Offline'
                    room.setAvailable(false);                                        // -> update availability status
                    room.setCleaningStatus(changedCleaning);
                    availabilityBox.setSelected(false);
                }
                else if (room.getLease() == null) {                                  // if room is NOT rented and Cleaning is NOT set to 'Offline' (set to Clean or Dirty)
                    room.setAvailable(true);                                         // -> update availability status
                    room.setCleaningStatus(changedCleaning);
                    availabilityBox.setSelected(true);
                }
                else if (room.getLease() != null) {
                    room.setCleaningStatus(changedCleaning);
                }
            }
            else {
                // CHANGING OTHER LEASE INFO (manager)
                // if EITHER Lease or Student ID is NOT specified
                if (changedLease.equals("") ^ changedStudentID.equals(""))
                    errorPopup("Both 'Lease' and 'Student ID' must be specified or None at all");

                // if BOTH Lease and Student ID are NOT specified, and there WAS a Lease in this room -> DELETE lease
                else if (changedLease.equals("") && changedStudentID.equals("") && room.getLease() != null)
                    deleteRoom(room, leaseNoText, studentIDText, studentNameText, occupancyBox, availabilityBox);

                // check if Lease number is digits only
                else if (changedLease.matches(".*\\D+.*") && !changedLease.equals(""))
                    errorPopup("Lease Number must be in digits");

                // check if Student ID is of the correct format (8 digits)
                else if (!changedStudentID.matches("\\d{8}") && !changedStudentID.equals(""))
                    errorPopup("Student ID is an 8-digit number");

                // if both Lease and Student are specified,
                else if (!changedLease.equals("") && !changedStudentID.equals("")) {
                    if (room.getCleaningStatus().equals("Offline"))      // if room is Offline
                        errorPopup("This room is currently offline and cannot be rented");
                    else {
                        // if (Lease changed) && (StudentID NOT changed) -> Student has a new Lease -> delete old Lease
                        if (!roomLeaseNo.equals(changedLease) && roomStudentID.equals(changedStudentID)) {
                            // if Lease Number already exists
                            if (Database.leaseMap.containsKey(changedLease))
                                errorPopup("This Lease Number is associated with another student");
                            else {
                                // delete the Lease Object (null?)
                                // -----------------------------
                                Database.leaseMap.remove(room.getLeaseNumber());        // remove Lease from database
                                // create a new Lease with the same Student
                                Lease newLease = new Lease(changedLease, room, Database.tenantMap.get(changedStudentID));
                                Database.leaseMap.put(changedLease, newLease);

                                room.setLease(newLease);    // update room info
                            }
                        }
                        else if (!roomStudentID.equals(changedStudentID)) {                     // if (student ID changed)
                            if (Database.tenantMap.containsKey(changedStudentID))               // if Student ID entered already in tenant list -> error
                                errorPopup("This Student already has a lease");
                            else if (!Database.applicationMap.containsKey(changedStudentID))    // if Student ID entered is NOT in application list -> error
                                errorPopup("There is no Application with this Student ID");
                            else if (roomLeaseNo.equals(changedLease))                          // if (Student ID changed) && (Lease NOT changed) --> error
                                errorPopup("Lease's tenant cannot be changed");
                            else {                                                              // if (Student ID changed) && (Lease ID changed)
                                if (Database.leaseMap.containsKey(changedLease))                // if Lease ID changed to another that already exists -> error
                                    errorPopup("This Lease Number already exists");
                                else {                                                          // if Lease ID does NOT exist
                                    Database.leaseMap.remove(room.getLeaseNumber());            // delete old Lease of the room
                                    // Create a new Lease
                                    Student appStd = Database.applicationMap.get(changedStudentID);
                                    Tenant newTenant = new Tenant(appStd.getFirstName(), appStd.getLastName(), appStd.getStudentID());
                                    Lease newLease = new Lease(changedLease, room, newTenant);
                                    newTenant.setLease(newLease);
                                    Database.leaseMap.put(changedLease, newLease);
                                    room.setLease(newLease);
                                    room.setOccupied(true);
                                    room.setAvailable(false);

                                    leaseNoText.setText(changedLease);
                                    studentIDText.setText(changedStudentID);
                                    studentNameText.setText(room.getLease().getTenant().getFullName());
                                    occupancyBox.setSelected(true);
                                    availabilityBox.setSelected(false);

                                    // add student into the tenant's map and table, remove them from application's
                                    Database.applicationMap.remove(changedStudentID);
                                    Database.tenantMap.put(changedStudentID, newTenant);

                                    tenantTableView.setItems(FXCollections.observableArrayList(Database.tenantMap.values()));           // update Tenant table
                                    applicationTableView.setItems(FXCollections.observableArrayList(Database.applicationMap.values())); // update Application table
                                }
                            }
                        }
                    }
                }
            }
            tableView.refresh();
        });

        deleteBut.setOnMouseClicked(e -> {
            if (room.getLease() == null)               // if room does NOT has a Lease
                errorPopup("This room does not have a Lease to delete");
            else
                deleteRoom(room, leaseNoText, studentIDText, studentNameText, occupancyBox, availabilityBox);
            tableView.refresh();                       // update room table display
        });
        deleteBut.getStyleClass().add("delete-button");

        overallVBox.getChildren().remove(2);     // remove the previous gridPane
        overallVBox.getChildren().add(2, gridPane);
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        GridPane.setMargin(gridPane, new Insets(5));
        gridPane.setPrefSize(740, 740);
        gridPane.getStyleClass().add("grid-pane");      // add a style class for css styling

        return gridPane;
    }   // a grid pane to display Lease Info and Application info

    private void deleteRoom(Room room, TextField lease, TextField studentID, TextField studentName, CheckBox occupancy, CheckBox availability) {
        Student roomStd = room.getLease().getTenant();
        Database.tenantMap.remove(roomStd.getStudentID());          // remove Tenant from database
        tenantTableView.setItems(FXCollections.observableArrayList(Database.tenantMap.values()));

        Database.leaseMap.remove(room.getLeaseNumber());            // remove lease from the LeaseMap
        room.setLease(null);
        room.setOccupied(false);
        room.setAvailable(true);

        lease.clear();                                              // changing the corresponding fields in Lease Info Area
        studentID.clear();
        studentName.clear();
        occupancy.setSelected(false);
        availability.setSelected(true);
    }

    private void errorPopup(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        ((Stage) errorAlert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("warning_icon.png"));
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}
