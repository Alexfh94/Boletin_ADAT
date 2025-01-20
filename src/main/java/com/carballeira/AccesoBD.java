package com.carballeira;

import java.sql.*;
import java.util.ArrayList;

public class AccesoBD {
    private static final String URL = "jdbc:mysql://localhost:3307/ejerciciosboletin";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // 1. Conectar a la base de datos
    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver JDBC: " + e.getMessage());
            throw new SQLException("Driver no encontrado", e);
        }
    }

    // 2. Insertar un departamento (recibe 3 argumentos)
    public static void insertarDepartamento(int numero, String nombre, String localidad) {
        String consulta = "INSERT INTO dept (DEPTNO, DNAME, LOC) VALUES (?, ?, ?)";

        try (Connection conexion = conectar();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, numero);
            pstmt.setString(2, nombre);
            pstmt.setString(3, localidad);

            int filas = pstmt.executeUpdate();
            System.out.println("Filas afectadas: " + filas);
        } catch (SQLException e) {
            System.err.println("Error al insertar el departamento: " + e.getMessage());
        }
    }

    // 3. Insertar un departamento (recibe un objeto Departamento)
    public static void insertarDepartamento(Departamento dept) {
        insertarDepartamento(dept.getNumero(), dept.getNombre(), dept.getLocalidad());
    }

    // 4. Devolver un ArrayList con todos los departamentos
    public static ArrayList<Departamento> obtenerTodosLosDepartamentos() {
        String consulta = "SELECT DEPTNO, DNAME, LOC FROM dept";
        ArrayList<Departamento> departamentos = new ArrayList<>();

        try (Connection conexion = conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {
            while (rs.next()) {
                int numero = rs.getInt("DEPTNO");
                String nombre = rs.getString("DNAME");
                String localidad = rs.getString("LOC");
                departamentos.add(new Departamento(numero, nombre, localidad));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los departamentos: " + e.getMessage());
        }

        return departamentos;
    }

    // 5. Obtener un departamento por número
    public static Departamento obtenerDepartamentoPorNumero(int numero) {
        String consulta = "SELECT DEPTNO, DNAME, LOC FROM dept WHERE DEPTNO = ?";
        Departamento departamento = null;

        try (Connection conexion = conectar();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, numero);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("DNAME");
                    String localidad = rs.getString("LOC");
                    departamento = new Departamento(numero, nombre, localidad);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el departamento: " + e.getMessage());
        }

        return departamento;
    }

    // 6. Actualizar un departamento
    public static void actualizarDepartamento(Departamento dept) {
        String consulta = "UPDATE dept SET DNAME = ?, LOC = ? WHERE DEPTNO = ?";

        try (Connection conexion = conectar();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setString(1, dept.getNombre());
            pstmt.setString(2, dept.getLocalidad());
            pstmt.setInt(3, dept.getNumero());

            int filas = pstmt.executeUpdate();
            System.out.println("Filas actualizadas: " + filas);
        } catch (SQLException e) {
            System.err.println("Error al actualizar el departamento: " + e.getMessage());
        }
    }

    // 7. Eliminar un departamento por número
    public static void eliminarDepartamento(int numero) {
        String consulta = "DELETE FROM dept WHERE DEPTNO = ?";

        try (Connection conexion = conectar();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, numero);

            int filas = pstmt.executeUpdate();
            System.out.println("Filas eliminadas: " + filas);
        } catch (SQLException e) {
            System.err.println("Error al eliminar el departamento: " + e.getMessage());
        }
    }

    // 8. Eliminar un departamento y devolver filas afectadas
    public static int eliminarDepartamentoConFilasAfectadas(int numero) {
        String consulta = "DELETE FROM dept WHERE DEPTNO = ?";
        int filas = 0;

        try (Connection conexion = conectar();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, numero);
            filas = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar el departamento: " + e.getMessage());
        }

        return filas;
    }

    // 9. Actualizar localidad de un departamento utilizando el procedimiento almacenado
    public static void actualizarLocalidad(int numeroDepartamento, String nuevaLocalidad) {
        String procedimiento = "{ CALL actualizaDept(?, ?) }";

        try (Connection conexion = conectar();
             CallableStatement cstmt = conexion.prepareCall(procedimiento)) {

            // Establecer los parámetros del procedimiento almacenado
            cstmt.setInt(1, numeroDepartamento);
            cstmt.setString(2, nuevaLocalidad);

            // Ejecutar el procedimiento
            int filasAfectadas = cstmt.executeUpdate();
            System.out.println("Filas actualizadas: " + filasAfectadas);
        } catch (SQLException e) {
            System.err.println("Error al actualizar la localidad con el procedimiento: " + e.getMessage());
        }
    }

}
