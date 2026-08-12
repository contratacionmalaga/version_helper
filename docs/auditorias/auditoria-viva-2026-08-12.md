# Auditoría viva del proyecto `version-helper`

Fecha de apertura: 2026-08-12  
Última actualización: 2026-08-12  
Repositorio: `C:\java\desarrollo\version_helper`  
Rama auditada: `main`  
Revisión local auditada: `f82a2bf Subir version a 6.0.0`  
Versión del artefacto: `local.jarios:version-helper:6.0.0`  
Estado global: `EN CURSO`

## 1. Cómo mantener viva esta auditoría

Este documento debe actualizarse cada vez que se cierre un hito, se descarte un hallazgo o cambie una decisión técnica relevante.

Reglas de mantenimiento:

- Cambiar el estado de cada hito en la tabla de seguimiento.
- Añadir una entrada en la bitácora con fecha, cambio realizado, commit o PR, y verificación ejecutada.
- No borrar hallazgos cerrados; moverlos a `Hecho` o `Descartado` con justificación.
- Reejecutar, como mínimo, los comandos de la sección `Comandos de verificación` antes de marcar hitos críticos como completados.
- Si una mejora cambia el contrato público de la librería, reflejarlo también en `README.md` y Javadocs.

Estados permitidos:

- `Pendiente`: identificado, sin trabajo iniciado.
- `En curso`: iniciado, no verificado.
- `Bloqueado`: requiere decisión, dependencia externa o cambio previo.
- `Hecho`: implementado y verificado.
- `Descartado`: no se hará, con motivo documentado.

## 2. Alcance

La auditoría cubre:

- Estructura del proyecto.
- Configuración Maven.
- Dependencias.
- API pública y diseño de librería.
- Pruebas.
- CI/CD y publicación.
- Seguridad básica y análisis estático.
- Documentación.
- Operabilidad y mantenibilidad.

No se han aplicado cambios funcionales al código de producción durante esta auditoría. La única modificación realizada es la creación de este documento.

## 3. Evidencia ejecutada

Comandos ejecutados el 2026-08-12:

```powershell
rg --files
rg --files -uu
git status --short
git branch --show-current
git log --oneline -5
.\mvnw.cmd test
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B dependency:analyze
.\mvnw.cmd -B dependency:tree
.\mvnw.cmd -B spotbugs:check
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
```

Resultado principal:

- `mvnw test`: correcto. 5 tests, 0 fallos.
- `mvnw -B clean verify`: correcto.
- `mvnw -B dependency:analyze`: correcto, sin problemas de dependencias.
- `mvnw -B spotbugs:check`: correcto, 0 bugs, 0 errores.
- `dependency:tree`: solo hay una dependencia runtime real, `org.slf4j:slf4j-api`.
- Maven compila con Java 21, pero muestra aviso de annotation processing implícito por Lombok.

## 4. Resumen ejecutivo

El proyecto está en buen estado operativo para una librería pequeña: compila con Java 21, tiene Maven Wrapper, CI actualizado a JDK 21, publicación configurada en GitHub Packages, tests unitarios básicos y SpotBugs sin hallazgos.

Los flancos actuales no son bloqueantes, pero sí conviene corregirlos para endurecer el proyecto antes de seguir evolucionándolo:

1. La API pública mezcla versiones reales y errores como `String`.
2. Existe código de finalización de proceso con `System.exit` dentro de `src/main`, algo arriesgado para una librería reusable.
3. La configuración de calidad contiene propiedades de Checkstyle sin plugin ni ejecución asociada.
4. Lombok se usa en una librería pequeña solo para logging, con annotation processing implícito.
5. Las dependencias de test están funcionales, pero JUnit y AssertJ tienen actualizaciones mayores que conviene evaluar de forma controlada.
6. CI usa una acción OWASP Dependency Check apuntando a `@main`, lo que reduce reproducibilidad.
7. Faltan controles de contrato de artefacto: validar automáticamente que el JAR contiene `App-Version` y `App-Name`.

