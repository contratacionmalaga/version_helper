# Encryptor

Servicio Java para la obtención de la versión de un fichero JAR.

---

## Características principales

- Obtención de la versión de un fichero JAR.
- Necesita tener configurado el plugin `maven-assembly-plugin` tal y como se indica a continuación para 
incluir la versión dentro del fichero MANIFEST.MF en el JAR.
- Excepciones personalizadas para errores de cifrado (`VersionFromManifestException`).
- Logging con [SLF4J](https://www.slf4j.org/) para seguimiento y auditoría.
- Test unitarios con JUnit 5 para garantizar la calidad del código.
- Test con Mockito.
- Fácil integración en otros proyectos como dependencia Maven.
- Preparado para publicación en GitHub Packages.

---

## Instalación y uso

Incluir como dependencia en el módulo que se quiera utilizar, 
configurar el plugin `maven-assembly-plugin` tay como se indica a continuación

## Configuración del plugin `maven-assembly-plugin`

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.7.1</version>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <mainClass>local.jarios.VersionFromManifestDemo</mainClass>
                <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Ejemplo de uso

```
log.info(INICIO);

try {

    VersionFromManifestService versionService = new VersionFromManifestServiceImpl();
    log.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

    String version = versionService.getVersion(VersionFromManifestDemo.class);
    log.info("Versión: {}", version);

} catch (VersionFromManifestException e) {

    log.error("Error al obtener la versión del fichero.");
    throw e; // <- Repropagar al consumidor del módulo

} finally {

    log.info(FINAL);
}
```