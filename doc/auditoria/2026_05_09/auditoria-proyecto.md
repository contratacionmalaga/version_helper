# Auditoria del proyecto version-helper

Fecha: 2026-05-09  
Repositorio auditado: `C:\java\desarrollo\version_helper`  
Rama revisada: `main`  
Revision local: `56cb1a3 Mejoras en el log y en el fichero y actualizacion de versiones`

## 1. Alcance y metodologia

La auditoria cubre estructura del proyecto, configuracion Maven, dependencias, CI/CD, codigo fuente, pruebas, documentacion, empaquetado, seguridad y mantenibilidad.

Se ha realizado revision estatica del repositorio y comprobaciones locales disponibles. No se han modificado archivos de produccion.

Comandos ejecutados:

- `rg --files`
- `git status --short`
- `git log --oneline -5`
- `Get-Content` sobre `pom.xml`, `README.md`, `.github`, `src/main/java` y `src/test`
- `Select-String` y `rg -n` para trazabilidad de hallazgos
- Lectura del `META-INF/MANIFEST.MF` del JAR existente mediante `System.IO.Compression`

Comandos no ejecutados por limitacion del entorno:

- `mvn test`
- `mvn spotbugs:check`
- `mvn dependency:analyze`
- `jar tf target\version-helper-5.3.0.jar`

Motivo: los comandos `mvn` y `jar` no estan disponibles en el `PATH` del entorno de auditoria.

## 2. Resumen ejecutivo

El proyecto es una libreria Java/Maven pequena cuyo objetivo principal es obtener la version de un artefacto JAR leyendo el manifiesto. La implementacion principal es sencilla y el JAR generado existente contiene correctamente `App-Version: 5.3.0`.

Los riesgos mas relevantes no estan en complejidad del codigo, sino en calidad de entrega: la integracion continua esta configurada con JDK 17 mientras el proyecto compila para Java 21, no hay pruebas unitarias automatizadas reales, SpotBugs referencia filtros inexistentes, y la documentacion publica contiene inconsistencias con el comportamiento actual.

Prioridad recomendada:

1. Corregir CI para usar JDK 21.
2. Anadir pruebas unitarias reales para `VersionImpl`.
3. Corregir o crear los filtros de SpotBugs referenciados en el `pom.xml`.
4. Alinear README, Javadocs y artifactId/version.
5. Revisar dependencias runtime para minimizar acoplamiento de una libreria pequena.

## 3. Inventario del proyecto

Estructura principal:

- `pom.xml`: configuracion Maven, version `5.3.0`, Java 21, plugins de build, release, javadoc, source y SpotBugs.
- `README.md`: documentacion de uso e instalacion.
- `.github/dependabot.yml`: actualizaciones Maven diarias.
- `.github/workflows/maven-ci.yml`: CI Maven y OWASP Dependency Check.
- `src/main/java/local/jarios/version/api/Version.java`: interfaz publica.
- `src/main/java/local/jarios/version/api/VersionImpl.java`: implementacion principal.
- `src/main/java/local/jarios/version/exception/VersionException.java`: excepcion runtime propia.
- `src/main/java/local/jarios/version/helpers/FinalDelProgramaHelper.java`: helper de finalizacion de proceso.
- `src/main/java/local/jarios/version/enums/TipoFinalEjecucion.java`: enum de finalizacion.
- `src/main/java/local/jarios/version/common/util/Mensajes.java`: constantes de mensajes.
- `src/test/java/local/jarios/version/VersionDemo.java`: demo ejecutable, no test automatizado.
- `src/test/resources/logback-test.xml`: configuracion Logback para pruebas.
- `target/version-helper-5.3.0.jar`: artefacto generado existente.

## 4. Hallazgos

### H-001 - CI incompatible con la version Java del proyecto

Severidad: Alta  
Categoria: Build / CI

Evidencia:

- `pom.xml:34` define `maven.compiler.source` como `21`.
- `pom.xml:35` define `maven.compiler.target` como `21`.
- `.github/workflows/maven-ci.yml:19` configura `java-version: '17'`.

Impacto:

La pipeline puede fallar al compilar o verificar el proyecto, aunque el build local con JDK 21 sea correcto. Esto reduce la fiabilidad de releases y pull requests.

