/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.model;


public class Produto {
    private int id; // Corresponde ao 'id' na tabela 'produtos'
    private String codigoBarras; // Corresponde a 'codigo_barras' na tabela 'produtos'
    private String nome; // Corresponde a 'nome' na tabela 'produtos'
    private int quantidade; // Quantidade em estoque ou quantidade utilizada no atendimento
    // Você pode adicionar outros campos como validade, unidade_medida se for usar

    // Construtor para carregar do estoque (com ID, codigoBarras e nome)
    public Produto(int id, String codigoBarras, String nome, int quantidade) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    // Construtor para itens de atendimento ou quando o produto é "novo" para o contexto da GUI
    // O 'identificador' aqui será o NOME do produto, conforme seu ComboBox e lógica atual da GUI.
    public Produto(String identificador, String nomeOuDescricao, int quantidade) {
        this.nome = identificador; // Mapeia o nome do produto para o campo 'nome'
        // this.codigoBarras = null; // Opcional: Você pode querer buscar o codigoBarras separadamente se necessário
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    // Este método é usado pela sua LogVetProntuarioGUI para identificar o produto no ComboBox
    // Ele agora retorna o 'nome' do produto para se alinhar com a forma como você o está usando.
    public String getCodigo() { 
        return nome; 
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        // Formato para exibição na lista de produtos do atendimento
        return nome + " x" + quantidade; 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Produto)) return false;
        Produto outro = (Produto) obj;
        // A igualdade é baseada no nome do produto para o contexto da sua aplicação atual
        return nome.equals(outro.nome); 
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }
}


