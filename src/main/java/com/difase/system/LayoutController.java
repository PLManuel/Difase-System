package com.difase.system;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class LayoutController implements Initializable {

  private static final String CONFIG_FILE_INTERNAL = "/config.properties";
  private static final String CONFIG_FILE_EXTERNAL = "config.properties";
  private int cotizacionNumero;

  @FXML
  private Label CodigoCOT;

  @FXML
  private VBox imagesContainer;

  @FXML
  private TextField totalAmount;

  @FXML
  private TextField sresField;

  @FXML
  private TextField atencionField;

  @FXML
  private TextField contactoField;

  @FXML
  private TextField referenciaField;

  @FXML
  private VBox rowsContainer;

  @FXML
  private HBox totalRow;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    verificarOCrearConfigExterno();
    cargarNumeroCotizacion();
    mostrarCodigoCotizacion();
    addRow();
    addImageRow();
    updateTotal();
  }

  private void verificarOCrearConfigExterno() {
    Path externalConfigPath = Paths.get(CONFIG_FILE_EXTERNAL);
    if (!Files.exists(externalConfigPath)) {
      try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE_INTERNAL)) {
        if (input != null) {
          Files.copy(input, externalConfigPath);
          System.out.println("Archivo config.properties copiado a la ubicación externa.");
        } else {
          System.out.println("No se pudo encontrar el archivo interno config.properties.");
        }
      } catch (IOException e) {
        System.out.println("Error al copiar config.properties al directorio externo: " + e.getMessage());
      }
    }
  }

  @FXML
  private void addImageRow() {
    VBox newImageRow = new VBox();
    newImageRow.setPrefWidth(400.0);
    newImageRow.setMinHeight(50.0);
    VBox.setMargin(newImageRow, new Insets(0, 0, 10, 0));

    TextField imageNumberField = new TextField(String.valueOf(imagesContainer.getChildren().size() + 1));
    imageNumberField.setPrefWidth(35);
    imageNumberField.setEditable(false);

    Button chooseImageButton = new Button("Seleccionar");
    chooseImageButton.setPrefWidth(265);
    ImageView previewImageView = new ImageView();
    previewImageView.setFitWidth(400);
    previewImageView.setPreserveRatio(true);

    chooseImageButton.setOnAction(_ -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
      File selectedFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
      if (selectedFile != null) {
        Image image = new Image(selectedFile.toURI().toString());
        previewImageView.setImage(image);
        chooseImageButton.setText(selectedFile.getName());
        double imageHeight = previewImageView.getBoundsInLocal().getHeight();
        newImageRow.setMinHeight(imageHeight);
      }
    });

    TextField imageTitleField = new TextField();
    imageTitleField.setPrefWidth(400);

    Button deleteImageButton = new Button("Eliminar");
    deleteImageButton.setPrefWidth(100);
    deleteImageButton.setOnAction(_ -> {
      imagesContainer.getChildren().remove(newImageRow);
      updateImageNumbers();
    });

    newImageRow.getChildren().addAll(
        new HBox(imageNumberField, chooseImageButton, deleteImageButton),
        imageTitleField,
        previewImageView);

    imagesContainer.getChildren().add(newImageRow);
  }

  private void updateImageNumbers() {
    int number = 1;
    for (Node node : imagesContainer.getChildren()) {
      if (node instanceof VBox) {
        VBox imageRow = (VBox) node;
        TextField imageNumberField = (TextField) ((HBox) imageRow.getChildren().get(0)).getChildren().get(0);
        imageNumberField.setText(String.valueOf(number++));
      }
    }
  }

  private void cargarNumeroCotizacion() {
    Properties props = new Properties();
    try (FileInputStream input = new FileInputStream(CONFIG_FILE_EXTERNAL)) {
      props.load(input);
      cotizacionNumero = Integer.parseInt(props.getProperty("cotizacion.numero", "1"));
    } catch (IOException e) {
      System.out.println("No se pudo cargar el archivo de configuración externo: " + CONFIG_FILE_EXTERNAL);
      cotizacionNumero = 1;
    }
  }

  private void guardarNumeroCotizacion() {
    Properties props = new Properties();
    props.setProperty("cotizacion.numero", String.valueOf(cotizacionNumero));
    try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_EXTERNAL)) {
      props.store(out, "Actualización de configuración de cotización");
    } catch (IOException e) {
      System.out.println("No se pudo escribir en el archivo de configuración externo: " + CONFIG_FILE_EXTERNAL);
      e.printStackTrace();
    }
  }

  private String generarCodigoCotizacion() {
    int year = Year.now().getValue() % 100;
    return String.format("DFM-%d-%02d", cotizacionNumero, year);
  }

  private void setupDynamicHeight(TextArea textArea) {
    textArea.sceneProperty().addListener((newScene) -> {
      if (newScene != null) {
        textArea.applyCss();
        Node text = textArea.lookup(".text");

        textArea.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
          return textArea.getFont().getSize() + text.getBoundsInLocal().getHeight();
        }, text.boundsInLocalProperty()));

        text.boundsInLocalProperty().addListener((_, _, _) -> {
          Platform.runLater(textArea::requestLayout);
        });
      }
    });
  }

  // Método para permitir solo números enteros y mostrar siempre 2 dígitos en el campo cantidad
  private void SoloNumerosEnteros(KeyEvent keyEvent, TextField textField) {
    try {
      char key = keyEvent.getCharacter().charAt(0);
      if (!Character.isDigit(key)) {
        keyEvent.consume();
      }
    } catch (Exception e) {
    }
  }

