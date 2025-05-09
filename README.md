# Chat Cliente-Servidor con Soporte Multicliente

Este proyecto implementa un sistema de chat cliente-servidor en Java que permite múltiples conexiones simultáneas de clientes al servidor.

## Características

- Servidor que acepta múltiples conexiones de clientes
- Gestión de conexiones mediante hilos
- Palabras clave personalizadas para cerrar chats
- Cierre automático del servidor cuando no hay clientes conectados
- Reutilización de slots de conexión

## Estructura del Proyecto

```
.
├── src/
│   ├── Server.java
│   └── Client.java
└── README.md
```

## Compilación

Para compilar el proyecto, ejecuta los siguientes comandos:

```bash
# Compilar el servidor y el cliente
javac src/Server.java src/Client.java
```

## Ejecución

### Servidor

Para iniciar el servidor, usa el siguiente comando:

```bash
java -cp src Server <puerto> <palabra_clave_servidor> <max_clientes>
```

Ejemplo:
```bash
java -cp src Server 1234 Cleopatra 5
```

### Cliente

Para iniciar un cliente, usa el siguiente comando:

```bash
java -cp src Client <host> <puerto> <palabra_clave_cliente>
```

Ejemplo:
```bash
java -cp src Client localhost 1234 "Marc Antoni"
```

## Funcionamiento

1. El servidor inicia y espera conexiones de clientes
2. Cada cliente se conecta al servidor y envía su palabra clave
3. Los clientes pueden enviar mensajes al servidor
4. El servidor responde a cada cliente
5. Para cerrar un chat:
   - Si un cliente usa su palabra clave, solo se cierra su chat
   - Si el servidor usa la palabra clave de un cliente, se cierra ese chat
   - Si el servidor usa su palabra clave, se cierran todos los chats
6. El servidor se cierra automáticamente cuando no hay clientes conectados

## Notas

- El número máximo de clientes debe especificarse al iniciar el servidor
- Cada cliente debe tener su propia palabra clave
- Los mensajes se muestran en la consola con el formato especificado
- El servidor responde a los clientes en el orden en que recibe los mensajes

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
