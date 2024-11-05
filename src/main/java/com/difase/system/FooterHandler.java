package com.difase.system;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import java.io.IOException;

public class FooterHandler implements IEventHandler {

  @Override
  public void handleEvent(Event event) {
    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
    PdfDocument pdfDoc = docEvent.getDocument();
    PdfCanvas canvas = new PdfCanvas(docEvent.getPage().newContentStreamBefore(), docEvent.getPage().getResources(),
        pdfDoc);

    PdfFont font;
    PdfFont fontBold;
    try {
      font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
      fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
      canvas.setFontAndSize(font, 10);
    } catch (IOException ex) {
      ex.printStackTrace();
      return;
    }

    float yPosition = 45;
    canvas.setLineWidth(2);
    canvas.setStrokeColorRgb(68 / 255f, 114 / 255f, 196 / 255f);
    canvas.moveTo(30, yPosition);
    canvas.lineTo(pdfDoc.getDefaultPageSize().getWidth() - 30, yPosition);
    canvas.stroke();

    String direccion = "Av. Pobladores California Mz. H Lote 7 - Villa el Salvador";
    String telefono = "Teléf. : 292 9917 / 935 178 423";
    String website = "WWW.DIFASE.COM";

    float direccionWidth = font.getWidth(direccion, 10);

    canvas.beginText();
    canvas.setFontAndSize(font, 10);
    canvas.moveText(85, yPosition - 15);
    canvas.showText(direccion);
    canvas.endText();

    float telefonoYLinkYPosition = yPosition - 30;
    float telefonoX = 85;
    float websiteX = 85 + direccionWidth - fontBold.getWidth(website, 10);

    canvas.beginText();
    canvas.setFontAndSize(font, 10);
    canvas.moveText(telefonoX, telefonoYLinkYPosition);
    canvas.showText(telefono);
    canvas.endText();

    canvas.beginText();
    canvas.setFontAndSize(fontBold, 10);
    canvas.setFillColorRgb(0 / 255f, 102 / 255f, 204 / 255f);
    canvas.moveText(websiteX, telefonoYLinkYPosition);
    canvas.showText(website);
    canvas.endText();

    canvas.setFillColorRgb(0, 0, 0);

    PdfLinkAnnotation link = new PdfLinkAnnotation(new com.itextpdf.kernel.geom.Rectangle(
        websiteX, telefonoYLinkYPosition - 5, fontBold.getWidth(website, 10), 12))
        .setAction(com.itextpdf.kernel.pdf.action.PdfAction.createURI("http://www.difase.com"));
    docEvent.getPage().addAnnotation(link);

    canvas.setLineWidth(0.5f);
    canvas.setStrokeColorRgb(0 / 255f, 102 / 255f, 204 / 255f);
    canvas.moveTo(websiteX, telefonoYLinkYPosition - 2);
    canvas.lineTo(websiteX + fontBold.getWidth(website, 10), telefonoYLinkYPosition - 2);
    canvas.stroke();

    canvas.release();
  }
}