// Método para permitir solo números con hasta 2 decimales (para precio unitario y total)
  private void SoloNumerosConDecimales(KeyEvent keyEvent) {
    try {
      char key = keyEvent.getCharacter().charAt(0);
      TextField source = (TextField) keyEvent.getSource();
      String currentText = source.getText();

      // Solo permite números y un punto decimal
      if (!Character.isDigit(key) && key != '.') {
        keyEvent.consume();
      }

      // Si ya hay un punto decimal, evitar insertar más puntos
      if (currentText.contains(".") && key == '.') {
        keyEvent.consume();
      }

      // Limitar la cantidad de decimales a 2
      if (currentText.contains(".") && currentText.split("\\.")[1].length() >= 2) {
        keyEvent.consume(); // No permitir más de dos decimales
      }
    } catch (Exception e) {
    }
  }

// Actualizar el total por fila (tanto en cantidad como precio unitario)
  private void updateRowTotal(TextField quantityField, TextField unitPriceField, TextField totalField) {
    try {
      // Obtener la cantidad, precio unitario y calcular el total
      int quantity = Integer.parseInt(quantityField.getText());
      double unitPrice = Double.parseDouble(unitPriceField.getText());
      double total = quantity * unitPrice;

      // Actualizar el campo total con 2 decimales
      totalField.setText(String.format("%.2f", total));
      updateTotal();
    } catch (NumberFormatException e) {
      totalField.setText("0.00"); // Si hay un error en el formato, establecer 0.00
    }
  }

// Actualizar el total general sumando los valores de los rowTotal
  private void updateTotal() {
    double totalSum = 0;
    for (Node row : rowsContainer.getChildren()) {
      if (row instanceof HBox && row != totalRow) {
        TextField rowTotal = (TextField) ((HBox) row).getChildren().get(3); // Acceder al campo total
        try {
          totalSum += Double.parseDouble(rowTotal.getText());
        } catch (NumberFormatException e) {
          // Ignorar si hay un error en la conversión
        }
      }
    }

    // Actualizar el campo total con 2 decimales
    totalAmount.setText(String.format("%.2f", totalSum));
  }

