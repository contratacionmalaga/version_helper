package local.jarios.version;

import local.jarios.version.enums.TipoFinalEjecucion;
import local.jarios.version.exception.VersionException;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import local.jarios.version.helpers.FinalDelProgramaHelper;
import local.jarios.version.common.util.Mensajes;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Clase de demostración para consultar la versión del JAR desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar version-helper.jar}
 * </p>
 */
@Slf4j
public class VersionDemo {

   /**
     * Constructor por defecto.
     * Esta clase solo contiene el método main, no se debe instanciar.
     */
    private VersionDemo() {
        // Constructor vacío
    }

    /**
     * Método principal para ejecutar la consulta de versión.
     */
    public static void main(String[] args) {

      // Inicio del log
      log.info(Mensajes.INICIO);

      // Carga de los mensajes de error
      List<String> mensajesError = Arrays.asList(
          Mensajes.ERROR_1,
          Mensajes.ERROR_2,
          Mensajes.ERROR_3,
          Mensajes.ERROR_4
      );

      try {

        // Creación del Servicio
        Version versionService = new VersionImpl();
        log.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

        // Obtengo la version
        String version = versionService.getVersion(VersionDemo.class);

        // Discrimino si el valor obtenido pertenece a la lista de mensajes de error
        if (mensajesError.contains(version)) {
          log.warn("No se obtuvo la versión real, mensaje: {}", version);
          FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.CORRECTO);
        } else {
          log.info("Versión obtenida correctamente: {}", version);
          FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.CORRECTO);
        }

        // Final de la ejecución
        FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.CORRECTO);

      } catch (VersionException ex) {

        FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.ERROR, ex.getMessage());
      }
    }
}
