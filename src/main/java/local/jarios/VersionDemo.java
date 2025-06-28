package local.jarios;

import local.jarios.version.exception.VersionException;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
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
     * Mensaje que indica el inicio de la ejecución del programa.
     */
    public static final String INICIO = "**** Inicio del log";

    /**
     * Mensaje que indica el inicio de la ejecución del programa.
     */
    public static final String FINAL = "**** Final del log";

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

        LOGGER.info(INICIO);

        try {

            Version versionService = new VersionImpl();
            LOGGER.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

            String version = versionService.getVersion(VersionDemo.class);
            LOGGER.info("Versión: {}", version);

        } catch (VersionException e) {

            LOGGER.error("Error al obtener la versión del fichero.");
            throw e; // <- Repropagar al consumidor del módulo

        } finally {

            LOGGER.info(FINAL);
        }
    }
}
