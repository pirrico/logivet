/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.dao;

import Prontuario.model.Registro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate; // Para converter String para DATE
import java.time.LocalTime; // Para converter String para TIME
import java.time.format.DateTimeFormatter; // Para formatar data e hora

public class RegistroDAO {

    // Adiciona um novo registro à tabela log_registro
    public boolean adicionarRegistro(Registro registro) {
        String sql = "INSERT INTO log_registro (data_registro, horario, professor, animal, especie, responsavel, produtos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Converte a string de data para java.sql.Date
            LocalDate localDate = LocalDate.parse(registro.getData(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            stmt.setDate(1, java.sql.Date.valueOf(localDate));

            // Converte a string de horário para java.sql.Time
            LocalTime localTime = LocalTime.parse(registro.getHorario(), DateTimeFormatter.ofPattern("HH:mm"));
            stmt.setTime(2, java.sql.Time.valueOf(localTime));

            stmt.setString(3, registro.getVeterinario()); // Usa getVeterinario que agora retorna professor
            stmt.setString(4, registro.getAnimal());
            stmt.setString(5, registro.getEspecie());
            stmt.setString(6, registro.getResponsavel());

            // Converte a lista de produtos para uma única string no formato "produto1 - qtd1;produto2 - qtd2"
            String produtosString = String.join(";", registro.getMedicamentos()); // Usa getMedicamentos que agora retorna produtos
            stmt.setString(7, produtosString);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar registro: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Erro de formato de data/hora: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Busca todos os registros da tabela log_registro
    public List<Registro> buscarTodos() {
        List<Registro> registros = new ArrayList<>();
        // Ordena por data_registro (decrescente) e horario (decrescente) para os mais recentes primeiro
        String sql = "SELECT data_registro, horario, professor, animal, especie, responsavel, produtos FROM log_registro ORDER BY data_registro DESC, horario DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Formata java.sql.Date para String "dd/MM/yyyy"
                java.sql.Date sqlDate = rs.getDate("data_registro");
                String data = (sqlDate != null) ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(sqlDate.toLocalDate()) : "";

                // Formata java.sql.Time para String "HH:mm"
                java.sql.Time sqlTime = rs.getTime("horario");
                String horario = (sqlTime != null) ? DateTimeFormatter.ofPattern("HH:mm").format(sqlTime.toLocalTime()) : "";

                String professor = rs.getString("professor");
                String animal = rs.getString("animal");
                String especie = rs.getString("especie");
                String responsavel = rs.getString("responsavel");
                String produtosStr = rs.getString("produtos");

                List<String> produtos = new ArrayList<>();
                if (produtosStr != null && !produtosStr.trim().isEmpty()) {
                    // Divide a string de produtos usando ";" como delimitador
                    produtos = Arrays.asList(produtosStr.split(";"));
                } else {
                    produtos.add("N/A"); // Ou trate como lista vazia, se for o caso
                }

                // Cria um novo objeto Registro com os dados do banco
                registros.add(new Registro(data, horario, professor, animal, especie, produtos, responsavel));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar registros: " + e.getMessage());
            e.printStackTrace();
        }
        return registros;
    }

    // Adicione outros métodos DAO, se necessário (e.g., buscarPorData, buscarPorAnimal)
}
