package com.difase.system;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class BodyHandler {

  private static final String FONT_PATH = "/fuentes/LSANS.woff";
  private static final int FONT_SIZE = 10;
  private static final DeviceRgb TEXT_COLOR = new DeviceRgb(7, 55, 99);

  public static void addBody(Document document, String sres, String atencion, String contacto, String referencia,
      List<Map<String, Object>> detallesFilas, String totalGeneral) throws IOException {
    PdfFont customFont = loadFont();
    Table table = new Table(UnitValue.createPercentArray(new float[]{17, 2, 100}))
        .setWidth(UnitValue.createPercentValue(100))
        .setMarginTop(-5)
        .setPadding(0);

    addRow(table, "SRES.", sres, customFont);
    addRow(table, "ATENCIÓN", atencion, customFont);
    addRow(table, "CONTACTO", contacto, customFont);
    addRow(table, "REFERENCIA", referencia, customFont);

    document.add(table);
    addHorizontalLine(document);

    document.add(new Paragraph("DE NUESTRA MAYOR CONSIDERACIÓN.").setMultipliedLeading(1f)
        .setFont(customFont)
        .setFontSize(FONT_SIZE)
        .setMarginBottom(-3));
    document.add(new Paragraph(
        "Con respecto a su requerimiento, tenemos el agrado de dirigirnos a Uds., para presentarles nuestra mejor Oferta Técnico-Comercial como sigue:")
        .setFont(customFont)
        .setMarginBottom(0)
        .setFontSize(FONT_SIZE));

    Paragraph subtitle1 = new Paragraph("I.- PROPUESTA ECONOMICA")
        .setFont(customFont)
        .setFontSize(10)
        .setMarginBottom(-5)
        .setMultipliedLeading(1f)
        .setFontColor(new DeviceRgb(7, 55, 99))
        .setTextAlignment(TextAlignment.LEFT)
        .setUnderline();
    document.add(subtitle1);

    addDetailsTable(document, detallesFilas, totalGeneral, customFont);

    Paragraph subtitle2 = new Paragraph("II.- CONDICIONES COMERCIALES")
        .setFont(customFont)
        .setFontSize(10)
        .setFontColor(new DeviceRgb(7, 55, 99))
        .setTextAlignment(TextAlignment.LEFT)
        .setUnderline();
    document.add(subtitle2);

    addConditionsTable(document, customFont);

    addFooter(document, customFont);

  }

private static PdfFont loadFont() throws IOException {
  // Cargar la fuente desde el archivo dentro de resources usando InputStream
  try (InputStream fontStream = BodyHandler.class.getResourceAsStream(FONT_PATH)) {
    if (fontStream == null) {
      throw new IOException("No se pudo encontrar el archivo de fuente en la ruta: " + FONT_PATH);
    }
    
    // Convertir el InputStream a un byte[]
    byte[] fontBytes = convertInputStreamToByteArray(fontStream);
    
    // Crear el FontProgram a partir del byte[]
    FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);
    
    // Crear el PdfFont usando el FontProgram
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

  private static void addRow(Table table, String label, String value, PdfFont customFont) {
    // Define the desired height for the cells
    float cellHeight = 15f; // Adjust this value to set the desired height

    // Columna del título en negrita
    Cell labelCell = new Cell()
        .add(new Paragraph(label).setFont(customFont).setFontSize(FONT_SIZE))
        .setPadding(0)
        .setBorder(null)
        .setHeight(cellHeight); // Set cell height
    table.addCell(labelCell).setMargin(0).setPadding(0);

    // Columna para el ":"
    Cell colonCell = new Cell()
        .add(new Paragraph(":").setFont(customFont).setFontSize(FONT_SIZE))
        .setPadding(0)
        .setBorder(null)
        .setHeight(cellHeight); // Set cell height
    table.addCell(colonCell).setMargin(0).setPadding(0);

    // Columna para el valor
    Cell valueCell = new Cell()
        .add(new Paragraph(value).setFont(customFont).setFontSize(FONT_SIZE))
        .setPadding(0)
        .setBorder(null)
        .setHeight(cellHeight); // Set cell height
    table.addCell(valueCell).setMargin(0).setPadding(0);
  }

  private static void addHorizontalLine(Document document) {
    SolidLine solidLine = new SolidLine(0.4f);
    solidLine.setColor(TEXT_COLOR);

    LineSeparator line = new LineSeparator(solidLine);
    line.setWidth(UnitValue.createPercentValue(98));

    Paragraph paragraph = new Paragraph().add(line).setMargin(0).setPadding(0);
    paragraph.setTextAlignment(TextAlignment.CENTER).setMargin(0).setPadding(0);

    document.add(paragraph);
  }

  private static void addDetailsTable(Document document, List<Map<String, Object>> detallesFilas,
      String totalGeneral,
      PdfFont customFont) {
    Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{11, 2, 2, 2}))
        .setWidth(UnitValue.createPercentValue(100))
        .setMarginTop(10);

    detailsTable.addHeaderCell(new Cell().add(new Paragraph("DESCRIPCIÓN").setFont(customFont)
        .setFontSize(FONT_SIZE).setTextAlignment(TextAlignment.CENTER)));
    detailsTable.addHeaderCell(
        new Cell().add(new Paragraph("CANT.").setFont(customFont).setFontSize(FONT_SIZE)
            .setTextAlignment(TextAlignment.CENTER)));
    detailsTable.addHeaderCell(
        new Cell().add(new Paragraph("P. UNIT.").setFont(customFont).setFontSize(FONT_SIZE)
            .setTextAlignment(TextAlignment.CENTER)));
    detailsTable.addHeaderCell(
        new Cell().add(new Paragraph("TOTAL").setFont(customFont).setFontSize(FONT_SIZE)
            .setTextAlignment(TextAlignment.CENTER)));

    for (Map<String, Object> fila : detallesFilas) {
      detailsTable.addCell(
          new Cell().add(new Paragraph((String) fila.get("descripcion")).setPaddingLeft(5)
              .setFont(customFont).setFontSize(8)));
      detailsTable.addCell(new Cell().add(new Paragraph((String) fila.get("cantidad"))
          .setTextAlignment(TextAlignment.CENTER).setFont(customFont).setFontSize(8)));
      detailsTable.addCell(new Cell().add(new Paragraph((String) fila.get("precioUnitario"))
          .setTextAlignment(TextAlignment.CENTER).setFont(customFont).setFontSize(8)));
      detailsTable.addCell(new Cell().add(new Paragraph((String) fila.get("total"))
          .setTextAlignment(TextAlignment.CENTER).setFont(customFont).setFontSize(8)));
    }

    detailsTable.addCell(new Cell().add(new Paragraph("")).setBorder(null));
    detailsTable.addCell(new Cell().add(new Paragraph("")).setBorder(null));
    detailsTable.addCell(new Cell().add(new Paragraph("TOTAL:").setFont(customFont).setFontSize(FONT_SIZE)
        .setTextAlignment(TextAlignment.RIGHT)).setBorder(null));
    detailsTable.addCell(
        new Cell().add(new Paragraph(totalGeneral).setFont(customFont).setFontSize(FONT_SIZE)
            .setTextAlignment(TextAlignment.CENTER)).setBorder(null));

    document.add(detailsTable);
  }

  private static void addConditionsTable(Document document, PdfFont customFont) {
    Table conditionsTable = new Table(UnitValue.createPercentArray(new float[]{5, 5}))
        .setWidth(UnitValue.createPercentValue(100)).setMarginLeft(5).setPadding(0)
        .setBorder(null);

    addConditionRow(conditionsTable, "PRECIO", "En SOLES NO incluye IGV.", customFont);
    addConditionRow(conditionsTable, "PLAZO DE ENTREGA", "5 DÍAS.", customFont);
    addConditionRow(conditionsTable, "FORMA DE PAGO", "50% Adelanto y la diferencia contra entrega.",
        customFont);
    addConditionRow(conditionsTable, "VALIDEZ DE OFERTA", "15 Días.", customFont);
    addConditionRow(conditionsTable, "GARANTÍA", "6 meses, bajo recomendaciones de servicio.", customFont);

    document.add(conditionsTable);
  }

  private static void addConditionRow(Table table, String label, String value, PdfFont customFont) {
    Cell labelCell = new Cell()
        .add(new Paragraph(label).setFont(customFont).setFontSize(9).setMargin(0)
            .setPaddingBottom(5))
        .setBorder(null).setMargin(0).setPadding(0);
    table.addCell(labelCell);

    Cell valueCell = new Cell()
        .add(new Paragraph(":  " + value).setFont(customFont).setFontSize(9).setMargin(0)
            .setPaddingBottom(5))
        .setBorder(null).setMargin(0).setPadding(0);
    table.addCell(valueCell);
  }

  private static void addFooter(Document document, PdfFont customFont) {
    // Crear tabla contenedora con ancho fijo
    Table footerTable = new Table(1).setMarginTop(15).setPadding(0).setFontColor(TEXT_COLOR)
        .setWidth(UnitValue.createPercentValue(28)) // Establece el ancho al 70% del documento
        .setHorizontalAlignment(HorizontalAlignment.LEFT); // Centrar el contenedor en el
    // documento

    // ATTE.
    Paragraph atteParagraph = new Paragraph("ATTE.").setFontColor(new DeviceRgb(0, 0, 0))
        .setFont(customFont)
        .setFontSize(9)
        .setTextAlignment(TextAlignment.LEFT).setPadding(0)
        .setMarginBottom(5);
    footerTable.addCell(new Cell().add(atteParagraph).setMargin(0).setPadding(0)
        .setBorder(null));

    // Nombre del asesor
    Paragraph nameParagraph = new Paragraph("Milder Lume Sacsa")
        .setFont(customFont).setBold()
        .setFontSize(9)
        .setTextAlignment(TextAlignment.CENTER).setMargin(0).setPadding(0);
    footerTable.addCell(new Cell().add(nameParagraph).setMargin(0).setPadding(0)
        .setBorder(null));

    // Cargo
    Paragraph positionParagraph = new Paragraph("ASESOR COMERCIAL")
        .setFont(customFont)
        .setFontSize(9)
        .setTextAlignment(TextAlignment.CENTER).setMargin(0).setPadding(0);
    footerTable.addCell(new Cell().add(positionParagraph).setMargin(0).setPadding(0)
        .setBorder(null));

    // Nombre de la empresa
    Paragraph companyParagraph = new Paragraph("DIFASE MACHINERY SAC")
        .setFont(customFont).setBold()
        .setFontSize(9)
        .setTextAlignment(TextAlignment.CENTER).setMargin(0).setPadding(0).setUnderline();
    footerTable.addCell(new Cell().add(companyParagraph).setMargin(0).setPadding(0)
        .setBorder(null));

    // Teléfono
    Table phoneTable = new Table(new float[]{1, 1})
        .setWidth(UnitValue.createPercentValue(100))
        .setBorder(null);
    phoneTable.addCell(
        new Cell().add(new Paragraph("Mobile:").setFont(customFont).setFontSize(9)).setMargin(0)
            .setPadding(0)
            .setBorder(null));
    phoneTable.addCell(new Cell()
        .add(new Paragraph("935-178-423").setFont(customFont).setFontSize(9)
            .setTextAlignment(TextAlignment.RIGHT))
        .setMargin(0).setPadding(0)
        .setBorder(null));
    footerTable.addCell(new Cell().add(phoneTable).setMargin(0).setPadding(0)
        .setBorder(null));

    // Web con Link
    Table webTable = new Table(new float[]{1, 1})
        .setWidth(UnitValue.createPercentValue(100)).setMargin(0).setPadding(0)
        .setBorder(null);
    webTable.addCell(
        new Cell().add(new Paragraph("Web:").setFont(customFont).setFontSize(9)).setMargin(0)
            .setPadding(0)
            .setBorder(null));
    Link webLink = (Link) new Link("www.dm-sac.com", PdfAction.createURI("http://www.dm-sac.com"))
        .setFontColor(new DeviceRgb(0, 102, 204)).setUnderline();
    webTable.addCell(new Cell()
        .add(new Paragraph().add(webLink).setFont(customFont).setFontSize(9)
            .setTextAlignment(TextAlignment.RIGHT))
        .setMargin(0).setPadding(0)
        .setBorder(null));
    footerTable.addCell(new Cell().add(webTable).setMargin(0).setPadding(0)
        .setBorder(null));

    Table emailTable = new Table(new float[]{1, 3})
        .setWidth(UnitValue.createPercentValue(100)).setMargin(0).setPadding(0)
        .setBorder(null);
    emailTable.addCell(
        new Cell().add(new Paragraph("E-mail:").setFont(customFont).setFontSize(9)).setMargin(0)
            .setPadding(0).setMargin(0).setPadding(0)
            .setBorder(null));
    Link emailLink = (Link) new Link("mlume@dm-sac.com", PdfAction.createURI("mailto:mlume@dm-sac.com"))
        .setFontColor(new DeviceRgb(0, 102, 204)).setUnderline();
    emailTable.addCell(new Cell()
        .add(new Paragraph().add(emailLink).setFont(customFont).setFontSize(9)
            .setTextAlignment(TextAlignment.RIGHT))
        .setMargin(0).setPadding(0)
        .setBorder(null));
    footerTable.addCell(new Cell().add(emailTable).setMargin(0).setPadding(0)
        .setBorder(null));

    document.add(footerTable);
  }
}
