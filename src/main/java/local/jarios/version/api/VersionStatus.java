package local.jarios.version.api;

/**
 * Estados posibles al consultar la versión de un artefacto.
 */
public enum VersionStatus {

    /** La versión se ha encontrado correctamente en el manifiesto. */
    VERSION_FOUND,

    /** No se ha podido localizar el recurso de clase usado como referencia. */
    CLASS_RESOURCE_NOT_FOUND,

    /** La clase se está ejecutando fuera de un JAR empaquetado. */
    DEVELOPMENT_MODE,

    /** El JAR no contiene MANIFEST.MF. */
    MANIFEST_NOT_FOUND,

    /** El manifiesto no contiene el atributo App-Version. */
    APP_VERSION_NOT_FOUND
}