Recomendacion:

Actualizar GitHub Actions a JDK 21:

```yaml
- name: Set up JDK 21
  uses: actions/setup-java@v4
  with:
    java-version: '21'
    distribution: 'temurin'
```

Tambien conviene actualizar `actions/checkout@v3`, `actions/setup-java@v3` y `actions/cache@v3` a versiones actuales.

### H-002 - No hay pruebas unitarias automatizadas reales

Severidad: Alta  
Categoria: Calidad / Test

Evidencia:

- `pom.xml` incluye JUnit 5 y AssertJ.
- No se encuentran usos de `@Test`, `Assertions` ni `assertThat` en `src/test/java`.
- `src/test/java/local/jarios/version/VersionDemo.java` contiene un metodo `main`, no casos de prueba.
- `VersionDemo` llama a `FinalDelProgramaHelper.finalizar`, que termina el proceso con `System.exit`.

Impacto:

`mvn test` no valida el comportamiento critico de la libreria. Cambios futuros podrian romper la lectura del manifiesto, el tratamiento de clases nulas o los mensajes de error sin deteccion automatica.

Recomendacion:

Crear una clase `VersionImplTest` con pruebas para:

- `getVersion(null)` lanza `VersionException`.
- Cuando la clase no procede de un JAR, devuelve el mensaje de modo desarrollo.
- Lectura correcta de `App-Version` desde un JAR de prueba.
- Manifiesto sin `App-Version`.
- JAR sin `MANIFEST.MF`, si aplica.

Para escenarios de JAR, generar fixtures temporales con `JarOutputStream` en la prueba.

### H-003 - SpotBugs referencia ficheros de filtro inexistentes

Severidad: Alta  
Categoria: Calidad / Seguridad

Evidencia:

- `pom.xml:237` referencia `src/main/resources/spotbugs-security-include.xml`.
- `pom.xml:238` referencia `src/main/resources/spotbugs-security-exclude.xml`.
- En el inventario del repositorio no existe `src/main/resources`.

Impacto:

La ejecucion de `mvn spotbugs:check` puede fallar por configuracion incompleta o no aplicar la politica esperada de analisis. Esto afecta directamente a los controles de calidad y seguridad.

Recomendacion:

Elegir una de estas opciones:

- Crear ambos filtros XML con reglas intencionadas y versionarlos.
- Eliminar `includeFilterFile` y `excludeFilterFile` si no son necesarios.

Despues, ejecutar `mvn spotbugs:check` en un entorno con Maven y JDK 21.

### H-004 - README desalineado con el codigo y el artefacto actual

Severidad: Media  
Categoria: Documentacion / Consumo externo

Evidencia:

- `README.md:9` y `README.md:19` mencionan `Implementation-Version`.
- `VersionImpl.java:25` lee el atributo `App-Version`.
- `pom.xml:176` escribe `App-Version` en el manifiesto.
- `README.md:69` usa `<artifactId>version_helper</artifactId>`.
- `pom.xml:5` define `<artifactId>version-helper</artifactId>`.
- `README.md` muestra version `2.0.0`, mientras `pom.xml:6` define `5.3.0`.

Impacto:

Un consumidor puede declarar una dependencia incorrecta o buscar un atributo de manifiesto distinto al realmente soportado.

Recomendacion:

Actualizar README para indicar:

- Atributo de manifiesto real: `App-Version`.
- Dependencia correcta: `local.jarios:version-helper:5.3.0`.
- Notar que en modo desarrollo, sin JAR, se devuelve un mensaje contextual.

### H-005 - Javadocs heredados de otro dominio funcional

Severidad: Media  
Categoria: Documentacion / Mantenibilidad

Evidencia:

- `src/test/java/local/jarios/version/VersionDemo.java:15`, `:18` y `:33` hablan de cifrado, descifrado y `encriptador.jar`.
- `src/main/java/local/jarios/version/exception/VersionException.java:4` habla de errores durante cifrado o descifrado.
- `src/main/java/local/jarios/version/api/Version.java:18` menciona `Implementation-Version`, mientras la implementacion usa `App-Version`.

Impacto:

