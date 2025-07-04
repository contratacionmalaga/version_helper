package local.jarios.version;

import local.jarios.version.enums.TipoFinalEjecucion;
import local.jarios.version.exception.VersionException;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import local.jarios.version.helpers.FinalDelProgramaHelper;
import local.jarios.version.common.util.Mensajes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase principal para ejecutar el cifrado y descifrado desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar encriptador.jar <claveMaestra> <texto>}
 * </p>
 */
public class VersionDemo {

    /**
     * Instancia única (singleton) del gestor de propiedades.
     * Inicialización temprana y thread-safe mediante static final.
     */
    private static final Logger LOGGER = LogManager.getLogger("local.jarios.version");

   /**
     * Constructor por defecto.
     * Esta clase solo contiene el método main, no se debe instanciar.
     */
    private VersionDemo() {
        // Constructor vacío
    }

    /**
     * Método principal para ejecutar el cifrado y descifrado.
     */
    public static void main() {

        // Inicio del log
        LOGGER.info(Mensajes.INICIO);

        try {

            Version versionService = new VersionImpl();
            LOGGER.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

            String version = versionService.getVersion(VersionDemo.class);
            LOGGER.info("Versión: {}", version);

            FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.CORRECTO);

        } catch (VersionException ex) {

            FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.ERROR);

        }
    }
}
