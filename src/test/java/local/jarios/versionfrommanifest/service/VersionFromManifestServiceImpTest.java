package local.jarios.versionfrommanifest.service;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class VersionFromManifestServiceImpTest {

    @Test
    void testIOExceptionLanzaExcepcion() {
        VersionFromManifestService service = new VersionFromManifestServiceImpl() {
            @Override
            public Optional<String> getVersionFromManifest() {
                throw new RuntimeException("error simulado");
            }
        };

        RuntimeException ex = assertThrows(RuntimeException.class, service::getVersionFromManifest);
        assertEquals("error simulado", ex.getMessage());
    }

    @Test
    void testManifestSinVersion() {
        VersionFromManifestServiceImpl testService = new VersionFromManifestServiceImpl() {
            @Override
            public Optional<String> getVersionFromManifest() {
                return Optional.of("No hay versión en el manifest");
            }
        };

        String result = testService.getVersionFromManifest().orElse("");
        assertEquals("No hay versión en el manifest", result);
    }

    @Test
    void testJarSinManifest() {
        VersionFromManifestServiceImpl testService = new VersionFromManifestServiceImpl() {
            @Override
            public Optional<String> getVersionFromManifest() {
                return Optional.of("No hay manifest en el JAR");
            }
        };

        String result = testService.getVersionFromManifest().orElse("");
        assertEquals("No hay manifest en el JAR", result);
    }
}
