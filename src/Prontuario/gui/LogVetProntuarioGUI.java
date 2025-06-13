/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// src/Prontuario/gui/LogVetProntuarioGUI.java
package Prontuario.gui;

import Prontuario.model.Atendimento;
import Prontuario.model.Produto;
import Prontuario.model.Professor;
import Prontuario.dao.AtendimentoDAO;
import Prontuario.dao.DatabaseConnection;
import Prontuario.dao.ProdutoDAO;
import Prontuario.dao.ProfessorDAO;
import Prontuario.service.GerenciadorAtendimentos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class LogVetProntuarioGUI extends JFrame {

    private GerenciadorAtendimentos gerenciador = new GerenciadorAtendimentos();
    private AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private ProfessorDAO professorDAO = new ProfessorDAO();

    private JPanel painelAtendimentos;
    private Color verdeEscuro = new Color(0, 98, 78);
    private Color verdeClaro = new Color(198, 237, 222);
    private Color laranja = new Color(245, 123, 58);
    private Color cinzaCampo = new Color(220, 220, 220);

    private JComboBox<String> comboVeterinario;
    private DefaultComboBoxModel<String> modelVeterinario;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private JScrollPane prontuarioPanel;
    private LogRegistro logRegistroPanel; // Store the instance of LogRegistro

    public LogVetProntuarioGUI() {
        setTitle("LogVet - Prontuário");
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        // TOPO (Barra de Navegação)
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(verdeEscuro);
        topo.setPreferredSize(new Dimension(1024, 50));

        // --- MODIFIED SECTION START ---
        JLabel logo;
        try {
            // Load the image icon from the resources
            ImageIcon logiVetLogo = new ImageIcon(getClass().getResource("/Prontuario/imagens/logiVetName4.png"));
            // You might want to scale the image if it's too large
            // For example, to scale it to fit the 50px height of the bar:
            Image image = logiVetLogo.getImage(); // transform it
            Image scaledImage = image.getScaledInstance(-1, 40, java.awt.Image.SCALE_SMOOTH); // scale it (e.g., 40px height)
            logiVetLogo = new ImageIcon(scaledImage); // convert it back to ImageIcon

            logo = new JLabel(logiVetLogo);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem logiVetName4.png: " + e.getMessage());
            // Fallback to text if image fails to load
            logo = new JLabel("  🐾 LogVet");
            logo.setForeground(Color.WHITE);
            logo.setFont(new Font("Arial", Font.BOLD, 18));
        }
        // --- MODIFIED SECTION END ---

        topo.add(logo, BorderLayout.WEST);

        // MENU DE NAVEGAÇÃO
        JPanel menuTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        menuTopo.setOpaque(false);
        String[] itens = {"MENU DE ITENS", "CONFIGURAÇÕES", "LOG", "SAIR"};

        Map<String, JComponent> panels = new HashMap<>();
        prontuarioPanel = createProntuarioPanelContent();
        logRegistroPanel = new LogRegistro(); // Initialize LogRegistro here

        panels.put("MENU DE ITENS", prontuarioPanel);
        panels.put("LOG", logRegistroPanel);
        panels.put("CONFIGURAÇÕES", new JPanel() {{ setBackground(Color.LIGHT_GRAY); add(new JLabel("Configurações em breve!")); }});


        for (String itemText : itens) {
            JLabel menuItem = new JLabel(itemText);
            menuItem.setForeground(Color.WHITE);
            menuItem.setFont(new Font("Arial", Font.BOLD, 12));
            menuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            menuItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (itemText.equals("SAIR")) {
                        System.exit(0);
                    } else if (itemText.equals("LOG")) {
                        logRegistroPanel.recarregarRegistros(); // Recarrega os registros ao entrar na tela de Log
                        cardLayout.show(mainContentPanel, itemText);
                    } else {
                        if (panels.containsKey(itemText)) {
                            cardLayout.show(mainContentPanel, itemText);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    menuItem.setForeground(laranja);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    menuItem.setForeground(Color.WHITE);
                }
            });
            menuTopo.add(menuItem);
        }
        topo.add(menuTopo, BorderLayout.EAST);
        container.add(topo, BorderLayout.NORTH);

        // PAINEL DE CONTEÚDO PRINCIPAL com CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.add(prontuarioPanel, "MENU DE ITENS");
        mainContentPanel.add(logRegistroPanel, "LOG");
        mainContentPanel.add(panels.get("CONFIGURAÇÕES"), "CONFIGURAÇÕES");


        container.add(mainContentPanel, BorderLayout.CENTER);

        add(container);
        setVisible(true);

        cardLayout.show(mainContentPanel, "MENU DE ITENS");
    }

    private JScrollPane createProntuarioPanelContent() {
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(new EmptyBorder(15, 15, 15, 15));

        try {
            ImageIcon iconeProntuario = new ImageIcon(getClass().getResource("/Prontuario/imagens/Prontuário.png"));
            JLabel imagemProntuario = new JLabel(iconeProntuario);
            imagemProntuario.setAlignmentX(Component.CENTER_ALIGNMENT);
            corpo.add(imagemProntuario);
            corpo.add(Box.createRigidArea(new Dimension(0, 10)));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem Prontuário.png: " + e.getMessage());
        }

        corpo.add(criarTituloSeccao("NOVO ATENDIMENTO", laranja));
        corpo.add(criarPainelNovoAtendimento());

        corpo.add(Box.createRigidArea(new Dimension(0, 20)));

        corpo.add(criarTituloSeccao("ATENDIMENTO(S) EM ANDAMENTO", laranja));
        painelAtendimentos = new JPanel();
        painelAtendimentos.setLayout(new BoxLayout(painelAtendimentos, BoxLayout.Y_AXIS));
        painelAtendimentos.setBackground(Color.WHITE);
        corpo.add(painelAtendimentos);

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);

        carregarAtendimentosDoBanco();
        return scroll;
    }

    private JPanel criarTituloSeccao(String texto, Color corFundo) {
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setBackground(corFundo);
        painelTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        painelTitulo.setBorder(new EmptyBorder(5, 20, 5, 20));

        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        painelTitulo.add(label, BorderLayout.CENTER);
        return painelTitulo;
    }

    private JPanel criarPainelNovoAtendimento() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(verdeEscuro);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        painel.setBorder(new EmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelNome = new JLabel("Nome do Animal");
        labelNome.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(labelNome, gbc);

        RoundedTextField nomeAnimal = new RoundedTextField();
        nomeAnimal.setBackground(cinzaCampo);
        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(nomeAnimal, gbc);

        JLabel labelVet = new JLabel("Professor/Veterinário");
        labelVet.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        painel.add(labelVet, gbc);

        modelVeterinario = new DefaultComboBoxModel<>();
        comboVeterinario = new JComboBox<>(modelVeterinario);
        comboVeterinario.setBackground(cinzaCampo);
        gbc.gridx = 1;
        gbc.gridy = 1;
        painel.add(comboVeterinario, gbc);

        RoundedButton btnCadastrarProfessor = new RoundedButton("Cadastrar Professor");
        btnCadastrarProfessor.setBackground(laranja);
        btnCadastrarProfessor.setForeground(Color.WHITE);
        btnCadastrarProfessor.setFocusPainted(false);
        btnCadastrarProfessor.setArc(20, 20);
        btnCadastrarProfessor.setHoverColor(laranja.brighter());
        gbc.gridx = 1;
        gbc.gridy = 2;
        painel.add(btnCadastrarProfessor, gbc);

        btnCadastrarProfessor.addActionListener(e -> {
            JDialog dialog = criarDialogoCadastroProfessor();
            dialog.setVisible(true);
        });

        JLabel labelEspecie = new JLabel("Espécie do Animal");
        labelEspecie.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 0;
        painel.add(labelEspecie, gbc);

        JComboBox<String> especie = new JComboBox<>(new String[]{"Cães", "Gatos", "Bovinos", "Equinos", "Suínos", "Aves", "Ovinos", "Caprinos"});
        especie.setBackground(cinzaCampo);
        gbc.gridx = 2;
        gbc.gridy = 1;
        painel.add(especie, gbc);

        RoundedButton criar = new RoundedButton("CRIAR");
        criar.setBackground(laranja);
        criar.setForeground(Color.WHITE);
        criar.setFocusPainted(false);
        criar.setArc(20, 20);
        criar.setHoverColor(laranja.brighter());
        gbc.gridx = 3;
        gbc.gridy = 1;
        painel.add(criar, gbc);

        criar.addActionListener(e -> {
            String nome = nomeAnimal.getText().trim();
            String vet = (String) comboVeterinario.getSelectedItem();
            String esp = (String) especie.getSelectedItem();

            if (nome.isEmpty() || vet == null || vet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
                return;
            }

            Atendimento atendimento = new Atendimento(nome, vet, esp);

            int idGerado = atendimentoDAO.salvarAtendimento(atendimento);
            if (idGerado != -1) {
                atendimento.setId(idGerado);
                gerenciador.adicionarAtendimento(atendimento);
                painelAtendimentos.add(criarPainelAtendimento(atendimento));
                painelAtendimentos.revalidate();
                painelAtendimentos.repaint();

                nomeAnimal.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao criar atendimento no banco de dados.");
            }
        });

        carregarProfessoresDoBanco();
        return painel;
    }

    private JDialog criarDialogoCadastroProfessor() {
        JDialog dialog = new JDialog(this, "Gerenciar Professores", true); // Changed title
        dialog.setSize(500, 300); // Increased size for better proportions
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(verdeEscuro);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel for adding a new professor
        JPanel addProfessorPanel = new JPanel(new GridBagLayout());
        addProfessorPanel.setOpaque(false); // Make it transparent to show parent background
        addProfessorPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Cadastrar Novo Professor",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE));

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        addProfessorPanel.add(lblNome, gbc);

        RoundedTextField txtNome = new RoundedTextField();
        txtNome.setBackground(cinzaCampo);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addProfessorPanel.add(txtNome, gbc);

        JLabel lblEspecialidade = new JLabel("Especialidade:");
        lblEspecialidade.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        addProfessorPanel.add(lblEspecialidade, gbc);

        RoundedTextField txtEspecialidade = new RoundedTextField();
        txtEspecialidade.setBackground(cinzaCampo);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addProfessorPanel.add(txtEspecialidade, gbc);

        RoundedButton btnSalvar = new RoundedButton("Salvar");
        btnSalvar.setBackground(laranja);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setArc(20, 20);
        btnSalvar.setHoverColor(laranja.brighter());
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        addProfessorPanel.add(btnSalvar, gbc);

        // Add the addProfessorPanel to the dialog
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(addProfessorPanel, gbc);

        // Panel for deleting a professor
        JPanel deleteProfessorPanel = new JPanel(new GridBagLayout());
        deleteProfessorPanel.setOpaque(false);
        deleteProfessorPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Excluir Professor",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE));

        JLabel lblSelectProfessor = new JLabel("Selecionar Professor:");
        lblSelectProfessor.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        deleteProfessorPanel.add(lblSelectProfessor, gbc);

        DefaultComboBoxModel<String> deleteProfModel = new DefaultComboBoxModel<>();
        JComboBox<String> comboDeleteProfessor = new JComboBox<>(deleteProfModel);
        comboDeleteProfessor.setBackground(cinzaCampo);
        gbc.gridx = 1;
        gbc.gridy = 0;
        deleteProfessorPanel.add(comboDeleteProfessor, gbc);

        RoundedButton btnExcluirProfessor = new RoundedButton("Excluir");
        btnExcluirProfessor.setBackground(laranja);
        btnExcluirProfessor.setForeground(Color.WHITE);
        btnExcluirProfessor.setArc(20, 20);
        btnExcluirProfessor.setHoverColor(laranja.brighter());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        deleteProfessorPanel.add(btnExcluirProfessor, gbc);

        // Add the deleteProfessorPanel to the dialog
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(deleteProfessorPanel, gbc);

        // Initial loading of professors for the delete combo box
        Runnable loadProfessorsForDelete = () -> {
            deleteProfModel.removeAllElements();
            List<Professor> professors = professorDAO.buscarTodosProfessores();
            for (Professor p : professors) {
                deleteProfModel.addElement(p.getNome());
            }
        };
        loadProfessorsForDelete.run(); // Load initially

        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String especialidade = txtEspecialidade.getText().trim();

            if (nome.isEmpty() || especialidade.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                Professor novoProfessor = new Professor(nome, especialidade);
                int id = professorDAO.salvarProfessor(novoProfessor);
                if (id != -1) {
                    JOptionPane.showMessageDialog(dialog, "Professor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarProfessoresDoBanco(); // Reload main combo box
                    loadProfessorsForDelete.run(); // Reload delete combo box
                    txtNome.setText("");
                    txtEspecialidade.setText("");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erro ao cadastrar professor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnExcluirProfessor.addActionListener(e -> {
            String selectedProfessorName = (String) comboDeleteProfessor.getSelectedItem();
            if (selectedProfessorName == null || selectedProfessorName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor, selecione um professor para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Deseja realmente excluir o professor '" + selectedProfessorName + "'? Esta ação é irreversível.",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                Professor professorToDelete = null;
                // Retrieve the Professor object by name to get its ID
                List<Professor> allProfessors = professorDAO.buscarTodosProfessores();
                for(Professor p : allProfessors) {
                    if (p.getNome().equals(selectedProfessorName)) {
                        professorToDelete = p;
                        break;
                    }
                }

                if (professorToDelete != null) {
                    boolean deleted = professorDAO.deletarProfessor(professorToDelete.getId());
                    if (deleted) {
                        JOptionPane.showMessageDialog(dialog, "Professor excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        carregarProfessoresDoBanco(); // Reload main combo box
                        loadProfessorsForDelete.run(); // Reload delete combo box
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Erro ao excluir professor.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Professor não encontrado para exclusão.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return dialog;
    }

    private void carregarProfessoresDoBanco() {
        modelVeterinario.removeAllElements();
        List<Professor> professores = professorDAO.buscarTodosProfessores();
        for (Professor p : professores) {
            modelVeterinario.addElement(p.getNome());
        }
    }

    private JPanel criarPainelAtendimento(Atendimento atendimento) {
        JPanel atendimentoPanel = new JPanel();
        atendimentoPanel.setLayout(new BoxLayout(atendimentoPanel, BoxLayout.Y_AXIS));
        atendimentoPanel.setBackground(verdeClaro);
        atendimentoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        atendimentoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel info = new JLabel("<html><b>ANIMAL:</b> " + atendimento.getNomeAnimal()
                + " &nbsp;&nbsp; <b>VET:</b> " + atendimento.getVeterinario()
                + " &nbsp;&nbsp; <b>ESPÉCIE:</b> " + atendimento.getEspecie() + "</html>");

        RoundedButton toggle = new RoundedButton("▼");
        toggle.setBackground(laranja);
        toggle.setForeground(Color.WHITE);
        toggle.setFocusPainted(false);
        toggle.setArc(10, 10);
        toggle.setHoverColor(laranja.brighter());

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(verdeClaro);
        cabecalho.add(info, BorderLayout.WEST);
        cabecalho.add(toggle, BorderLayout.EAST);
        atendimentoPanel.add(cabecalho);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(verdeClaro);
        conteudo.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel listaProdutos = new JPanel();
        listaProdutos.setLayout(new BoxLayout(listaProdutos, BoxLayout.Y_AXIS));
        listaProdutos.setBackground(verdeClaro);

        JScrollPane scrollProdutos = new JScrollPane(listaProdutos);
        scrollProdutos.setPreferredSize(new Dimension(900, 180));
        scrollProdutos.setBorder(null);
        scrollProdutos.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        conteudo.add(scrollProdutos);

        for (Produto p : atendimento.getProdutos()) {
            adicionarProdutoAoPainel(p, atendimento, listaProdutos);
        }

        JPanel adicionar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adicionar.setBackground(verdeClaro);

        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        List<Produto> produtosEstoque = produtoDAO.carregarProdutosEstoque();
        for (Produto p : produtosEstoque) {
            comboModel.addElement(p.getNome() + " (Estoque: " + p.getQuantidade() + ")");
        }
        JComboBox<String> comboProdutos = new JComboBox<>(comboModel);

        RoundedTextField campoQuant = new RoundedTextField("1", 3);
        campoQuant.setArc(10, 10);

        RoundedButton btnAdd = new RoundedButton("ADICIONAR");
        btnAdd.setArc(10, 10);
        btnAdd.setHoverColor(laranja.brighter());

        adicionar.add(new JLabel("Produto:"));
        adicionar.add(comboProdutos);
        adicionar.add(campoQuant);
        adicionar.add(btnAdd);
        conteudo.add(adicionar);

        btnAdd.addActionListener(ev -> {
            if (comboProdutos.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Nenhum produto selecionado.");
                return;
            }
            String selectedItemDisplay = (String) comboProdutos.getSelectedItem();
            String nomeProduto = selectedItemDisplay.substring(0, selectedItemDisplay.indexOf(" (Estoque:"));

            int quant;
            try {
                quant = Integer.parseInt(campoQuant.getText().trim());
                if (quant <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.");
                return;
            }

            Produto produtoNoEstoque = produtoDAO.buscarProdutoPorNome(nomeProduto);

            if (produtoNoEstoque == null || produtoNoEstoque.getQuantidade() < quant) {
                JOptionPane.showMessageDialog(this, "Quantidade solicitada (" + quant + ") excede o estoque disponível (" + (produtoNoEstoque != null ? produtoNoEstoque.getQuantidade() : 0) + ") para o produto: " + nomeProduto);
                return;
            }

            Produto produtoParaAdicionar = new Produto(produtoNoEstoque.getId(), produtoNoEstoque.getCodigoBarras(), produtoNoEstoque.getNome(), quant);

            Optional<Produto> produtoExistenteNoAtendimento = atendimento.getProdutos().stream()
                    .filter(prod -> prod.getNome().equals(nomeProduto))
                    .findFirst();

            if (produtoExistenteNoAtendimento.isPresent()) {
                Produto pExistente = produtoExistenteNoAtendimento.get();
                pExistente.setQuantidade(pExistente.getQuantidade() + quant);
                produtoDAO.atualizarItemAtendimento(atendimento.getId(), pExistente.getId(), pExistente.getQuantidade());
                atualizarPainelProduto(listaProdutos, pExistente);
            } else {
                atendimento.adicionarProduto(produtoParaAdicionar);
                try {
                    produtoDAO.salvarItemAtendimento(DatabaseConnection.getConnection(), atendimento.getId(), produtoParaAdicionar.getId(), quant);
                } catch (SQLException ex) {
                    Logger.getLogger(LogVetProntuarioGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                adicionarProdutoAoPainel(produtoParaAdicionar, atendimento, listaProdutos);
            }

            produtoDAO.atualizarEstoqueProduto(nomeProduto, -quant);
            recarregarTodosCombosProdutos();

            listaProdutos.revalidate();
            listaProdutos.repaint();
        });

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(verdeClaro);

        RoundedButton finalizar = new RoundedButton("FINALIZAR ATENDIMENTO");
        RoundedButton cancelar = new RoundedButton("CANCELAR ATENDIMENTO");
        finalizar.setBackground(laranja);
        finalizar.setForeground(Color.WHITE);
        cancelar.setBackground(laranja);
        cancelar.setForeground(Color.WHITE);
        finalizar.setArc(20, 20);
        cancelar.setArc(20, 20);
        finalizar.setHoverColor(laranja.brighter());
        cancelar.setHoverColor(laranja.brighter());

        botoes.add(cancelar);
        botoes.add(finalizar);
        conteudo.add(botoes);

        cancelar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente cancelar o atendimento? Os produtos serão devolvidos ao estoque.",
                    "Confirmar Cancelamento",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (Produto p : atendimento.getProdutos()) {
                    produtoDAO.atualizarEstoqueProduto(p.getNome(), p.getQuantidade());
                }
                atendimentoDAO.removerAtendimento(atendimento.getId());
                gerenciador.removerAtendimento(atendimento);
                painelAtendimentos.remove(atendimentoPanel);
                painelAtendimentos.revalidate();
                painelAtendimentos.repaint();
                recarregarTodosCombosProdutos();
            }
        });

        finalizar.addActionListener(e -> {
            if (atendimento.getProdutos().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não é possível finalizar um prontuário sem nenhum produto adicionado.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja finalizar este atendimento?",
                    "Confirmar Finalização",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                StringBuilder produtosLog = new StringBuilder();
                for (int i = 0; i < atendimento.getProdutos().size(); i++) {
                    Produto p = atendimento.getProdutos().get(i);
                    produtosLog.append(p.getNome()).append(" - ").append(p.getQuantidade());
                    if (i < atendimento.getProdutos().size() - 1) {
                        produtosLog.append(";");
                    }
                }

                LocalDate data = LocalDate.now();
                LocalTime horario = LocalTime.now();
                String dataStr = data.toString();
                String horarioStr = horario.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                String responsavel = ""; // TODO: Implement how to get 'responsavel' if needed

                atendimentoDAO.salvarLogRegistro(
                    dataStr,
                    horarioStr,
                    atendimento.getVeterinario(),
                    atendimento.getNomeAnimal(),
                    atendimento.getEspecie(),
                    responsavel,
                    produtosLog.toString()
                );

                JOptionPane.showMessageDialog(this, "Atendimento finalizado e registrado no log.");
                atendimentoDAO.removerAtendimento(atendimento.getId());
                gerenciador.removerAtendimento(atendimento);
                painelAtendimentos.remove(atendimentoPanel);
                painelAtendimentos.revalidate();
                painelAtendimentos.repaint();
                recarregarTodosCombosProdutos();

                // CHAMADA PARA ATUALIZAR O PAINEL DE LOG AQUI!
                if (logRegistroPanel != null) {
                    logRegistroPanel.recarregarRegistros();
                }
            }
        });

        atendimentoPanel.add(conteudo);

        toggle.addActionListener(e -> {
            conteudo.setVisible(!conteudo.isVisible());
            toggle.setText(conteudo.isVisible() ? "▼" : "►");
        });

        return atendimentoPanel;
    }

    private void adicionarProdutoAoPainel(Produto p, Atendimento atendimento, JPanel listaProdutos) {
        JPanel linhaProduto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaProduto.setBackground(verdeClaro);
        JLabel labelItem = new JLabel(p.toString());

        JButton btnMais = new JButton(new ImageIcon(getClass().getResource("/Prontuario/imagens/mais.png")));
        JButton btnMenos = new JButton(new ImageIcon(getClass().getResource("/Prontuario/imagens/menos.png")));
        JButton btnExcluir = new JButton(new ImageIcon(getClass().getResource("/Prontuario/imagens/lixeira.png")));

        for (JButton btn : new JButton[]{btnMais, btnMenos, btnExcluir}) {
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                }
            });
        }

        btnMais.addActionListener(eMais -> {
            Produto produtoNoEstoque = produtoDAO.buscarProdutoPorNome(p.getNome());

            if (produtoNoEstoque != null && produtoNoEstoque.getQuantidade() > 0) {
                p.setQuantidade(p.getQuantidade() + 1);
                labelItem.setText(p.toString());
                produtoDAO.atualizarItemAtendimento(atendimento.getId(), produtoNoEstoque.getId(), p.getQuantidade());
                produtoDAO.atualizarEstoqueProduto(p.getNome(), -1);
                recarregarTodosCombosProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente para adicionar mais do produto: " + p.getNome());
            }
        });

        btnMenos.addActionListener(eMenos -> {
            if (p.getQuantidade() > 1) {
                Produto produtoNoEstoque = produtoDAO.buscarProdutoPorNome(p.getNome());
                p.setQuantidade(p.getQuantidade() - 1);
                labelItem.setText(p.toString());
                produtoDAO.atualizarItemAtendimento(atendimento.getId(), produtoNoEstoque.getId(), p.getQuantidade());
                produtoDAO.atualizarEstoqueProduto(p.getNome(), 1);
                recarregarTodosCombosProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "A quantidade mínima permitida é 1.");
            }
        });

        btnExcluir.addActionListener(eExcluir -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente remover o produto do atendimento? Ele será devolvido ao estoque.",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Produto produtoNoEstoque = produtoDAO.buscarProdutoPorNome(p.getNome());
                if (produtoNoEstoque != null) {
                    atendimento.removerProduto(p);
                    produtoDAO.removerItemAtendimento(atendimento.getId(), produtoNoEstoque.getId());
                    produtoDAO.atualizarEstoqueProduto(p.getNome(), p.getQuantidade());
                    listaProdutos.remove(linhaProduto);
                    listaProdutos.revalidate();
                    listaProdutos.repaint();
                    recarregarTodosCombosProdutos();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro: Produto não encontrado no estoque para devolução.");
                }
            }
        });

        linhaProduto.add(labelItem);
        linhaProduto.add(btnMais);
        linhaProduto.add(btnMenos);
        linhaProduto.add(btnExcluir);
        listaProdutos.add(linhaProduto);
    }

    private void atualizarPainelProduto(JPanel listaProdutos, Produto produtoAtualizado) {
        for (Component comp : listaProdutos.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel linhaProduto = (JPanel) comp;
                for (Component subComp : linhaProduto.getComponents()) {
                    if (subComp instanceof JLabel) {
                        JLabel labelItem = (JLabel) subComp;
                        if (labelItem.getText().startsWith(produtoAtualizado.getNome())) {
                            labelItem.setText(produtoAtualizado.toString());
                            return;
                        }
                    }
                }
            }
        }
    }

    private void carregarAtendimentosDoBanco() {
        List<Atendimento> atendimentosDoBanco = atendimentoDAO.carregarAtendimentosEmAndamento();
        for (Atendimento at : atendimentosDoBanco) {
            gerenciador.adicionarAtendimento(at);
            painelAtendimentos.add(criarPainelAtendimento(at));
        }
        painelAtendimentos.revalidate();
        painelAtendimentos.repaint();
    }

    private void recarregarComboProdutos(JComboBox<String> comboProdutos) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboProdutos.getModel();
        model.removeAllElements();
        List<Produto> produtosEstoque = produtoDAO.carregarProdutosEstoque();
        for (Produto p : produtosEstoque) {
            model.addElement(p.getNome() + " (Estoque: " + p.getQuantidade() + ")");
        }
    }

    private void recarregarTodosCombosProdutos() {
        for (Component comp : painelAtendimentos.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel atendimentoPanel = (JPanel) comp;
                for (Component subComp : atendimentoPanel.getComponents()) {
                    if (subComp instanceof JPanel && ((JPanel) subComp).getBorder() instanceof EmptyBorder) {
                        JPanel conteudoPanel = (JPanel) subComp;
                        for (Component addComp : conteudoPanel.getComponents()) {
                            if (addComp instanceof JPanel && ((JPanel) addComp).getLayout() instanceof FlowLayout) {
                                JPanel adicionarPanel = (JPanel) addComp;
                                for (Component comboComp : adicionarPanel.getComponents()) {
                                    if (comboComp instanceof JComboBox) {
                                        recarregarComboProdutos((JComboBox<String>) comboComp);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LogVetProntuarioGUI::new);
    }
}