PersonalBankAccount

Prototipo de aplicación de gestión de finanzas personales que permite a los usuarios registrar ingresos y gastos, llevar un historial de transacciones y visualizar el balance total. Incluye funcionalidades de registro e inicio de sesión para múltiples usuarios.

Características

    Registro e inicio de sesión de usuarios: Cada usuario puede crear una cuenta y tener su propio historial de transacciones.
    Registro de ingresos y gastos: Permite registrar transacciones con cantidad y concepto.
    Historial de transacciones: Muestra un historial detallado con fecha y hora.
    Función de deshacer: Permite eliminar la última transacción registrada.
    Interfaz gráfica de usuario (GUI): Desarrollada con JavaFX para una experiencia interactiva.

Requisitos

    Java JDK 8 o superior: Asegúrate de tener instalado Java Development Kit en tu sistema.
    JavaFX: Incluido en JDK 8. Para versiones posteriores, necesitarás agregar las librerías de JavaFX.
    SQLite JDBC Driver: La aplicación utiliza una base de datos SQLite para almacenar usuarios y transacciones.

Instalación

    Clonar el repositorio

    bash

    git clone https://github.com/tu_usuario/PersonalBankAccount.git

    Importar el proyecto
        Importa el proyecto en tu IDE preferido como un proyecto Java.
        Configura el SDK de Java y asegúrate de que el proyecto reconoce las librerías de JavaFX si utilizas JDK 11 o superior
        (deberás añadir los módulos javafx.controls,javafx.fxml a las opciones de la MV de java: en IntelliJ -> run -> edit 
        configurations -> new configuration (+ application) -> modify options -> add VM options -> especificas los módulos a 
        añadir y la ruta).

    Configurar las dependencias
        JavaFX: Si utilizas JDK 11 o superior, descarga las librerías de JavaFX desde Gluon y agrégalas al proyecto.
        SQLite JDBC Driver: Descarga el driver JDBC para SQLite y añádelo al classpath del proyecto si no está incluido.

Uso

    Ejecutar la aplicación
        Ejecuta la clase LogInWindow, que contiene el método main.

    Registro de usuario
        En la ventana de inicio, ingresa un nombre de usuario y contraseña.
        Haz clic en "Registrarse" para crear una nueva cuenta.

    Iniciar sesión
        Ingresa tus credenciales y haz clic en "Iniciar Sesión".
        Si las credenciales son correctas, accederás a la aplicación principal.

    Registrar transacciones
        Ingresa una cantidad en el campo "Cantidad".
        Ingresa un concepto en el campo "Concepto" (máximo 16 caracteres).
        Haz clic en "Registrar ingreso" o "Registrar gasto" según corresponda.
        La transacción aparecerá en el historial con la fecha y hora correspondientes.

    Deshacer última transacción
        Haz clic en el botón "Deshacer" para eliminar la última transacción registrada.

    Cerrar la aplicación
        Al cerrar la aplicación, todas las transacciones se guardan automáticamente en la base de datos local.

Notas

    Base de datos: La base de datos PersonalBank.db se crea automáticamente en el directorio de inicio del usuario /home/user.
    Historial: El historial de transacciones utiliza una fuente monoespaciada para mantener la alineación.
    Concepto: El campo de concepto está limitado a 16 caracteres para mantener la interfaz limpia.

Contribuciones

Si deseas contribuir al proyecto, siéntete libre de hacer un fork del repositorio y enviar tus pull requests.


V-1.4.0-alpha
