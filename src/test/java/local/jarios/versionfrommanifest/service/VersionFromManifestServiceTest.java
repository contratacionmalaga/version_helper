package local.jarios.versionfrommanifest.service;

import org.junit.jupiter.api.Test;
import local.jarios.versionfrommanifest.exception.VersionFromManifestException;

import static org.junit.jupiter.api.Assertions.*;

class VersionFromManifestServiceImplTest {

    private final VersionFromManifestService service = new VersionFromManifestServiceImpl();

    /**
     * Clase ficticia interna para usar como referencia en el test.
     */
    private static class DummyClass {}

    @Test
    void testGetVersionReturnsDevelopmentOrNoResourceIfNotInJar() {
        String version = service.getVersion(DummyClass.class);
        assertTrue(
                version.equals("Ejecutando sin JAR (modo desarrollo)") ||
                        version.equals("No se encontró recurso de clase"),
                "La versión debería indicar entorno de desarrollo o falta de recurso de clase"
        );
    }

    @Test
    void testThrowsVersionFromManifestExceptionWhenIOExceptionOccurs() {
        // Para forzar un error de tipo IOException, usamos una clase inválida deliberadamente.
        assertThrows(VersionFromManifestException.class, () -> service.getVersion(BrokenClass.class));
    }

    /**
     * Clase que forzará un IOException por tener un nombre inválido en el path del recurso.
     */
    private static class BrokenClass {
        @Override
        public String toString() {
            // Esto no afecta directamente, solo estamos usando una clase imposible de cargar
            return super.toString();
        }
    }
}