Prioridad recomendada:

1. Definir si se mantiene compatibilidad de la API actual o se introduce un contrato tipado.
2. Sacar `FinalDelProgramaHelper` de la librería principal o aislarlo como demo/CLI.
3. Añadir verificación automática del manifiesto generado.
4. Corregir la configuración de annotation processing de Lombok o eliminar Lombok.
5. Normalizar calidad: Checkstyle real o eliminar propiedades muertas.
6. Fijar versiones de acciones críticas en CI.

## 5. Inventario actual

Código principal:

- `src/main/java/local/jarios/version/api/Version.java`: interfaz pública.
- `src/main/java/local/jarios/version/api/VersionImpl.java`: implementación que lee `App-Version` desde `MANIFEST.MF`.
- `src/main/java/local/jarios/version/exception/VersionException.java`: excepción runtime propia.
- `src/main/java/local/jarios/version/common/util/Mensajes.java`: constantes de mensajes.
- `src/main/java/local/jarios/version/helpers/FinalDelProgramaHelper.java`: helper que registra logs y termina la JVM.
- `src/main/java/local/jarios/version/enums/TipoFinalEjecucion.java`: enum de finalización.

Pruebas y demos:

- `src/test/java/local/jarios/version/api/VersionImplTest.java`: 5 pruebas unitarias.
- `src/test/java/local/jarios/version/VersionDemo.java`: demo ejecutable, no prueba automatizada.
- `src/test/resources/logback-test.xml`: logging para tests/demos.

Automatización:

- `.github/workflows/maven-ci.yml`: ejecuta `./mvnw -B clean verify` con JDK 21 y OWASP Dependency Check.
- `.github/workflows/release-package.yml`: publica en GitHub Packages y adjunta JARs a releases.
- `.github/dependabot.yml`: actualizaciones Maven diarias.
- `.mvn/wrapper/maven-wrapper.properties`, `mvnw`, `mvnw.cmd`: Maven Wrapper presente.

Documentación previa:

- `doc/auditoria/2026_05_09/`: auditoría anterior. Parte de sus hallazgos ya están corregidos.

## 6. Hallazgos

### H-001 - La API mezcla resultado correcto y errores como `String`

Severidad: Media  
Categoría: API / robustez  
Estado: Pendiente  
Hito asociado: M-01

Evidencia:

- `VersionImpl` devuelve `Mensajes.ERROR_1`, `ERROR_2`, `ERROR_3` y `ERROR_4` en distintos escenarios.
- La interfaz pública `Version#getVersion(Class<?>)` devuelve solo `String`.
- `VersionDemo` tiene que comparar el resultado contra una lista de textos de error.

Impacto:

El consumidor no puede distinguir de forma tipada entre una versión válida y un estado de error/ausencia. Si cambian los textos, se rompe cualquier consumidor que compare cadenas.

Recomendación:

Elegir una estrategia:

- Compatible: mantener `getVersion(Class<?>)`, pero documentar explícitamente que los mensajes de `Mensajes.ERROR_*` forman parte del contrato.
- Evolutiva: añadir una API nueva, por ejemplo `VersionResult getVersionResult(Class<?>)` u `Optional<String> findVersion(Class<?>)`, y dejar `getVersion` como método de compatibilidad.

No se recomienda romper directamente `getVersion` sin plan de versión mayor y notas de migración.

### H-002 - `FinalDelProgramaHelper` llama a `System.exit` dentro de `src/main`

Severidad: Media  
Categoría: Diseño de librería / seguridad operativa  
Estado: Pendiente  
Hito asociado: M-02

Evidencia:

- `FinalDelProgramaHelper.finalizar(...)` termina la JVM con `System.exit(exitCode)`.
- La clase vive en `src/main`, por tanto queda publicada como parte del artefacto.

Impacto:

En una librería reusable, exponer una utilidad que finaliza el proceso completo es peligroso. Un consumidor podría terminar su propia aplicación accidentalmente. También complica tests y composición con frameworks.

