# Biblioteca de Consulta de Versiones

Biblioteca Java para consultar la versión de un JAR mediante el atributo `App-Version` de su manifiesto. Su API tipada distingue una versión encontrada de la ejecución en desarrollo y de la ausencia de metadatos.

**Artefacto Maven:** `local.jarios:version-helper:6.2.0`

**Repositorio:** [contratacionmalaga/version_helper](https://github.com/contratacionmalaga/version_helper)

## Información de la versión

| Característica | Configuración |
|---|---|
| Versión | **6.2.0** |
| Tipo de artefacto | Biblioteca `jar` |
| Parent Maven | `local.jarios:jarios-parent:1.0.15` |
| Java de referencia y compilación | **21** |
| Maven mínimo y distribuido mediante Wrapper | **3.9.16** |
| Maven Wrapper | **3.3.4** |
| Publicación | GitHub Packages |
| Revisión de dependencias y herramientas | 26 de septiembre de 2026 |

Esta versión actualiza el parent, las dependencias de logging, las herramientas de construcción y calidad y GitHub Actions. Mantiene la API pública. Consulte el detalle en las [notas de 6.2.0](docs/releases/6.2.0.md).

## Alcance y funcionamiento

`VersionImpl` localiza el recurso de una clase, identifica el JAR que la contiene y consulta `META-INF/MANIFEST.MF`.

- Lee `App-Version` de los atributos principales del manifiesto.
- Devuelve un `VersionResult` con el estado de la consulta.
- Conserva `getVersion(Class<?>)` para integraciones basadas en texto.
- Distingue la ejecución fuera de un JAR de la ausencia de manifiesto o versión.
- Comunica errores de lectura mediante `VersionException`.
- Emite logs de diagnóstico mediante SLF4J.

La clase de referencia determina el artefacto consultado. Para obtener la versión de una aplicación, utilice una clase de esa aplicación; para consultar esta biblioteca, utilice `VersionImpl.class`.

La biblioteca consulta exclusivamente `App-Version`: no aplica alternativas basadas en `Implementation-Version`, `Specification-Version` o `pom.properties`.

## Requisitos e integración Maven

Se requiere Java 21 o superior. Para compilar se necesita un JDK y Maven 3.9.16 o superior; el Wrapper incluido proporciona Maven.

Declare la dependencia en el POM consumidor:

```xml
<dependency>
  <groupId>local.jarios</groupId>
  <artifactId>version-helper</artifactId>
  <version>6.2.0</version>
</dependency>
```

Puede omitir la versión si su parent gestiona la versión deseada. `jarios-parent:1.0.15` todavía gestiona `version-helper:6.1.4`, por lo que utilizar ese parent no selecciona automáticamente la 6.2.0.

### Acceso a GitHub Packages

Incorpore estos repositorios al POM consumidor o a un perfil activo de Maven:

```xml
<repositories>
  <repository>
    <id>github-version-helper</id>
    <url>https://maven.pkg.github.com/contratacionmalaga/version_helper</url>
  </repository>
  <repository>
    <id>github-jarios-parent</id>
    <url>https://maven.pkg.github.com/contratacionmalaga/jarios-parent</url>
  </repository>
</repositories>
```

Añada los servidores a su `settings.xml`, conservando la configuración existente:

```xml
<servers>
  <server>
    <id>github-version-helper</id>
    <username>${env.GITHUB_ACTOR}</username>
    <password>${env.PACKAGES_TOKEN}</password>
  </server>
  <server>
    <id>github-jarios-parent</id>
    <username>${env.GITHUB_ACTOR}</username>
    <password>${env.PACKAGES_TOKEN}</password>
  </server>
</servers>
```

Defina `GITHUB_ACTOR` y `PACKAGES_TOKEN` con un usuario y una credencial con acceso de lectura a los paquetes. Los identificadores de servidor deben coincidir con los de los repositorios.

## Uso recomendado

La API tipada permite tratar cada resultado sin interpretar mensajes de texto:

```java
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import local.jarios.version.api.VersionResult;
import local.jarios.version.exception.VersionException;

public class Aplicacion {
  public static void main(String[] args) {
    Version servicio = new VersionImpl();
    try {
      VersionResult resultado = servicio.getVersionResult(Aplicacion.class);
      if (resultado.isFound()) {
        System.out.println("Versión: " + resultado.version());
      } else {
        System.out.printf("Versión no disponible: %s — %s%n",
            resultado.status(), resultado.message());
      }
    } catch (VersionException ex) {
      System.err.println("Error consultando la versión: " + ex.getMessage());
    }
  }
}
```

Desde un IDE o un directorio de clases compiladas, el resultado esperado es `DEVELOPMENT_MODE`. Para obtener una versión, la clase debe encontrarse en un JAR cuyo manifiesto incluya `App-Version`.

### Estados de la consulta

| Estado | Significado |
|---|---|
| `VERSION_FOUND` | Se ha leído `App-Version`. |
| `CLASS_RESOURCE_NOT_FOUND` | No se ha localizado el recurso de la clase indicada. |
| `DEVELOPMENT_MODE` | El recurso utiliza un protocolo distinto de `jar`. |
| `MANIFEST_NOT_FOUND` | El JAR no contiene un manifiesto. |
| `APP_VERSION_NOT_FOUND` | `App-Version` no existe o está vacío. |

En los resultados producidos por `VersionImpl`, `version()` contiene la versión cuando `isFound()` devuelve `true`. En los demás estados, `message()` describe la situación.

### API compatible con versiones anteriores

```java
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;

Version servicio = new VersionImpl();
String valor = servicio.getVersion(Aplicacion.class);
```

Este método devuelve la versión o un mensaje descriptivo. Para código nuevo se recomienda `getVersionResult(Class<?>)`.

### Excepciones y límites

`VersionImpl` lanza `VersionException` cuando la clase es `null` o se produce un error de entrada/salida al leer el JAR.

La implementación utiliza `JarURLConnection`. Los cargadores de clases personalizados y los empaquetados con JAR anidados requieren comprobar su compatibilidad.

Utilice preferentemente una clase pública de nivel superior como referencia: la búsqueda actual construye el nombre del recurso a partir de `Class.getSimpleName()`.

El valor de `App-Version` se devuelve tal como está declarado; no se valida como una versión semántica.

## Configurar el manifiesto de la aplicación

El JAR consultado debe declarar su propia versión. En una aplicación Maven puede configurarse mediante Maven JAR Plugin:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-jar-plugin</artifactId>
      <version>3.5.1</version>
      <configuration>
        <archive>
          <manifestEntries>
            <App-Version>${project.version}</App-Version>
            <App-Name>${project.artifactId}</App-Name>
          </manifestEntries>
        </archive>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Puede omitir la versión del plugin si ya está gestionada por su parent. `App-Version` es el atributo consultado; `App-Name` aporta información adicional al manifiesto.

## Registro de finalización

`FinalDelProgramaHelper` permite registrar el resultado de una ejecución:

```java
import local.jarios.version.enums.TipoFinalEjecucion;
import local.jarios.version.helpers.FinalDelProgramaHelper;

FinalDelProgramaHelper.finalizar(TipoFinalEjecucion.CORRECTO);
FinalDelProgramaHelper.finalizar(
    TipoFinalEjecucion.ERROR, "No se ha podido completar la operación");
int codigo = FinalDelProgramaHelper.resolverCodigoSalida(TipoFinalEjecucion.CORRECTO);
```

El helper registra mensajes y vacía las salidas estándar. La finalización de la JVM corresponde a la aplicación consumidora. `resolverCodigoSalida(...)` devuelve `0` para `CORRECTO` y `1` para `ERROR`.

## Dependencias

Las versiones se gestionan desde `jarios-parent:1.0.15`.

| Dependencia | Versión | Ámbito | Finalidad |
|---|---|---|---|
| SLF4J API | 2.0.20 | `compile` | API de logging. |
| Lombok | 1.18.48 | `provided` | Generación del logger durante la compilación. |
| JUnit Jupiter | 6.1.3 | `test` | Pruebas automatizadas. |
| AssertJ Core | 3.27.7 | `test` | Aserciones. |
| Logback Classic | 1.6.4 | `test` | Logging para pruebas. |

La aplicación consumidora proporciona una implementación de logging compatible con SLF4J. Logback está limitado a las pruebas de esta biblioteca.

## Compilación y verificación

Desde la raíz del repositorio:

| Operación | Windows PowerShell | Linux / macOS |
|---|---|---|
| Consultar entorno | `.\mvnw.cmd -version` | `./mvnw -version` |
| Ejecutar pruebas | `.\mvnw.cmd test` | `./mvnw test` |
| Construir y verificar | `.\mvnw.cmd clean verify` | `./mvnw clean verify` |
| Instalar localmente | `.\mvnw.cmd clean install` | `./mvnw clean install` |
| Controles de calidad | `.\mvnw.cmd -Pquality verify` | `./mvnw -Pquality verify` |

En Linux o macOS puede ser necesario ejecutar `chmod +x mvnw`.

El parent debe estar disponible mediante el repositorio local, GitHub Packages o el proyecto vecino con la versión coincidente. La ruta relativa del POM es `../jarios-parent/pom.xml`.

La construcción genera:

- `target/version-helper-6.2.0.jar`
- `target/version-helper-6.2.0-sources.jar`
- `target/version-helper-6.2.0-javadoc.jar`

La fase `verify` comprueba los atributos `App-Name` y `App-Version` del JAR generado.

Las pruebas cubren la clase nula, la ejecución en desarrollo, la lectura desde un JAR, la ausencia de manifiesto o atributo y las operaciones principales de `VersionResult`.

### Calidad y análisis de dependencias

El perfil `quality` ejecuta Spotless, Checkstyle y SpotBugs durante `verify`. SpotBugs incorpora Find Security Bugs.

OWASP Dependency-Check se ejecuta por separado:

```powershell
.\mvnw.cmd org.owasp:dependency-check-maven:check
```

En Linux o macOS, sustituya `.\mvnw.cmd` por `./mvnw`.

## Automatización y publicación

| Workflow | Activación | Función |
|---|---|---|
| `CI` | Push y PR sobre `main`; manual | Construcción y verificación con JDK 21. |
| `Quality` | Push y PR sobre `main`; manual | Perfil `quality`. |
| `Dependency Check` | Lunes a las 04:10 UTC; manual | Análisis OWASP y conservación del informe. |
| `Release Package` | Push de tags `v*`; manual con `release_tag` | Publicación Maven y GitHub Release. |

El tag `v6.2.0` debe apuntar al commit cuyo POM declara `6.2.0`. El workflow comprueba esa coincidencia.

La publicación selecciona el tag, configura Java y el acceso Maven, ejecuta `clean verify`, publica mediante `deploy`, crea la GitHub Release y adjunta los JAR generados. Las notas se mantienen en `docs/releases/<versión>.md`.

La lectura de paquetes utiliza `PACKAGES_TOKEN` o `GITHUB_TOKEN` como alternativa, con acceso a los paquetes necesarios. La publicación requiere permisos de escritura sobre contenido y paquetes.

## Resolución de problemas

| Síntoma | Comprobación |
|---|---|
| `DEVELOPMENT_MODE` | Ejecutar la clase desde un JAR con protocolo `jar`. |
| `CLASS_RESOURCE_NOT_FOUND` | Utilizar una clase de nivel superior con recurso accesible. |
| `MANIFEST_NOT_FOUND` | Comprobar `META-INF/MANIFEST.MF`. |
| `APP_VERSION_NOT_FOUND` | Añadir `App-Version` al manifiesto consultado. |
| Versión de otro artefacto | Revisar la clase de referencia utilizada. |
| `VersionException` | Revisar la clase suministrada y la causa del error de lectura. |
| No aparecen logs | Revisar el backend SLF4J y sus niveles. |
| No se resuelve el parent o la biblioteca | Comprobar versión, repositorios y credenciales Maven. |
| Error 401 o 403 | Revisar permisos e identificadores de servidor y repositorio. |
| Falla la publicación por versión | Alinear el tag y el POM. |

## Documentación y mantenimiento

- [Cambios y validación de 6.2.0](docs/releases/6.2.0.md).
- [Auditoría de agosto de 2026](docs/auditorias/auditoria-viva-2026-08-12.md).
- [Actualizaciones de agosto de 2026](docs/auditorias/actualizaciones-versiones-2026-08-12.md).

Los documentos históricos describen el estado de su fecha. La API, el POM y los workflows constituyen la referencia para mantener actualizado este README.

## Licencia

El repositorio no incluye un archivo de licencia propio. Las dependencias conservan sus respectivas licencias.
