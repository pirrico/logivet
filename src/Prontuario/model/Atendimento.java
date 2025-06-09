/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.model;




import java.util.ArrayList;
import java.util.List;


public class Atendimento {
    private int id; // ID gerado pelo banco de dados (tabela atendimentos)
    private String nomeAnimal;
    private String veterinario; // Mapeia para 'professor' na tabela
    private String especie;
    private List<Produto> produtos; // Produtos associados a este atendimento

    // Construtor para novos atendimentos (sem ID inicial, será gerado pelo banco)
    public Atendimento(String nomeAnimal, String veterinario, String especie) {
        this.nomeAnimal = nomeAnimal;
        this.veterinario = veterinario;
        this.especie = especie;
        this.produtos = new ArrayList<>();
    }

    // Construtor para atendimentos carregados do banco (com ID)
    public Atendimento(int id, String nomeAnimal, String veterinario, String especie) {
        this.id = id;
        this.nomeAnimal = nomeAnimal;
        this.veterinario = veterinario;
        this.especie = especie;
        this.produtos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeAnimal() {
        return nomeAnimal;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public String getEspecie() {
        return especie;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void adicionarProduto(Produto novo) {
        for (Produto p : produtos) {
            // Se o produto já existe no atendimento (baseado no nome do produto)
            if (p.getNome().equals(novo.getNome())) { 
                p.setQuantidade(p.getQuantidade() + novo.getQuantidade());
                return;
            }
        }
        produtos.add(novo);
    }

    public void removerProduto(Produto produto) {
        // Remove o produto da lista do atendimento baseado no nome
        produtos.removeIf(p -> p.getNome().equals(produto.getNome()));
    }
}