Recomendación:

- Si solo sirve para demo, moverla a `src/test` o eliminarla del artefacto publicado.
- Si se quiere CLI, crear una clase o módulo CLI separado y mantener el core sin `System.exit`.
- Si se mantiene, marcarla claramente como API no recomendada y cubrirla con una decisión técnica documentada.

### H-003 - Falta una prueba automática del manifiesto del JAR generado

Severidad: Media  
Categoría: Testing / empaquetado  
Estado: Pendiente  
Hito asociado: M-03

Evidencia:

- El `pom.xml` configura `App-Version` y `App-Name` en el manifiesto.
- Las pruebas unitarias validan lectura desde JARs generados en test, pero no verifican el artefacto real producido por Maven.

Impacto:

Un cambio futuro en `maven-jar-plugin`, `finalName` o manifest entries podría publicar un JAR sin los atributos que la propia librería necesita leer.

Recomendación:

Añadir una prueba de integración o una verificación en fase `verify` que inspeccione `target/version-helper-${project.version}.jar` y valide:

- `META-INF/MANIFEST.MF` existe.
- `App-Name = version-helper`.
- `App-Version = ${project.version}`.

### H-004 - Annotation processing implícito por Lombok

Severidad: Baja  
Categoría: Build / mantenibilidad  
Estado: Hecho  
Hito asociado: M-04

Evidencia:

Durante `mvnw -B clean verify`, `javac` muestra aviso indicando que el annotation processing está activado porque se encontraron procesadores en el classpath y que futuros JDKs podrían cambiar este comportamiento.

Impacto:

El build funciona ahora, pero depende de comportamiento implícito. Esto puede generar ruido o fallos en futuras versiones de JDK/Maven.

Recomendación:

Elegir una:

- Configurar `maven-compiler-plugin` con `annotationProcessorPaths` para Lombok.
- Sustituir `@Slf4j` por loggers explícitos y eliminar Lombok. Para este proyecto, esta opción es razonable porque Lombok solo se usa para logging.

### H-005 - Propiedades de Checkstyle declaradas pero no usadas

Severidad: Baja  
Categoría: Calidad / configuración  
Estado: Pendiente  
Hito asociado: M-05

Evidencia:

- `pom.xml` define `maven.checkstyle.plugin.version`.
- `pom.xml` define `checkstyle.max.violations`.
- No hay plugin Checkstyle configurado en `build/plugins`.
- No hay `checkstyle.xml` versionado.

Impacto:

La configuración sugiere un control de estilo que realmente no se ejecuta. Esto puede generar falsa confianza y deuda de configuración.

Recomendación:

Elegir una:

- Activar Checkstyle con reglas mínimas y ejecución en `verify`.
- Eliminar las propiedades si no se va a usar Checkstyle.

### H-006 - Dependencias de test desactualizadas o con salto mayor pendiente

Severidad: Baja  
Categoría: Dependencias  
Estado: En curso  
Hito asociado: M-06

Evidencia de `versions:display-dependency-updates`:

- `org.junit.jupiter:junit-jupiter-api`: `5.8.2 -> 6.1.3`.
- `org.junit.jupiter:junit-jupiter-engine`: `5.8.2 -> 6.1.3`.
- `org.assertj:assertj-core`: `3.21.0 -> 4.0.0-M1`.
- `org.projectlombok:lombok`: `1.18.42 -> 1.18.46`.
- `ch.qos.logback:logback-classic`: `1.5.26 -> 1.6.2`.
- `org.slf4j:slf4j-api`: `2.0.17 -> 2.1.0-alpha1`.

Impacto:

No hay fallo actual, pero los saltos mayores y versiones milestone/alpha deben gestionarse de forma explícita para evitar actualizaciones automáticas arriesgadas.

Recomendación:

- Actualizar primero parches/menores estables: Lombok y plugins Maven estables.
- Evaluar JUnit 6 en rama separada, porque es salto mayor.
- No adoptar `assertj-core 4.0.0-M1` ni `slf4j-api 2.1.0-alpha1` salvo necesidad concreta.

