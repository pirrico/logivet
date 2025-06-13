/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.model;

import java.util.List;

public class Registro {
    // Campos mapeados para as colunas da tabela log_registro
    private String dataRegistro; // Mapeia para data_registro
    private String horario;
    private String professor;    // Mapeia para professor (era veterinario)
    private String animal;
    private String especie;
    private List <String> produtos;   // Mapeia para produtos (era medicamentos)
    private String responsavel;

    // Construtor
    public Registro(String dataRegistro, String horario, String professor, String animal, String especie, List<String> produtos, String responsavel) {
        this.dataRegistro = dataRegistro;
        this.horario = horario;
        this.professor = professor;
        this.animal = animal;
        this.especie = especie;
        this.produtos = produtos;
        this.responsavel = responsavel;
    }

    // Getters atualizados para refletir os novos nomes de campo
    public String getData() { return dataRegistro; } // Mantém o nome do getter como getData para compatibilidade com LogRegistro GUI
    public String getHorario() { return horario; }
    public String getVeterinario() { return professor; } // Mantém o nome do getter como getVeterinario, mas retorna o professor
    public String getAnimal() { return animal; }
    public String getEspecie() { return especie; }
    public List<String> getMedicamentos() { return produtos; } // Mantém o nome do getter como getMedicamentos, mas retorna os produtos
    public String getResponsavel() { return responsavel; }

    // Métodos set para facilitar a manipulação dos dados, se necessário (opcional, dependendo do uso)
    public void setDataRegistro(String dataRegistro) { this.dataRegistro = dataRegistro; }
    public void setHorario(String horario) { this.horario = horario; }
    public void setProfessor(String professor) { this.professor = professor; }
    public void setAnimal(String animal) { this.animal = animal; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setProdutos(List<String> produtos) { this.produtos = produtos; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}
