/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.dao;



 // Importa a classe Produto do pacote model

import Prontuario.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // Busca um produto pelo nome na tabela 'produtos' (estoque)
    public Produto buscarProdutoPorNome(String nomeProduto) {
        String sql = "SELECT id, codigo_barras, nome, quantidade FROM produtos WHERE nome = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nomeProduto);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Produto(
                        rs.getInt("id"),
                        rs.getString("codigo_barras"),
                        rs.getString("nome"),
                        rs.getInt("quantidade")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por nome: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return null;
    }
    
    // Salva um item de atendimento na tabela 'produtos_atendimentos'
    // A conexão é passada para garantir que faça parte da mesma transação do AtendimentoDAO
    public void salvarItemAtendimento(Connection conn, int atendimentoId, int produtoId, int quantidadeUtilizada) throws SQLException {
        String sql = "INSERT INTO produtos_atendimentos (atendimento_id, produto_id, quantidade_utilizada) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, atendimentoId);
            stmt.setInt(2, produtoId);
            stmt.setInt(3, quantidadeUtilizada);
            stmt.executeUpdate();
            System.out.println("Item de atendimento salvo para atendimento " + atendimentoId + ", produto ID " + produtoId + " x" + quantidadeUtilizada);
        }
    }
    
    // Atualiza a quantidade de um item de atendimento na tabela 'produtos_atendimentos'
    public void atualizarItemAtendimento(int atendimentoId, int produtoId, int novaQuantidade) {
        String sql = "UPDATE produtos_atendimentos SET quantidade_utilizada = ? WHERE atendimento_id = ? AND produto_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, novaQuantidade);
                stmt.setInt(2, atendimentoId);
                stmt.setInt(3, produtoId);
                stmt.executeUpdate();
                System.out.println("Item de atendimento atualizado para atendimento " + atendimentoId + ", produto ID " + produtoId + " nova quantidade: " + novaQuantidade);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar item de atendimento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    // Remove um item de atendimento da tabela 'produtos_atendimentos'
    public void removerItemAtendimento(int atendimentoId, int produtoId) {
        String sql = "DELETE FROM produtos_atendimentos WHERE atendimento_id = ? AND produto_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, atendimentoId);
                stmt.setInt(2, produtoId);
                stmt.executeUpdate();
                System.out.println("Item de atendimento removido do atendimento " + atendimentoId + ", produto ID " + produtoId);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover item de atendimento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    // Carrega os produtos específicos de um atendimento, juntando com informações da tabela 'produtos'
    public List<Produto> carregarItensAtendimento(int atendimentoId) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT pa.produto_id, p.codigo_barras, p.nome, pa.quantidade_utilizada " +
                     "FROM produtos_atendimentos pa " +
                     "JOIN produtos p ON pa.produto_id = p.id " +
                     "WHERE pa.atendimento_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, atendimentoId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int produtoId = rs.getInt("produto_id");
                    String codigoBarras = rs.getString("codigo_barras");
                    String nome = rs.getString("nome");
                    int quantidadeUtilizada = rs.getInt("quantidade_utilizada");
                    // Cria um objeto Produto com o ID do banco e o nome
                    produtos.add(new Produto(produtoId, codigoBarras, nome, quantidadeUtilizada));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar itens de atendimento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return produtos;
    }

    // Carrega todos os produtos disponíveis no estoque para o JComboBox
    public List<Produto> carregarProdutosEstoque() {
        List<Produto> produtosEstoque = new ArrayList<>();
        String sql = "SELECT id, codigo_barras, nome, quantidade FROM produtos ORDER BY nome";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String codigoBarras = rs.getString("codigo_barras");
                    String nome = rs.getString("nome");
                    int quantidade = rs.getInt("quantidade");
                    produtosEstoque.add(new Produto(id, codigoBarras, nome, quantidade));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar produtos do estoque: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return produtosEstoque;
    }

    // Atualiza a quantidade em estoque de um produto na tabela 'produtos'
    public void atualizarEstoqueProduto(String nomeProduto, int quantidadeAlteracao) {
        String sql = "UPDATE produtos SET quantidade = quantidade + ? WHERE nome = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, quantidadeAlteracao);
                stmt.setString(2, nomeProduto);
                stmt.executeUpdate();
                System.out.println("Estoque do produto '" + nomeProduto + "' atualizado em " + quantidadeAlteracao);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estoque do produto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }
}