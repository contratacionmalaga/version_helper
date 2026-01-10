package local.jarios.version.common.util;

/**
 * Clase final que contiene constantes de mensajes estáticos
 * usados en la aplicación para logging y trazabilidad.
 * <p>
 * Facilita la gestión centralizada de textos comunes para logs,
 * evitando duplicación y facilitando modificaciones.
 * </p>
 *
 * <p><b>Author:</b> Juan Antonio</p>
 * <p><b>Date:</b> 04/06/2024</p>
 * <p><b>Team:</b> Juan Antonio</p>
 */
public final class Mensajes {

    /** Mensaje Error-1. */
    public static final String ERROR_1 = "No se encontró recurso de clase";

    /** Mensaje Error-2. */
    public static final String ERROR_2 = "Ejecutando sin JAR (modo desarrollo)";

    /** Mensaje Error-3. */
    public static final String ERROR_3 = "No se encontró MANIFEST.MF en el JAR";

    /** Mensaje Error-4. */
    public static final String ERROR_4 = "Versión no especificada en MANIFEST.MF. Revisar pom.xml";

    /** Mensaje que indica el inicio de la ejecución del programa. */
    public static final String INICIO = "==== INICIO DE LA APLICACIÓN: version-helper ====";

    /** Mensaje que indica el final de la ejecución del programa. */
    public static final String FINAL = "==== FINAL DE LA APLICACIÓN: version-helper ====  ";

    /** Mensaje que indica que la ejecución ha finalizado correctamente. */
    public static final String FINAL_CORRECTO = "La ejecución ha finalizado CORRECTAMENTE.";

    /** Mensaje que indica que la ejecución ha finalizado con errores. */
    public static final String FINAL_ERROR = "!!!! La ejecución ha finalizado con ERRORES !!!!";

    /** Constructor privado para evitar la instanciación de esta clase de utilidades. */
    private Mensajes() {
        // Constructor privado
    }
}
