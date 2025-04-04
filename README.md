# Chat Cliente-Servidor en Java

Este es un programa simple de chat implementado en Java que utiliza sockets para la comunicación entre un **servidor** y un **cliente**. El servidor y el cliente pueden enviarse mensajes entre sí de manera bidireccional. El programa finaliza cuando cualquiera de las partes envía la palabra clave de cierre.

## Requisitos

- **Java 8 o superior** debe estar instalado en tu sistema.
- La máquina debe poder ejecutar aplicaciones cliente-servidor usando sockets de red locales (localhost).

## Descripción del proyecto

- El **servidor** se ejecuta en un puerto configurado en el código (por defecto, `1234`) y está esperando a que el **cliente** se conecte.
- El **cliente** puede conectarse al servidor, enviar mensajes e interactuar con él de manera bidireccional.
- El **servidor** también puede enviar mensajes al cliente y ambos pueden intercambiar mensajes en tiempo real.
- La **conversación se cierra** cuando cualquiera de las partes envía una palabra clave (por defecto, `cerrar`).

## Funcionamiento

### 1. El servidor:

El servidor escucha en un puerto específico (por defecto, `1234`) y acepta la conexión del cliente. El servidor lee los mensajes enviados por el cliente y responde con su propio mensaje, que es escrito en la consola del servidor.

### 2. El cliente:

El cliente se conecta al servidor usando la dirección `localhost` y el puerto `1234`. Una vez conectado, el cliente puede escribir mensajes que serán enviados al servidor. El cliente también recibe las respuestas del servidor y puede seguir la conversación.

### 3. Cierre de la conversación:

La conversación se cierra cuando cualquiera de las partes (cliente o servidor) escribe la palabra clave configurada, que es `cerrar` por defecto.

## Instrucciones de uso

### Paso 1: Compilar los programas

Abre una terminal y navega a la carpeta donde se encuentran los archivos `Server.java` y `Client.java`. Compila ambos programas con los siguientes comandos:

```bash
javac Server.java
javac Client.java
