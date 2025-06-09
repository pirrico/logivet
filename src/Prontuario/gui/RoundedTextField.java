/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.gui;

import javax.swing.JTextField;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;

public class RoundedTextField extends JTextField {

    private int arcWidth = 10; // Raio do arredondamento horizontal
    private int arcHeight = 10; // Raio do arredondamento vertical
    private Color backgroundColor; // Cor de fundo do campo

    public RoundedTextField(String string, int columns) {
        super(columns);
        setOpaque(false); // Importante: desativa o preenchimento padrão
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Adiciona um padding interno
        this.backgroundColor = getBackground(); // Pega a cor de fundo inicial
    }

    public RoundedTextField() {
        this("1", 0); // Chama o construtor com 0 colunas
    }

    public void setArc(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.backgroundColor = bg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha o fundo arredondado
        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
        } else {
            g2.setColor(getBackground()); // Fallback
        }
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));

        super.paintComponent(g2); // Desenha o texto
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Não desenha a borda padrão para que possamos controlá-la no paintComponent se necessário
        // Ou você pode desenhar uma borda arredondada personalizada aqui
        // Graphics2D g2 = (Graphics2D) g.create();
        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // g2.setColor(getForeground()); // Cor da borda
        // g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        // g2.dispose();
    }

    // Para garantir que o layout considere o padding
    @Override
    public Insets getInsets() {
        return new Insets(5, 10, 5, 10);
    }
}
