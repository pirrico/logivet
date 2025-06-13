/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// src/Prontuario/dao/ProfessorDAO.java
package Prontuario.dao;

import Prontuario.model.Professor; // You'll need to create this model class
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorDAO {

    public int salvarProfessor(Professor professor) {
        String sql = "INSERT INTO professores (nome, especialidade) VALUES (?, ?)"; // Assuming a 'professores' table
        int idGerado = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, professor.getNome());
            stmt.setString(2, professor.getEspecialidade());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    professor.setId(idGerado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar professor: " + e.getMessage());
            e.printStackTrace();
        }
        return idGerado;
    }

    public List<Professor> buscarTodosProfessores() {
        List<Professor> professores = new ArrayList<>();
        String sql = "SELECT id, nome, especialidade FROM professores ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                professores.add(new Professor(rs.getInt("id"), rs.getString("nome"), rs.getString("especialidade")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar professores: " + e.getMessage());
            e.printStackTrace();
        }
        return professores;
    }
    
    public boolean deletarProfessor(int id) {
    String sql = "DELETE FROM professores WHERE id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException e) {
        Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, "Erro ao deletar professor com ID: " + id, e);
        return false;
    }
}
    
}
