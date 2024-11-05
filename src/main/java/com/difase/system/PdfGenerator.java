package com.difase.system;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class PdfGenerator {

  public void generateInvoice(String nombreArchivoPdf, String sres, String atencion, String contacto, String referencia,
      String totalGeneral, String codigoCotizacion, List<Map<String, Object>> detallesFilas,
      List<Map<String, String>> imagesData) {
    try {
      PdfWriter writer = new PdfWriter(nombreArchivoPdf);
      PdfDocument pdf = new PdfDocument(writer);

      HeaderHandler headerHandler = new HeaderHandler(codigoCotizacion);
      pdf.addEventHandler(PdfDocumentEvent.END_PAGE, headerHandler);

      FooterHandler footerHandler = new FooterHandler();
      pdf.addEventHandler(PdfDocumentEvent.END_PAGE, footerHandler);

      try (Document document = new Document(pdf, PageSize.A4, false)) {
        document.setMargins(85, 84, 47, 84);
        String fontPath = "src/main/java/recursos/LSANS.woff";
        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(fontPath);
        document.setFontProvider(fontProvider);
        document.setFont(PdfFontFactory.createFont(fontPath));

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

      System.out.println("PDF generado con éxito en " + nombreArchivoPdf);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}