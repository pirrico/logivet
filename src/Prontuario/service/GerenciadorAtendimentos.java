/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.service;

/**
 *
 * @author João Pedro
 */


import Prontuario.model.Atendimento;
import java.util.ArrayList;
import java.util.List;




import java.util.ArrayList;
import java.util.List;

public class GerenciadorAtendimentos {
    private final List<Atendimento> atendimentos;

    public GerenciadorAtendimentos() {
        this.atendimentos = new ArrayList<>();
    }

    public void adicionarAtendimento(Atendimento atendimento) {
        atendimentos.add(atendimento);
    }

    public void removerAtendimento(Atendimento atendimento) {
        atendimentos.remove(atendimento);
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }
}
