package local.jarios.version.helpers;

import local.jarios.version.common.util.Mensajes;
import local.jarios.version.enums.TipoFinalEjecucion;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase utilitaria para registrar la finalización del programa.
 *
 * <p>Esta clase no termina la JVM. Si una aplicación CLI necesita finalizar el proceso, debe
 * hacerlo explícitamente en su capa de entrada.
 */
@Slf4j
public final class FinalDelProgramaHelper {

  /** Constructor privado para evitar instanciación. */
  private FinalDelProgramaHelper() {
    // Constructor privado para evitar instanciación
  }

  /**
   * Registra la finalización del programa sin terminar la JVM.
   *
   * @param tipoFinal Tipo de finalización
   */
  public static void finalizar(TipoFinalEjecucion tipoFinal) {
    finalizar(tipoFinal, null);
  }

  /**
   * Registra la finalización del programa, con mensaje adicional en caso de error, sin terminar la
   * JVM.
   *
   * @param tipoFinal Tipo de finalización
   * @param mensajeError Mensaje opcional, solo usado si tipoFinal es ERROR
   */
  public static void finalizar(TipoFinalEjecucion tipoFinal, String mensajeError) {
    String mensaje;

    if (tipoFinal == TipoFinalEjecucion.CORRECTO) {
      mensaje = Mensajes.FINAL_CORRECTO;
    } else {
      mensaje = Mensajes.FINAL_ERROR;
      if (mensajeError != null && !mensajeError.isBlank()) {
        mensaje += " Detalle: " + mensajeError;
      }
    }

    log.info(mensaje);
    log.info(Mensajes.FINAL);

    System.out.flush();
    System.err.flush();

    log.debug("Código de salida sugerido: {}", resolverCodigoSalida(tipoFinal));
  }

  /**
   * Resuelve el código de salida recomendado para un tipo de finalización.
   *
   * @param tipoFinal Tipo de finalización
   * @return 0 si es correcto, 1 si es error
   */
  public static int resolverCodigoSalida(TipoFinalEjecucion tipoFinal) {
    return tipoFinal == TipoFinalEjecucion.CORRECTO ? 0 : 1;
  }
}
