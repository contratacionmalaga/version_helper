# Información

Servicio Java para obtener la versión de un fichero JAR consultando el `MANIFEST.MF`.

---

## Descripción

La clase `VersionImpl` implementa el servicio para extraer la versión (`App-Version`)
definida en el archivo `MANIFEST.MF` del JAR que contiene una clase determinada.

Este servicio es útil para obtener de forma programática la versión del artefacto en tiempo de ejecución, 
especialmente cuando el código está empaquetado en un JAR.

---

## Características principales

- Obtiene la versión del atributo `App-Version` del `MANIFEST.MF`.
- Maneja correctamente la ejecución en entorno de desarrollo (cuando no se ejecuta desde JAR).
- Captura y lanza una excepción personalizada en caso de error leyendo el `MANIFEST.MF`.
- Utiliza SLF4J para logging de diagnóstico.

---

## Uso

### Implementación de la interfaz `Version`

```
Version versionService = new VersionImpl();
String version = versionService.getVersion(TuClase.class);
System.out.println("Versión obtenida: " + version);
```

Donde TuClase.class es una clase que se encuentra dentro del JAR cuyo manifiesto quieres consultar.

## Instalación y uso (maven)

### Generar el paquete JAR

Para generar el paquete JAR con Maven, ejecuta en la raíz del proyecto:

```bash
mvn clean package
```

Esto generará el archivo JAR en el directorio target/.

### Instalar el paquete en el repositorio local de Maven

Para poder usar este artefacto en otros proyectos Maven, instálalo en tu repositorio local con:

```bash
mvn install
```

Esto copiará el JAR generado y el POM al repositorio local (~/.m2/repository), 
haciéndolo disponible para otros proyectos en la máquina local.
Incluir como dependencia en el módulo que se quiera utilizar,

### Publicación de releases

Cuando se publica una release en GitHub, el workflow `Publish Release Package`:

- compila y ejecuta las pruebas con JDK 21;
- publica el artefacto Maven en GitHub Packages;
- adjunta a la release los JAR generados (`jar`, `sources` y `javadoc`).

El workflow usa `GITHUB_TOKEN` y el repositorio configurado en `distributionManagement`.

### Usar el paquete en otro proyecto

Agrega la dependencia en el pom.xml del proyecto consumidor:

```
<dependency>
  <groupId>local.jarios</groupId>
  <artifactId>version-helper</artifactId>
  <version>5.3.0</version>
</dependency>
```

## Dependencias

- Lombok (para @Slf4j y logging)
- SLF4J para logging

## Ejemplo de uso

```
public class Demo {
    public static void main(String[] args) {
        Version versionService = new VersionImpl();
        String version = versionService.getVersion(Demo.class);
        System.out.println("Versión del JAR: " + version);
    }
}
```

## Manejo de errores

Retorna mensajes descriptivos cuando:

- No se encuentra recurso de clase.
- No se ejecuta desde un JAR (modo desarrollo).
- No se encuentra el manifiesto.
- No existe el atributo `App-Version`.

## Gestión de Excepciones

Lanza VersionException si ocurre un error de I/O al leer el manifiesto.
