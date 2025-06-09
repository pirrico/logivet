/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.gui;


import Prontuario.model.Atendimento;
import Prontuario.model.Produto;
import Prontuario.dao.AtendimentoDAO;
import Prontuario.dao.DatabaseConnection;
import Prontuario.dao.ProdutoDAO;
import Prontuario.service.GerenciadorAtendimentos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogVetProntuarioGUI extends JFrame {

    private GerenciadorAtendimentos gerenciador = new GerenciadorAtendimentos();
    private AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    private final JPanel painelAtendimentos;
    private Color verdeEscuro = new Color(0, 98, 78);
    private Color verdeClaro = new Color(198, 237, 222);
    private Color laranja = new Color(245, 123, 58);
    private Color cinzaCampo = new Color(220, 220, 220);

    public LogVetProntuarioGUI() {
        setTitle("LogVet - Prontuário");
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        // TOPO
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(verdeEscuro);
        topo.setPreferredSize(new Dimension(1024, 50));

        JLabel logo = new JLabel("  🐾 LogVet");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 18));
        topo.add(logo, BorderLayout.WEST);

        JPanel menuTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        menuTopo.setOpaque(false);
        String[] itens = {"MENU DE ITENS", "CONFIGURAÇÕES", "LOG", "SAIR"};
        for (String item : itens) {
            JLabel menuItem = new JLabel(item);
            menuItem.setForeground(Color.WHITE);
            menuItem.setFont(new Font("Arial", Font.BOLD, 12));
            menuTopo.add(menuItem);
        }
        topo.add(menuTopo, BorderLayout.EAST);
        container.add(topo, BorderLayout.NORTH);

        // CORPO
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Imagem do prontuário
        try {
            ImageIcon iconeProntuario = new ImageIcon(getClass().getResource("/Prontuario/imagens/Prontuário.png"));
            JLabel imagemProntuario = new JLabel(iconeProntuario);
            imagemProntuario.setAlignmentX(Component.CENTER_ALIGNMENT);
            corpo.add(imagemProntuario);
            corpo.add(Box.createRigidArea(new Dimension(0, 10)));
        } catch (Exception e) {
            // ignore se imagem não estiver disponível
        }

        // NOVO ATENDIMENTO
        corpo.add(criarTituloSeccao("NOVO ATENDIMENTO", laranja));
        corpo.add(criarPainelNovoAtendimento());

        corpo.add(Box.createRigidArea(new Dimension(0, 20)));

        // ATENDIMENTOS EM ANDAMENTO
        corpo.add(criarTituloSeccao("ATENDIMENTO(S) EM ANDAMENTO", laranja));
        painelAtendimentos = new JPanel();
        painelAtendimentos.setLayout(new BoxLayout(painelAtendimentos, BoxLayout.Y_AXIS));
        painelAtendimentos.setBackground(Color.WHITE);
        corpo.add(painelAtendimentos);

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        container.add(scroll, BorderLayout.CENTER);

        add(container);
        setVisible(true);

        // Carregar atendimentos existentes do banco ao iniciar a GUI
        carregarAtendimentosDoBanco();
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

        // Usando RoundedTextField
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

        // Usando RoundedTextField
        RoundedTextField veterinario = new RoundedTextField();
        veterinario.setBackground(cinzaCampo);
        gbc.gridx = 1;
        gbc.gridy = 1;
        painel.add(veterinario, gbc);

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

        // Usando RoundedButton
        RoundedButton criar = new RoundedButton("CRIAR");
        criar.setBackground(laranja);
        criar.setForeground(Color.WHITE);
        criar.setFocusPainted(false);
        criar.setArc(20, 20); // Mantém arredondado
        criar.setHoverColor(laranja.brighter()); // Efeito hover
        gbc.gridx = 3;
        gbc.gridy = 1;
        painel.add(criar, gbc);

        criar.addActionListener(e -> {
            String nome = nomeAnimal.getText().trim();
            String vet = veterinario.getText().trim();
            String esp = (String) especie.getSelectedItem();

            if (nome.isEmpty() || vet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
                return;
            }

            Atendimento atendimento = new Atendimento(nome, vet, esp);

            // Salvar o novo atendimento no banco de dados
            int idGerado = atendimentoDAO.salvarAtendimento(atendimento);
            if (idGerado != -1) {
                atendimento.setId(idGerado);
                 gerenciador.adicionarAtendimento(atendimento);
                painelAtendimentos.add(criarPainelAtendimento(atendimento));
                painelAtendimentos.revalidate();
                painelAtendimentos.repaint();

                nomeAnimal.setText("");
                veterinario.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao criar atendimento no banco de dados.");
            }
        });

        return painel;
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

        // Usando RoundedButton
        RoundedButton toggle = new RoundedButton("▼");
        toggle.setBackground(laranja);
        toggle.setForeground(Color.WHITE);
        toggle.setFocusPainted(false);
        toggle.setArc(10, 10); // Ajuste o arredondamento do botão toggle
        toggle.setHoverColor(laranja.brighter()); // Efeito hover

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(verdeClaro);
        cabecalho.add(info, BorderLayout.WEST);
        cabecalho.add(toggle, BorderLayout.EAST);
        atendimentoPanel.add(cabecalho);

        // Conteúdo do atendimento (produtos, campos)
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(verdeClaro);
        conteudo.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Lista visual de produtos
        JPanel listaProdutos = new JPanel();
        listaProdutos.setLayout(new BoxLayout(listaProdutos, BoxLayout.Y_AXIS));
        listaProdutos.setBackground(verdeClaro);

        JScrollPane scrollProdutos = new JScrollPane(listaProdutos);
        scrollProdutos.setPreferredSize(new Dimension(900, 180));
        scrollProdutos.setBorder(null);
        scrollProdutos.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        conteudo.add(scrollProdutos);

        // Adicionar produtos existentes do atendimento (se carregado do banco)
        for (Produto p : atendimento.getProdutos()) {
            adicionarProdutoAoPainel(p, atendimento, listaProdutos);
        }

        // Campos para adicionar produto
        JPanel adicionar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adicionar.setBackground(verdeClaro);

        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        List<Produto> produtosEstoque = produtoDAO.carregarProdutosEstoque();
        for (Produto p : produtosEstoque) {
            comboModel.addElement(p.getNome() + " (Estoque: " + p.getQuantidade() + ")");
        }
        JComboBox<String> comboProdutos = new JComboBox<>(comboModel);

        // Usando RoundedTextField
        RoundedTextField campoQuant = new RoundedTextField("1", 3);
        campoQuant.setArc(10, 10); // Ajuste o arredondamento do campo de quantidade

        // Usando RoundedButton
        RoundedButton btnAdd = new RoundedButton("ADICIONAR");
        btnAdd.setArc(10, 10); // Ajuste o arredondamento do botão adicionar
        btnAdd.setHoverColor(laranja.brighter()); // Efeito hover

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
            // Extrai o nome do produto da string de exibição do ComboBox
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

            // Criar um objeto Produto para o atendimento (com o ID do produto do estoque)
            Produto produtoParaAdicionar = new Produto(produtoNoEstoque.getId(), produtoNoEstoque.getCodigoBarras(), produtoNoEstoque.getNome(), quant);

            Optional<Produto> produtoExistenteNoAtendimento = atendimento.getProdutos().stream()
                    .filter(prod -> prod.getNome().equals(nomeProduto))
                    .findFirst();

            if (produtoExistenteNoAtendimento.isPresent()) {
                Produto pExistente = produtoExistenteNoAtendimento.get();
                pExistente.setQuantidade(pExistente.getQuantidade() + quant);
                // Atualiza na tabela produtos_atendimentos
                produtoDAO.atualizarItemAtendimento(atendimento.getId(), pExistente.getId(), pExistente.getQuantidade());
                atualizarPainelProduto(listaProdutos, pExistente);
            } else {
                atendimento.adicionarProduto(produtoParaAdicionar);
                try {
                    // Salva na tabela produtos_atendimentos
                    produtoDAO.salvarItemAtendimento(DatabaseConnection.getConnection(), atendimento.getId(), produtoParaAdicionar.getId(), quant); // Passa a conexão
                } catch (SQLException ex) {
                    Logger.getLogger(LogVetProntuarioGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                adicionarProdutoAoPainel(produtoParaAdicionar, atendimento, listaProdutos);
            }

            // Atualizar estoque no banco e na GUI
            produtoDAO.atualizarEstoqueProduto(nomeProduto, -quant);
            recarregarTodosCombosProdutos();

            listaProdutos.revalidate();
            listaProdutos.repaint();
        });

        // Botões de ação
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(verdeClaro);

        // Usando RoundedButton
        RoundedButton finalizar = new RoundedButton("FINALIZAR ATENDIMENTO");
        RoundedButton cancelar = new RoundedButton("CANCELAR ATENDIMENTO");
        finalizar.setBackground(laranja);
        finalizar.setForeground(Color.WHITE);
        cancelar.setBackground(laranja);
        cancelar.setForeground(Color.WHITE);
        finalizar.setArc(20, 20); // Ajuste o arredondamento
        cancelar.setArc(20, 20); // Ajuste o arredondamento
        finalizar.setHoverColor(laranja.brighter()); // Efeito hover
        cancelar.setHoverColor(laranja.brighter()); // Efeito hover

        botoes.add(cancelar);
        botoes.add(finalizar);
        conteudo.add(botoes);

        cancelar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente cancelar o atendimento? Os produtos serão devolvidos ao estoque.",
                    "Confirmar Cancelamento",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Devolver produtos ao estoque
                for (Produto p : atendimento.getProdutos()) {
                    produtoDAO.atualizarEstoqueProduto(p.getNome(), p.getQuantidade());
                }
                atendimentoDAO.removerAtendimento(atendimento.getId()); // Remove do banco
                gerenciador.removerAtendimento(atendimento);
                painelAtendimentos.remove(atendimentoPanel);
                painelAtendimentos.revalidate();
                painelAtendimentos.repaint();
                recarregarTodosCombosProdutos(); // Atualiza todos os combos de produto abertos
            }
        });

        finalizar.addActionListener(e -> {
            // VERIFICAÇÃO ADICIONADA AQUI
            if (atendimento.getProdutos().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não é possível finalizar um prontuário sem nenhum produto adicionado.");
                return; // Impede a finalização se não houver produtos
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja finalizar este atendimento?",
                    "Confirmar Finalização",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Atendimento finalizado.");
                // Não é necessário devolver produtos ao estoque, pois já foram descontados
                // O atendimento será removido do painel e do banco (considerando que atendimentos finalizados não ficam 'em andamento')
                atendimentoDAO.removerAtendimento(atendimento.getId()); // Remove do banco (ou altere o status para 'finalizado' e mova para uma tela de histórico)
                gerenciador.removerAtendimento(atendimento);
                painelAtendimentos.remove(atendimentoPanel);
                painelAtendimentos.revalidate();
                painelAtendimentos.repaint();
                recarregarTodosCombosProdutos(); // Atualiza todos os combos de produto abertos
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
        JLabel labelItem = new JLabel(p.toString()); // Usa o toString de Produto

        // Revertendo para JButton padrão para botões de ícone
        JButton btnMais = new JButton(new ImageIcon(getClass().getResource("/Prontuario/imagens/mais.png")));
        JButton btnMenos = new JButton(new ImageIcon(getClass().getResource("/Prontuario/imagens/menos.png")));
        JButton btnExcluir = new JButton(new ImageIcon(getClass().getResource("/Prontuario/imagens/lixeira.png")));

        // Aplicar propriedades para deixar sem a parte branca e com a sensação de clique
        for (JButton btn : new JButton[]{btnMais, btnMenos, btnExcluir}) {
            btn.setContentAreaFilled(false); // Remove o preenchimento da área do botão
            btn.setBorderPainted(false);      // Remove a borda padrão do botão
            btn.setFocusPainted(false);      // Remove o foco pintado ao clicar
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mão

            // Adicionar efeito hover manual para estes botões padrão
            Color originalBg = btn.getBackground(); // Pode não ter efeito visual real se contentAreaFilled for false
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {

                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    // Voltar ao normal
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
                        // O label agora começa com o nome do produto
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