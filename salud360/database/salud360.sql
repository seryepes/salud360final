-- =====================================================================
--  SALUD360 - Base de datos del proyecto de citas médicas
--  Motor: MariaDB / MySQL (el que trae XAMPP)
--
--  Cómo usarlo:
--    1. Abrir XAMPP y encender Apache + MySQL.
--    2. Ir a http://localhost/phpmyadmin  ->  pestaña "Importar"
--       -> elegir este archivo -> "Importar".
--  Se puede importar más de una vez sin perder datos (usa IF NOT EXISTS
--  e INSERT IGNORE).
-- =====================================================================

-- Asegura que los acentos (á, é, ñ...) se guarden bien sin importar el cliente
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS salud360
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE salud360;

-- ---------------------------------------------------------------------
--  USUARIO y PACIENTE
--  Cada usuario tiene un solo paciente y comparten el mismo número
--  (igual que en UsuarioRepositoryMemoria): id_paciente = id_usuario.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(80)  NOT NULL,
    apellido    VARCHAR(80)  NOT NULL,
    correo      VARCHAR(120) NOT NULL,
    contrasena  VARCHAR(100) NOT NULL,
    telefono    VARCHAR(15)  NOT NULL,
    CONSTRAINT uq_usuario_correo UNIQUE (correo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS paciente (
    id_paciente      INT PRIMARY KEY,
    documento        VARCHAR(15)  NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    direccion        VARCHAR(150),
    eps              VARCHAR(80),
    CONSTRAINT fk_paciente_usuario FOREIGN KEY (id_paciente)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ---------------------------------------------------------------------
--  ESPECIALIDAD, PROFESIONAL y su relación muchos-a-muchos
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS especialidad (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(80)  NOT NULL,
    descripcion     VARCHAR(200)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS profesional (
    id_profesional INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(80)  NOT NULL,
    apellido       VARCHAR(80)  NOT NULL,
    correo         VARCHAR(120) NOT NULL,
    telefono       VARCHAR(15)  NOT NULL,
    documento      VARCHAR(15)  NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS profesional_especialidad (
    id_profesional  INT NOT NULL,
    id_especialidad INT NOT NULL,
    PRIMARY KEY (id_profesional, id_especialidad),
    CONSTRAINT fk_pe_profesional FOREIGN KEY (id_profesional)
        REFERENCES profesional (id_profesional) ON DELETE CASCADE,
    CONSTRAINT fk_pe_especialidad FOREIGN KEY (id_especialidad)
        REFERENCES especialidad (id_especialidad) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ---------------------------------------------------------------------
--  PAGO
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    id_pago     INT AUTO_INCREMENT PRIMARY KEY,
    monto       DECIMAL(12,2) NOT NULL,
    metodo_pago VARCHAR(40)   NOT NULL,
    fecha_pago  DATETIME      NOT NULL,
    estado_pago ENUM('PENDIENTE','APROBADO','RECHAZADO') NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ---------------------------------------------------------------------
--  CITA
--  La hora se guarda como texto "HH:mm" porque así la maneja el dominio.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cita (
    id_cita        INT AUTO_INCREMENT PRIMARY KEY,
    fecha          DATE        NOT NULL,
    hora           CHAR(5)     NOT NULL,
    estado         ENUM('PENDIENTE','CONFIRMADA','REPROGRAMADA','CANCELADA') NOT NULL,
    motivo         VARCHAR(255),
    modalidad      VARCHAR(30),
    id_paciente    INT NOT NULL,
    id_profesional INT NOT NULL,
    id_pago        INT NULL,
    CONSTRAINT fk_cita_paciente    FOREIGN KEY (id_paciente)    REFERENCES paciente (id_paciente),
    CONSTRAINT fk_cita_profesional FOREIGN KEY (id_profesional) REFERENCES profesional (id_profesional),
    CONSTRAINT fk_cita_pago        FOREIGN KEY (id_pago)        REFERENCES pago (id_pago),
    INDEX idx_cita_agenda (id_profesional, fecha, hora)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ---------------------------------------------------------------------
--  CONFIRMACIÓN (una por cita) y NOTIFICACIONES de esa confirmación
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS confirmacion_cita (
    id_confirmacion     INT AUTO_INCREMENT PRIMARY KEY,
    id_cita             INT          NOT NULL,
    fecha_confirmacion  DATETIME     NOT NULL,
    codigo_confirmacion VARCHAR(30)  NOT NULL,
    estado              VARCHAR(20)  NOT NULL,
    CONSTRAINT uq_confirmacion_cita UNIQUE (id_cita),
    CONSTRAINT fk_confirmacion_cita FOREIGN KEY (id_cita)
        REFERENCES cita (id_cita) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_confirmacion INT          NOT NULL,
    mensaje         VARCHAR(255) NOT NULL,
    fecha_envio     DATETIME     NOT NULL,
    tipo            VARCHAR(30),
    estado          VARCHAR(20),
    CONSTRAINT fk_notificacion_confirmacion FOREIGN KEY (id_confirmacion)
        REFERENCES confirmacion_cita (id_confirmacion) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ---------------------------------------------------------------------
--  DATOS INICIALES (los mismos de prueba que usaban los repos en memoria;
--  no hay pantalla para crear especialidades ni profesionales)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO especialidad (id_especialidad, nombre, descripcion) VALUES
    (1, 'Medicina General', 'Consulta médica general y control de salud'),
    (2, 'Pediatría',        'Atención médica para niños y adolescentes'),
    (3, 'Cardiología',      'Diagnóstico y tratamiento del corazón'),
    (4, 'Odontología',      'Salud y cuidado dental');

INSERT IGNORE INTO profesional (id_profesional, nombre, apellido, correo, telefono, documento) VALUES
    (1, 'Laura',  'Gómez',   'laura.gomez@clinica.com',    '3001112233', '1010101010'),
    (2, 'Carlos', 'Ramírez', 'carlos.ramirez@clinica.com', '3002223344', '2020202020'),
    (3, 'Andrea', 'Torres',  'andrea.torres@clinica.com',  '3003334455', '3030303030'),
    (4, 'Julián', 'Herrera', 'julian.herrera@clinica.com', '3004445566', '4040404040');

INSERT IGNORE INTO profesional_especialidad (id_profesional, id_especialidad) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (4, 1);