### H-007 - Plugins Maven con actualizaciones disponibles

Severidad: Baja  
Categoría: Build  
Estado: En curso  
Hito asociado: M-06

Evidencia de `versions:display-plugin-updates`:

- `spotbugs-maven-plugin`: `4.9.8.2 -> 4.10.3.0`.
- `maven-compiler-plugin`: `3.14.1 -> 3.15.0`.
- `maven-dependency-plugin`: `3.9.0 -> 3.11.0`.
- `maven-enforcer-plugin`: `3.6.2 -> 3.6.3`.
- `maven-jar-plugin`: `3.5.0 -> 3.5.1`.
- `maven-surefire-plugin`: `3.5.4 -> 3.6.0-M1`.

Impacto:

El build actual funciona. La mejora es de mantenimiento, no de urgencia. Conviene evitar milestones salvo que resuelvan un problema concreto.

Recomendación:

Actualizar plugins estables en una rama y ejecutar `clean verify`, `dependency:analyze` y `spotbugs:check`.

### H-008 - CI de seguridad usa una acción apuntando a `@main`

Severidad: Media  
Categoría: CI/CD / reproducibilidad  
Estado: Hecho  
Hito asociado: M-07

Evidencia:

- `.github/workflows/maven-ci.yml` usa `dependency-check/Dependency-Check_Action@main`.

Impacto:

Usar `@main` hace que el comportamiento de CI pueda cambiar sin cambios en el repositorio. Esto reduce reproducibilidad y puede romper la pipeline por cambios externos.

Recomendación:

Fijar la acción a una versión/tag estable o a un SHA. Si se fija a SHA, documentar el proceso de actualización.

### H-009 - Dependabot diario puede generar ruido para un proyecto pequeño

Severidad: Baja  
Categoría: Mantenimiento  
Estado: Pendiente  
Hito asociado: M-08

Evidencia:

- `.github/dependabot.yml` configura actualizaciones Maven con `interval: daily`.
- Existen ramas remotas Dependabot para varias actualizaciones.

Impacto:

Para una librería pequeña, el ritmo diario puede producir demasiados PRs y diluir los cambios importantes.

Recomendación:

Valorar frecuencia semanal, agrupar actualizaciones Maven y separar majors de minors/patches.

### H-010 - La demo contiene flujo redundante de finalización

Severidad: Baja  
Categoría: Limpieza / demo  
Estado: Pendiente  
Hito asociado: M-02

Evidencia:

- `VersionDemo` llama a `FinalDelProgramaHelper.finalizar(...)` dentro de ambos branches y después vuelve a llamar a finalizar en el flujo principal.
- En la práctica, `System.exit` impide llegar a la llamada final, pero el código queda engañoso.

Impacto:

No afecta al build ni a la librería principal, pero reduce claridad y dificulta convertir la demo en prueba o CLI.

Recomendación:

Simplificar el flujo de la demo o eliminar la demo si no forma parte del producto.

## 7. Aspectos ya corregidos respecto a la auditoría anterior

La auditoría histórica de `doc/auditoria/2026_05_09/` indicaba varios problemas que ya no aplican:

- CI ya usa JDK 21.
- Maven Wrapper ya existe.
- `VersionImpl` ya usa constantes centralizadas de `Mensajes`.
- Ya hay tests unitarios reales para `VersionImpl`.
- `logback-classic` está en scope `test`, no como dependencia runtime.
- SpotBugs ya no referencia filtros inexistentes y `spotbugs:check` pasa.
- README ya referencia `App-Version`, `version-helper` y versión `6.0.0`.

## 8. Matriz de hitos

