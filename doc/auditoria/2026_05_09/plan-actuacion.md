# Plan de actuacion para aplicar la auditoria

Fecha: 2026-05-09  
Proyecto: `version-helper`  
Auditoria base: `doc/auditoria/2026_05_09/auditoria-proyecto.md`

## Objetivo

Corregir los hallazgos detectados en la auditoria priorizando primero los riesgos que afectan a build, CI, pruebas y publicacion del artefacto. El objetivo final es que el proyecto pueda compilarse, probarse, analizarse y publicarse de forma reproducible.

## Criterios de finalizacion

El plan se considera aplicado cuando:

- GitHub Actions compila con JDK 21.
- Existe una suite minima de pruebas unitarias para `VersionImpl`.
- `mvn test` pasa en local y en CI.
- La configuracion de SpotBugs no referencia ficheros inexistentes.
- README y Javadocs describen el comportamiento real del proyecto.
- Las dependencias runtime son adecuadas para una libreria reusable.
- El repositorio incluye una forma reproducible de ejecutar Maven, preferiblemente Maven Wrapper.

## Fase 0 - Preparacion

Prioridad: Alta  
Duracion estimada: 30 minutos

Acciones:

1. Instalar o activar JDK 21 en el entorno local.
2. Instalar Maven o generar Maven Wrapper.
3. Ejecutar una linea base:

```bash
mvn -version
mvn clean test
```

Criterio de aceptacion:

- Se confirma que el entorno usa Java 21.
- Se conoce el estado real inicial del build.

Notas:

- En la auditoria no se pudo ejecutar Maven porque `mvn` no estaba disponible en el `PATH`.

## Fase 1 - Corregir CI y build

Prioridad: Alta  
Hallazgos relacionados: H-001, H-011, H-012

Acciones:

1. Actualizar `.github/workflows/maven-ci.yml` para usar JDK 21.
2. Actualizar acciones de GitHub:
   - `actions/checkout@v4`
   - `actions/setup-java@v4`
   - `actions/cache@v4`, si se mantiene cache manual.
3. Valorar simplificar cache usando `cache: maven` en `setup-java`.
4. Cambiar la configuracion Maven de `source`/`target` a `release`:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

5. Generar Maven Wrapper:

```bash
mvn -N wrapper:wrapper
```

Criterio de aceptacion:

- `./mvnw clean test` funciona en local.
- GitHub Actions usa Java 21.
- No hay incoherencia entre `pom.xml` y CI.

## Fase 2 - Recuperar analisis estatico

Prioridad: Alta  
Hallazgo relacionado: H-003

Acciones:

1. Decidir si se quieren filtros propios de SpotBugs/FindSecBugs.
2. Si no son necesarios, eliminar del `pom.xml`:

```xml
<includeFilterFile>src/main/resources/spotbugs-security-include.xml</includeFilterFile>
<excludeFilterFile>src/main/resources/spotbugs-security-exclude.xml</excludeFilterFile>
```

3. Si son necesarios, crear:
   - `src/main/resources/spotbugs-security-include.xml`
   - `src/main/resources/spotbugs-security-exclude.xml`
4. Ejecutar:

```bash
mvn spotbugs:check
```

Criterio de aceptacion:

- `mvn spotbugs:check` no falla por ficheros inexistentes.
- La configuracion aplicada es intencionada y versionada.

Recomendacion:

- Para este proyecto, empezar eliminando los filtros inexistentes es la opcion mas simple. Se pueden reintroducir filtros especificos cuando haya una politica clara.

## Fase 3 - Anadir pruebas unitarias reales

Prioridad: Alta  
Hallazgo relacionado: H-002

Acciones:

1. Crear `src/test/java/local/jarios/version/api/VersionImplTest.java`.
2. Cubrir como minimo:
   - `getVersion(null)` lanza `VersionException`.
   - Una clase cargada desde `target/classes` devuelve modo desarrollo.
   - Un JAR temporal con `App-Version` devuelve la version esperada.
   - Un JAR temporal sin `App-Version` devuelve el mensaje esperado.
3. Revisar si `VersionDemo` debe quedarse como demo o moverse fuera de `src/test/java`.
4. Evitar pruebas que ejecuten `System.exit`.

Criterio de aceptacion:

