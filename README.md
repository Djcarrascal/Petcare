# PetCare Center

Sistema en Java SE para la gestión interna de una red de clínicas veterinarias: propietarios, mascotas, medicamentos, usuarios y consultas veterinarias. Reemplaza el manejo manual por hojas de cálculo y formularios físicos que la clínica venía usando, centralizando la información en una sola aplicación con persistencia en base de datos relacional.

## MIS DATOS:

| Campo | Valor |
|---|---|
| Nombre | David Carrascal |
| Clan | Puerta de Oro / Java |



## Descripción general

PetCare Center permite:

- Gestionar el inventario de medicamentos (registro, edición, filtrado por categoría/laboratorio, consulta de disponibilidad).
- Registrar propietarios y las mascotas asociadas a cada uno, validando historia clínica única y que el propietario esté activo.
- Administrar usuarios del sistema con dos roles (`ADMIN` y `RECEPCIONISTA`), con autenticación por usuario y contraseña.
- Registrar consultas veterinarias completas: propietario, mascota, veterinario responsable, motivo, diagnóstico, tratamiento y medicamentos utilizados, con descuento automático de inventario y cálculo del costo total.
- Controlar el ciclo de vida de una consulta a través de estados (`REGISTRADA`, `EN_ATENCION`, `FINALIZADA`, `CANCELADA`) con transiciones validadas.

La interfaz de usuario se construyó completamente con `JOptionPane` (sin una ventana Swing tradicional), y la persistencia se maneja con JDBC puro sobre MySQL, sin frameworks de mapeo objeto-relacional.

## Tecnologías y requisitos previos

- **Java 17** o superior (JDK).
- **Maven** (gestión de dependencias y build).
- **MySQL 8+** en ejecución localmente (o accesible por red).
- IDE recomendado: NetBeans (el proyecto es un proyecto Maven estándar, así que también funciona en IntelliJ o Eclipse).

## Arquitectura por capas

El proyecto sigue una arquitectura por capas clásica, con una responsabilidad clara para cada una:

```
view          -> Menús JOptionPane. Solo interacción con el usuario, sin lógica de negocio ni SQL.

controller    -> Orquesta la llamada entre la vista y el service. Registra trazas simuladas de
                 llamadas HTTP y el detalle técnico de los errores. Sin SQL, sin reglas de negocio.

service       -> Contiene las reglas y validaciones de negocio (unicidad, stock, estados activos,
                 transiciones de estado válidas). Habla con los DAO por interfaz.

dao           -> Acceso a datos puro con JDBC (SELECT/INSERT/UPDATE/DELETE). No decide reglas de
                 negocio, solo ejecuta lo que se le pide.

model         -> Clases de dominio (POJOs) con atributos privados, getters/setters y relaciones
                 entre objetos (por ejemplo, una Mascota referencia a su Propietario completo).

exception     -> Jerarquía de excepciones personalizadas (checked), todas heredando de una
                 excepción base común del dominio.

util          -> Utilidades transversales: fábrica de conexiones JDBC, logger de trazas HTTP
                 simuladas, helper para construir tablas de texto en JOptionPane.
```

**Flujo típico de una operación:** `View → Controller → Service → DAO → Base de datos`, y la respuesta (o excepción) recorre el camino inverso.

### Decisiones de diseño relevantes

- **Patrón Decorator en la creación de usuarios**: `BasicUserCreator` contiene la lógica base de creación (validar unicidad y guardar). `DefaultPropertiesUserDecorator` envuelve a `BasicUserCreator` y añade los valores por defecto (`role`, `estado`, `createdAt`) antes de delegar, sin modificar la lógica base.
- **Transacciones JDBC explícitas**: el registro de una consulta veterinaria (inserción de la consulta, inserción de los medicamentos utilizados y descuento del inventario) se ejecuta dentro de una única transacción (`setAutoCommit(false)` → operaciones → `commit()`/`rollback()`), garantizando que ante cualquier error no quede ninguna operación parcial guardada. La finalización de una consulta sigue el mismo principio.
- **Excepciones personalizadas checked**: todas las excepciones de negocio heredan de `PetCareException` (que extiende `Exception`), obligando a manejarlas explícitamente en cada capa que las propaga.
- **Enums para valores cerrados**: `RolUsuario`, `EstadoUsuario` y `EstadoConsulta` se modelan como enums de Java en vez de `String`, evitando valores inválidos y facilitando la validación de transiciones de estado.

