package com.difase.system;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    cargarNumeroCotizacion();
    mostrarCodigoCotizacion();
    addRow();
    addImageRow();
    updateTotal();
  }

  private void cargarNumeroCotizacion() {
    cotizacionNumero = ConfigManager.getCotizacionNumero();
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
    imageNumberField.getStyleClass().add("img-number");

    ImageView previewImageView = new ImageView();
    previewImageView.setFitWidth(400);
    previewImageView.setPreserveRatio(true);
    previewImageView.getStyleClass().add("img-prev");

    Button chooseImageButton = new Button("Seleccionar");
    chooseImageButton.setPrefWidth(265);
    chooseImageButton.getStyleClass().add("img-select");
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

    Button deleteImageButton = new Button("Eliminar");
    deleteImageButton.setPrefWidth(100);
    deleteImageButton.getStyleClass().add("img-delete");
    deleteImageButton.setOnAction(_ -> {
      imagesContainer.getChildren().remove(newImageRow);
      updateImageNumbers();
    });

    TextField imageTitleField = new TextField();
    imageTitleField.setPrefWidth(400);
    imageTitleField.getStyleClass().add("img-title");

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

  private void guardarNumeroCotizacion() {
    ConfigManager.setCotizacionNumero(cotizacionNumero);
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

  private void SoloNumerosEnteros(KeyEvent keyEvent, TextField textField) {
    try {
      char key = keyEvent.getCharacter().charAt(0);
      if (!Character.isDigit(key)) {
        keyEvent.consume();
      }
    } catch (Exception e) {
    }
  }

  private void SoloNumerosConDecimales(KeyEvent keyEvent) {
    try {
      char key = keyEvent.getCharacter().charAt(0);
      TextField source = (TextField) keyEvent.getSource();
      String currentText = source.getText();
      if (!Character.isDigit(key) && key != '.') {
        keyEvent.consume();
      }
      if (currentText.contains(".") && key == '.') {
        keyEvent.consume();
      }
      if (currentText.contains(".") && currentText.split("\\.")[1].length() >= 2) {
        keyEvent.consume();
      }
    } catch (Exception e) {
    }
  }

  private void updateRowTotal(TextField quantityField, TextField unitPriceField, TextField totalField) {
    try {
      int quantity = Integer.parseInt(quantityField.getText());
      double unitPrice = Double.parseDouble(unitPriceField.getText());
      double total = quantity * unitPrice;
      totalField.setText(String.format("%.2f", total));
      updateTotal();
    } catch (NumberFormatException e) {
      totalField.setText("0.00");
    }
  }

  private void updateTotal() {
    double totalSum = 0;
    for (Node row : rowsContainer.getChildren()) {
      if (row instanceof HBox && row != totalRow) {
        TextField rowTotal = (TextField) ((HBox) row).getChildren().get(3);
        try {
          totalSum += Double.parseDouble(rowTotal.getText());
        } catch (NumberFormatException e) {
        }
      }
    }
    totalAmount.setText(String.format("%.2f", totalSum));
  }

  @FXML
  private void addRow() {
    HBox newRow = new HBox();
    newRow.setPrefWidth(695.0);
    newRow.setMinHeight(50.0);

    TextArea newDescription = new TextArea();
    newDescription.setPrefWidth(430);
    newDescription.setMaxHeight(Double.MAX_VALUE);
    newDescription.setWrapText(true);
    newDescription.getStyleClass().add("cell-style");
    setupDynamicHeight(newDescription);

    TextField newQuantity = new TextField();
    newQuantity.setPrefWidth(80);
    newQuantity.setMaxHeight(Double.MAX_VALUE);
    newQuantity.getStyleClass().add("cell-style");
    newQuantity.addEventHandler(KeyEvent.KEY_TYPED, event -> SoloNumerosEnteros(event, newQuantity));

    TextField newUnitPrice = new TextField();
    newUnitPrice.setPrefWidth(80);
    newUnitPrice.setMaxHeight(Double.MAX_VALUE);
    newUnitPrice.getStyleClass().add("cell-style");
    newUnitPrice.addEventHandler(KeyEvent.KEY_TYPED, event -> SoloNumerosConDecimales(event));

    TextField newTotal = new TextField();
    newTotal.setPrefWidth(80);
    newTotal.setMaxHeight(Double.MAX_VALUE);
    newTotal.getStyleClass().add("cell-style");
    newTotal.addEventHandler(KeyEvent.KEY_TYPED, event -> SoloNumerosConDecimales(event));
    newQuantity.textProperty().addListener((_, _, _) -> updateRowTotal(newQuantity, newUnitPrice, newTotal));
    newUnitPrice.textProperty().addListener((_, _, _) -> updateRowTotal(newQuantity, newUnitPrice, newTotal));

    Button deleteButton = new Button();
    deleteButton.setPrefWidth(25);
    deleteButton.setMaxHeight(Double.MAX_VALUE);
    deleteButton.getStyleClass().add("delete-button");
    Image trashImage = new Image(getClass().getResourceAsStream("/imagenes/delete.png"));
    ImageView trashImageView = new ImageView(trashImage);
    trashImageView.setFitWidth(20);
    trashImageView.setFitHeight(20);
    deleteButton.setGraphic(trashImageView);
    deleteButton.setOnAction(_ -> {
      rowsContainer.getChildren().remove(newRow);
      updateTotal();
    });

    newRow.getChildren().addAll(newDescription, newQuantity, newUnitPrice, newTotal, deleteButton);
    rowsContainer.getChildren().add(rowsContainer.getChildren().size() - 1, newRow);

    updateTotal();
  }

  private void mostrarCodigoCotizacion() {
    String codigoCotizacion = generarCodigoCotizacion();
    CodigoCOT.setText(codigoCotizacion);
  }

  @FXML
  private void handleGeneratePdf() {
    String pdfSavePath = ConfigManager.getPdfSavePath();
    if (pdfSavePath == null) {
      System.out.println("No se seleccionó ninguna ruta de guardado. Operación cancelada.");
      return;
    }

    String codigoCotizacion = generarCodigoCotizacion();
    String nombreArchivoPdf = String.format("cotizacion-%d-%02d.pdf", cotizacionNumero, Year.now().getValue() % 100);
    String fullPath = pdfSavePath + File.separator + nombreArchivoPdf;

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
        fullPath,
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
