-- =========================================================
-- Base de datos: PetCare Center
-- =========================================================
CREATE DATABASE IF NOT EXISTS petcare_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE petcare_db;

-- =========================================================
-- Tabla: medicamento
-- =========================================================
CREATE TABLE medicamento (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    codigo_medicamento  VARCHAR(30)     NOT NULL UNIQUE,
    nombre              VARCHAR(100)    NOT NULL,
    categoria           VARCHAR(50)     NOT NULL,
    laboratorio         VARCHAR(100)    NOT NULL,
    stock_total         INT             NOT NULL DEFAULT 0,
    stock_disponible    INT             NOT NULL DEFAULT 0,
    precio_unitario     DECIMAL(10,2)   NOT NULL,
    is_activo           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_stock_total_no_negativo      CHECK (stock_total >= 0),
    CONSTRAINT chk_stock_disponible_no_negativo CHECK (stock_disponible >= 0)
);

-- =========================================================
-- Tabla: propietario
-- =========================================================
CREATE TABLE propietario (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    documento     VARCHAR(30)   NOT NULL UNIQUE,
    telefono      VARCHAR(20),
    email         VARCHAR(100),
    direccion     VARCHAR(150),
    is_activo     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- Tabla: mascota
-- =========================================================
CREATE TABLE mascota (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    historia_clinica      VARCHAR(30)   NOT NULL UNIQUE,
    nombre                VARCHAR(100)  NOT NULL,
    especie               VARCHAR(50)   NOT NULL,
    raza                  VARCHAR(50),
    edad                  INT,
    propietario_id        INT           NOT NULL,
    is_activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mascota_propietario
        FOREIGN KEY (propietario_id) REFERENCES propietario(id)
);

-- =========================================================
-- Tabla: usuario
-- =========================================================
CREATE TABLE usuario (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)   NOT NULL UNIQUE,
    password      VARCHAR(255)  NOT NULL,
    nombre        VARCHAR(100)  NOT NULL,
    rol           ENUM('ADMIN', 'RECEPCIONISTA') NOT NULL,
    estado        ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- Tabla: consulta
-- =========================================================
CREATE TABLE consulta (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    propietario_id      INT           NOT NULL,
    mascota_id          INT           NOT NULL,
    veterinario_id      INT           NOT NULL,
    fecha_consulta      DATETIME      NOT NULL,
    motivo              VARCHAR(255)  NOT NULL,
    diagnostico         TEXT,
    tratamiento         TEXT,
    estado              ENUM('REGISTERED', 'IN_CONSULTATION', 'COMPLETED', 'CANCELLED')
                         NOT NULL DEFAULT 'REGISTERED',
    costo_total         DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consulta_propietario
        FOREIGN KEY (propietario_id) REFERENCES propietario(id),
    CONSTRAINT fk_consulta_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id),
    CONSTRAINT fk_consulta_veterinario
        FOREIGN KEY (veterinario_id) REFERENCES usuario(id)
);

-- =========================================================
-- Tabla: consulta_medicamento (relación N:M)
-- =========================================================
CREATE TABLE consulta_medicamento (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id         INT           NOT NULL,
    medicamento_id      INT           NOT NULL,
    cantidad            INT           NOT NULL,
    precio_unitario     DECIMAL(10,2) NOT NULL,
    subtotal            DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_cm_consulta
        FOREIGN KEY (consulta_id) REFERENCES consulta(id),
    CONSTRAINT fk_cm_medicamento
        FOREIGN KEY (medicamento_id) REFERENCES medicamento(id),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0)
);


-- =========================================================
-- Usuario ADMIN inicial (necesario para el primer login)
-- =========================================================
INSERT INTO usuario (username, password, nombre, rol, estado)
VALUES ('admin', 'admin123', 'Administrador General', 'ADMIN', 'ACTIVE');


INSERT INTO usuario (username, password, nombre, rol, estado)
VALUES ('recepcion1', 'recep123', 'Recepcionista Uno', 'RECEPCIONISTA', 'ACTIVE');