La documentacion interna induce a error y aumenta el coste de mantenimiento.

Recomendacion:

Corregir Javadocs para que reflejen exclusivamente el dominio `version-helper`.

### H-006 - Libreria con dependencia runtime a Logback

Severidad: Media  
Categoria: Arquitectura / Dependencias

Evidencia:

- `pom.xml:96-99` incluye `logback-classic` sin scope de test.
- `pom.xml:111-114` incluye `slf4j-api`.
- `VersionImpl` y helpers usan `@Slf4j`.

Impacto:

Una libreria deberia depender normalmente de `slf4j-api` y dejar que la aplicacion consumidora elija la implementacion de logging. Incluir Logback como dependencia runtime puede provocar conflictos de logging en aplicaciones consumidoras.

Recomendacion:

Mover `logback-classic` a `test` o eliminarlo del artefacto runtime, manteniendo `slf4j-api` como dependencia normal. Conservar `logback-test.xml` para pruebas.

### H-007 - `FinalDelProgramaHelper` no encaja bien en una libreria reusable

Severidad: Media  
Categoria: Diseno / Reutilizacion

Evidencia:

- `src/main/java/local/jarios/version/helpers/FinalDelProgramaHelper.java:56` llama a `System.exit(exitCode)`.
- `src/test/java/local/jarios/version/VersionDemo.java` usa ese helper desde codigo de test/demo.

Impacto:

Un helper que termina la JVM es peligroso en una libreria porque puede finalizar procesos consumidores si se usa accidentalmente. Tambien dificulta las pruebas automatizadas.

Recomendacion:

Mover este helper fuera de `src/main` si solo es demo/CLI, o sustituirlo por retorno de codigos/resultado sin llamar directamente a `System.exit`. Si se necesita CLI, crear un modulo o clase claramente separada.

### H-008 - `VersionImpl` devuelve strings de error en vez de un contrato tipado

Severidad: Media  
Categoria: API / Robustez

Evidencia:

- `VersionImpl.java:54` devuelve `"No se encontro recurso de clase"`.
- `VersionImpl.java:60` devuelve `"Ejecutando sin JAR (modo desarrollo)"`.
- `VersionImpl.java:71` devuelve `"No se encontro MANIFEST.MF en el JAR"`.
- `VersionImpl.java:82` devuelve `"Version no especificada en MANIFEST.MF. Revisar fichero pom.xml"`.
- `VersionDemo` necesita comparar el resultado contra una lista de mensajes de error.

Impacto:

El contrato de la API mezcla resultado exitoso y errores como `String`, lo que obliga a los consumidores a comparar textos. Esto es fragil ante traducciones o cambios de mensaje.

Recomendacion:

Valorar un contrato mas expresivo:

- `Optional<String> getVersion(Class<?> clazz)` para ausencia esperada.
- `VersionResult` con estado y mensaje.
- Excepciones para estados no recuperables.

Si se mantiene el contrato actual por compatibilidad, centralizar los mensajes en constantes y documentar que son parte de la API.

### H-009 - Inconsistencia entre mensajes centralizados y mensajes reales

Severidad: Baja  
Categoria: Mantenibilidad

Evidencia:

- `Mensajes.ERROR_4` dice `"Version no especificada en MANIFEST.MF. Revisar pom.xml"`.
- `VersionImpl.java:82` devuelve `"Version no especificada en MANIFEST.MF. Revisar fichero pom.xml"`.
- `VersionImpl` no usa las constantes `Mensajes.ERROR_1` a `ERROR_4`.

Impacto:

La demo compara contra constantes, pero la implementacion devuelve literales propios. En este caso una variacion textual puede impedir detectar correctamente estados esperados.

Recomendacion:

Usar las constantes de `Mensajes` desde `VersionImpl`, o eliminar `Mensajes.ERROR_*` si no deben formar parte del contrato.

### H-010 - `@Slf4j` innecesario en enum

Severidad: Baja  
Categoria: Limpieza / Mantenibilidad

Evidencia:

- `src/main/java/local/jarios/version/enums/TipoFinalEjecucion.java` declara `@Slf4j`.
- El enum no usa `log`.

Impacto:

