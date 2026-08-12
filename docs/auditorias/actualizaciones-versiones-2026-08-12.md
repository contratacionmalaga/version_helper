# Anexo vivo: revisión de actualizaciones de versiones

Fecha de apertura: 2026-08-12  
Última actualización: 2026-08-12  
Proyecto: `version-helper`  
Estado: `Pendiente de aplicar`

Este anexo complementa `auditoria-viva-2026-08-12.md` y debe actualizarse cada vez que se apliquen, descarten o vuelvan a revisar actualizaciones de Java, Maven, dependencias o plugins.

## 1. Evidencia local

Comandos ejecutados:

```powershell
.\mvnw.cmd -v
Get-Content .mvn\wrapper\maven-wrapper.properties
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
```

Resultado:

- Apache Maven usado por Wrapper: `3.9.15`.
- Java usado por Maven: `21.0.9`, vendor `Oracle Corporation`.
- Maven Wrapper Plugin configurado: `3.3.4`.
- Distribución en `.mvn/wrapper/maven-wrapper.properties`: `apache-maven-3.9.15-bin.zip`.

Fuentes oficiales consultadas el 2026-08-12:

- Apache Maven Releases History: `https://maven.apache.org/docs/history.html`.
- Apache Maven Wrapper Download: `https://maven.apache.org/tools/wrapper/download.cgi`.
- Oracle Java SE overview: `https://www.oracle.com/java/technologies/java-se-glance.html`.

## 2. Maven y Maven Wrapper

| Componente | Versión actual | Última versión estable observada | Estado | Acción recomendada |
|---|---:|---:|---|---|
| Apache Maven usado por Wrapper | `3.9.15` | `3.9.16` | Actualizable | Actualizar Wrapper a Maven `3.9.16`. |
| Maven Wrapper Plugin | `3.3.4` | `3.3.4` | Actualizado | Sin acción. |
| Maven 4 | No usado | `4.0.0-rc-6` | Pre-release | No migrar todavía; probar solo en rama experimental. |

Conclusión:

- Hay actualización menor recomendable: Maven `3.9.15 -> 3.9.16`.
- No conviene migrar a Maven 4 en producción porque sigue en release candidate.

## 3. Java

| Componente | Versión actual | Última versión observada | Estado | Acción recomendada |
|---|---:|---:|---|---|
| JDK local usado por Maven | `21.0.9` | Java SE `21.0.12` para línea 21 | Actualizable | Actualizar al último parche Java 21 disponible. |
| Baseline del proyecto | `maven.compiler.release=21` | Java 25 LTS posterior; Java 26 release actual | Correcto | Mantener Java 21 como baseline salvo decisión explícita. |

Conclusión:

- Actualizar el parche de Java 21 es recomendable por mantenimiento y seguridad.
- No cambiar el baseline de compilación a Java 25/26 sin validar consumidores.
- Java 25 LTS puede evaluarse en rama separada, pero no es una mejora automática para esta librería.

## 4. Dependencias Maven

| Dependencia | Actual | Disponible | Ámbito | Riesgo | Acción recomendada |
|---|---:|---:|---|---|---|
| `ch.qos.logback:logback-classic` | `1.5.26` | `1.6.2` | Test | Bajo-medio | Evaluar en rama y ejecutar tests. |
| `org.assertj:assertj-core` | `3.21.0` | `4.0.0-M1` | Test | Medio | No actualizar por ahora; es milestone. |
| `org.junit.jupiter:junit-jupiter-api` | `5.8.2` | `6.1.3` | Test | Medio | Evaluar en rama separada; salto mayor. |
| `org.junit.jupiter:junit-jupiter-engine` | `5.8.2` | `6.1.3` | Test | Medio | Evaluar en rama separada; salto mayor. |
| `org.projectlombok:lombok` | `1.18.42` | `1.18.46` | Provided | Bajo | Actualizar. |
| `org.slf4j:slf4j-api` | `2.0.17` | `2.1.0-alpha1` | Runtime | Medio | No actualizar por ahora; es alpha. |

## 5. Plugins Maven

| Plugin | Actual | Disponible | Tipo | Acción recomendada |
|---|---:|---:|---|---|
| `com.github.spotbugs:spotbugs-maven-plugin` | `4.9.8.2` | `4.10.3.0` | Estable | Actualizar. |
| `maven-compiler-plugin` | `3.14.1` | `3.15.0` | Estable | Actualizar. |
| `maven-dependency-plugin` | `3.9.0` | `3.11.0` | Estable | Actualizar. |
| `maven-enforcer-plugin` | `3.6.2` | `3.6.3` | Estable | Actualizar. |
| `maven-jar-plugin` | `3.5.0` | `3.5.1` | Estable | Actualizar. |
| `maven-surefire-plugin` | `3.5.4` | `3.6.0-M1` | Milestone | No actualizar salvo necesidad concreta. |

## 6. Orden recomendado

1. Actualizar Maven Wrapper de `3.9.15` a `3.9.16`.
2. Actualizar JDK local/CI de `21.0.9` a `21.0.12` o equivalente del proveedor.
3. Actualizar plugins Maven estables: SpotBugs, Compiler, Dependency, Enforcer y Jar.
4. Actualizar Lombok `1.18.42 -> 1.18.46`.
5. Evaluar Logback `1.5.26 -> 1.6.2`.
6. Evaluar JUnit `5.8.2 -> 6.1.3` en rama separada.
7. No aplicar por ahora versiones milestone/alpha: AssertJ `4.0.0-M1`, SLF4J `2.1.0-alpha1`, Surefire `3.6.0-M1`.

## 7. Criterio de cierre

Para marcar este anexo como actualizado/cerrado:

```powershell
.\mvnw.cmd -v
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B dependency:analyze
.\mvnw.cmd -B spotbugs:check
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
```

Además:

- Si se actualiza Java en CI, comprobar el workflow en GitHub.
- Si se actualiza Maven Wrapper, comprobar que `.mvn/wrapper/maven-wrapper.properties` apunta a la versión prevista.
- Si se descarta una actualización, documentar el motivo en la bitácora.

## 8. Bitácora

| Fecha | Cambio | Estado | Verificación |
|---|---|---|---|
| 2026-08-12 | Revisión inicial de actualizaciones de Java, Maven, dependencias y plugins | Pendiente de aplicar | `mvnw -v`, `versions:display-dependency-updates`, `versions:display-plugin-updates` |
