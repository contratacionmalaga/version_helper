package local.jarios;

import local.jarios.version.exception.VersionException;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal para ejecutar el cifrado y descifrado desde línea de comandos.
 * <p>
 * Uso:
 * {@code java -jar encriptador.jar <claveMaestra> <texto>}
 * </p>
 */
@Slf4j
public class VersionDemo {

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

        log.info(INICIO);

        try {

            Version versionService = new VersionImpl();
            log.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

            String version = versionService.getVersion(VersionDemo.class);
            log.info("Versión: {}", version);

        } catch (VersionException e) {

            log.error("Error al obtener la versión del fichero.");
            throw e; // <- Repropagar al consumidor del módulo

        } finally {

            log.info(FINAL);
        }
    }
}
