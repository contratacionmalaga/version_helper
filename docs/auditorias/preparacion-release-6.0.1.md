# Preparación de release `6.0.1`

Fecha: 2026-08-12  
Rama: `jarp/auditoria-release-prep`  
Estado: `Preparada localmente, pendiente de commit/push/PR`

## Cambios aplicados

- Subida de versión del artefacto de `6.0.0` a `6.0.1`.
- Actualización de Maven Wrapper de `3.9.15` a `3.9.16`.
- Actualización de Lombok de `1.18.42` a `1.18.46`.
- Actualización de plugins Maven estables:
  - `maven-compiler-plugin`: `3.14.1 -> 3.15.0`.
  - `maven-dependency-plugin`: `3.9.0 -> 3.11.0`.
  - `maven-enforcer-plugin`: `3.6.2 -> 3.6.3`.
  - `maven-jar-plugin`: `3.5.0 -> 3.5.1`.
  - `spotbugs-maven-plugin`: `4.9.8.2 -> 4.10.3.0`.
- Configuración explícita de `annotationProcessorPaths` para Lombok.
- Fijado de OWASP Dependency Check Action de `@main` a `@1.1.0`.
- Actualización de README para publicar `v6.0.1`.

## Cambios no aplicados deliberadamente

- No se actualiza Java baseline: se mantiene `maven.compiler.release=21`.
- No se actualiza a Java 25/26.
- No se actualiza `maven-surefire-plugin` a `3.6.0-M1` por ser milestone.
- No se actualiza `assertj-core` a `4.0.0-M1` por ser milestone.
- No se actualiza `slf4j-api` a `2.1.0-alpha1` por ser alpha.
- No se actualiza JUnit a `6.1.3` en esta rama por ser salto mayor.
- No se actualiza Logback a `1.6.2` en esta rama; queda para evaluación separada.

## Verificación ejecutada

```powershell
.\mvnw.cmd -v
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B dependency:analyze
.\mvnw.cmd -B spotbugs:check
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
```

Resultados:

- Maven Wrapper ejecuta Apache Maven `3.9.16`.
- Build correcto para `version-helper 6.0.1`.
- Tests: 5 ejecutados, 0 fallos, 0 errores.
- `dependency:analyze`: sin problemas de dependencias.
- `spotbugs:check`: 0 bugs, 0 errores.
- JAR generado: `target/version-helper-6.0.1.jar`.
- Manifiesto verificado:
  - `App-Name: version-helper`.
  - `App-Version: 6.0.1`.

## Actualizaciones pendientes tras esta rama

- `ch.qos.logback:logback-classic`: `1.5.26 -> 1.6.2`, evaluar aparte.
- `org.junit.jupiter`: `5.8.2 -> 6.1.3`, evaluar aparte por salto mayor.
- `org.assertj:assertj-core`: `3.21.0 -> 4.0.0-M1`, no aplicar hasta versión estable.
- `org.slf4j:slf4j-api`: `2.0.17 -> 2.1.0-alpha1`, no aplicar hasta versión estable.
- `maven-surefire-plugin`: `3.5.4 -> 3.6.0-M1`, no aplicar hasta versión estable.

## Siguiente paso operativo

1. Revisar diff.
2. Stage explícito de los archivos de esta rama.
3. Commit.
4. Push de `jarp/auditoria-release-prep`.
5. Crear PR contra `main`.
6. Tras merge, publicar release GitHub con tag `v6.0.1`.
