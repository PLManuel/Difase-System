package com.difase.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javafx.stage.DirectoryChooser;

public class ConfigManager {

  private static final String CONFIG_FILE_INTERNAL = "/config.properties"; // Archivo interno
  private static final String CONFIG_FILE_EXTERNAL = "config.properties";   // Archivo externo
  private static final String PDF_SAVE_PATH_KEY = "cotizacion.ruta";
  private static final String COTIZACION_NUMERO_KEY = "cotizacion.numero";

  private static Properties properties = new Properties();

  static {
    verificarOCrearConfigExterno();
    cargarConfiguracion();
  }

  // Copia el archivo de configuración de recursos a la ruta externa si no existe
  private static void verificarOCrearConfigExterno() {
    Path externalConfigPath = Paths.get(CONFIG_FILE_EXTERNAL);
    if (!Files.exists(externalConfigPath)) {
      try (InputStream input = ConfigManager.class.getResourceAsStream(CONFIG_FILE_INTERNAL)) {
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

  // Cargar configuración del archivo externo
  private static void cargarConfiguracion() {
    try (FileInputStream input = new FileInputStream(CONFIG_FILE_EXTERNAL)) {
      properties.load(input);
    } catch (IOException e) {
      System.out.println("No se pudo cargar el archivo de configuración externo. Se usará uno nuevo.");
    }
  }

  // Obtener el número de cotización
  public static int getCotizacionNumero() {
    String numero = properties.getProperty(COTIZACION_NUMERO_KEY, "1"); // valor por defecto 1
    return Integer.parseInt(numero);
  }

  // Guardar el número de cotización
  public static void setCotizacionNumero(int numero) {
    properties.setProperty(COTIZACION_NUMERO_KEY, String.valueOf(numero));
    guardarConfiguracion();
  }

  // Obtener la ruta de guardado de PDFs, pidiendo al usuario si no está configurada
  public static String getPdfSavePath() {
    String pdfSavePath = properties.getProperty(PDF_SAVE_PATH_KEY);

    if (pdfSavePath == null || pdfSavePath.isEmpty()) {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setTitle("Seleccione la carpeta para guardar PDFs");
      File selectedDirectory = directoryChooser.showDialog(null);

      if (selectedDirectory != null) {
        pdfSavePath = selectedDirectory.getAbsolutePath();
        setPdfSavePath(pdfSavePath); // Guardar la ruta en config.properties
      } else {
        System.out.println("Operación cancelada por el usuario.");
        return null;
      }
    }

    return pdfSavePath;
  }

  // Guardar la ruta de PDFs
  public static void setPdfSavePath(String path) {
    properties.setProperty(PDF_SAVE_PATH_KEY, path);
    guardarConfiguracion();
  }

  // Método centralizado para guardar las propiedades en el archivo externo
  private static void guardarConfiguracion() {
    try (FileOutputStream output = new FileOutputStream(CONFIG_FILE_EXTERNAL)) {
      properties.store(output, "Configuración de Cotizaciones");
      System.out.println("Archivo config.properties actualizado correctamente.");
    } catch (IOException e) {
      System.out.println("No se pudo guardar el archivo de configuración.");
      e.printStackTrace();
    }
  }
}
