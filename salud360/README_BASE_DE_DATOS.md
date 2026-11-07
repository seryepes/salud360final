# Salud360 – Conexión a base de datos (XAMPP)

## 1. Preparar la base de datos (una sola vez)

1. Abre **XAMPP Control Panel** y pulsa **Start** en **MySQL** (Apache es opcional, solo si usarás phpMyAdmin: también Start).
2. Entra a <http://localhost/phpmyadmin>.
3. Pestaña **Importar** → **Seleccionar archivo** → elige `database/salud360.sql` → **Importar** (abajo).
4. Debe aparecer la base `salud360` a la izquierda con 9 tablas.

> Puedes importar el script varias veces: no borra datos.
> Si quieres dejar la base limpia para la presentación: en phpMyAdmin selecciona `salud360` → **Operaciones** → **Eliminar la base de datos**, y vuelve a importar.

## 2. Abrir y ejecutar en IntelliJ

1. **File → Open** y elige la carpeta `salud360`.
2. El driver ya viene incluido en `lib/mariadb-java-client-2.7.6.jar` y registrado en el `.iml`.
   Si IntelliJ no lo reconoce (marca `org.mariadb...` en rojo o error *No suitable driver*):
   **File → Project Structure → Modules → Dependencies → “+” → JARs or directories** → elige ese `.jar`.
3. Ejecuta `Main` (clic derecho → Run 'Main.main()').

Si MySQL está apagado o falta importar el script, el programa lo dice al arrancar con los pasos a revisar.

## 3. Si tu XAMPP tiene otra configuración

Los datos de conexión están en `com/salud/citas/infrastructure/config/ConexionBD.java`:

| Dato       | Valor por defecto de XAMPP                 |
|------------|--------------------------------------------|
| URL        | `jdbc:mariadb://localhost:3306/salud360`   |
| Usuario    | `root`                                     |
| Contraseña | *(vacía)*                                  |

Cámbialos ahí si tu puerto, usuario o contraseña son distintos.

## 4. Qué se agregó (para explicarlo en la presentación)

El proyecto usa arquitectura hexagonal, así que **la lógica (services, vistas, dominio) no se modificó**. Solo se cambió *dónde se guardan los datos*:

| Antes (memoria)                    | Ahora (MySQL)                    |
|------------------------------------|----------------------------------|
| `UsuarioRepositoryMemoria`         | `UsuarioRepositoryJdbc`          |
| `CitaRepositoryMemoria`            | `CitaRepositoryJdbc`             |
| `PagoRepositoryMemoria`            | `PagoRepositoryJdbc`             |
| `ProfesionalRepositoryMemoria`     | `ProfesionalRepositoryJdbc`      |
| `EspecialidadRepositoryMemoria`    | `EspecialidadRepositoryJdbc`     |

- Los nuevos repositorios implementan **los mismos puertos** (`*RepositoryPort`) que los de memoria.
- `infrastructure/config/ConexionBD.java`: abre las conexiones JDBC y prueba la conexión al iniciar.
- `infrastructure/config/RepositorioException.java`: errores de BD que se muestran como `[!]` en el menú.
- `Main.java`: único archivo existente que cambió (crea los repos JDBC en lugar de los de memoria).
- Los repos en memoria y `PruebaFlujo` se dejaron intactos (siguen sirviendo para pruebas sin base de datos).

### Tablas

`usuario` → `paciente` (1 a 1, mismo id) · `especialidad` ↔ `profesional` (muchos a muchos con `profesional_especialidad`) · `pago` · `cita` (une paciente, profesional y pago) → `confirmacion_cita` → `notificacion`.

Se usan `PreparedStatement` (evita inyección SQL) y transacciones al guardar usuario+paciente y cita+confirmación (se guarda todo o nada).

## 5. Posibles problemas

| Mensaje | Solución |
|---|---|
| `Connection refused` | MySQL no está iniciado en XAMPP (botón Start). |
| `Unknown database 'salud360'` | Falta importar `database/salud360.sql`. |
| `Access denied for user 'root'` | Tu root tiene contraseña: ponla en `ConexionBD`. |
| `No suitable driver` | Falta el `.jar` en las dependencias (ver paso 2). |
| Puerto 3306 ocupado | En XAMPP cambia el puerto de MySQL y actualiza la URL en `ConexionBD`. |
