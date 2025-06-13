/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RegistroLogPanel extends JPanel {

    private JPanel detalhesMedicamentos;
    private boolean expandido = false;

    public RegistroLogPanel(String data, String horario, String veterinario, String animal, String especie, List<String> medicamentos, String responsavel) {

        setLayout(new BorderLayout());
        setBackground(new Color(75, 0, 130));
        setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255), 1),
                new EmptyBorder(11, 11, 11, 11) // espaçamento dento da barra roxa
        ));

        // Linha principal
        JPanel linha = new JPanel(new GridLayout(1, 7, 5, 0));
        linha.setOpaque(false);

        linha.add(criarCampo(data));
        linha.add(criarCampo(horario));
        linha.add(criarCampo(veterinario));
        linha.add(criarCampo(animal));
        linha.add(criarCampo(especie));
        linha.add(criarCampo(medicamentos.isEmpty() ? "N/A" : medicamentos.get(0))); // Primeiro medicamento
        linha.add(criarCampo(responsavel));

        // Botão expandir
        // CORREÇÃO AQUI: Caminho da imagem atualizado para Prontuario/imagens
        ImageIcon setaIcone = new ImageIcon(getClass().getResource("/Prontuario/imagens/seta_baixo.png"));
        JButton btnExpandir = new JButton(setaIcone);
        btnExpandir.setFocusPainted(false);
        btnExpandir.setBackground(new Color(255, 140, 0));
        btnExpandir.setForeground(Color.WHITE);
        btnExpandir.setPreferredSize(new Dimension(40, 30));
        btnExpandir.addActionListener(e -> toggleMedicamentos(medicamentos));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(linha, BorderLayout.CENTER);
        topo.add(btnExpandir, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);

        // Detalhes dos medicamentos (expansão)
        detalhesMedicamentos = new JPanel();
        detalhesMedicamentos.setLayout(new BoxLayout(detalhesMedicamentos, BoxLayout.Y_AXIS));
        detalhesMedicamentos.setOpaque(false);
        detalhesMedicamentos.setVisible(false);

        add(detalhesMedicamentos, BorderLayout.CENTER);
    }

    private JTextField criarCampo(String texto) {
        JTextField campo = new JTextField(texto);
        campo.setEditable(false);
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campo.setBackground(new Color(245, 245, 245));
        campo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Tamanho compacto e fixo
        campo.setPreferredSize(new Dimension(100, 20));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        campo.setMinimumSize(new Dimension(100, 20));

        return campo;
    }

    private void toggleMedicamentos(List<String> medicamentos) {
        detalhesMedicamentos.removeAll();

        if (!expandido && medicamentos.size() > 1) {
            for (int i = 1; i < medicamentos.size(); i++) {
                JPanel novaLinha = new JPanel(new GridLayout(1, 7, 10, 0)); // config dos med. expandidos
                novaLinha.setOpaque(false);

                // Adiciona células vazias para alinhar
                for (int j = 0; j < 5; j++) novaLinha.add(placeholderCampo());

                novaLinha.add(criarCampo(medicamentos.get(i)));
                novaLinha.add(placeholderCampo());

                detalhesMedicamentos.add(Box.createVerticalStrut(3));
                detalhesMedicamentos.add(novaLinha);
            }
        }

        expandido = !expandido;
        detalhesMedicamentos.setVisible(expandido);
        revalidate();
        repaint();
    }

    private Component placeholderCampo() {
        JLabel placeholder = new JLabel(); // vazio e invisível
        placeholder.setPreferredSize(new Dimension(100, 20));
        placeholder.setMinimumSize(new Dimension(100, 20));
        placeholder.setMaximumSize(new Dimension(100, 20));
        return placeholder;
    }
}
