package local.jarios.version.api;

import local.jarios.version.exception.VersionException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Implementación del servicio de obtención de la versión de un fichero jar
 *
 * @author Juan
 * @since 15/06/2025
 * @version 1.0.0
 */
@Slf4j
public class VersionImpl implements Version {

    /**
     Nombre de la clase
     */
    private static final String  CLASS_EXTENSION = ".class";

    /**
     Nombre de la clase
     */
    private static final String  CLASS_NAME = "Nombre de la clase: {}";

    /**
     URL de la clase
     */
    private static final String  CLASS_URL = "URL de la clase: {}";

    /**
     No se encontró recurso de clase
     */
    private static final String  SIN_RECURSO_CLASE = "No se encontró recurso de clase";

    /**
     Ejecutando sin JAR (modo desarrollo)
     */
    private static final String  EJECUCION_SIN_JAR = "Ejecutando sin JAR (modo desarrollo)";

    /**
     No se encontró MANIFEST.MF en el JAR
     */
    private static final String  SIN_MANIFEST = "No se encontró MANIFEST.MF en el JAR";

    /**
     Versión no especificada en MANIFEST.MF
     */
    private static final String  SIN_VERSION = "Versión no especificada en MANIFEST.MF. Revisar fichero pom.xml";


    /**
     Versión no especificada en MANIFEST.MF
     */
    private static final String  EXCEPCION = "Error leyendo el fichero MANIFEST.MF dentro del JAR. %s";

    /**
     Versión no especificada en MANIFEST.MF
     */
    private static final String  MANIFEST_MAIN_ATTRIBUTE_VERSION = "Implementation-Version";

    /**
     * Constructor vacío.
     */
    public VersionImpl() {
        // Constructor vacío
    }

    /**
     * Obtiene la versión (Implementation-Version) desde el MANIFEST.MF
     * del JAR que contiene la clase especificada.
     *
     * @param clazz Clase de referencia para localizar el JAR
     * @return Versión obtenida del MANIFEST.MF o "Desconocida" si no se encuentra
     */
    public String getVersion(
            Class<?> clazz
    ) {


            String className = clazz.getSimpleName() + CLASS_EXTENSION;
            log.debug(CLASS_NAME, className);

            URL classUrl = getResourceURL(clazz, className);
            log.debug(CLASS_URL, classUrl);

            if (classUrl == null) {
                return SIN_RECURSO_CLASE;
            }

            String protocol = classUrl.getProtocol();

            if (!"jar".equals(protocol)) {
                // Probablemente en entorno desarrollo (no en JAR)
                return EJECUCION_SIN_JAR;
            }

        try {

            JarURLConnection jarConnection = (JarURLConnection) classUrl.openConnection();
            log.debug("Conexión con la URL del fichero JAR realizada correctamente.");

            JarFile jarFile = jarConnection.getJarFile();
            log.debug("Fichero JAR obtenido corerctamente.");

            Manifest manifest = jarFile.getManifest();
            log.debug("Obtención del Manifest asociado al fichero JAR.");

            if (manifest == null) {
                return SIN_MANIFEST;
            }

            Attributes mainAttributes = manifest.getMainAttributes();
            log.debug("Obtención de los atributos asociados al Manifest.");

            String version = mainAttributes.getValue(MANIFEST_MAIN_ATTRIBUTE_VERSION);
            log.debug("{}: {}", MANIFEST_MAIN_ATTRIBUTE_VERSION, version);

            if (version == null || version.isEmpty()) {
                return SIN_VERSION;
            }

            return version;

        } catch (IOException e) {
            String mensaje = String.format(EXCEPCION, e.getMessage());
            log.error(mensaje);
            throw new VersionException(mensaje, e);
        }
    }

    /**
     * Método protegido para facilitar testing
     * @param clazz Clase
     * @param className Nombre de la case
     * @return URL
     */
    protected URL getResourceURL(Class<?> clazz, String className) {
        return clazz.getResource(className);
    }
}
