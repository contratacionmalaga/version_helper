package local.jarios.version.exception;

/**
 * Excepción personalizada que representa errores durante la consulta de versión.
 */
public class VersionException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message mensaje descriptivo del error
     */
    public VersionException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa del error.
     *
     * @param message mensaje descriptivo
     * @param cause   excepción que causó el error
     */
    public VersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
