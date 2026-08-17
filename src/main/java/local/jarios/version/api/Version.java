package local.jarios.version.api;

import local.jarios.version.common.util.Mensajes;

/**
 * Interfaz para la definición de los métodos para la obtención de la versión de un fichero jar.
 *
 * <p>Uso típico:
 *
 * <pre>{@code
 * Version versionService = new VersionImpl();
 * VersionResult result = versionService.getVersionResult(NombreClase.class);
 * }</pre>
 *
 * @author Juan
 * @since 15/06/2025
 * @version 1.1.0
 */
public interface Version {

  /**
   * Obtiene la versión (App-Version) desde el MANIFEST.MF del JAR que contiene la clase
   * especificada.
   *
   * <p>Este método se mantiene por compatibilidad con la API histórica. Para código nuevo, usar
   * {@link #getVersionResult(Class)}.
   *
   * @param clazz Clase de referencia para localizar el JAR
   * @return Versión obtenida del MANIFEST.MF o mensaje descriptivo si no se encuentra
   */
  String getVersion(Class<?> clazz);

  /**
   * Obtiene un resultado tipado con el estado de la consulta de versión.
   *
   * <p>La implementación por defecto preserva compatibilidad con implementaciones externas
   * existentes de esta interfaz. Las implementaciones específicas pueden sobrescribirlo para
   * devolver estados más precisos.
   *
   * @param clazz Clase de referencia para localizar el JAR
   * @return resultado tipado de la consulta
   */
  default VersionResult getVersionResult(Class<?> clazz) {
    String legacyValue = getVersion(clazz);

    if (Mensajes.ERROR_1.equals(legacyValue)) {
      return VersionResult.notFound(VersionStatus.CLASS_RESOURCE_NOT_FOUND, legacyValue);
    }

    if (Mensajes.ERROR_2.equals(legacyValue)) {
      return VersionResult.notFound(VersionStatus.DEVELOPMENT_MODE, legacyValue);
    }

    if (Mensajes.ERROR_3.equals(legacyValue)) {
      return VersionResult.notFound(VersionStatus.MANIFEST_NOT_FOUND, legacyValue);
    }

    if (Mensajes.ERROR_4.equals(legacyValue)) {
      return VersionResult.notFound(VersionStatus.APP_VERSION_NOT_FOUND, legacyValue);
    }

    return VersionResult.found(legacyValue);
  }
}