| Hito | Objetivo | Hallazgos | Prioridad | Estado | Criterio de cierre | Verificación mínima |
|---|---|---:|---|---|---|---|
| M-01 | Definir contrato de API para ausencia/error de versión | H-001 | Alta | Pendiente | API actual documentada o API tipada nueva añadida sin romper compatibilidad | `mvnw -B clean verify` |
| M-02 | Sacar o aislar lógica `System.exit` del artefacto core | H-002, H-010 | Alta | Pendiente | `src/main` no expone helpers que terminen la JVM, o decisión documentada | `mvnw -B clean verify` |
| M-03 | Verificar automáticamente el manifiesto del JAR real | H-003 | Alta | Pendiente | Fase `verify` falla si faltan `App-Version` o `App-Name` | `mvnw -B clean verify` |
| M-04 | Resolver annotation processing implícito | H-004 | Media | Hecho | Build sin aviso de annotation processing implícito | `mvnw -B clean verify` |
| M-05 | Normalizar Checkstyle | H-005 | Media | Pendiente | Checkstyle configurado y activo, o propiedades eliminadas | `mvnw -B clean verify` |
| M-06 | Actualizar dependencias/plugins de forma controlada | H-006, H-007 | Media | En curso | Actualizaciones estables aplicadas o descartadas con motivo | `mvnw -B clean verify`; `mvnw -B dependency:analyze`; `mvnw -B spotbugs:check` |
| M-07 | Fijar acción OWASP Dependency Check | H-008 | Media | Hecho | Workflow usa tag/SHA estable, no `@main` | `mvnw -B clean verify`; pendiente CI remoto tras PR |
| M-08 | Ajustar estrategia Dependabot | H-009 | Baja | Pendiente | Frecuencia/grupos configurados según política decidida | PR Dependabot generado correctamente |

## 9. Plan de actuación recomendado

### Fase 1 - Endurecimiento sin romper API

1. Añadir test/validación del manifiesto real del JAR.
2. Configurar annotation processing explícito o eliminar Lombok.
3. Fijar `dependency-check` a versión/tag estable.
4. Eliminar propiedades Checkstyle no usadas o activar Checkstyle mínimo.

### Fase 2 - Limpieza de diseño

1. Mover `VersionDemo` y `FinalDelProgramaHelper` fuera del artefacto core si no son parte de la API.
2. Simplificar `VersionDemo` si se conserva.
3. Documentar explícitamente qué clases forman parte de la API pública soportada.

### Fase 3 - Evolución de contrato

1. Diseñar `VersionResult` u otra API tipada.
2. Mantener `getVersion(Class<?>)` como compatibilidad durante una versión.
3. Documentar migración en README.
4. Considerar release mayor si se elimina el contrato antiguo.

### Fase 4 - Mantenimiento periódico

1. Revisar Dependabot.
2. Aplicar actualizaciones estables de plugins.
3. Evaluar saltos mayores de JUnit/AssertJ en rama separada.
4. Reejecutar auditoría viva después de cada release.

## 10. Comandos de verificación

Usar este bloque antes de cerrar cualquier hito:

```powershell
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B dependency:analyze
.\mvnw.cmd -B spotbugs:check
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
git status --short
```

Para revisar trazabilidad de manifiesto:

```powershell
jar tf target\version-helper-6.0.0.jar
jar xf target\version-helper-6.0.0.jar META-INF\MANIFEST.MF
```

Si se automatiza esta comprobación, no debe depender de extracción manual.

## 11. Bitácora de seguimiento

| Fecha | Cambio | Hitos afectados | Verificación | Referencia |
|---|---|---|---|---|
| 2026-08-12 | Apertura de auditoría viva y snapshot del estado actual | Todos | `test`, `clean verify`, `dependency:analyze`, `dependency:tree`, `spotbugs:check`, `versions:*` | Documento inicial |

## 12. Decisiones pendientes

