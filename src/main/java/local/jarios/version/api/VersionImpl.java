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
 * Servicio para obtener la versión de un JAR a partir de su MANIFEST.MF
 * usando como referencia una clase contenida en el JAR.
 *
 * @author Juan
 * @since 15/06/2025
 * @version 1.1.0
 */
@Slf4j
public class VersionImpl implements Version {

    private static final String CLASS_EXTENSION = ".class";
    private static final String MANIFEST_ATTRIBUTE_VERSION = "Implementation-Version";

    /**
     * Constructor vacío.
     */
    public VersionImpl() {
        // Constructor vacío
    }

    /**
     * Obtiene la versión del JAR que contiene la clase indicada.
     *
     * @param clazz Clase de referencia
     * @return Versión obtenida del MANIFEST.MF, o mensaje de contexto si no se encuentra
     * @throws VersionException en caso de errores de lectura
     */
    @Override
    public String getVersion(Class<?> clazz) throws VersionException {

        if (clazz == null) {
            throw new VersionException("La clase no puede ser nula");
        }

        String className = clazz.getSimpleName() + CLASS_EXTENSION;
        log.debug("Obteniendo versión para la clase: {}", className);

        URL classUrl = getResourceURL(clazz, className);
        if (classUrl == null) {
            log.debug("No se encontró recurso de clase para {}", className);
            return "No se encontró recurso de clase";
        }

        String protocol = classUrl.getProtocol();
        if (!"jar".equals(protocol)) {
            log.debug("Ejecutando fuera de un JAR (modo desarrollo). URL: {}", classUrl);
            return "Ejecutando sin JAR (modo desarrollo)";
        }

        try {
            JarURLConnection jarConnection = (JarURLConnection) classUrl.openConnection();
            JarFile jarFile = jarConnection.getJarFile();
            log.debug("Conexión con JAR establecida correctamente: {}", jarFile.getName());

            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                log.debug("No se encontró MANIFEST.MF en el JAR: {}", jarFile.getName());
                return "No se encontró MANIFEST.MF en el JAR";
            }

            Attributes mainAttributes = manifest.getMainAttributes();
            String version = mainAttributes.getValue(MANIFEST_ATTRIBUTE_VERSION);

            if (version == null || version.isEmpty()) {
                log.debug("La versión no está especificada en MANIFEST.MF para el JAR: {}", jarFile.getName());
                return "Versión no especificada en MANIFEST.MF. Revisar fichero pom.xml";
            }

            log.info("Versión obtenida del JAR {}: {}", jarFile.getName(), version);
            return version;

        } catch (IOException ex) {
            String mensaje = "Error leyendo el MANIFEST.MF dentro del JAR: " + ex.getMessage();
            log.error(mensaje, ex);
            throw new VersionException(mensaje, ex);
        }
    }

    /**
     * Obtiene la URL del recurso de clase.
     *
     * @param clazz Clase de referencia
     * @param className Nombre del archivo de clase
     * @return URL del recurso
     * @throws VersionException si ocurre algún error
     */
    protected URL getResourceURL(Class<?> clazz, String className) {
        try {
            URL url = clazz.getResource(className);
            log.debug("URL de la clase {}: {}", className, url);
            return url;
        } catch (NullPointerException ex) {
            String mensaje = "Error obteniendo URL del recurso de clase: " + ex.getMessage();
            log.error(mensaje, ex);
            throw new VersionException(mensaje, ex);
        }
    }
}
