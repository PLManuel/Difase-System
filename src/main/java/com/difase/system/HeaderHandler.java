package com.difase.system;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HeaderHandler implements IEventHandler {

  private static final String LOGO_PATH = "/imagenes/logo.png"; // Ruta ajustada a resources/imagenes
  private final PdfFont font;  // Fuente que se pasará al header
  private final String cotizacion;

  public HeaderHandler(String cotizacion, PdfFont font) {
    this.cotizacion = cotizacion;
    this.font = font; // Reutilizamos la fuente
  }

  @Override
  public void handleEvent(Event event) {
    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
    PdfDocument pdfDoc = docEvent.getDocument();
    PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage().newContentStreamBefore(), docEvent.getPage().getResources(),
        pdfDoc);

    ImageData imageData = null;

    try {
      // Cargar la imagen del logo desde los recursos
      InputStream logoStream = getClass().getResourceAsStream(LOGO_PATH);  // Utiliza getResourceAsStream
      if (logoStream != null) {
        byte[] logoBytes = logoStream.readAllBytes();  // Convertir InputStream a byte[]
        imageData = ImageDataFactory.create(logoBytes);  // Crear ImageData a partir del byte[]
      } else {
        throw new MalformedURLException("No se pudo cargar el logo desde la ruta: " + LOGO_PATH);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    Image image = new Image(imageData).setHeight(54);
    float imageX = pdfDoc.getDefaultPageSize().getLeft() + 83;
    float imageY = pdfDoc.getDefaultPageSize().getTop() - 84;
    image.setFixedPosition(imageX, imageY);

    // Añadir la imagen al canvas
    try (Canvas canvas = new Canvas(pdfCanvas, pdfDoc.getDefaultPageSize())) {
      canvas.add(image);
    }

    pdfCanvas.setFontAndSize(font, 9);  // Usar la fuente pasada al HeaderHandler

    String fechaActual = obtenerFechaActualFormato();
    String linea1 = "Lima  " + fechaActual;
    String linea2 = "COTIZACIÓN: " + cotizacion;

    float pageWidth = pdfDoc.getDefaultPageSize().getWidth();
    float rightMargin = 83;
    float textXLinea1 = pageWidth - rightMargin - font.getWidth(linea1, 9);
    float textXLinea2 = pageWidth - rightMargin - font.getWidth(linea2, 9);
    float textY = pdfDoc.getDefaultPageSize().getTop() - 45;

    pdfCanvas.beginText();
    pdfCanvas.setFontAndSize(font, 9);
    pdfCanvas.moveText(textXLinea1, textY);
    pdfCanvas.showText(linea1);
    pdfCanvas.moveText(textXLinea2 - textXLinea1, -15);
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
//package com.difase.system;
//
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.kernel.events.Event;
//import com.itextpdf.kernel.events.IEventHandler;
//import com.itextpdf.kernel.events.PdfDocumentEvent;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.layout.Canvas;
//import com.itextpdf.layout.element.Image;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Locale;
//
//public class HeaderHandler implements IEventHandler {
//
//  private static final String LOGO_PATH = "/imagenes/logo.png"; // Ruta ajustada a resources/imagenes
//  private final PdfFont font;  // Fuente que se pasará al header
//  private final String cotizacion;
//
//  public HeaderHandler(String cotizacion, PdfFont font) {
//    this.cotizacion = cotizacion;
//    this.font = font; // Reutilizamos la fuente
//  }
//
//  @Override
//  public void handleEvent(Event event) {
//    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
//    PdfDocument pdfDoc = docEvent.getDocument();
//    PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage().newContentStreamBefore(), docEvent.getPage().getResources(),
//        pdfDoc);
//
//    ImageData imageData = null;
//
//    try {
//      // Cargar la imagen del logo desde los recursos
//      InputStream logoStream = getClass().getResourceAsStream(LOGO_PATH);  // Utiliza getResourceAsStream
//      if (logoStream != null) {
//        imageData = ImageDataFactory.create(logoStream);
//      } else {
//        throw new MalformedURLException("No se pudo cargar el logo desde la ruta: " + LOGO_PATH);
//      }
//    } catch (MalformedURLException ex) {
//      ex.printStackTrace();
//    }
//
//    Image image = new Image(imageData).setHeight(54);
//    float imageX = pdfDoc.getDefaultPageSize().getLeft() + 83;
//    float imageY = pdfDoc.getDefaultPageSize().getTop() - 84;
//    image.setFixedPosition(imageX, imageY);
//
//    // Añadir la imagen al canvas
//    try (Canvas canvas = new Canvas(pdfCanvas, pdfDoc.getDefaultPageSize())) {
//      canvas.add(image);
//    }
//
//    pdfCanvas.setFontAndSize(font, 9);  // Usar la fuente pasada al HeaderHandler
//
//    String fechaActual = obtenerFechaActualFormato();
//    String linea1 = "Lima  " + fechaActual;
//    String linea2 = "COTIZACIÓN: " + cotizacion;
//
//    float pageWidth = pdfDoc.getDefaultPageSize().getWidth();
//    float rightMargin = 83;
//    float textXLinea1 = pageWidth - rightMargin - font.getWidth(linea1, 9);
//    float textXLinea2 = pageWidth - rightMargin - font.getWidth(linea2, 9);
//    float textY = pdfDoc.getDefaultPageSize().getTop() - 45;
//
//    pdfCanvas.beginText();
//    pdfCanvas.setFontAndSize(font, 9);
//    pdfCanvas.moveText(textXLinea1, textY);
//    pdfCanvas.showText(linea1);
//    pdfCanvas.moveText(textXLinea2 - textXLinea1, -15);
//    pdfCanvas.showText(linea2);
//    pdfCanvas.endText();
//    pdfCanvas.release();
//  }
//
//  private String obtenerFechaActualFormato() {
//    LocalDate fechaActual = LocalDate.now();
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
//        Locale.forLanguageTag("es-ES"));
//    return fechaActual.format(formatter);
//  }
//}
//package com.difase.system;
//
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.kernel.events.Event;
//import com.itextpdf.kernel.events.IEventHandler;
//import com.itextpdf.kernel.events.PdfDocumentEvent;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.layout.Canvas;
//import com.itextpdf.layout.element.Image;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Locale;
//
//public class HeaderHandler implements IEventHandler {
//
//  private static final String LOGO_PATH = "recursos/logo.png";
//  private static final String FONT_PATH = "recursos/LSANS.ttf";
//  private final String cotizacion;
//
//  public HeaderHandler(String cotizacion) {
//    this.cotizacion = cotizacion;
//  }
//
//  @Override
//  public void handleEvent(Event event) {
//    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
//    PdfDocument pdfDoc = docEvent.getDocument();
//    PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage().newContentStreamBefore(), docEvent.getPage().getResources(),
//        pdfDoc);
//
//    ImageData imageData = null;
//
//    try {
//      imageData = ImageDataFactory.create(LOGO_PATH);
//    } catch (MalformedURLException ex) {
//      ex.printStackTrace();
//    }
//
//    Image image = new Image(imageData).setHeight(54);
//    float imageX = pdfDoc.getDefaultPageSize().getLeft() + 83;
//    float imageY = pdfDoc.getDefaultPageSize().getTop() - 84;
//    image.setFixedPosition(imageX, imageY);
//    try (Canvas canvas = new Canvas(pdfCanvas, pdfDoc.getDefaultPageSize())) {
//      canvas.add(image);
//    }
//
//    PdfFont font = null;
//
//    try {
//      font = PdfFontFactory.createFont(FONT_PATH, "Identity-H");
//    } catch (IOException ex) {
//      ex.printStackTrace();
//    }
//
//    pdfCanvas.setFontAndSize(font, 9);
//
//    String fechaActual = obtenerFechaActualFormato();
//    String linea1 = "Lima  " + fechaActual;
//    String linea2 = "COTIZACIÓN: " + cotizacion;
//
//    float pageWidth = pdfDoc.getDefaultPageSize().getWidth();
//    float rightMargin = 83;
//    float textXLinea1 = pageWidth - rightMargin - font.getWidth(linea1, 9);
//    float textXLinea2 = pageWidth - rightMargin - font.getWidth(linea2, 9);
//    float textY = pdfDoc.getDefaultPageSize().getTop() - 45;
//
//    pdfCanvas.beginText();
//    pdfCanvas.setFontAndSize(font, 9);
//    pdfCanvas.moveText(textXLinea1, textY);
//    pdfCanvas.showText(linea1);
//    pdfCanvas.moveText(textXLinea2 - textXLinea1, -15);
//    pdfCanvas.showText(linea2);
//    pdfCanvas.endText();
//    pdfCanvas.release();
//  }
//
//  private String obtenerFechaActualFormato() {
//    LocalDate fechaActual = LocalDate.now();
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
//        Locale.forLanguageTag("es-ES"));
//    return fechaActual.format(formatter);
//  }
//}
