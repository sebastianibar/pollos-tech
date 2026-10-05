package com.example.crudjavafx.controller;

import com.example.crudjavafx.LoginApplication;
import com.example.crudjavafx.db.ManejadorEmpleadoDB;
import com.example.crudjavafx.model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    String userBD = "tu_user";
    String passwdBD = "tu_contraseña";
    String nombreBD = "PollosTech"; // El nombre limpio
    String urlBD = "jdbc:postgresql://localhost:5432/" + nombreBD;
    ManejadorEmpleadoDB test = new ManejadorEmpleadoDB(urlBD, userBD, passwdBD);
    @FXML
    private TextField userText;
    @FXML
    private TextField passwdText;
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        System.out.println("Conectando al contenedor de base de datos...");
        if (test.probarConexion()) {
            System.out.println("Conexión exitosa con el contenedor.");
        } else {
            System.err.println("Error: Asegúrate de que el contenedor de Docker esté corriendo.");
        }
    }

    @FXML
    public void ingresar() {
        try {
            String user = userText.getText();
            String contra = passwdText.getText();

            if (user.isEmpty()) {
                mostrarAlerta("Debe ingresar el Usuario", "Error");
                userText.requestFocus();
                return;
            } else if (contra.isEmpty()) {
                mostrarAlerta("Debe ingresar la Contraseña", "Error");
                passwdText.requestFocus();
                return;
            }


            if (!test.probarConexion()) {
                mostrarAlerta("No se pudo conectar a la base de datos. \nVerifica la red o los datos de conexión", "Error de Conexión");
                return;
            }


            int staff_id = test.getCredentials(user, contra);

            if (staff_id != 0) {
                String staff_name = test.getName(staff_id);
                Session.cerrarSesion();

                Session.iniciarSesion(staff_id, staff_name, urlBD, userBD, passwdBD);

                FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("dashboard-view.fxml"));
                Scene mainScene = new Scene(fxmlLoader.load(), 1000, 700);
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());            // 2. Crea el Stage para el Dashboard
                Stage mainStage = new Stage();
                mainStage.setTitle("Dashboard Principal");
                mainStage.setScene(mainScene);
                mainStage.setFullScreen(true);
                mainStage.setOnCloseRequest((WindowEvent event) -> primaryStage.show());

                mainStage.show();
                primaryStage.hide(); // Oculta el login
            } else {
                mostrarAlerta("No se encontró un usuario con esas credenciales", "Error de Autenticación");
            }


        } catch (IOException e) {
            mostrarAlerta("No se pudo iniciar sesión.", "Error");
        }
    }


    private void mostrarAlerta(String mensaje, String titulo) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        javafx.scene.control.DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        Stage stage = primaryStage;
        if (stage != null) {
            alerta.initOwner(stage);
        }

        alerta.showAndWait();
    }

}