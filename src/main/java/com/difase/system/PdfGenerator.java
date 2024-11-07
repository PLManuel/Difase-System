package com.difase.system;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class PdfGenerator {

  private static final String FONT_PATH = "/fuentes/LSANS.woff";

  public void generateInvoice(String fullPath, String sres, String atencion, String contacto, String referencia,
      String totalGeneral, String codigoCotizacion, List<Map<String, Object>> detallesFilas,
      List<Map<String, String>> imagesData) {
    try {
      PdfWriter writer = new PdfWriter(fullPath);
      PdfDocument pdf = new PdfDocument(writer);

      PdfFont customFont = loadFont();

      HeaderHandler headerHandler = new HeaderHandler(codigoCotizacion, customFont);
      pdf.addEventHandler(PdfDocumentEvent.END_PAGE, headerHandler);

      FooterHandler footerHandler = new FooterHandler();
      pdf.addEventHandler(PdfDocumentEvent.END_PAGE, footerHandler);

      try (Document document = new Document(pdf, PageSize.A4, false)) {
        document.setMargins(85, 84, 47, 84);
        document.setFont(customFont);

        BodyHandler.addBody(document, sres, atencion, contacto, referencia, detallesFilas, totalGeneral);

        if (imagesData != null && !imagesData.isEmpty()) {
          document.add(new AreaBreak());

          Paragraph referenciasTitle = new Paragraph("REFERENCIAS")
              .setTextAlignment(TextAlignment.CENTER)
              .setFontSize(14)
              .setMarginBottom(10);
          document.add(referenciasTitle);

          for (Map<String, String> imageData : imagesData) {
            String imagePath = imageData.get("path");
            String imageTitle = imageData.get("title");

            document.add(new Paragraph(imageTitle).setTextAlignment(TextAlignment.CENTER).setFontSize(12));

            String decodedPath = URLDecoder.decode(imagePath, StandardCharsets.UTF_8.toString());
            ImageData imgData = ImageDataFactory.create(decodedPath);
            Image img = new Image(imgData);

            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            img.setMaxHeight(UnitValue.createPointValue(400));
            img.setWidth(UnitValue.createPercentValue(80));

            document.add(img);
          }
        }
      }

      System.out.println("PDF generado con éxito en " + fullPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static PdfFont loadFont() throws IOException {
    try (InputStream fontStream = BodyHandler.class.getResourceAsStream(FONT_PATH)) {
      if (fontStream == null) {
        throw new IOException("No se pudo encontrar el archivo de fuente en la ruta: " + FONT_PATH);
      }
      byte[] fontBytes = convertInputStreamToByteArray(fontStream);
      FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);
      return PdfFontFactory.createFont(fontProgram);
    }
  }

  private static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;

    while ((bytesRead = inputStream.read(buffer)) != -1) {
      byteArrayOutputStream.write(buffer, 0, bytesRead);
    }

    return byteArrayOutputStream.toByteArray();
  }
}
