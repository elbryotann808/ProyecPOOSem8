package Dao;

import DB.ConnectionDB;
import Model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    // Crear un nuevo producto
    public boolean create(Product p) throws SQLException {
        String sql =  "INSERT INTO products" + "(code, name, description, priceBase, priceSale, category, stock) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try{
            conn = ConnectionDB.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos");
            }

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, p.getCode());
                ps.setString(2, p.getName());
                ps.setString(3, p.getDescription());
                ps.setBigDecimal(4, p.getPriceBase());
                ps.setBigDecimal(5, p.getPriceSale());
                ps.setString(6, p.getCategory());
                ps.setInt(7, p.getStock());
                int rows = ps.executeUpdate();
                if(rows != 1){
                    throw new SQLException("No se incerto 1 una fila");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.out.println("Error durante el rollback al crear producto:");
                    rbEx.printStackTrace();
                }
            }
            System.out.println("Error al crear el producto: " + p.getCode());
            ex.printStackTrace();
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Error al cerrar la conexión tras crear producto:");
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // Traer informacion de un producto por su codigo de producto
    public Product readByCode(String code)throws SQLException {
        String slq = "SELECT * FROM products WHERE code = ?";

        Connection conn = null;
        try{
            conn = ConnectionDB.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos");
            }

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(slq)){
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return new Product(
                                rs.getString("code"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getBigDecimal("priceBase"),
                                rs.getBigDecimal("priceSale"),
                                rs.getString("category"),
                                rs.getInt("stock")
                        );
                    }else {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al leer el producto con código: " + code);
            ex.printStackTrace();
            throw ex;

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Error al cerrar la conexión tras leer producto:");
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // Traer todo los producto de la base de datos
    public List<Product> readAll() throws SQLException{
        String slq = "SELECT * FROM products";
        Connection conn = null;
        List<Product> list = new ArrayList<>();

        try {
            conn = ConnectionDB.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexión con la base de datos");
            }

            try ( PreparedStatement ps = conn.prepareStatement(slq);
                  ResultSet rs = ps.executeQuery();
            ){
                while (rs.next()){
                    list.add(new Product(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("priceBase"),
                            rs.getBigDecimal("priceSale"),
                            rs.getString("category"),
                            rs.getInt("stock")
                    ));
                }
            }
            return list;
        }catch (SQLException ex) {
            System.out.println("Error al leer todos los productos");
            ex.printStackTrace();
            throw ex;

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Error al cerrar la conexión tras leer todos los productos:");
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // Actualizar un producto
    public boolean update(Product p)throws SQLException{
        String slq = "UPDATE products SET name=?, description=?, priceBase=?, priceSale=?, category=?, stock=? WHERE code = ?";
        Connection conn = null;
        try{
            conn = ConnectionDB.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos");
            }
            conn.setAutoCommit(false);


            try (PreparedStatement ps = conn.prepareStatement(slq)) {
                ps.setString(1, p.getName());
                ps.setString(2, p.getDescription());
                ps.setBigDecimal(3, p.getPriceBase());
                ps.setBigDecimal(4, p.getPriceSale());
                ps.setString(5, p.getCategory());
                ps.setInt(6, p.getStock());
                ps.setString(7, p.getCode());

                int rows = ps.executeUpdate();
                if (rows != 1){
                    throw new SQLException("No se actualizo ninguna fila");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.out.println("Error durante el rollback al actualizar producto:");
                    rbEx.printStackTrace();
                }
            }
            System.out.println("Error al actualizar el producto: " + p.getCode());
            ex.printStackTrace();
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Error al cerrar la conexión tras actualizar producto:");
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // Actualiza el stock de un producto en especifico
    public boolean updateStock(String codeProduct, int amount)throws SQLException {
        String sqlGetStock = "SELECT stock FROM products WHERE code = ?";
        String sqlUpdateStock = "UPDATE products SET stock = stock + ? WHERE code = ?";

        Connection conn = null;
        try {
            conn = ConnectionDB.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos");
            }
            conn.setAutoCommit(false);

            int currentStock;
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetStock)) {
                psGet.setString(1, codeProduct);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        currentStock = rs.getInt("stock");
                    } else {
                        throw new SQLException("Producto no se ha encontrado");
                    }
                }
            }

            try (PreparedStatement psUp = conn.prepareStatement(sqlUpdateStock)) {
                psUp.setInt(1, amount);
                psUp.setString(2, codeProduct);
                int rows = psUp.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("No se pudo actualizar el stock");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.out.println("Error durante el rollback al aumentar stock:");
                    rbEx.printStackTrace();
                }
            }
            System.out.println("Error al aumentar el stock del producto: " + codeProduct);
            ex.printStackTrace();
            throw ex;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Error al cerrar la conexión tras aumentar stock:");
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // Eliminar un producto por su codigo
    public boolean delete(String code)throws SQLException {
        String sql =  "DELETE FROM products WHERE code = ?";
        Connection conn = null;
        try {
            conn = ConnectionDB.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexión con la base de datos");
            }
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, code);
                int rows = ps.executeUpdate();
                if (rows != 1) {
                    throw new SQLException("No se elimino ningun fila de la base de datos");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e){
            if (conn != null){
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        }finally {
            if (conn != null){
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}

