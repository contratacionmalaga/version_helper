package local.jarios.version;

import java.util.Arrays;
import java.util.List;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import local.jarios.version.common.util.Mensajes;
import local.jarios.version.enums.TipoFinalEjecucion;
import local.jarios.version.exception.VersionException;
import local.jarios.version.helpers.FinalDelProgramaHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase de demostración para consultar la versión del JAR desde línea de comandos.
 *
 * <p>Uso: {@code java -jar version-helper.jar}
 */
@Slf4j
public class VersionDemo {

  /** Constructor por defecto. Esta clase solo contiene el método main, no se debe instanciar. */
  private VersionDemo() {
    // Constructor vacío
  }

  /** Método principal para ejecutar la consulta de versión. */
  public static void main(String[] args) {

    log.info(Mensajes.INICIO);

    List<String> mensajesError =
        Arrays.asList(Mensajes.ERROR_1, Mensajes.ERROR_2, Mensajes.ERROR_3, Mensajes.ERROR_4);

    try {
      Version versionService = new VersionImpl();
      log.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

      String version = versionService.getVersion(VersionDemo.class);

      if (mensajesError.contains(version)) {
        log.warn("No se obtuvo la versión real, mensaje: {}", version);
      } else {
        log.info("Versión obtenida correctamente: {}", version);
      }

      finalizarProceso(TipoFinalEjecucion.CORRECTO);
    } catch (VersionException ex) {
      finalizarProceso(TipoFinalEjecucion.ERROR, ex.getMessage());
    }
  }

  private static void finalizarProceso(TipoFinalEjecucion tipoFinal) {
    finalizarProceso(tipoFinal, null);
  }

  private static void finalizarProceso(TipoFinalEjecucion tipoFinal, String mensajeError) {
    FinalDelProgramaHelper.finalizar(tipoFinal, mensajeError);
    System.exit(FinalDelProgramaHelper.resolverCodigoSalida(tipoFinal));
  }
}
