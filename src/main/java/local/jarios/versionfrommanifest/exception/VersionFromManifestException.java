package local.jarios.versionfrommanifest.exception;

/**
 * Excepción personalizada que representa errores durante el cifrado o descifrado.
 */
public class VersionFromManifestException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message mensaje descriptivo del error
     */
    public VersionFromManifestException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa del error.
     *
     * @param message mensaje descriptivo
     * @param cause   excepción que causó el error
     */
    public VersionFromManifestException(String message, Throwable cause) {
        super(message, cause);
    }
}
