package local.jarios.version.api;

/**
 * Resultado tipado de una consulta de versión.
 *
 * @param status estado de la consulta
 * @param version versión encontrada, solo informada cuando el estado es {@link
 *     VersionStatus#VERSION_FOUND}
 * @param message mensaje descriptivo para estados sin versión
 */
public record VersionResult(VersionStatus status, String version, String message) {

  /**
   * Crea un resultado correcto con versión encontrada.
   *
   * @param version versión encontrada
   * @return resultado correcto
   */
  public static VersionResult found(String version) {
    return new VersionResult(VersionStatus.VERSION_FOUND, version, null);
  }

  /**
   * Crea un resultado sin versión.
   *
   * @param status estado de ausencia/error esperado
   * @param message mensaje descriptivo
   * @return resultado sin versión
   */
  public static VersionResult notFound(VersionStatus status, String message) {
    if (status == VersionStatus.VERSION_FOUND) {
      throw new IllegalArgumentException("Use found(version) para resultados correctos");
    }

    return new VersionResult(status, null, message);
  }

  /**
   * Indica si el resultado contiene una versión real.
   *
   * @return {@code true} si se ha encontrado versión
   */
  public boolean isFound() {
    return status == VersionStatus.VERSION_FOUND;
  }

  /**
   * Devuelve el valor compatible con la API histórica basada en String.
   *
   * @return versión real o mensaje descriptivo
   */
  public String legacyValue() {
    return isFound() ? version : message;
  }
}