- Hay tests JUnit 5 reales con `@Test`.
- `mvn test` valida el comportamiento principal de `VersionImpl`.
- Las pruebas no terminan la JVM.

## Fase 4 - Corregir documentacion publica e interna

Prioridad: Media  
Hallazgos relacionados: H-004, H-005

Acciones:

1. Actualizar `README.md`:
   - Cambiar `Implementation-Version` por `App-Version`.
   - Cambiar `version_helper` por `version-helper`.
   - Cambiar version de ejemplo a `5.3.0` o explicar como consultar la ultima version publicada.
2. Corregir Javadocs heredados:
   - `VersionException.java`
   - `VersionDemo.java`
   - `Version.java`
3. Alinear mensajes de ejemplo con el comportamiento real.

Criterio de aceptacion:

- README permite consumir correctamente la libreria.
- No quedan referencias a cifrado, descifrado ni `encriptador.jar`.
- La documentacion menciona el atributo real `App-Version`.

## Fase 5 - Ajustar diseno de libreria y dependencias

Prioridad: Media  
Hallazgos relacionados: H-006, H-007, H-008, H-009, H-010

Acciones:

1. Cambiar `logback-classic` a scope `test` o eliminarlo como dependencia runtime.
2. Mantener `slf4j-api` como dependencia de la libreria.
3. Eliminar `@Slf4j` de `TipoFinalEjecucion`.
4. Centralizar los mensajes de error:
   - O `VersionImpl` usa `Mensajes.ERROR_*`.
   - O se eliminan esas constantes si no forman parte de la API.
5. Revisar `FinalDelProgramaHelper`:
   - Moverlo a test/demo si no es API publica.
   - O reemplazar `System.exit` por retorno de codigo de salida en una capa CLI.
6. Valorar evolucionar el contrato de `Version`:
   - Mantener `String` por compatibilidad en una version menor.
   - Preparar `Optional<String>` o `VersionResult` para una version mayor.

Criterio de aceptacion:

- El artefacto no fuerza Logback en aplicaciones consumidoras.
- No hay logging innecesario en el enum.
- Los mensajes usados para estados no exitosos son consistentes.
- `System.exit` no forma parte accidental de la API reusable.

## Fase 6 - Validacion final

Prioridad: Alta  
Dependencias: fases 1 a 5

Acciones:

Ejecutar:

```bash
./mvnw clean verify
./mvnw spotbugs:check
./mvnw dependency:analyze
```

Validar tambien:

1. El JAR generado contiene:
   - `App-Name`
   - `App-Version`
   - `Build-Jdk-Spec: 21`
2. GitHub Actions pasa en pull request.
3. El README coincide con el `pom.xml`.

Criterio de aceptacion:

- Build completo correcto.
- Analisis estatico correcto.
- Tests correctos.
- CI correcto.

## Orden recomendado de commits

1. `build: align ci and maven with java 21`
2. `test: add version implementation coverage`
3. `build: fix spotbugs configuration`
4. `docs: align readme and javadocs with app-version`
5. `refactor: reduce runtime logging coupling`
6. `chore: add maven wrapper`

## Riesgos y decisiones pendientes

### Mantener compatibilidad de API

Cambiar `String getVersion(Class<?> clazz)` por `Optional<String>` o un resultado tipado seria mas robusto, pero rompe API. Se recomienda mantener el contrato actual en una correccion menor y preparar el cambio para una version mayor.

### `FinalDelProgramaHelper`

Si algun consumidor ya lo usa, moverlo o cambiarlo puede ser ruptura. Antes de eliminarlo, revisar si esta publicado como API consumida.

### SpotBugs

Eliminar filtros inexistentes es rapido y pragmatico. Crear filtros propios solo tiene sentido si se define una politica clara de inclusion/exclusion.

## Checklist operativo

- [ ] Entorno local con Java 21 y Maven/Maven Wrapper.
- [ ] CI actualizado a JDK 21.
- [ ] Compiler plugin usando `release`.
- [ ] SpotBugs sin rutas inexistentes.
- [ ] Tests unitarios para `VersionImpl`.
- [ ] README corregido.
- [ ] Javadocs corregidos.
- [ ] Logback fuera de runtime principal.
- [ ] `System.exit` revisado o aislado.
- [ ] `mvn clean verify` correcto.
- [ ] `mvn spotbugs:check` correcto.
- [ ] Pull request con CI en verde.
