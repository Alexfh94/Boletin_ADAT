package com.carballeira;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import static com.carballeira.AccesoBD.conectar;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3307/ejerciciosboletin";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        crearProcedimiento();
        while (opcion != 9) {
            // Menú de opciones principal
            System.out.println("Seleccione una opción:");
            System.out.println("1. Mostrar departamentos(Ejercicio 1)");
            System.out.println("2. Modificar sin sentencias preparadas (Ejercicio 2)");
            System.out.println("3. Modificar con sentencias preparadas (Ejercicio 3)");
            System.out.println("4. Modificar con transacciones (Ejercicio 4)");
            System.out.println("5. Menú de operaciones con departamentos (Ejercicio 9)");
            System.out.println("9. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    mostrarDepartamentos();
                    break;
                case 2:
                    modificarDepartamentoSinPreparadas(scanner);
                    break;
                case 3:
                    modificarDepartamentoConPreparadas(scanner);
                    break;
                case 4:
                    modificarDepartamentoConTransacciones(scanner);
                    break;
                case 5:
                    menuAccesoBD(scanner);
                    break;
                case 9:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente nuevamente.");
            }
        }
        scanner.close();
    }

    // Métodos de los ejercicios anteriores (1 al 4)
    public static void mostrarDepartamentos() {
        String consulta = "SELECT dept_no, dnombre FROM departamentos";

        try (Connection conexion = conectar(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(consulta)) {
            System.out.println("Departamentos:");
            while (rs.next()) {
                System.out.println("Número: " + rs.getInt("dept_no") + ", Nombre: " + rs.getString("dnombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los departamentos: " + e.getMessage());
        }
    }

    public static void modificarDepartamentoSinPreparadas(Scanner scanner) {
        System.out.println("Introduce numero de departamento");
        int numeroDepartamento = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        System.out.println("Introduce el nuevo nombre del departamento");
        String nuevoNombre = scanner.nextLine();
        String consulta = "UPDATE departamentos SET dnombre = '" + nuevoNombre + "' WHERE dept_no = " + numeroDepartamento;
        try (Connection conexion = conectar(); Statement stmt = conexion.createStatement()) {
            int filasAfectadas = stmt.executeUpdate(consulta);
            System.out.println("Filas afectadas : " + filasAfectadas);
        } catch (SQLException e) {
            System.err.println("Error al modificar el departamento: " + e.getMessage());
        }
    }

    public static void modificarDepartamentoConPreparadas(Scanner scanner) {
        System.out.println("Introduce numero de departamento");
        int numeroDepartamento = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        System.out.println("Introduce el nuevo nombre del departamento");
        String nuevoNombre = scanner.nextLine();

        String consulta = "UPDATE departamentos SET dnombre = ? WHERE dept_no = ?";

        try (Connection conexion = conectar(); PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setString(1, nuevoNombre);
            pstmt.setInt(2, numeroDepartamento);

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Filas afectadas : " + filasAfectadas);
        } catch (SQLException e) {
            System.err.println("Error al modificar el departamento: " + e.getMessage());
        }
    }

    public static void modificarDepartamentoConTransacciones(Scanner scanner) {
        System.out.println("Introduce numero de departamento");
        int numeroDepartamento = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        System.out.println("Introduce el nuevo nombre del departamento");
        String nuevoNombre = scanner.nextLine();

        String consulta = "UPDATE departamentos SET dnombre = ? WHERE dept_no = ?";

        try (Connection conexion = conectar()) {
            conexion.setAutoCommit(false); // Inicia la transacción

            try (PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
                pstmt.setString(1, nuevoNombre);
                pstmt.setInt(2, numeroDepartamento);

                int filasAfectadas = pstmt.executeUpdate();
                System.out.println("Filas afectadas (transacción): " + filasAfectadas);

                conexion.commit(); // Confirma la transacción
                System.out.println("Transacción completada.");
            } catch (SQLException e) {
                conexion.rollback(); // Revierte la transacción en caso de error
                System.err.println("Error en la transacción, se hizo rollback: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar o manejar la transacción: " + e.getMessage());
        }
    }
    public static void crearProcedimiento() {
        Connection connection = null;
        Statement statement = null;

        try {
            // Conexión a la base de datos
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Crear la sentencia para eliminar y luego crear el procedimiento
            statement = connection.createStatement();

            // Sentencia para eliminar el procedimiento si existe
            String dropProcedure = "DROP PROCEDURE IF EXISTS actualizaDept";
            statement.execute(dropProcedure);

            // Sentencia para crear el procedimiento
            String createProcedure =
                    "CREATE PROCEDURE actualizaDept(cod INT, localidad VARCHAR(13)) " +
                            "BEGIN " +
                            "UPDATE dept SET loc = localidad WHERE deptno = cod; " +
                            "END";

            // Ejecutar la creación del procedimiento almacenado
            statement.execute(createProcedure);

            System.out.println("Procedimiento creado con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Menú de operaciones para la clase AccesoBD
    public static void menuAccesoBD(Scanner scanner) {
        int opcion = 0;

        while (opcion != 9) {
            System.out.println("\nMenú de operaciones con departamentos:");
            System.out.println("1. Insertar departamento");
            System.out.println("2. Obtener todos los departamentos");
            System.out.println("3. Obtener departamento por número");
            System.out.println("4. Actualizar departamento");
            System.out.println("5. Eliminar departamento");
            System.out.println("6. Actualizar localidad usando el procedimiento almacenado");

            System.out.println("9. Volver al menú principal");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el número del departamento: ");
                    int numero = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea
                    System.out.print("Ingrese el nombre del departamento: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Ingrese la localidad del departamento: ");
                    String localidad = scanner.nextLine();
                    AccesoBD.insertarDepartamento(numero, nombre, localidad);
                    break;
                case 2:
                    ArrayList<Departamento> departamentos = AccesoBD.obtenerTodosLosDepartamentos();
                    System.out.println("Departamentos:");
                    for (Departamento dept : departamentos) {
                        System.out.println(dept);
                    }
                    break;
                case 3:
                    System.out.print("Ingrese el número del departamento a buscar: ");
                    int numBuscar = scanner.nextInt();
                    Departamento dept = AccesoBD.obtenerDepartamentoPorNumero(numBuscar);
                    if (dept != null) {
                        System.out.println("Departamento encontrado: " + dept);
                    } else {
                        System.out.println("Departamento no encontrado.");
                    }
                    break;
                case 4:
                    System.out.print("Ingrese el número del departamento a actualizar: ");
                    int numActualizar = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea
                    System.out.print("Ingrese el nuevo nombre del departamento: ");
                    String nuevoNombre = scanner.nextLine();
                    System.out.print("Ingrese la nueva localidad del departamento: ");
                    String nuevaLocalidad = scanner.nextLine();
                    Departamento deptActualizar = new Departamento(numActualizar, nuevoNombre, nuevaLocalidad);
                    AccesoBD.actualizarDepartamento(deptActualizar);
                    break;
                case 5:
                    System.out.print("Ingrese el número del departamento a eliminar: ");
                    int numEliminar = scanner.nextInt();
                    AccesoBD.eliminarDepartamento(numEliminar);
                    break;

                case 6:
                    System.out.println("Introduce el número del departamento:");
                    int deptNumero = scanner.nextInt();
                    scanner.nextLine(); // Limpiar el buffer
                    System.out.println("Introduce la nueva localidad:");
                    String nuevaLocalidad2 = scanner.nextLine();
                    AccesoBD.actualizarLocalidad(deptNumero, nuevaLocalidad2);
                    break;

                case 9:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente nuevamente.");
            }
        }
    }


}
