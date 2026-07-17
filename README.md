<div align="center">

# 💬 AlphaEci — Microservicio de Chat por Parche

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge&logo=socketdotio&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-CloudAMQP-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Azure](https://img.shields.io/badge/Azure-Container_Apps-0078D4?style=for-the-badge&logo=microsoftazure&logoColor=white)
![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=for-the-badge)

> 💡 **PATRICI.A** es un proyecto académico de la Escuela Colombiana de Ingeniería Julio Garavito, construido con arquitectura de microservicios orientada a producción.

</div>

---

## 📑 Tabla de Contenidos

1. [👤 Integrantes](#1--integrantes)
2. [⚙️ Tecnologías Utilizadas](#2-️-tecnologías-utilizadas)
3. [🎯 Descripción del Módulo](#3--descripción-del-módulo)
4. [🏗️ Cómo Funciona el Módulo](#4-️-cómo-funciona-el-módulo)
5. [📊 Diagramas](#5--diagramas)
6. [🧩 Funcionalidades](#6--funcionalidades)
7. [🧪 Evidencia de Pruebas Unitarias](#7--evidencia-de-pruebas-unitarias)
8. [📈 Evidencia de Cobertura](#8--evidencia-del-análisis-de-cobertura)
9. [🚀 Cómo Ejecutar el Proyecto](#9--cómo-ejecutar-el-proyecto)
10. [🔄 Evidencia CI/CD](#10--evidencia-del-despliegue-cicd)
11. [🌐 Link Expuesto en Azure con Swagger](#11--link-expuesto-en-azure-con-swagger)
12. [🗂️ Organización del Código](#12-️-organización-del-código)
13. [📝 Código Documentado](#13--código-documentado)
14. [🔗 Conexiones con Servicios Externos](#14--conexiones-con-servicios-externos)
15. [⚙️ Pipeline de Desarrollo](#15-️-pipeline-de-desarrollo)
16. [🚢 Pipeline de PROD](#16--pipeline-de-prod)

---

## 1. 👤 Integrantes

| Nombre | Correo |
|---|---|
| _(pendiente)_ | _(pendiente)_ |

El equipo aplicó la metodología **Scrum** con sprints semanales, usando **Jira** para seguimiento de tareas y **GitHub Projects** como tablero de trabajo.

---

## 2. ⚙️ Tecnologías Utilizadas

| Tecnología | Versión | Justificación |
|---|---|---|
| **Java** | 21 | Soporte para Virtual Threads: alto número de conexiones WebSocket concurrentes con bajo consumo de recursos. Tipado estático y ecosistema maduro con Spring Boot 3. |
| **Spring Boot** | 3.3.0 | WebSocket STOMP nativo, soporte integrado de MongoDB y RabbitMQ, reduciendo significativamente la configuración necesaria. |
| **Spring Security** | Incluido en Boot 3.3 | Configuración stateless; la autenticación se delega al API Gateway mediante el header `X-User-Id`, evitando duplicar validación de tokens en el servicio. |
| **Spring WebSocket + STOMP** | Incluido en Boot 3.3 | Comunicación bidireccional persistente para la mensajería en tiempo real dentro del parche, con SockJS como respaldo. |
| **Spring AMQP** | Incluido en Boot 3.3 | Abstracción sobre RabbitMQ para publicar eventos de dominio (mensajes enviados) hacia el microservicio de Notificaciones. |
| **Spring Data MongoDB** | Incluido en Boot 3.3 | Repositorios simplificados para el esquema flexible de mensajes y salas, evitando migraciones rígidas. |
| **Jakarta Bean Validation** | Incluido en Boot 3.3 | Validación declarativa sobre DTOs REST (longitud de mensajes, campos obligatorios). |
| **Lombok** | Última estable | Reducción de boilerplate con `@Builder`, `@Getter`, `@RequiredArgsConstructor`. |
| **MapStruct** | 1.5.5.Final | Mapeo entre dominio, DTOs y documentos de persistencia generado en tiempo de compilación. |
| **SpringDoc OpenAPI** | 2.5.0 | Documentación Swagger UI automática de los endpoints REST. |
| **JaCoCo** | 0.8.10 | Cobertura mínima **85% global** y **95% en casos de uso**, verificada en el pipeline de CI. |
| **JUnit 5 + Mockito** | Incluido en Boot 3.3 | Pruebas unitarias de los casos de uso mediante mocking de puertos, sin MongoDB ni RabbitMQ reales. |
| **Apache Maven** | 3.9 | Gestión de dependencias y construcción del proyecto. |
| **MongoDB Atlas** | Cloud | Base de datos NoSQL administrada: colecciones `chat_rooms`, `messages`, `message_reports`. |
| **RabbitMQ (CloudAMQP)** | Cloud | Broker de mensajería asíncrona gestionado con conexión cifrada SSL (puerto 5671). |
| **Docker** | Última estable | Build multi-stage e imagen consistente entre desarrollo, pruebas y producción. |
| **GitHub Actions** | — | Pipelines de CI, CD QA y CD Prod. |
| **GHCR** | — | Registro de imágenes de contenedor (`ghcr.io/cybersapienseci/chat-service`). |
| **Azure Container Apps** | — | Plataforma de despliegue en la nube del microservicio. |
| **IntelliJ IDEA** | — | IDE principal con soporte avanzado para Spring Boot. |
| **Draw.io** | — | Elaboración de los diagramas de arquitectura. |
| **Git / GitHub** | — | Control de versiones bajo estrategia Git Flow. |

---

## 3. 🎯 Descripción del Módulo

El microservicio de **Chat** se encarga de la **mensajería en tiempo real dentro de los parches** de PATRICI.A. Implementa arquitectura hexagonal (puertos y adaptadores) sobre Spring Boot 3 con Java 21, persiste mensajes, salas y reportes en **MongoDB**, distribuye los mensajes vía **WebSocket STOMP** y publica eventos de dominio hacia el microservicio de Notificaciones mediante **RabbitMQ**.

Además de la mensajería, el módulo gestiona el **ciclo de vida de las conexiones entre usuarios**: una solicitud crea una sala en estado `PENDING`, que pasa a `ACTIVE` o `REJECTED` según la respuesta del destinatario. También ofrece **moderación** de contenido: cualquier miembro puede reportar un mensaje inapropiado, y el reporte queda registrado con estado `PENDING`, `REVIEWED` o `DISMISSED`.

### Funcionalidades Principales

| Funcionalidad | Descripción |
|---|---|
| **Mensajería en Tiempo Real** | Envío por STOMP a `/app/chat/{chatRoomId}/send` y difusión a los miembros por el topic `/topic/parche/{chatRoomId}/messages`. |
| **Historial Paginado** | Consulta REST del historial de una sala, con validación de membresía. |
| **Mensajes de Texto e Imagen** | `MessageType.TEXT` exige `content`; `MessageType.IMAGE` exige `mediaUrl`. |
| **Gestión de Conexiones** | Solicitar, aceptar o rechazar conexión, y listar las salas del usuario. |
| **Moderación de Mensajes** | Reporte de mensajes inapropiados con motivo y estado de revisión. |
| **Publicación de Eventos** | Cada mensaje enviado publica un evento sobre `notification.exchange` con routing key `chat.message`. |
| **Control de Membresía** | Enviar y leer mensajes exige pertenecer a la sala; en caso contrario `403 Forbidden`. |

---

## 4. 🏗️ Cómo Funciona el Módulo

### Flujo de un Mensaje

1. El cliente envía por STOMP a `/app/chat/{chatRoomId}/send` con el header `X-User-Id`.
2. `ChatWebSocketHandler` delega en `SendMessageUseCase`.
3. El caso de uso carga la sala, valida la membresía y valida el tipo (`TEXT` → `content`; `IMAGE` → `mediaUrl`).
4. El mensaje se persiste en MongoDB.
5. Se publica el evento `ChatMessageEvent` sobre `notification.exchange` con routing key `chat.message`.
6. El mensaje se difunde a `/topic/parche/{chatRoomId}/messages`.

### Integración con otros módulos

| Destino | Exchange | Routing key | Evento |
|---|---|---|---|
| **Notification Service** | `notification.exchange` (Topic) | `chat.message` | `ChatMessageEvent` — `chatId`, `senderName`, `content` |

El chat es **exclusivamente productor**: no consume colas. El acoplamiento con Notificaciones es asíncrono y unidireccional.

### Patrones Utilizados

| Patrón | Descripción |
|---|---|
| **Ports & Adapters (Hexagonal)** | El dominio define interfaces (puertos); la infraestructura provee implementaciones (adaptadores). |
| **Repository Pattern** | Persistencia abstraída tras `ChatRoomRepository`, `MessageRepository`, `MessageReportRepository`. |
| **Publisher / Event-Driven** | `ChatEventPublisher` desacopla la publicación de eventos del dominio. |
| **DTO Pattern** | DTOs de request/response desacoplan la API del modelo de dominio. |
| **State Pattern (ligero)** | `ChatRoomStatus` (`PENDING` → `ACTIVE` / `REJECTED`) y `ReportStatus` modelan ciclos de vida explícitos. |

### Estilo de Arquitectura

Arquitectura Hexagonal (Ports & Adapters) según la propuesta de Alistair Cockburn, en cuatro capas:

```
┌──────────────────────────────────────────────────────────────┐
│  ENTRYPOINTS (Adaptadores de Entrada)                        │
│  ChatController · ConnectionController (REST)                │
│  ChatWebSocketHandler (STOMP) · ChatExceptionHandler         │
├──────────────────────────────────────────────────────────────┤
│  APPLICATION (Casos de Uso)                                  │
│  SendMessage · GetMessageHistory · ReportMessage             │
│  SendConnectionRequest · RespondConnectionRequest            │
│  GetConnections · ChatMapper                                 │
├──────────────────────────────────────────────────────────────┤
│  DOMAIN (Núcleo — sin dependencias externas)                 │
│  ChatRoom · Message · MessageReport · Enums                  │
│  Ports/In (6) · Ports/Out (4) · Exceptions (5)               │
├──────────────────────────────────────────────────────────────┤
│  INFRASTRUCTURE (Adaptadores de Salida)                      │
│  MongoDB Repos · ChatEventPublisherAdapter (RabbitMQ)        │
│  RabbitMQConfig · WebSocketConfig · SecurityConfig · OpenApi │
└──────────────────────────────────────────────────────────────┘
```

**Beneficios obtenidos:**

| Beneficio | Implementación |
|---|---|
| **Testabilidad** | Los casos de uso se prueban con mocks de los puertos, sin base de datos ni broker real. |
| **Intercambiabilidad** | Cambiar el motor de persistencia solo modifica el adaptador, sin tocar el dominio. |
| **Claridad de responsabilidades** | Cada clase tiene una sola razón para cambiar (SRP). |
| **Independencia de frameworks** | El dominio no importa ninguna clase de Spring ni de infraestructura. |

---

## 5. 📊 Diagramas

### 5.1 Diagrama de Datos — Modelo MongoDB

Modelo desnormalizado en tres colecciones. `chat_rooms` es el punto de entrada: representa el canal de comunicación y guarda la lista de miembros usada para validar acceso. `messages` es el núcleo, referenciando su sala por `chatRoomId`; `content` y `mediaUrl` son complementarios, lo que permite mensajes de texto o de imagen sin colecciones separadas. `message_reports` registra los reportes de contenido inapropiado y su ciclo de vida vía `status`.

> ⚠️ **Pendiente:** agregar `docs/imagenes/ChatDB.drawio.png`.

#### Colección `chat_rooms`

| Campo | Tipo | Descripción |
|---|---|---|
| _id | String (UUID) | Identificador de la sala |
| parcheId | String (UUID) | Parche asociado — indexado |
| requesterId | String (UUID) | Usuario que originó la solicitud de conexión |
| status | String | `PENDING` / `ACTIVE` / `REJECTED` |
| memberIds | List\<String\> | Miembros de la sala (replicados para validar acceso) |
| createdAt | DateTime | Fecha de creación |

#### Colección `messages`

| Campo | Tipo | Descripción |
|---|---|---|
| _id | String (UUID) | Identificador del mensaje |
| chatRoomId | String (UUID) | Sala a la que pertenece — indexado |
| senderId | String (UUID) | Remitente |
| content | String | Contenido textual (máx. 500 caracteres) |
| mediaUrl | String | URL del archivo multimedia |
| type | String | `TEXT` / `IMAGE` |
| deleted | Boolean | Borrado lógico |
| sentAt | DateTime | Marca temporal de envío |

#### Colección `message_reports`

| Campo | Tipo | Descripción |
|---|---|---|
| _id | String (UUID) | Identificador del reporte |
| messageId | String (UUID) | Mensaje reportado |
| reporterId | String (UUID) | Usuario que reporta |
| reason | String | Motivo (máx. 300 caracteres) |
| status | String | `PENDING` / `REVIEWED` / `DISMISSED` |
| createdAt | DateTime | Fecha del reporte |

### 5.2 Diagrama de Clases

Modelo de dominio con tres entidades y tres enumeraciones de soporte:

- **`ChatRoom`** — sala asociada a un parche; encapsula la membresía con `isMember()`, `addMember()`, `removeMember()`.
- **`Message`** — entidad central; referencia su sala por `chatRoomId` y distingue texto de imagen por `MessageType`.
- **`MessageReport`** — reporte de un mensaje inapropiado, asociado por `messageId`, con ciclo de vida en `ReportStatus`.
- **Enums:** `ChatRoomStatus` (`PENDING`, `ACTIVE`, `REJECTED`), `MessageType` (`TEXT`, `IMAGE`), `ReportStatus` (`PENDING`, `REVIEWED`, `DISMISSED`).
- **Excepciones de dominio:** `ChatRoomNotFoundException`, `MessageNotFoundException`, `MessageReportNotFoundException`, `UserNotMemberException`, `InvalidMessageException`.

> ⚠️ **Pendiente:** agregar `docs/imagenes/DiagramaClasesChat.drawio.png`.

### 5.3 Diagrama de Componentes

- **Adaptadores de entrada:** `ChatController`, `ConnectionController` (REST) y `ChatWebSocketHandler` (STOMP).
- **Puertos de entrada (`ports/in`):** `SendMessageUseCase`, `GetMessageHistoryUseCase`, `ReportMessageUseCase`, `SendConnectionRequestUseCase`, `RespondConnectionRequestUseCase`, `GetConnectionsUseCase`.
- **Puertos de salida (`ports/out`):** `ChatRoomRepository`, `MessageRepository`, `MessageReportRepository`, `ChatEventPublisher`.
- **Adaptadores de salida:** `ChatRoomRepositoryAdapter`, `MessageRepositoryAdapter`, `MessageReportRepositoryAdapter` (MongoDB) y `ChatEventPublisherAdapter` (RabbitMQ vía `RabbitTemplate`).

> ⚠️ **Pendiente:** agregar `docs/imagenes/PDCE-Chat.drawio.png`.

---

## 6. 🧩 Funcionalidades

### Endpoints REST

Base: `/api/chat`

| # | Método | Ruta | Descripción |
|---|---|---|---|
| 1 | GET | `/api/chat/{chatRoomId}/messages` | Historial de mensajes (paginado) |
| 2 | POST | `/api/chat/messages/report` | Reportar un mensaje |
| 3 | POST | `/api/chat/connections/request` | Enviar solicitud de conexión |
| 4 | POST | `/api/chat/connections/respond` | Aceptar o rechazar una solicitud |
| 5 | GET | `/api/chat/connections` | Listar conexiones del usuario |

Todos exigen el header `X-User-Id: {uuid}` propagado por el API Gateway.

---

#### 1️⃣ Historial de Mensajes

**Endpoint:** `GET /api/chat/{chatRoomId}/messages` · **Headers:** `X-User-Id: {uuid}`

**Query Params:** `page` (default `0`), `size` (default `30`), `sort` — paginación estándar de Spring `Pageable`.

**Response:** `200 OK` — `Page<MessageResponse>`

```json
{
  "content": [
    {
      "id": "5d4c3b2a-1f0e-49d8-8c7b-6a5f4e3d2c1b",
      "chatRoomId": "7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c",
      "senderId": "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03",
      "content": "¿A qué hora nos vemos?",
      "mediaUrl": null,
      "type": "TEXT",
      "deleted": false,
      "sentAt": "2026-04-15T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

**Errores:**

| Código HTTP | Escenario |
|---|---|
| 404 | La sala no existe (`ChatRoomNotFoundException`) |
| 403 | El usuario no es miembro de la sala (`UserNotMemberException`) |

---

#### 2️⃣ Reportar un Mensaje

**Endpoint:** `POST /api/chat/messages/report` · **Headers:** `X-User-Id: {uuid}`

**Request Body:**

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| messageId | UUID | Obligatorio | Mensaje reportado |
| reason | String | Obligatorio, máx. 300 | Motivo del reporte |

```json
// Request
{
  "messageId": "5d4c3b2a-1f0e-49d8-8c7b-6a5f4e3d2c1b",
  "reason": "Contenido ofensivo"
}

// Response 201 CREATED
{
  "id": "1a2b3c4d-5e6f-4708-9a0b-1c2d3e4f5a6b",
  "messageId": "5d4c3b2a-1f0e-49d8-8c7b-6a5f4e3d2c1b",
  "reporterId": "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03",
  "reason": "Contenido ofensivo",
  "status": "PENDING",
  "createdAt": "2026-04-15T10:35:00"
}
```

**Errores:**

| Código HTTP | Escenario |
|---|---|
| 400 | `messageId` ausente o `reason` vacío / mayor a 300 caracteres |
| 404 | El mensaje reportado no existe (`MessageNotFoundException`) |

---

#### 3️⃣ Enviar Solicitud de Conexión

**Endpoint:** `POST /api/chat/connections/request?targetId={uuid}` · **Headers:** `X-User-Id: {uuid}`

Crea una `ChatRoom` en estado `PENDING` con ambos usuarios como miembros.

**Response:** `201 CREATED` (sin cuerpo)

---

#### 4️⃣ Responder Solicitud de Conexión

**Endpoint:** `POST /api/chat/connections/respond?requesterId={uuid}&accepted={true|false}` · **Headers:** `X-User-Id: {uuid}`

Busca la sala `PENDING` originada por `requesterId` y la deja en `ACTIVE` (aceptada) o `REJECTED` (rechazada).

**Response:** `200 OK` · **Error:** `404` si no existe una solicitud pendiente de ese usuario.

---

#### 5️⃣ Listar Conexiones

**Endpoint:** `GET /api/chat/connections` · **Headers:** `X-User-Id: {uuid}`

**Response:** `200 OK` — `List<ConnectionResponse>`

```json
[
  {
    "chatRoomId": "7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c",
    "otherUserId": "9c8b7a6d-5e4f-43c2-b1a0-9f8e7d6c5b4a",
    "status": "ACTIVE",
    "createdAt": "2026-04-14T09:00:00"
  }
]
```

---

### 🔌 WebSocket — Mensajería en Tiempo Real

STOMP sobre SockJS, con broker simple en memoria.

| Campo | Valor |
|---|---|
| **Endpoint de conexión** | `/ws-chat` (SockJS) |
| **Destino de envío** | `/app/chat/{chatRoomId}/send` |
| **Topic de difusión** | `/topic/parche/{chatRoomId}/messages` |
| **Prefijo de aplicación** | `/app` |
| **Prefijo del broker** | `/topic` |

**Payload de envío (`SendMessageRequest`):**

| Campo | Tipo | Restricciones |
|---|---|---|
| type | Enum | Obligatorio — `TEXT` / `IMAGE` |
| content | String | Máx. 500; obligatorio si `type = TEXT` |
| mediaUrl | String | Obligatorio si `type = IMAGE` |

```javascript
const socket = new SockJS('/ws-chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  // Recibir mensajes de la sala
  stompClient.subscribe(`/topic/parche/${chatRoomId}/messages`, (message) => {
    console.log('Nuevo mensaje:', JSON.parse(message.body));
  });

  // Enviar un mensaje
  stompClient.send(
    `/app/chat/${chatRoomId}/send`,
    { 'X-User-Id': userId },
    JSON.stringify({ type: 'TEXT', content: 'Hola parche' })
  );
});
```

> 🧪 El repositorio incluye una página de prueba manual en `src/main/resources/static/chat-test.html`, disponible en `http://localhost:8082/chat-test.html`.

---

### ⚠️ Manejo de Errores

Handler centralizado con `@RestControllerAdvice` en `entrypoints/advice/ChatExceptionHandler`.

| Código HTTP | Excepción | Escenario |
|---|---|---|
| 404 | `ChatRoomNotFoundException` | Sala inexistente o sin solicitud pendiente |
| 404 | `MessageNotFoundException` | Mensaje inexistente |
| 404 | `MessageReportNotFoundException` | Reporte inexistente |
| 403 | `UserNotMemberException` | El usuario no pertenece a la sala |
| 400 | `InvalidMessageException` | Mensaje `TEXT` sin `content` o `IMAGE` sin `mediaUrl` |

```json
{ "error": "User 3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03 is not a member of chat room 7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c" }
```

---

## 7. 🧪 Evidencia de Pruebas Unitarias

| Tipo | Descripción | Herramientas |
|---|---|---|
| **Pruebas Unitarias** | Validan cada caso de uso de forma aislada, mockeando puertos y dependencias | JUnit 5 + Mockito |

### Clases de prueba

| Clase | Casos cubiertos |
|---|---|
| `SendMessageUseCaseImplTest` | Envío exitoso, sala inexistente, usuario no miembro, `TEXT` sin contenido, `IMAGE` sin `mediaUrl` |
| `GetMessageHistoryUseCaseImplTest` | Historial paginado y validación de membresía |
| `ReportMessageUseCaseImplTest` | Reporte exitoso y mensaje inexistente |
| `SendConnectionRequestUseCaseImplTest` | Creación de sala en estado `PENDING` |
| `RespondConnectionRequestUseCaseImplTest` | Aceptar (`ACTIVE`) y rechazar (`REJECTED`) |
| `GetConnectionsUseCaseImplTest` | Listado de conexiones del usuario |

### Cómo ejecutar las pruebas

```bash
# Todas las pruebas
mvn clean test

# Una prueba específica
mvn test -Dtest=SendMessageUseCaseImplTest

# Pruebas + verificación de cobertura JaCoCo
mvn clean verify
```

### Criterios de aceptación

- ✅ Cobertura mínima del **85%** global (regla `BUNDLE`)
- ✅ Cobertura mínima del **95%** en `application.usecase` (regla `PACKAGE`)
- ✅ Todas las pruebas en estado **PASSED**
- ✅ Casos felices **y** casos de error cubiertos

> ⚠️ **Pendiente:** agregar captura del reporte de pruebas en `docs/`.

---

## 8. 📈 Evidencia del Análisis de Cobertura

Cobertura generada con **JaCoCo 0.8.10** (`prepare-agent` en `test`, `report` en `test`, `check` en `verify`). Se excluyen infraestructura, entrypoints, DTOs, mappers, modelo, puertos y excepciones: la verificación recae sobre la lógica de negocio.

```bash
mvn clean verify
```

Reporte HTML: `target/site/jacoco/index.html`. El pipeline de CI lo publica como artefacto `jacoco-report-chat`.

> ⚠️ **Pendiente:** agregar captura del reporte en `docs/Jacoco.png`.

---

## 9. 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos

- **Java 21**
- **Maven 3.9+** (o el wrapper `./mvnw` incluido)
- **Docker** (opcional, para ejecutar la imagen del servicio)

### Opción 1: Ejecución Local (Maven)

```bash
# 1. Clonar repositorio
git clone https://github.com/CybersapiensECI/chat-service.git
cd chat-service

# 2. Ejecutar la aplicación
./mvnw spring-boot:run
```

📍 **URL Local:** `http://localhost:8082`
📚 **Swagger UI:** `http://localhost:8082/swagger-ui.html`
📄 **OpenAPI JSON:** `http://localhost:8082/v3/api-docs`
📡 **WebSocket:** `ws://localhost:8082/ws-chat`
🧪 **Página de prueba:** `http://localhost:8082/chat-test.html`

### Opción 2: Docker

```bash
docker build -t chat-service .
docker run -p 8082:8082 \
  -e SPRING_DATA_MONGODB_URI="..." \
  -e SPRING_RABBITMQ_HOST="..." \
  -e SPRING_RABBITMQ_USERNAME="..." \
  -e SPRING_RABBITMQ_PASSWORD="..." \
  -e SPRING_RABBITMQ_VIRTUAL_HOST="..." \
  chat-service
```

El `Dockerfile` usa build multi-stage (`maven:3.9.6-eclipse-temurin-21-alpine` → `eclipse-temurin:21-jre-alpine`) y expone el puerto **8082**.

### Variables de Entorno

Sobrescriben los valores de `application.properties` mediante el relaxed binding de Spring Boot:

| Variable | Descripción | Default |
|---|---|---|
| `SERVER_PORT` | Puerto del servicio | `8082` |
| `SPRING_DATA_MONGODB_URI` | URI de conexión a MongoDB Atlas (base `chat-db`) | Requerido |
| `SPRING_RABBITMQ_HOST` | Host del broker CloudAMQP | Requerido |
| `SPRING_RABBITMQ_PORT` | Puerto RabbitMQ | `5671` |
| `SPRING_RABBITMQ_USERNAME` | Usuario RabbitMQ | Requerido |
| `SPRING_RABBITMQ_PASSWORD` | Contraseña RabbitMQ | Requerido |
| `SPRING_RABBITMQ_VIRTUAL_HOST` | Virtual host RabbitMQ | Requerido |
| `SPRING_RABBITMQ_SSL_ENABLED` | SSL en RabbitMQ | `true` |

---

## 10. 🔄 Evidencia del Despliegue CI/CD

Tres workflows de **GitHub Actions**:

| Workflow | Archivo | Disparador |
|---|---|---|
| **CI** | `.github/workflows/ci.yml` | push a `main`, `develop`, `feature/**`; PR a `main` / `develop` |
| **CD QA** | `.github/workflows/cd-qa.yml` | push a `develop` |
| **CD Prod** | `.github/workflows/cd-prod.yml` | push a `main` |

> ⚠️ **Pendiente:** agregar capturas de las ejecuciones en `docs/CI.png` y `docs/CD.png`.

---

## 11. 🌐 Link Expuesto en Azure con Swagger

Despliegue en **Azure Container Apps**, resource group `gr-alphaeci-prod`:

| Entorno | Container App |
|---|---|
| **QA** | `chat-service-qa` |
| **PROD** | `chat-service-prod` |

Rutas expuestas sobre el FQDN asignado por Azure (ingress externo, target port 8082):

- **Swagger UI:** `https://{fqdn}/swagger-ui.html`
- **OpenAPI JSON:** `https://{fqdn}/v3/api-docs`

> ⚠️ **Pendiente:** registrar aquí el FQDN definitivo de cada Container App.

---

## 12. 🗂️ Organización del Código

```
chat-service/
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/alphaeci/chat/
│   │   │   │
│   │   │   ├── 📁 domain/                          # 🟢 DOMINIO (sin dependencias externas)
│   │   │   │   ├── 📁 model/                       # ChatRoom, Message, MessageReport
│   │   │   │   │   └── 📁 enums/                   # ChatRoomStatus, MessageType, ReportStatus
│   │   │   │   ├── 📁 ports/
│   │   │   │   │   ├── 📁 in/                      # 6 puertos de entrada (casos de uso)
│   │   │   │   │   └── 📁 out/                     # 4 puertos de salida (3 repos + publisher)
│   │   │   │   ├── 📁 exceptions/                  # ChatRoomNotFound, MessageNotFound,
│   │   │   │   │                                   # MessageReportNotFound, UserNotMember, InvalidMessage
│   │   │   │   └── 📁 validation/
│   │   │   │
│   │   │   ├── 📁 application/                     # 🔵 APLICACIÓN
│   │   │   │   ├── 📁 usecase/                     # 6 implementaciones de casos de uso
│   │   │   │   ├── 📁 dto/
│   │   │   │   │   ├── 📁 request/                 # SendMessageRequest, ReportMessageRequest
│   │   │   │   │   └── 📁 response/                # MessageResponse, MessageReportResponse, ConnectionResponse
│   │   │   │   └── 📁 mapper/                      # ChatMapper
│   │   │   │
│   │   │   ├── 📁 entrypoints/                     # 🟠 ADAPTADORES DE ENTRADA
│   │   │   │   ├── 📁 rest/controller/             # ChatController, ConnectionController
│   │   │   │   ├── 📁 websocket/                   # ChatWebSocketHandler (STOMP)
│   │   │   │   ├── 📁 messaging/publisher/         # ChatMessageEvent
│   │   │   │   └── 📁 advice/                      # ChatExceptionHandler
│   │   │   │
│   │   │   └── 📁 infrastructure/                  # 🔴 INFRAESTRUCTURA
│   │   │       ├── 📁 adapters/
│   │   │       │   ├── 📁 adapter/                 # ChatEventPublisherAdapter (RabbitMQ),
│   │   │       │   │                               # ChatRoom/Message/MessageReport RepositoryAdapter
│   │   │       │   └── 📁 persistence/             # Documentos, PersistenceMapper y repositorios Spring Data
│   │   │       └── 📁 config/                      # RabbitMQConfig, WebSocketConfig,
│   │   │                                           # SecurityConfig, OpenApiConfig
│   │   │
│   │   └── 📁 resources/
│   │       ├── application.properties
│   │       └── 📁 static/                          # chat-test.html (cliente STOMP de prueba)
│   │
│   └── 📁 test/                                    # 🧪 PRUEBAS UNITARIAS
│
├── 📁 .github/workflows/                           # ci.yml, cd-qa.yml, cd-prod.yml
├── 📄 Dockerfile
├── 📄 pom.xml
└── 📄 README.md
```

---

## 13. 📝 Código Documentado

La documentación viva del API se genera con **SpringDoc OpenAPI 2.5.0** y se publica en Swagger UI (`/swagger-ui.html`), configurada en `infrastructure/config/OpenApiConfig` bajo el título *Chat Service API*.

> ⚠️ **Pendiente:** completar JavaDoc en dominio, casos de uso y adaptadores, y enriquecer los controladores con `@Tag`, `@Operation`, `@ApiResponse` y los DTOs con `@Schema`.

---

## 14. 🔗 Conexiones con Servicios Externos

| Servicio Externo | Tipo de Conexión | Propósito |
|---|---|---|
| **MongoDB Atlas** | Spring Data MongoDB | Persistencia de salas, mensajes y reportes (`chat-db`) |
| **RabbitMQ (CloudAMQP)** | Spring AMQP — SSL, puerto 5671 | Publicación de eventos de mensaje hacia Notification Service |
| **Notification Service** | RabbitMQ (asíncrono) | Consume `chat.message` para generar notificaciones `PARCHE_MESSAGE` |
| **GHCR** | Docker registry | Publicación de la imagen del servicio |
| **Azure Container Apps** | Despliegue | Ejecución del microservicio en QA y PROD |

### Configuración requerida

En los despliegues, todas las credenciales se inyectan como variables de entorno desde **GitHub Secrets** (`MONGODB_URI_CHAT`, `RABBITMQ_*`, `AZURE_CREDENTIALS_Chat`).

> 🔐 **Pendiente de seguridad:** `src/main/resources/application.properties` aún contiene credenciales reales de MongoDB Atlas y CloudAMQP en texto plano y versionadas en Git. Deben reemplazarse por placeholders (`${SPRING_DATA_MONGODB_URI:}`, etc.) y **rotarse**, ya que quedaron expuestas en el historial del repositorio. Son las mismas credenciales que usa el notification-service.

---

## 15. ⚙️ Pipeline de Desarrollo

### Estrategia de Ramas (Git Flow)

| Rama | Propósito | Reglas |
|---|---|---|
| `main` | Versión estable en producción | El merge dispara **CD Prod**. PR obligatorio con CI en verde. |
| `develop` | Integración continua | El merge dispara **CD QA**. Rama protegida. |
| `feature/*` | Desarrollo de una funcionalidad | Base: `develop`. Cierre: PR hacia `develop`. |

### Convenciones

```
Ramas:    feature/[NombreFuncionalidad]     → feature/ChatService
Commits:  Feat: [Descripción]               → Feat: Implementacion CI/CD
          Fix: [Descripción]                → Fix: Correccion Replicas Azure
```

### Etapas del Pipeline de CI (`.github/workflows/ci.yml`)

```yaml
on:
  push:
    branches: [ main, develop, feature/** ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build-and-test:
    - Checkout del código
    - Setup JDK 21 (temurin, cache maven)
    - mvn verify          # compila, prueba y valida cobertura JaCoCo (85% / 95%)
    - Upload artefacto jacoco-report-chat
```

Las credenciales se inyectan como variables de entorno desde GitHub Secrets.

---

## 16. 🚢 Pipeline de PROD

### CD QA (`.github/workflows/cd-qa.yml`) — push a `develop`

```yaml
jobs:
  deploy-qa:
    - Checkout del código
    - Login en ghcr.io
    - docker build & push  → ghcr.io/cybersapienseci/chat-service:qa-{sha}, :qa-latest
    - Login en Azure (AZURE_CREDENTIALS_Chat)
    - az containerapp up      # chat-service-qa, ingress external, target-port 8082
    - az containerapp update  # min-replicas 0, max-replicas 1, env vars desde secrets
```

### CD Prod (`.github/workflows/cd-prod.yml`) — push a `main`

```yaml
jobs:
  deploy-prod:
    - Checkout del código
    - Login en ghcr.io
    - docker build & push  → ghcr.io/cybersapienseci/chat-service:latest
    - Login en Azure (AZURE_CREDENTIALS_Chat)
    - az containerapp up      # chat-service-prod, ingress external, target-port 8082
    - az containerapp update  # min-replicas 0, max-replicas 1, env vars desde secrets
```

Ambos despliegan sobre el resource group y environment `gr-alphaeci-prod` con escalado a cero (`min-replicas 0`) para optimizar costo.

---

<div align="center">

### 🐢 Equipo **AlphaECI**

![Course](https://img.shields.io/badge/Course-DOSW-orange?style=for-the-badge)
![Year](https://img.shields.io/badge/Year-2026--1-blue?style=for-the-badge)

**🎓 Escuela Colombiana de Ingeniería Julio Garavito**

</div>
