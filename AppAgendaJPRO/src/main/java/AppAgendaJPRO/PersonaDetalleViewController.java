package AppAgendaJPRO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import AppAgendaJPRO.Persona;
import AppAgendaJPRO.Provincia;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author UsuarioDAM
 */
public class PersonaDetalleViewController implements Initializable {

    private Pane rootAgendaView;

    public void setRootAgendaView(Pane rootAgendaView) {
        this.rootAgendaView = rootAgendaView;
    }
    // Objetos
    private TableView tableViewPrevio;
    private Persona persona;
    private EntityManager entityManager;
    private boolean nuevaPersona;

    // Estado Civil
    public static final char CASADO = 'C';
    public static final char SOLTERO = 'S';
    public static final char VIUDO = 'V';

    // Imágenes
    public static final String CARPETA_FOTOS = "src/appagenda/Fotos";

    // Variable para comprobar todos los datos
    boolean errorFormato = false;

    public void setTableViewPrevio(TableView tableViewPrevio) {
        this.tableViewPrevio = tableViewPrevio;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setPersona(EntityManager entityManager, Persona persona, Boolean nuevaPersona) {
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        //EntityManager em = emf.createEntityManager();

        this.entityManager = entityManager;
        //em.getTransaction().begin();

        if (!nuevaPersona) {
            this.persona = entityManager.find(Persona.class, persona.getId());
        } else {
            this.persona = persona;
        }
        this.nuevaPersona = nuevaPersona;
    }

    @FXML
    private AnchorPane rootPersonaDetalleView;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private ImageView imageViewFoto;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void mostrarDatos() {
        //Persona persona = new Persona();

        // Muestra el nombre de la persona selecionada
        if(persona.getNombre() != null)
        {
            textFieldNombre.setText(persona.getNombre().toString());
        }
        
        // Muestra el apellido de la persona seleccionada
        if(persona.getApellidos() != null)
        {
            textFieldApellidos.setText(persona.getApellidos().toString());
        }
        
        // Muestra el telefono de la persona en caso de que este exista
        if(persona.getTelefono() != null)
        {
            textFieldTelefono.setText(persona.getTelefono().toString());
        }
        
        // Muestra el Email en caso de que exista
        if(persona.getEmail() != null)
        {
            textFieldEmail.setText(persona.getEmail().toString());
        }

        // Cambia el contenido de 'short' a 'string' en el paso de números de hijos
        if (persona.getNumHijos() != null) {
            textFieldNumHijos.setText(persona.getNumHijos().toString());
        }

        // Cambia el contenido de 'BigDecimal' a 'string' en el paso de salario
        if (persona.getSalario() != null) {
            textFieldSalario.setText(persona.getSalario().toString());
        }

        // Cambia el balor de tipo 'boolean' de forma que aparezca seleccionado
        if (persona.getJubilado() != null) {
            checkBoxJubilado.setSelected(persona.getJubilado());
        }

        // Selecciona un valor según nosotro el estado que pulsemos
        if (persona.getEstadoCivil() != null) {
            switch (persona.getEstadoCivil()) {
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }

        // Pasamos la fecha de nacimiento de Date a LocalDate
        if (persona.getFechaNacimiento() != null) {
            Date date = persona.getFechaNacimiento();
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalDate localDate = zdt.toLocalDate();
            datePickerFechaNacimiento.setValue(localDate);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        // Creamos nuestra lista desplegable con los datos de las provincias de nuestra base de datos
        Query queryProvinciaFindAll = em.createNamedQuery("Provincia.findAll");
        List listProvincia = queryProvinciaFindAll.getResultList();
        comboBoxProvincia.setItems(FXCollections.observableList(listProvincia));

        if (persona.getProvincia() != null) {
            comboBoxProvincia.setValue(persona.getProvincia());
        }

        /*
        comboBoxProvincia.setCellFactory((ListView<Provincia> l) -> new ListCell<Provincia>() {
            protected void updateItem(Provincia provincia, Boolean empty) {
                super.updateItem(provincia, empty);
                if (provincia == null || empty) {
                    setText("");
                } else {
                    setText(provincia.getCodigo() + "-" + provincia.getNombre());
                }
            }
        });
*/
        comboBoxProvincia.setConverter(new StringConverter<Provincia>() {
            @Override
            public String toString(Provincia provincia) {
                if (provincia == null) {
                    return null;
                } else {
                    return provincia.getCodigo() + "-" + provincia.getNombre();
                }
            }

            @Override
            public Provincia fromString(String userId) {
                return null;
            }
        });

        // Añade la nueva imagen
        if (persona.getFoto() != null) {
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } else {
                Alert alert = new Alert(AlertType.INFORMATION,
                        "No se encuentra laimagen en " + file.toURI().toString());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);

        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        //EntityManager em = emf.createEntityManager();

        comprobarDatos();

        // Añade la persona
        if (nuevaPersona) {
            entityManager.getTransaction().begin();
            entityManager.persist(persona);
        } else {
            entityManager.getTransaction().begin();
            entityManager.merge(persona);
        }
        entityManager.getTransaction().commit();

        int numFilaSeleccionada;
        if (nuevaPersona) {
            tableViewPrevio.getItems().add(persona);
            numFilaSeleccionada = tableViewPrevio.getItems().size() - 1;
            tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
            tableViewPrevio.scrollTo(numFilaSeleccionada);
        } else {
            numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
            tableViewPrevio.getItems().set(numFilaSeleccionada, persona);
        }
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
    }

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {

        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);

        /*
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().rollback();
         */
        int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();

    }

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        File file = fileChooser.showOpenDialog(
                rootPersonaDetalleView.getScene().getWindow());
        if (file != null) {
            try {
                Files.copy(file.toPath(), new File(CARPETA_FOTOS
                        + "/" + file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex) {
                Alert alert = new Alert(AlertType.WARNING, "Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(AlertType.WARNING,
                        "No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }
    }

    public void comprobarDatos() {
        boolean errorFormato = false;
        if (!errorFormato) {
            try {
                persona.setNombre(textFieldNombre.getText());
                
                 if(!textFieldNombre.getText().isEmpty()){
                     try{
                         persona.setNombre(String.valueOf(textFieldNombre.getText()));
                     }catch(StringIndexOutOfBoundsException ex){
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido");
                        alert.showAndWait();
                        textFieldNumHijos.requestFocus();
                     }
                 }
                        
                        
                persona.setApellidos(textFieldApellidos.getText());
                persona.setTelefono(textFieldTelefono.getText());
                persona.setEmail(textFieldEmail.getText());
                // Comprobamos el numero de hijo
                if (!textFieldNumHijos.getText().isEmpty()) {
                    try {
                        persona.setNumHijos(Short.valueOf(textFieldNumHijos.getText()));
                    } catch (NumberFormatException ex) {
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido");
                        alert.showAndWait();
                        textFieldNumHijos.requestFocus();
                    }
                }

                // Comprobamos el salario
                if (!textFieldSalario.getText().isEmpty()) {
                    try {
                        persona.setSalario(BigDecimal.valueOf(Double.valueOf(textFieldSalario.getText()).doubleValue()));
                    } catch (NumberFormatException ex) {
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido");
                        alert.showAndWait();
                        textFieldSalario.requestFocus();
                    }
                }

                // Comprobamos si el checkBox de jubilado a sido seleccionado
                persona.setJubilado(checkBoxJubilado.isSelected());

                // Comprobamos la opción seleccionada respecto a estado civil
                if (radioButtonCasado.isSelected()) {
                    persona.setEstadoCivil(CASADO);
                } else if (radioButtonSoltero.isSelected()) {
                    persona.setEstadoCivil(SOLTERO);
                } else if (radioButtonViudo.isSelected()) {
                    persona.setEstadoCivil(VIUDO);
                }

                // Comprobamos que la fecha haya sido incluida
                if (datePickerFechaNacimiento.getValue() != null) {
                    LocalDate localDate = datePickerFechaNacimiento.getValue();
                    ZonedDateTime zonedDateTime
                            = localDate.atStartOfDay(ZoneId.systemDefault());
                    Instant instant = zonedDateTime.toInstant();
                    Date date = Date.from(instant);
                    persona.setFechaNacimiento(date);
                } else {
                    persona.setFechaNacimiento(null);
                }

                // Comprobamos que el usuario introduzaca una provincia
                if (comboBoxProvincia.getValue() != null) {
                    persona.setProvincia(comboBoxProvincia.getValue());
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION, "Debe indicar una provincia");
                    alert.showAndWait();
                    errorFormato = true;
                }

            } catch (RollbackException ex) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. "
                        + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }

        }
    }

    
}
