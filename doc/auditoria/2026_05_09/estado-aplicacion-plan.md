# Estado de aplicacion del plan

Fecha: 2026-05-09  
Plan base: `doc/auditoria/2026_05_09/plan-actuacion.md`

## Aplicado

- CI actualizado a JDK 21.
- Acciones de GitHub actualizadas a `actions/checkout@v4` y `actions/setup-java@v4`.
- Cache Maven simplificada mediante `setup-java`.
- Maven configurado con `maven.compiler.release=21`.
- Maven Surefire fijado para ejecutar JUnit 5 de forma explicita.
- SpotBugs corregido eliminando referencias a filtros inexistentes.
- `logback-classic` movido a scope `test`.
- Mensajes de error centralizados en `Mensajes`.
- Eliminado `@Slf4j` innecesario de `TipoFinalEjecucion`.
- Corregidos README y Javadocs obsoletos.
- Anadidas pruebas unitarias reales para `VersionImpl`.
- Corregido cierre de `JarFile` y desactivada cache de `JarURLConnection`.
- Anadido Maven Wrapper.
- Configurado `maven-dependency-plugin` para ignorar dependencias de test usadas indirectamente.
- Anadido workflow `Publish Release Package` para publicar el paquete Maven en GitHub Packages y adjuntar los JAR a cada release publicada.

## Validaciones ejecutadas

Todas las validaciones se ejecutaron con JDK 21 (`C:\java\software\jdk-21.0.9`) y Maven Wrapper.

```bash
.\mvnw.cmd clean verify
.\mvnw.cmd compile spotbugs:check
.\mvnw.cmd dependency:analyze
```

Resultado:

- `clean verify`: correcto.
- `compile spotbugs:check`: correcto, sin bugs.
- `dependency:analyze`: correcto, sin problemas de dependencias.

Tambien se inspecciono el manifiesto del JAR generado:

```text
Java-Version: 21
Build-Jdk-Spec: 21
Class-Path: slf4j-api-2.0.17.jar
App-Name: version-helper
App-Version: 5.3.0
```

## Decision conservadora

`FinalDelProgramaHelper` permanece en `src/main` para evitar una ruptura de API. El plan recomendaba revisarlo por el uso de `System.exit`; se ha revisado, pero retirarlo o moverlo a test/demo debe hacerse en una version mayor o tras confirmar que ningun consumidor externo lo usa.

## Pendiente recomendado

- Decidir si `FinalDelProgramaHelper` debe marcarse como obsoleto y retirarse en una version mayor.
- Valorar un contrato tipado para `Version` (`Optional<String>` o `VersionResult`) en una version mayor, ya que cambiarlo ahora romperia compatibilidad.