// Método para agregar nuevas filas con validación y cálculo automático
  @FXML
  private void addRow() {
    HBox newRow = new HBox();
    newRow.setPrefWidth(695.0);
    newRow.setMinHeight(50.0);

    // Descripción (sin cambios)
    TextArea newDescription = new TextArea();
    newDescription.setPrefWidth(430);
    newDescription.setMaxHeight(Double.MAX_VALUE);
    newDescription.setWrapText(true);
    setupDynamicHeight(newDescription);

    // Cantidad (solo números enteros, con 2 dígitos)
    TextField newQuantity = new TextField();
    newQuantity.setPrefWidth(80);
    newQuantity.setMaxHeight(Double.MAX_VALUE);

    // Validación para que solo se permitan números enteros
    newQuantity.addEventHandler(KeyEvent.KEY_TYPED, event -> SoloNumerosEnteros(event, newQuantity));

    // Precio Unitario (números con hasta 2 decimales)
    TextField newUnitPrice = new TextField();
    newUnitPrice.setPrefWidth(80);
    newUnitPrice.setMaxHeight(Double.MAX_VALUE);

    // Validación para que solo se permitan números con hasta 2 decimales
    newUnitPrice.addEventHandler(KeyEvent.KEY_TYPED, event -> SoloNumerosConDecimales(event));

    // Total (números con hasta 2 decimales, igual que precio unitario)
    TextField newTotal = new TextField();
    newTotal.setPrefWidth(80);
    newTotal.setMaxHeight(Double.MAX_VALUE);

    // Usar el mismo TextFormatter que para precio unitario
    newTotal.addEventHandler(KeyEvent.KEY_TYPED, event -> SoloNumerosConDecimales(event));

    // Actualizar el total cuando cambian los valores
    newQuantity.textProperty().addListener((_, _, _) -> updateRowTotal(newQuantity, newUnitPrice, newTotal));
    newUnitPrice.textProperty().addListener((_, _, _) -> updateRowTotal(newQuantity, newUnitPrice, newTotal));

    // Botón de eliminar
    Button deleteButton = new Button("");
    deleteButton.setPrefWidth(25);
    deleteButton.setMaxHeight(Double.MAX_VALUE);
    deleteButton.setOnAction(_ -> {
      rowsContainer.getChildren().remove(newRow);
      updateTotal();
    });

    // Añadir los elementos a la fila
    newRow.getChildren().addAll(newDescription, newQuantity, newUnitPrice, newTotal, deleteButton);

    // Añadir la fila al contenedor
    rowsContainer.getChildren().add(rowsContainer.getChildren().size() - 1, newRow);

    updateTotal();
  }

  private void mostrarCodigoCotizacion() {
    String codigoCotizacion = generarCodigoCotizacion();
    CodigoCOT.setText(codigoCotizacion);
  }

  @FXML
  private void handleGeneratePdf() {
    String codigoCotizacion = generarCodigoCotizacion();
    String nombreArchivoPdf = String.format("cotizacion-%d-%02d.pdf", cotizacionNumero, Year.now().getValue() % 100);

    String sres = sresField.getText();
    String atencion = atencionField.getText();
    String contacto = contactoField.getText();
    String referencia = referenciaField.getText();

    List<Map<String, Object>> detallesFilas = new ArrayList<>();
    for (Node row : rowsContainer.getChildren()) {
      if (row instanceof HBox && row != totalRow) {
        HBox hbox = (HBox) row;
        TextArea description = (TextArea) hbox.getChildren().get(0);
        TextField quantity = (TextField) hbox.getChildren().get(1);
        TextField unitPrice = (TextField) hbox.getChildren().get(2);
        TextField total = (TextField) hbox.getChildren().get(3);

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("descripcion", description.getText());
        detalle.put("cantidad", quantity.getText());
        detalle.put("precioUnitario", unitPrice.getText());
        detalle.put("total", total.getText());

        detallesFilas.add(detalle);
      }
    }

    List<Map<String, String>> imagesData = new ArrayList<>();
    for (Node imageRow : imagesContainer.getChildren()) {
      if (imageRow instanceof VBox) {
        VBox vbox = (VBox) imageRow;
        ImageView imageView = (ImageView) vbox.getChildren().get(2);
        if (imageView.getImage() != null) {
          String imagePath = imageView.getImage().getUrl().substring(5);
          String imageNumber = ((TextField) ((HBox) vbox.getChildren().get(0)).getChildren().get(0)).getText();
          String imageTitle = ((TextField) vbox.getChildren().get(1)).getText();

          Map<String, String> imageData = new HashMap<>();
          imageData.put("path", imagePath);
          imageData.put("number", imageNumber);
          imageData.put("title", imageTitle);
          imagesData.add(imageData);
        }
      }
    }

    String totalGeneral = totalAmount.getText();

    PdfGenerator pdfGenerator = new PdfGenerator();
    pdfGenerator.generateInvoice(
        nombreArchivoPdf,
        sres,
        atencion,
        contacto,
        referencia,
        totalGeneral,
        codigoCotizacion,
        detallesFilas,
        imagesData);

    cotizacionNumero++;
    guardarNumeroCotizacion();
    mostrarCodigoCotizacion();
  }

  @FXML
  private void vistaPreviaPdf() throws IOException {
    String codigoCotizacion = generarCodigoCotizacion();
    String nombreArchivoPdfTemporal = "vista-previa-" + codigoCotizacion + ".pdf";

    String sres = sresField.getText();
    String atencion = atencionField.getText();
    String contacto = contactoField.getText();
    String referencia = referenciaField.getText();

    List<Map<String, Object>> detallesFilas = new ArrayList<>();
    for (Node row : rowsContainer.getChildren()) {
      if (row instanceof HBox && row != totalRow) {
        HBox hbox = (HBox) row;
        TextArea description = (TextArea) hbox.getChildren().get(0);
        TextField quantity = (TextField) hbox.getChildren().get(1);
        TextField unitPrice = (TextField) hbox.getChildren().get(2);
        TextField total = (TextField) hbox.getChildren().get(3);

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("descripcion", description.getText());
        detalle.put("cantidad", quantity.getText());
        detalle.put("precioUnitario", unitPrice.getText());
        detalle.put("total", total.getText());

        detallesFilas.add(detalle);
      }
    }

    List<Map<String, String>> imagesData = new ArrayList<>();
    for (Node imageRow : imagesContainer.getChildren()) {
      if (imageRow instanceof VBox) {
        VBox vbox = (VBox) imageRow;
        ImageView imageView = (ImageView) vbox.getChildren().get(2);
        if (imageView.getImage() != null) {
          String imagePath = imageView.getImage().getUrl().substring(5);
          String imageNumber = ((TextField) ((HBox) vbox.getChildren().get(0)).getChildren().get(0)).getText();
          String imageTitle = ((TextField) vbox.getChildren().get(1)).getText();

          Map<String, String> imageData = new HashMap<>();
          imageData.put("path", imagePath);
          imageData.put("number", imageNumber);
          imageData.put("title", imageTitle);
          imagesData.add(imageData);
        }
      }
    }

    String totalGeneral = totalAmount.getText();

    PdfGenerator pdfGenerator = new PdfGenerator();
    pdfGenerator.generateInvoice(
        nombreArchivoPdfTemporal,
        sres,
        atencion,
        contacto,
        referencia,
        totalGeneral,
        codigoCotizacion,
        detallesFilas,
        imagesData);

    abrirPdfTemporal(nombreArchivoPdfTemporal);
  }

  private void abrirPdfTemporal(String nombreArchivoPdfTemporal) throws IOException {
    File pdfFile = new File(nombreArchivoPdfTemporal);
    if (pdfFile.exists()) {
      Desktop.getDesktop().open(pdfFile);
      new Thread(() -> {
        try {
          Thread.sleep(5000);
          pdfFile.delete();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }).start();
    } else {
      System.out.println("El archivo PDF de vista previa no existe.");
    }
  }
}
