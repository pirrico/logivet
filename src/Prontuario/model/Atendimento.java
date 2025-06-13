/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author João Pedro
 */
public class Atendimento {
    private int id;
    private String nomeAnimal;
    private String veterinario; // Mantido como 'veterinario' no modelo para corresponder a getVeterinario() na GUI
    private String especie;
    private List<Produto> produtos;

    // Construtor para novos atendimentos
    public Atendimento(String nomeAnimal, String veterinario, String especie) {
        this.nomeAnimal = nomeAnimal;
        this.veterinario = veterinario;
        this.especie = especie;
        this.produtos = new ArrayList<>(); // Inicializa como ArrayList modificável
    }

    // Construtor para atendimentos carregados do banco (com ID)
    public Atendimento(int id, String nomeAnimal, String veterinario, String especie) {
        this.id = id;
        this.nomeAnimal = nomeAnimal;
        this.veterinario = veterinario;
        this.especie = especie;
        this.produtos = new ArrayList<>(); // Inicializa como ArrayList modificável
    }

    // Getters
    public int getId() {
        return id;
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

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNomeAnimal(String nomeAnimal) {
        this.nomeAnimal = nomeAnimal;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    // Setter para a lista de produtos (corrigido para ser modificável)
    public void setProdutos(List<Produto> produtos) {
        // Limpa a lista existente e adiciona todos os produtos da nova lista
        this.produtos.clear(); // Limpa produtos existentes
        this.produtos.addAll(produtos); // Adiciona todos os produtos da lista fornecida
    }

    // Métodos para gerenciar produtos individualmente
    public void adicionarProduto(Produto novo) {
        for (Produto p : produtos) {
            if (p.getNome().equals(novo.getNome())) {
                p.setQuantidade(p.getQuantidade() + novo.getQuantidade());
                return;
            }
        }
        produtos.add(novo);
    }

    public void removerProduto(Produto produto) {
        produtos.removeIf(p -> p.getNome().equals(produto.getNome()));
    }
}


