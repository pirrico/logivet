/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.dao;

/**
 *
 * @author João Pedro
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane; // Para exibir mensagens de erro

public class DatabaseConnection {

    // ALtere estas constantes com as suas informações do banco de dados MySQL
    // Exemplo: "jdbc:mysql://localhost:3306/prontuario_vet"
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/logivet"; 
    private static final String USER = "root"; // Seu usuário do MySQL
    private static final String PASSWORD = "12345"; // Sua senha do MySQL

    /**
     * Estabelece e retorna uma conexão com o banco de dados MySQL.
     * @return Uma instância de Connection se a conexão for bem-sucedida, ou null em caso de erro.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Carrega o driver JDBC do MySQL.
            // O Class.forName() é necessário para registrar o driver JDBC.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Tenta estabelecer a conexão com o banco de dados usando as credenciais.
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
        } catch (ClassNotFoundException e) {
            // Captura erro se o driver JDBC não for encontrado.
            JOptionPane.showMessageDialog(null, "Driver JDBC do MySQL não encontrado. Adicione o JAR ao seu projeto.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprime o stack trace para depuração
        } catch (SQLException e) {
            // Captura erros relacionados à conexão SQL (usuário/senha errados, banco não existe, etc.)
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage() + "\nVerifique as credenciais e a URL.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprime o stack trace para depuração
        }
        return connection;
    }

    /**
     * Fecha uma conexão com o banco de dados.
     * @param connection A conexão a ser fechada.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão com o banco de dados fechada.");
            } catch (SQLException e) {
                // Captura erros ao fechar a conexão
                JOptionPane.showMessageDialog(null, "Erro ao fechar a conexão com o banco de dados: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace(); // Imprime o stack trace para depuração
            }
        }
    }

    // Método main para testar a conexão separadamente
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Teste de conexão bem-sucedido!");
            DatabaseConnection.closeConnection(conn);
        } else {
            System.out.println("Falha no teste de conexão.");
        }
    }
}
