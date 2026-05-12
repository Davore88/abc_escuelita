# Sistema de Control Escolar y Accesos

Un sistema web responsivo (adaptable a móviles y tablets) desarrollado con Spring Boot y Java 17 para gestionar el control de accesos escolares de múltiples instituciones mediante lectura de códigos QR.

## Características Principales

*   **Multitenant:** Soporte para múltiples instituciones, cada una administrada por sus propios usuarios (Administradores).
*   **Gestión de Alumnos y Tutores:** 
    *   Registro de alumnos.
    *   Asignación de 2 a 5 tutores por alumno.
    *   Almacenamiento de fotografías en base de datos para identificación visual rápida.
    *   Registro de correos electrónicos para tutores.
*   **Módulo de Credenciales:** Generación automatizada de credenciales en formato PDF con códigos QR únicos por tutor usando la librería `OpenPDF` y `ZXing`.
*   **Control de Accesos (Escáner QR):** Interfaz adaptativa que utiliza la cámara de dispositivos móviles (mediante `html5-qrcode`) para escanear las credenciales y mostrar inmediatamente la fotografía del tutor y del alumno autorizado, registrando la hora de entrada o salida en una bitácora.
*   **Módulo de Avisos:** Envío masivo de notificaciones y anuncios por correo electrónico a todos los tutores de un grupo escolar específico.
*   **Reportes:** Consultas de la bitácora de accesos filtradas por grupo, alumno y rango de fechas.
*   **Diseño Moderno:** Interfaz de usuario construida con **Thymeleaf**, **Tailwind CSS** y **FontAwesome** para una experiencia atractiva, fluida y colorida.

## Tecnologías Utilizadas

*   **Backend:** Java 17, Spring Boot 3.x (Web, Data JPA, Security, Mail, Validation)
*   **Base de Datos:** MySQL
*   **Frontend:** HTML5, Thymeleaf, Tailwind CSS (vía CDN), JavaScript (`html5-qrcode`)
*   **Librerías Adicionales:** 
    *   `ZXing` (Zebra Crossing) para generación y validación de códigos QR.
    *   `OpenPDF` para la generación de documentos PDF.

## Estructura de la Base de Datos

El sistema se basa en un esquema relacional con las siguientes entidades clave:
*   **Institution:** Datos de la escuela.
*   **AdminUser:** Usuarios administrativos.
*   **Student:** Alumnos matriculados.
*   **Tutor:** Padres o tutores autorizados.
*   **Credential:** Códigos QR vinculados a la relación Tutor-Alumno.
*   **AccessLog:** Bitácora histórica de entradas y salidas.
*   **Announcement:** Registro histórico de avisos enviados por correo.

## Requisitos Previos

Para ejecutar el proyecto localmente, necesitas:
1.  [Java Development Kit (JDK) 17](https://adoptium.net/)
2.  [MySQL Server](https://dev.mysql.com/downloads/mysql/) corriendo en el puerto `3306`.

## Configuración y Ejecución

1.  **Configurar Base de Datos:**
    Crea una base de datos en MySQL llamada `escuelita_db` (la tabla y esquema se crearán automáticamente al arrancar gracias a Hibernate/JPA).
    
    Abre el archivo `src/main/resources/application.properties` y verifica que tus credenciales coincidan:
    ```properties
    spring.datasource.username=
    spring.datasource.password=
    ```

2.  **Configurar Servidor de Correo (Para el Módulo de Avisos):**
    En el mismo archivo `application.properties`, agrega los datos de tu servidor SMTP (Ej. Gmail):
    ```properties
    spring.mail.username=tu_correo@gmail.com
    spring.mail.password=tu_contraseña_de_aplicacion
    ```

3.  **Ejecutar la Aplicación:**
    Desde la raíz del proyecto (en la carpeta `abc_Escuelita`), ejecuta el siguiente comando en tu terminal de PowerShell o CMD:
    ```bash
    mvn spring-boot:run
    ```

4.  **Acceder al Sistema:**
    Abre tu navegador web y visita: `http://localhost:8080/dashboard`

## Arquitectura y Diseño Visual

El frontend ha sido diseñado con un enfoque *mobile-first*, garantizando que los administradores de la escuela puedan utilizar una tablet o celular para ubicarse en la entrada de la institución y escanear los códigos QR de forma cómoda y sin requerir hardware especializado (pistolas láser).

---
*Proyecto de Control Escolar - Desarrollado con Spring Boot y Tailwind CSS.*