| ID | Decisión | Opciones | Recomendación |
|---|---|---|---|
| D-01 | Contrato de API para ausencia/error | Mantener strings; añadir `Optional`; añadir `VersionResult`; lanzar excepciones | Añadir API tipada sin romper `getVersion` todavía |
| D-02 | Lombok | Mantener con annotation processing explícito; eliminar | Eliminar si solo aporta `@Slf4j` |
| D-03 | Checkstyle | Activar reglas; eliminar propiedades | Activar reglas mínimas si se quiere disciplina de estilo; si no, eliminar propiedades |
| D-04 | Demo/CLI | Mantener en test; crear módulo CLI; eliminar | Sacar del artefacto core |
| D-05 | Dependabot | Diario; semanal; grupos | Semanal con agrupación de minors/patches |

## 13. Riesgo residual actual

Riesgo global: Medio-bajo.

Motivo:

- El build está verde y los análisis básicos pasan.
- La superficie funcional es pequeña.
- Los riesgos principales están en diseño de API, reproducibilidad de CI y limpieza de empaquetado, no en fallos detectados de ejecución.

El proyecto puede seguir publicándose, pero antes de añadir funcionalidad nueva conviene cerrar M-02 y M-03. Antes de romper o rediseñar la API pública, cerrar D-01 con una decisión explícita.

## 14. Seguimiento de mejoras aplicadas en `jarp/auditoria-release-prep`

Fecha de actualización: 2026-08-12  
Commit local: `d24d3ea Preparar release 6.0.1 tras auditoria`  
Release preparada: `6.0.1`

### Mejoras aplicadas

- `M-04 / H-004`: Hecho. Se configuró `annotationProcessorPaths` para Lombok en `maven-compiler-plugin`; el build ya no depende del annotation processing implícito.
- `M-06 / H-006`: En curso. Se actualizó Lombok de `1.18.42` a `1.18.46`. Quedan pendientes o descartadas temporalmente las actualizaciones de Logback, JUnit, AssertJ y SLF4J según riesgo.
- `M-06 / H-007`: En curso. Se actualizaron plugins Maven estables: Compiler, Dependency, Enforcer, Jar y SpotBugs. Se deja pendiente Surefire `3.6.0-M1` por ser milestone.
- `M-07 / H-008`: Hecho localmente. OWASP Dependency Check Action queda fijada a `dependency-check/Dependency-Check_Action@1.1.0` en lugar de `@main`. Queda pendiente confirmación de CI remoto cuando se abra PR.
- Toolchain: Maven Wrapper actualizado de `3.9.15` a `3.9.16`.
- Release: versión del artefacto subida de `6.0.0` a `6.0.1` para evitar republicar el mismo paquete.

### Verificación ejecutada tras las mejoras

```powershell
.\mvnw.cmd -v
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B dependency:analyze
.\mvnw.cmd -B spotbugs:check
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
```

Resultados:

- Maven Wrapper: Apache Maven `3.9.16`.
- Build: correcto para `version-helper 6.0.1`.
- Tests: 5 ejecutados, 0 fallos, 0 errores.
- `dependency:analyze`: sin problemas.
- `spotbugs:check`: 0 bugs, 0 errores.
- Manifiesto del JAR `target/version-helper-6.0.1.jar` verificado con:
  - `App-Name: version-helper`.
  - `App-Version: 6.0.1`.

### Actualizaciones no aplicadas en esta rama

- `ch.qos.logback:logback-classic 1.5.26 -> 1.6.2`: pendiente de evaluación separada.
- `org.junit.jupiter 5.8.2 -> 6.1.3`: pendiente de rama separada por salto mayor.
- `org.assertj:assertj-core 3.21.0 -> 4.0.0-M1`: descartada temporalmente por ser milestone.
- `org.slf4j:slf4j-api 2.0.17 -> 2.1.0-alpha1`: descartada temporalmente por ser alpha.
- `maven-surefire-plugin 3.5.4 -> 3.6.0-M1`: descartada temporalmente por ser milestone.

### Regla operativa incorporada

Cada mejora derivada de esta auditoría debe actualizar este documento principal de auditoría viva en el mismo commit o PR que aplica la mejora. Los anexos pueden complementar el detalle, pero no sustituyen la actualización de estado en este documento.