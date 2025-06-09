/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.dao;



import Prontuario.model.Atendimento; // Importa a classe Atendimento do pacote model
import Prontuario.model.Produto;






import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // Para data_atendimento
import java.util.ArrayList;
import java.util.List;

public class AtendimentoDAO {

    public int salvarAtendimento(Atendimento atendimento) {
        // Colunas: nome_animal, especie, professor, data_atendimento
        String sql = "INSERT INTO atendimentos (nome_animal, especie, professor, data_atendimento) VALUES (?, ?, ?, ?)";
        int idGerado = -1;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                conn.setAutoCommit(false); // Inicia a transação
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, atendimento.getNomeAnimal());
                stmt.setString(2, atendimento.getEspecie());
                stmt.setString(3, atendimento.getVeterinario()); // 'getVeterinario' da sua classe Atendimento mapeia para 'professor' no banco
                stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // data_atendimento automática

                stmt.executeUpdate();

                // Obter o ID gerado para o atendimento
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    atendimento.setId(idGerado); // Define o ID no objeto Atendimento em memória
                }

                // Salvar os produtos do atendimento na tabela 'produtos_atendimentos'
                if (idGerado != -1 && !atendimento.getProdutos().isEmpty()) {
                    ProdutoDAO produtoDAO = new ProdutoDAO(); // Instancia ProdutoDAO (pode ser injetado)
                    for (Produto p : atendimento.getProdutos()) {
                        // Precisamos do ID do produto do estoque para salvar em produtos_atendimentos
                        // Busca o produto pelo NOME (que é o que vem do ComboBox e está no p.getNome())
                        Produto produtoDoEstoque = produtoDAO.buscarProdutoPorNome(p.getNome()); 
                        if (produtoDoEstoque != null) {
                            produtoDAO.salvarItemAtendimento(conn, idGerado, produtoDoEstoque.getId(), p.getQuantidade());
                        } else {
                            System.err.println("Produto '" + p.getNome() + "' não encontrado no estoque para salvar item de atendimento.");
                        }
                    }
                }

                conn.commit(); // Confirma a transação
                System.out.println("Atendimento salvo com sucesso! ID: " + idGerado);
                return idGerado;
            }
            return -1; // Conexão nula

        } catch (SQLException e) {
            System.err.println("Erro ao salvar atendimento: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Desfaz a transação em caso de erro
                    System.err.println("Rollback realizado.");
                }
            } catch (SQLException ex) {
                System.err.println("Erro durante o rollback: " + ex.getMessage());
            }
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn); // Fecha a conexão
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    public void removerAtendimento(int idAtendimento) {
        // Primeiro, remova os itens de atendimento associados da tabela 'produtos_atendimentos'
        String sqlRemoverItens = "DELETE FROM produtos_atendimentos WHERE atendimento_id = ?";
        // Depois, remova o atendimento em si da tabela 'atendimentos'
        String sqlRemoverAtendimento = "DELETE FROM atendimentos WHERE id = ?";

        Connection conn = null; 
        PreparedStatement stmtItens = null;
        PreparedStatement stmtAtendimento = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                conn.setAutoCommit(false); // Inicia transação

                stmtItens = conn.prepareStatement(sqlRemoverItens);
                stmtItens.setInt(1, idAtendimento);
                stmtItens.executeUpdate();

                stmtAtendimento = conn.prepareStatement(sqlRemoverAtendimento);
                stmtAtendimento.setInt(1, idAtendimento);
                stmtAtendimento.executeUpdate();

                conn.commit(); // Confirma a transação
                System.out.println("Atendimento e seus itens removidos com sucesso! ID: " + idAtendimento);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao remover atendimento: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Desfaz a transação em caso de erro
                    System.err.println("Rollback realizado.");
                }
            } catch (SQLException ex) {
                System.err.println("Erro durante o rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (stmtItens != null) stmtItens.close();
                if (stmtAtendimento != null) stmtAtendimento.close();
                DatabaseConnection.closeConnection(conn); // Fecha a conexão
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    public List<Atendimento> carregarAtendimentosEmAndamento() {
        List<Atendimento> atendimentos = new ArrayList<>();
        // Colunas: id, nome_animal, especie, professor
        String sql = "SELECT id, nome_animal, especie, professor FROM atendimentos ORDER BY data_atendimento DESC"; 

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
                    String nomeAnimal = rs.getString("nome_animal");
                    String especie = rs.getString("especie");
                    String professor = rs.getString("professor");
                    Atendimento atendimento = new Atendimento(id, nomeAnimal, professor, especie);

                    ProdutoDAO produtoDAO = new ProdutoDAO(); // Instancia ProdutoDAO
                    List<Produto> produtosDoAtendimento = produtoDAO.carregarItensAtendimento(id);
                    for (Produto p : produtosDoAtendimento) {
                        atendimento.adicionarProduto(p); // Adiciona os produtos ao objeto Atendimento
                    }
                    atendimentos.add(atendimento);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar atendimentos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DatabaseConnection.closeConnection(conn); // Fecha a conexão
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return atendimentos;
    }
}