## Estructura del proyecto

```
src/main/java/com/petcare/
 ├── model/          Medication, Owner, Pet, User, Consultation, ConsultationMedication
 ├── enums/           RolUsuario, EstadoUsuario, EstadoConsulta
 ├── exception/       PetCareException y las excepciones específicas del dominio
 ├── dao/             Interfaces DAO
 ├── dao/impl/        Implementaciones JDBC
 ├── service/         Interfaces de servicio
 ├── service/impl/    Implementaciones con las reglas de negocio
 ├── controller/      Controladores (orquestación + trazas)
 ├── view/            Vistas JOptionPane
 ├── util/            ConnectionFactory, HttpTraceLogger, ConsoleTableHelper
 └── Main.java         Punto de entrada
```

## Configuración y ejecución

### 1. Clonar el repositorio

```bash
git clone <URL-del-repositorio>
cd Petcare
```

### 2. Crear la base de datos

Ejecuta el script SQL incluido en `database/schema.sql` sobre tu instancia de MySQL. Puedes hacerlo desde MySQL Workbench, DBeaver, o por consola:

```bash
mysql -u root -p < database/schema.sql
```

Este script crea la base de datos `petcare_db`, todas las tablas, y dos usuarios iniciales (`admin` / `admin123` con rol `ADMIN`, y `recepcion1` / `recep123` con rol `RECEPCIONISTA`).

### 3. Configurar la conexión

Edita `src/main/resources/db.properties` con tus credenciales locales de MySQL:

```properties
db.url=jdbc:mysql://localhost:3306/petcare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=tu_password
```

> Si tu servidor MySQL usa el método de autenticación `caching_sha2_password` (el predeterminado desde MySQL 8) y no tienes SSL configurado, `allowPublicKeyRetrieval=true` es necesario para poder conectar en un entorno de desarrollo local.

### 4. Compilar y ejecutar

Desde NetBeans: click derecho sobre el proyecto → **Clean and Build**, y luego click derecho sobre `Main.java` → **Run File**.

Desde consola:

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.petcare.Main"
```

### 5. Iniciar sesión

Usa cualquiera de los dos usuarios creados por el script SQL:

- **ADMIN**: `admin` / `admin123`
- **RECEPCIONISTA**: `recepcion1` / `recep123`

## Diagramas

### Diagrama de clases

<img width="1254" height="685" alt="class_diagram" src="https://github.com/user-attachments/assets/8b2e5494-877e-4468-b62b-15fa2a1c9bca" />


### Diagrama de casos de uso

<img width="446" height="1137" alt="use_case_diagram-2" src="https://github.com/user-attachments/assets/70d8aad6-4411-4f50-b5b4-9d8cda2f0c4f" />


## Capturas de pantalla

- Login
<img width="301" height="156" alt="Screenshot 2026-09-03 at 8 04 20 PM" src="https://github.com/user-attachments/assets/53728012-0639-427c-a5c2-51307c6afc9e" />

- Menú:
<img width="885" height="122" alt="menu" src="https://github.com/user-attachments/assets/21580344-b886-4120-b6d7-109130bfb9a7" />

- Vista Medicamentos:
<img width="1166" height="123" alt="Medicamentos" src="https://github.com/user-attachments/assets/e582b860-2f7c-42a2-a0e1-608d66415031" /> 
- Vista Consultas:
<img width="891" height="118" alt="consultas" src="https://github.com/user-attachments/assets/21938f76-8428-432b-879c-cb297ba912c2" />


## Excepciones personalizadas

El sistema define una jerarquía de excepciones checked, todas heredando de `PetCareException`:

`MedicationNotFoundException`, `DuplicateMedicationCodeException`, `InsufficientStockException` (`StockInsuficienteException`), `OwnerNotFoundException`, `InactiveOwnerException`, `PetNotFoundException`, `DuplicateMedicalRecordException`, `ConsultationNotFoundException` (`ConsultaNoEncontradaException`), `InvalidConsultationException` (`ConsultaInvalidaException`), `InvalidCredentialsException` (`CredencialesInvalidasException`).

Los errores se capturan en la capa `controller`, donde se registra el detalle técnico en consola, y se muestran en la vista únicamente con un mensaje comprensible para el usuario mediante `JOptionPane`.
