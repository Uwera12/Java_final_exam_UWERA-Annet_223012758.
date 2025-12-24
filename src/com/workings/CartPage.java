package com.workings;

import com.test.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

    public class CartPage extends JFrame {

        private JTable cartTable;
        private DefaultTableModel model;
        private int userId;

        public CartPage(int userId) {
            this.userId = userId;
            setTitle("My Cart");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            model = new DefaultTableModel(new String[]{"Product", "Price", "Quantity", "Total"}, 0);
            cartTable = new JTable(model);
            JScrollPane scroll = new JScrollPane(cartTable);

            JButton orderBtn = new JButton("Place Order");
            orderBtn.addActionListener(e -> placeOrder());

            add(scroll, BorderLayout.CENTER);
            add(orderBtn, BorderLayout.SOUTH);

            loadCart();
            setVisible(true);
        }

        private void loadCart() {
            model.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                SELECT p.name, p.price, c.quantity, (p.price*c.quantity) AS total
                FROM cart_items c JOIN products p ON c.product_id = p.product_id
                WHERE c.user_id=?
            """;
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDouble("total")
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void placeOrder() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                PreparedStatement totalPs = conn.prepareStatement(
                        "SELECT SUM(p.price*c.quantity) AS total FROM cart_items c JOIN products p ON c.product_id=p.product_id WHERE c.user_id=?"
                );
                totalPs.setInt(1, userId);
                ResultSet rs = totalPs.executeQuery();
                rs.next();
                double totalAmount = rs.getDouble("total");

                PreparedStatement orderPs = conn.prepareStatement(
                        "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                orderPs.setInt(1, userId);
                orderPs.setDouble(2, totalAmount);
                orderPs.executeUpdate();

                rs = orderPs.getGeneratedKeys();
                rs.next();
                int orderId = rs.getInt(1);

                PreparedStatement movePs = conn.prepareStatement("""
                INSERT INTO order_items (order_id, product_id, quantity, price)
                SELECT ?, c.product_id, c.quantity, p.price
                FROM cart_items c JOIN products p ON c.product_id=p.product_id
                WHERE c.user_id=?
            """);
                movePs.setInt(1, orderId);
                movePs.setInt(2, userId);
                movePs.executeUpdate();

                PreparedStatement stockPs = conn.prepareStatement("""
                UPDATE products p
                JOIN cart_items c ON p.product_id=c.product_id
                SET p.stock = p.stock - c.quantity
                WHERE c.user_id=?
            """);
                stockPs.setInt(1, userId);
                stockPs.executeUpdate();

                PreparedStatement clearPs = conn.prepareStatement("DELETE FROM cart_items WHERE user_id=?");
                clearPs.setInt(1, userId);
                clearPs.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                model.setRowCount(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void main(String[] args) {
            new CartPage(1);
        }
    }


   
