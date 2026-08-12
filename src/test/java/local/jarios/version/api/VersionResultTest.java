package local.jarios.version.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionResultTest {

    @Test
    void foundCreatesSuccessfulResult() {
        VersionResult result = VersionResult.found("6.0.1");

        assertThat(result.status()).isEqualTo(VersionStatus.VERSION_FOUND);
        assertThat(result.version()).isEqualTo("6.0.1");
        assertThat(result.message()).isNull();
        assertThat(result.isFound()).isTrue();
        assertThat(result.legacyValue()).isEqualTo("6.0.1");
    }

    @Test
    void notFoundCreatesTypedResultWithoutVersion() {
        VersionResult result = VersionResult.notFound(VersionStatus.DEVELOPMENT_MODE, "modo desarrollo");

        assertThat(result.status()).isEqualTo(VersionStatus.DEVELOPMENT_MODE);
        assertThat(result.version()).isNull();
        assertThat(result.message()).isEqualTo("modo desarrollo");
        assertThat(result.isFound()).isFalse();
        assertThat(result.legacyValue()).isEqualTo("modo desarrollo");
    }

    @Test
    void notFoundRejectsSuccessfulStatus() {
        assertThatThrownBy(() -> VersionResult.notFound(VersionStatus.VERSION_FOUND, "incorrecto"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Use found(version) para resultados correctos");
    }
}
