package local.jarios.version.api;

import local.jarios.version.common.util.Mensajes;
import local.jarios.version.exception.VersionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionImplTest {

    private static final String CLASS_ENTRY = "example/Demo.class";

    @TempDir
    Path tempDir;

    @Test
    void getVersionThrowsWhenClassIsNull() {
        VersionImpl version = new VersionImpl();

        assertThatThrownBy(() -> version.getVersion(null))
            .isInstanceOf(VersionException.class)
            .hasMessage("La clase no puede ser nula");
    }

    @Test
    void getVersionReturnsDevelopmentMessageOutsideJar() {
        VersionImpl version = new VersionImpl();

        assertThat(version.getVersion(VersionImplTest.class)).isEqualTo(Mensajes.ERROR_2);
    }

    @Test
    void getVersionReadsAppVersionFromManifest() throws IOException {
        Path jarPath = createJarWithManifest("5.3.0");
        VersionImpl version = versionReturning(jarResourceUrl(jarPath));

        assertThat(version.getVersion(VersionImplTest.class)).isEqualTo("5.3.0");
    }

    @Test
    void getVersionReturnsMessageWhenManifestIsMissing() throws IOException {
        Path jarPath = createJarWithoutManifest();
        VersionImpl version = versionReturning(jarResourceUrl(jarPath));

        assertThat(version.getVersion(VersionImplTest.class)).isEqualTo(Mensajes.ERROR_3);
    }

    @Test
    void getVersionReturnsMessageWhenAppVersionIsMissing() throws IOException {
        Path jarPath = createJarWithManifest(null);
        VersionImpl version = versionReturning(jarResourceUrl(jarPath));

        assertThat(version.getVersion(VersionImplTest.class)).isEqualTo(Mensajes.ERROR_4);
    }

    private VersionImpl versionReturning(URL resourceUrl) {
        return new VersionImpl() {
            @Override
            protected URL getResourceURL(Class<?> clazz, String className) {
                return resourceUrl;
            }
        };
    }

    private URL jarResourceUrl(Path jarPath) throws IOException {
        return URI.create("jar:" + jarPath.toUri() + "!/" + CLASS_ENTRY).toURL();
    }

    private Path createJarWithManifest(String appVersion) throws IOException {
        Path jarPath = tempDir.resolve("version-helper-test.jar");
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (appVersion != null) {
            attributes.putValue("App-Version", appVersion);
        }

        try (JarOutputStream jar = new JarOutputStream(java.nio.file.Files.newOutputStream(jarPath), manifest)) {
            addDummyClass(jar);
        }

        return jarPath;
    }

    private Path createJarWithoutManifest() throws IOException {
        Path jarPath = tempDir.resolve("version-helper-test-no-manifest.jar");

        try (JarOutputStream jar = new JarOutputStream(java.nio.file.Files.newOutputStream(jarPath))) {
            addDummyClass(jar);
        }

        return jarPath;
    }

    private void addDummyClass(JarOutputStream jar) throws IOException {
        jar.putNextEntry(new JarEntry(CLASS_ENTRY));
        jar.write(new byte[] {0});
        jar.closeEntry();
    }
}