Genera codigo innecesario y ruido conceptual.

Recomendacion:

Eliminar `@Slf4j` del enum y el import asociado.

### H-011 - Configuracion Maven mejorable para Java moderno

Severidad: Baja  
Categoria: Build

Evidencia:

- `pom.xml:131-132` configura `source` y `target`.

Impacto:

Con Java moderno suele ser preferible `maven.compiler.release` para asegurar API target, no solo bytecode/source level.

Recomendacion:

Sustituir por:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

y configurar el compiler plugin con `<release>${maven.compiler.release}</release>`.

### H-012 - Ausencia de Maven Wrapper

Severidad: Baja  
Categoria: Operabilidad

Evidencia:

- No existe `mvnw` ni `.mvn/wrapper`.
- En este entorno `mvn` no esta disponible.

Impacto:

La reproducibilidad local depende de que cada maquina tenga Maven instalado y en `PATH`.

Recomendacion:

Anadir Maven Wrapper con una version fija compatible, por ejemplo Maven 3.9.x.

## 5. Seguridad

No se observan entradas de usuario complejas, acceso a red, persistencia, serializacion ni SQL. La superficie de seguridad de la libreria es baja.

Riesgos detectados:

- Dependencia runtime a Logback en una libreria: puede introducir conflictos en consumidores.
- Analisis SpotBugs/FindSecBugs configurado pero probablemente incompleto por filtros inexistentes.
- OWASP Dependency Check esta en CI, pero no se ha podido ejecutar localmente por falta de Maven.

Recomendaciones de seguridad:

- Arreglar SpotBugs y hacerlo fallar en CI ante hallazgos relevantes.
- Ejecutar `mvn dependency:tree` y `mvn dependency:analyze`.
- Mantener Dependabot, pero revisar si la frecuencia diaria genera ruido excesivo para un proyecto pequeno.

## 6. Pruebas recomendadas

Suite minima propuesta:

- `VersionImplTest#getVersionThrowsWhenClassIsNull`
- `VersionImplTest#getVersionReturnsDevelopmentMessageOutsideJar`
- `VersionImplTest#getVersionReadsAppVersionFromManifest`
- `VersionImplTest#getVersionReturnsMessageWhenManifestMissing`
- `VersionImplTest#getVersionReturnsMessageWhenAppVersionMissing`

Adicionalmente:

- Prueba de integracion que empaquete el JAR y valide que el manifiesto contiene `App-Name` y `App-Version`.
- Validacion de que el README coincide con `pom.xml`, si se quiere automatizar consistencia documental.

## 7. Estado del artefacto generado

Se inspecciono el manifiesto del JAR existente `target/version-helper-5.3.0.jar`.

Contenido relevante:

```text
Java-Version: 21
Build-Jdk-Spec: 21
App-Name: version-helper
App-Version: 5.3.0
```

Conclusion:

El artefacto generado existente esta alineado con `VersionImpl`, porque expone `App-Version`. La discrepancia esta principalmente en README/Javadocs y en la pipeline configurada con JDK 17.

## 8. Plan de remediacion propuesto

### Corto plazo

1. Cambiar CI a JDK 21.
2. Corregir README y Javadocs obsoletos.
3. Crear o eliminar los filtros SpotBugs referenciados.
4. Anadir tests unitarios basicos para `VersionImpl`.

### Medio plazo

1. Mover `logback-classic` a scope `test`.
2. Revisar si `FinalDelProgramaHelper` debe estar en `src/main`.
3. Sustituir mensajes de error como `String` por un contrato tipado o documentar explicitamente esos valores.
4. Anadir Maven Wrapper.

### Largo plazo

1. Automatizar release con pipeline reproducible.
2. Publicar artefactos source/javadoc solo si el CI pasa.
3. Introducir control de cobertura si el proyecto crece.

## 9. Conclusion

El proyecto es funcional y tiene una configuracion Maven razonablemente completa para empaquetar una libreria con sources y javadocs. El mayor riesgo actual es de industrializacion: CI incoherente con Java 21, ausencia de tests reales y configuracion de analisis estatico incompleta.

La correccion de estos puntos es acotada y deberia abordarse antes de nuevos cambios funcionales o releases.
