/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.gui; // Corrected package declaration

import javax.swing.*;
import javax.swing.border.*;

import Prontuario.dao.RegistroDAO; // Corrected import
import Prontuario.model.Registro; // Corrected import

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors; // Added for sorting

public class LogRegistro extends JPanel { // Changed from JFrame to JPanel

    private JPanel painelRegistros; // Declare it as a class member
    private RegistroDAO registroDAO; // Declare DAO as a class member
    private JComboBox<String> comboFiltro; // Declare the JComboBox as a class member

    // Constructor now takes a CardLayout and a parent JPanel to switch screens
    public LogRegistro() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); // Set background for the panel

        registroDAO = new RegistroDAO(); // Initialize the DAO

        // ================= Area de cartões =================
        painelRegistros = new JPanel();
        painelRegistros.setLayout(new BoxLayout(painelRegistros, BoxLayout.Y_AXIS));
        painelRegistros.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(painelRegistros);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Header for the log entries
        JPanel cabecalho = new JPanel(new GridLayout(1, 7, 10, 0));
        cabecalho.setBackground(new Color(75, 0, 130));
        cabecalho.setBorder(new EmptyBorder(5, -20, 5, 10));

        String[] titulos = {
                "DATA", "HORÁRIO", "PROFESSOR / VETERINÁRIO",
                "ANIMAL", "ESPÉCIE", "PRODUTOS / QUANTIDADE", "RESPONSÁVEL"
        };

        for (String titulo : titulos) {
            JLabel label = new JLabel(titulo, SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            cabecalho.add(label);
        }

        painelRegistros.add(cabecalho);
        painelRegistros.add(Box.createVerticalStrut(10)); // space below the title

        // ================= Filtro SelecBOX =================
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filtro.setBackground(Color.WHITE);

        String[] opcoes = {"Recentes", "Mais Antigos"};
        comboFiltro = new JComboBox<>(opcoes); // Initialize the class member
        comboFiltro.setFont(new Font("SansSerif", Font.BOLD, 14));
        comboFiltro.setBackground(new Color(189, 189, 189));
        comboFiltro.setForeground(Color.BLACK);
        comboFiltro.setFocusable(false);
        comboFiltro.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        comboFiltro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? new Color(189, 189, 189) : Color.WHITE);
                c.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                return c;
            }
        });

        // Add action listener to the combo box to reorder records
        comboFiltro.addActionListener(e -> {
            recarregarRegistros(); // Call the refresh method when filter changes
        });

        filtro.add(new JLabel(" Selecionar : "));
        filtro.add(comboFiltro);
        add(filtro, BorderLayout.SOUTH);

        // Initially load and display records
        recarregarRegistros();
    }

    // Public method to refresh the displayed records
    public void recarregarRegistros() {
        painelRegistros.removeAll(); // Clear existing records

        // Re-add header
        JPanel cabecalho = new JPanel(new GridLayout(1, 7, 10, 0));
        cabecalho.setBackground(new Color(75, 0, 130));
        cabecalho.setBorder(new EmptyBorder(5, -20, 5, 10));
        String[] titulos = {
                "DATA", "HORÁRIO", "PROFESSOR / VETERINÁRIO",
                "ANIMAL", "ESPÉCIE", "PRODUTOS / QUANTIDADE", "RESPONSÁVEL"
        };
        for (String titulo : titulos) {
            JLabel label = new JLabel(titulo, SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            cabecalho.add(label);
        }
        painelRegistros.add(cabecalho);
        painelRegistros.add(Box.createVerticalStrut(10)); // space below the title

        List<Registro> registros = registroDAO.buscarTodos();

        // Apply sorting based on the selected filter
        String selectedOption = (String) comboFiltro.getSelectedItem();
        if ("Mais Antigos".equals(selectedOption)) {
            // Sort by date/time for "Mais Antigos"
            // Assuming data is "DD/MM/YYYY" and horario is "HH:MM"
            registros.sort((r1, r2) -> {
                // Parse date strings for comparison
                String[] dateParts1 = r1.getData().split("/");
                String[] dateParts2 = r2.getData().split("/");
                String formattedDate1 = dateParts1[2] + dateParts1[1] + dateParts1[0]; // YYYYMMDD
                String formattedDate2 = dateParts2[2] + dateParts2[1] + dateParts2[0];

                // Parse time strings for comparison
                String formattedTime1 = r1.getHorario().replace(":", ""); // HHMM
                String formattedTime2 = r2.getHorario().replace(":", "");

                // Combine for full comparison
                int dateCompare = formattedDate1.compareTo(formattedDate2);
                if (dateCompare != 0) {
                    return dateCompare;
                }
                return formattedTime1.compareTo(formattedTime2);
            });
        } else { // "Recentes" (default behavior)
            // Sort by date/time for "Recentes" (already fetched in descending order from DAO)
            // Reversing the list if it was ordered oldest first by the DAO.
            // If the DAO already fetches "Recentes" first, no explicit sort is needed here.
            // My RegistroDAO fetches DESC, so for "Recentes" this is fine. For "Mais Antigos", reverse.
            // The DAO already fetches in DESC order, so for "Recentes" we don't need to do anything.
            // For "Mais Antigos", we need to reverse the order as implemented above.
        }


        for (Registro r : registros) {
            painelRegistros.add(new RegistroLogPanel(
                    r.getData(), r.getHorario(), r.getVeterinario(), r.getAnimal(),
                    r.getEspecie(), r.getMedicamentos(), r.getResponsavel()
            ));
            painelRegistros.add(Box.createVerticalStrut(70));
        }
        painelRegistros.revalidate();
        painelRegistros.repaint();
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
        boolean expandido = false;

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
