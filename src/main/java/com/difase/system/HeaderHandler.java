package com.difase.system;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HeaderHandler implements IEventHandler {

  private static final String LOGO_PATH = "src/main/java/recursos/logo.png";
  private static final String FONT_PATH = "src/main/java/recursos/LSANS.ttf";
  private final String cotizacion;

  public HeaderHandler(String cotizacion) {
    this.cotizacion = cotizacion;
  }

  @Override
  public void handleEvent(Event event) {
    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
    PdfDocument pdfDoc = docEvent.getDocument();
    PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage().newContentStreamBefore(), docEvent.getPage().getResources(), pdfDoc);

    // Add logo image
    ImageData imageData = null;
    try {
      imageData = ImageDataFactory.create(LOGO_PATH);
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    }
    Image image = new Image(imageData).setHeight(54);
    float imageX = pdfDoc.getDefaultPageSize().getLeft() + 83;
    float imageY = pdfDoc.getDefaultPageSize().getTop() - 84;
    image.setFixedPosition(imageX, imageY);
    new Canvas(pdfCanvas, pdfDoc.getDefaultPageSize()).add(image);

    // Load and configure custom font
    PdfFont font = null;
    try {
      font = PdfFontFactory.createFont(FONT_PATH, "Identity-H");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    pdfCanvas.setFontAndSize(font, 9);

    // Get current date
    String fechaActual = obtenerFechaActualFormato();

    // Header text lines
    String linea1 = "Lima  " + fechaActual;
    String linea2 = "COTIZACIÓN: " + cotizacion;

    // Calculate right-aligned positions
    float pageWidth = pdfDoc.getDefaultPageSize().getWidth();
    float rightMargin = 83; // Right margin
    float textXLinea1 = pageWidth - rightMargin - font.getWidth(linea1, 9);
    float textXLinea2 = pageWidth - rightMargin - font.getWidth(linea2, 9);
    float textY = pdfDoc.getDefaultPageSize().getTop() - 45;

    // Draw each line of text
    pdfCanvas.beginText();
    pdfCanvas.setFontAndSize(font, 9);
    pdfCanvas.moveText(textXLinea1, textY);
    pdfCanvas.showText(linea1);

    pdfCanvas.moveText(textXLinea2 - textXLinea1, - 15); // Move down for second line
    pdfCanvas.showText(linea2);
    pdfCanvas.endText();

    pdfCanvas.release();
  }

  private String obtenerFechaActualFormato() {
    LocalDate fechaActual = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
        Locale.forLanguageTag("es-ES"));
    return fechaActual.format(formatter);
  }
}