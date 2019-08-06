package ui.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import model.exception.OutOfBoundListIndexException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class Controller implements Observer {

    private ObservableList<Todo> list = FXCollections.observableArrayList();
    private TodoList todoList;
    private Weather weather;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private JFXListView<Todo> listOfTodos;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label errorLabel;

    @FXML
    private Label currentTemp;

    @FXML
    private Label weatherDescription;

    @FXML
    private Label minTemp;

    @FXML
    private Label maxTemp;

    @FXML
    private Label currentDate;

    @FXML
    void deleteTodo(ActionEvent event) {
        if (listOfTodos.getSelectionModel().isEmpty()) {
            printError("Error. Must select Todo to delete");
        } else {
            Todo temp = listOfTodos.getSelectionModel().getSelectedItem();
            if (!temp.getType().equals("Sub")) {
                try {
                    todoList.removeTodo(temp);
                } catch (OutOfBoundListIndexException e) {
                    System.out.println("Error deleting todo");
                }
                listOfTodos.getSelectionModel().clearSelection();
            } else {
                SubTodo st = (SubTodo) temp;
                todoList.removeSuperTodoSub(st);
            }
            listOfTodos.getSelectionModel().clearSelection();
        }
    }

    @FXML
    void setStatus(ActionEvent event) {
        if (listOfTodos.getSelectionModel().isEmpty()) {
            printError("Error. Must select Todo to change status of");
        } else {
            Todo temp = listOfTodos.getSelectionModel().getSelectedItem();
            if (temp.getStatus()) {
                temp.setStatus(false);
            } else {
                temp.setStatus(true);
            }
            listOfTodos.getSelectionModel().clearSelection();
            listOfTodos.refresh();
            calculateProgress();
        }
    }

    @FXML
    void showNewItemDialog(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/gui/addWindow.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            AddTodoController controller = fxmlLoader.getController();
            controller.setTodoList(todoList);
            stage.initOwner(mainAnchorPane.getScene().getWindow());
            stage.setTitle("Add New Todo Item");
            stage.setScene(new Scene(root1));
            stage.showAndWait();
        } catch (Exception e) {
            System.out.println("Error loading new window");
            e.printStackTrace();
        }
    }

    @FXML
    void showEditItemDialog(ActionEvent event) {
        if (listOfTodos.getSelectionModel().isEmpty()) {
            printError("Error. Must select Todo to edit");
        } else {
            if (!listOfTodos.getSelectionModel().getSelectedItem().getType().equals("Super")) {
                openEditWindow();
            } else {
                openEditSuperWindow();
            }
            listOfTodos.getSelectionModel().clearSelection();
        }
    }

    private void openEditWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../gui/editWindow.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            EditTodoController controller = fxmlLoader.getController();
            controller.setTodoList(todoList, listOfTodos.getSelectionModel().getSelectedItem());
            stage.initOwner(mainAnchorPane.getScene().getWindow());
            stage.setTitle("Edit Todo Item");
            stage.setScene(new Scene(root1));
            stage.showAndWait();
        } catch (Exception e) {
            System.out.println("Error loading new window");
            e.printStackTrace();
        }
    }

    private void openEditSuperWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/gui/editSuperWindow.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            EditSuperController controller = fxmlLoader.getController();
            controller.setTodoList(todoList, listOfTodos.getSelectionModel().getSelectedItem());
            stage.initOwner(mainAnchorPane.getScene().getWindow());
            stage.setTitle("Edit Super Todo Item");
            stage.setScene(new Scene(root1));
            stage.showAndWait();
        } catch (Exception e) {
            System.out.println("Error loading new window");
            e.printStackTrace();
        }
    }

    private void printError(String text) {
        errorLabel.setText(text);
        errorLabel.setTextFill(Color.WHITE);
        FadeTransition ft = new FadeTransition(Duration.millis(3000), errorLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.play();
    }

    private void calculateProgress() {
        double progress = todoList.getCompleted() / todoList.getTotalTodos();
        progressBar.setProgress(progress);
    }

    @FXML
    void initialize() {
    }

    public void init(TodoList todos) throws IOException {
        this.todoList = todos;
        // Load previous data
        todos.load(null);
        // Initialize progress bar
        calculateProgress();
        // Initialize weather info
        refreshWeather();
    }

    @FXML
    void refreshWeather(ActionEvent event) throws IOException {
        refreshWeather();
        System.out.println("Weather Refreshed!");
    }

    private void refreshWeather() throws IOException {
        weather = new Weather();
        currentDate.setText(LocalDate.now().toString());
        currentTemp.setText(weather.getCurrentTemp() + "°C");
        maxTemp.setText(weather.getMaxTemp() + " °C");
        minTemp.setText(weather.getMinTemp() + " °C");
        weatherDescription.setText(weather.getDescription());
    }

    @Override
    public void update(Observable o, Object arg) {
        transferList();
        listOfTodos.setItems(this.list);
    }

    public void transferList() {
        this.list.clear();
        for (Todo t : todoList) {
            this.list.add(t);
            if (t.getType().equals("Super")) {
                SuperTodo tempSuper = (SuperTodo) t;
                for (Todo st : tempSuper.getSubList()) {
                    this.list.add(st);
                }
            }
        }
    }

}
