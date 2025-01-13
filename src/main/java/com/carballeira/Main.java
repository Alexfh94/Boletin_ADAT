package com.carballeira;

import java.sql.*;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3307/ejerciciosboletin";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        // Ejercicio 1: Mostrar departamentos
        System.out.println("Ejercicio 1: Visualizar departamentos");
        mostrarDepartamentos();

        // Ejercicio 2: Modificar sin sentencias preparadas
        System.out.println("\nEjercicio 2: Modificar sin sentencias preparadas");
        modificarDepartamentoSinPreparadas(10, "FINANZAS");

        // Ejercicio 3: Modificar con sentencias preparadas
        System.out.println("\nEjercicio 3: Modificar con sentencias preparadas");
        modificarDepartamentoConPreparadas(20, "INVESTIGACIÓN");

        // Ejercicio 4: Modificar con transacciones
        System.out.println("\nEjercicio 4: Modificar con transacciones");
        modificarDepartamentoConTransacciones(30, "VENTAS");
    }




    // Conectar a la base de datos
    public static Connection conectar() throws SQLException {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
        return conexion;
    }

    // Ejercicio 1: Visualizar número y nombre de todos los departamentos
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

    // Ejercicio 2: Modificar departamento sin sentencias preparadas
    public static void modificarDepartamentoSinPreparadas(int numeroDepartamento, String nuevoNombre) {
        String consulta = "UPDATE departamentos SET dnombre = '" + nuevoNombre + "' WHERE dept_no = " + numeroDepartamento;

        try (Connection conexion = conectar(); Statement stmt = conexion.createStatement()) {
            int filasAfectadas = stmt.executeUpdate(consulta);
            System.out.println("Filas afectadas : " + filasAfectadas);
        } catch (SQLException e) {
            System.err.println("Error al modificar el departamento: " + e.getMessage());
        }
    }

    // Ejercicio 3: Modificar departamento con sentencias preparadas
    public static void modificarDepartamentoConPreparadas(int numeroDepartamento, String nuevoNombre) {
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

    // Ejercicio 4: Modificar departamento con transacciones
    public static void modificarDepartamentoConTransacciones(int numeroDepartamento, String nuevoNombre) {
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

}
