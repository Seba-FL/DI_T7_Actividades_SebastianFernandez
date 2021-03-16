package AppAgendaJPRO;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import AppAgendaJPRO.Persona;
import AppAgendaJPRO.Provincia;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author UsuarioDAM
 */
public class AgendaViewController implements Initializable {

    private EntityManager entityManager;
    private Persona personaSeleccionada;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @FXML
    private AnchorPane rootAgendaView;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private javafx.scene.control.TableColumn<Persona, String> columnNombre;
    @FXML
    private javafx.scene.control.TableColumn<Persona, String> columnApellidos;
    @FXML
    private javafx.scene.control.TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    if (cellData.getValue().getProvincia() != null) {
                        property.setValue(cellData.getValue().getProvincia().getNombre());
                    }
                    return property;
                });
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    personaSeleccionada = newValue;
                    if (personaSeleccionada != null) {
                        textFieldNombre.setText(personaSeleccionada.getNombre());
                        textFieldApellidos.setText(personaSeleccionada.getApellidos());
                    } else {
                        textFieldNombre.setText("");
                        textFieldApellidos.setText("");
                    }
                });

    }

    public void cargarTodasPersonas() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        Query queryPersonaFindAll = em.createNamedQuery("Persona.findAll");
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona));
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        if (personaSeleccionada != null) {
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());

            em.getTransaction().begin();
            em.merge(personaSeleccionada);
            em.getTransaction().commit();

            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);

        }

    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {

        try {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);

            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, true);
            personaDetalleViewController.setEntityManager(entityManager);

            // Mostramos Datos
            personaDetalleViewController.mostrarDatos();

        } catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {
        try {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);

            // Para el botón Editar
            //personaSeleccionada = new Persona();
            personaDetalleViewController.setEntityManager(entityManager);
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, false);

            // Mostramos Datos
            personaDetalleViewController.mostrarDatos();

        } catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        //EntityManager em = emf.createEntityManager();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " "
                + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            // Acciones a realizar si el usuario acepta
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
                entityManager.merge(personaSeleccionada);
                entityManager.remove(personaSeleccionada);
                entityManager.getTransaction().commit();

            }

            tableViewAgenda.getItems().remove(this.personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
        } else {
            // Acciones a realizar si el usuario cancela
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, this.personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();

        }

    }
}
