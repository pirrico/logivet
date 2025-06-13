/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.dao;

import Prontuario.model.Atendimento;
import Prontuario.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author João Pedro
 */
public class AtendimentoDAO {

    public int salvarAtendimento(Atendimento atendimento) {
        String sql = "INSERT INTO atendimentos (nome_animal, professor, especie, data_cadastro) VALUES (?, ?, ?, ?)"; // Adicionando data_cadastro
        int idGerado = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, atendimento.getNomeAnimal());
            stmt.setString(2, atendimento.getVeterinario()); // Note: getVeterinario() do modelo Atendimento mapeia para 'professor' no banco
            stmt.setString(3, atendimento.getEspecie());
            stmt.setString(4, LocalDate.now().toString()); // Adiciona a data atual ao salvar
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
        return idGerado;
    }

    public List<Atendimento> carregarAtendimentosEmAndamento() {
        List<Atendimento> atendimentos = new ArrayList<>();
        // Query corrigida para usar 'professor' e 'data_cadastro'
        String sql = "SELECT id, nome_animal, professor, especie, data_cadastro FROM atendimentos ORDER BY data_cadastro DESC"; // Selecionando data_cadastro também
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nomeAnimal = rs.getString("nome_animal");
                String professor = rs.getString("professor"); // CORRIGIDO: Lendo a coluna 'professor'
                String especie = rs.getString("especie");
                // String dataCadastro = rs.getString("data_cadastro"); // Se precisar usar a data diretamente no objeto Atendimento

                Atendimento atendimento = new Atendimento(id, nomeAnimal, professor, especie);

                // Carregar produtos associados a este atendimento
                List<Produto> produtosDoAtendimento = carregarProdutosDoAtendimento(id);
                atendimento.setProdutos(produtosDoAtendimento); // setProdutos agora deve funcionar

                atendimentos.add(atendimento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar atendimentos em andamento: " + e.getMessage());
            e.printStackTrace();
        }
        return atendimentos;
    }

    private List<Produto> carregarProdutosDoAtendimento(int atendimentoId) {
        List<Produto> produtos = new ArrayList<>();
        // Query corrigida para usar 'quantidade_utilizada'
        String sql = "SELECT p.id, p.codigo_barras, p.nome, pa.quantidade_utilizada FROM produtos_atendimentos pa JOIN produtos p ON pa.produto_id = p.id WHERE pa.atendimento_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, atendimentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(new Produto(
                            rs.getInt("id"),
                            rs.getString("codigo_barras"),
                            rs.getString("nome"),
                            rs.getInt("quantidade_utilizada") // CORRIGIDO: Lendo a coluna 'quantidade_utilizada'
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar produtos do atendimento: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    // In AtendimentoDAO.java

public void removerAtendimento(int id) {
    String sqlRemoveProdutos = "DELETE FROM produtos_atendimentos WHERE atendimento_id = ?"; // SQL to delete child records
    String sqlRemoveAtendimento = "DELETE FROM atendimentos WHERE id = ?"; // SQL to delete parent record

    Connection conn = null;
    PreparedStatement stmtRemoveProdutos = null;
    PreparedStatement stmtRemoveAtendimento = null;

    try {
        conn = DatabaseConnection.getConnection(); // Get a connection
        if (conn != null) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete associated products first
            stmtRemoveProdutos = conn.prepareStatement(sqlRemoveProdutos);
            stmtRemoveProdutos.setInt(1, id);
            stmtRemoveProdutos.executeUpdate();

            // 2. Then delete the attendance itself
            stmtRemoveAtendimento = conn.prepareStatement(sqlRemoveAtendimento);
            stmtRemoveAtendimento.setInt(1, id);
            stmtRemoveAtendimento.executeUpdate();

            conn.commit(); // Commit the transaction
            System.out.println("Atendimento ID " + id + " e seus produtos associados removidos com sucesso.");
        }
    } catch (SQLException e) {
        System.err.println("Erro ao remover atendimento e seus produtos: " + e.getMessage());
        e.printStackTrace();
        try {
            if (conn != null) {
                conn.rollback(); // Rollback in case of error
                System.err.println("Rollback realizado após erro na remoção de atendimento.");
            }
        } catch (SQLException ex) {
            System.err.println("Erro durante o rollback: " + ex.getMessage());
        }
    } finally {
        try {
            if (stmtRemoveProdutos != null) stmtRemoveProdutos.close();
            if (stmtRemoveAtendimento != null) stmtRemoveAtendimento.close();
            DatabaseConnection.closeConnection(conn); // Close the connection
        } catch (SQLException e) {
            System.err.println("Erro ao fechar recursos após remoção: " + e.getMessage());
        }
    }
}

    // Método para salvar log de atendimento - ASSINATURA E CORPO CORRIGIDOS
    public void salvarLogRegistro(String dataRegistro, String horario, String professor,
                                String animal, String especie, String responsavel, String produtos) {
        // Query corrigida para usar 'professor'
        String sql = "INSERT INTO log_registro (data_registro, horario, professor, animal, especie, responsavel, produtos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dataRegistro);
            stmt.setString(2, horario);
            stmt.setString(3, professor);
            stmt.setString(4, animal);
            stmt.setString(5, especie);
            stmt.setString(6, responsavel);
            stmt.setString(7, produtos);
            stmt.executeUpdate();
            System.out.println("Log de atendimento salvo com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar log de atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}