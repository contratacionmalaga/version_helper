package local.jarios.version.api;

/**
 * Interfaz para la definición de los métodos para la obtención de la versión de un fichero jar
 *
 * <p>Uso típico:</p>
 * <pre>{@code
 *   Version versionService = new VersionImpl();
 *   String version = versionService.getVersion(NombreClase.class);
 * }</pre>
 *
 * @author Juan
 * @since 15/06/2025
 * @version 1.0.0
 */
public interface Version {

    /**
     * Obtiene la versión (App-Version) desde el MANIFEST.MF
     * del JAR que contiene la clase especificada.
     *
     * @param clazz Clase de referencia para localizar el JAR
     * @return Versión obtenida del MANIFEST.MF o "Desconocida" si no se encuentra
     */
    String getVersion(Class<?> clazz);
}

