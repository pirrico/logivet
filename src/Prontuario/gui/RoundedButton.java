/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Prontuario.gui;

import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.event.MouseAdapter; // Importar MouseAdapter
import java.awt.event.MouseEvent;   // Importar MouseEvent

public class RoundedButton extends JButton {

    private int arcWidth = 20;
    private int arcHeight = 20;
    private Color backgroundColor;
    private Color originalBackgroundColor; // Para guardar a cor original
    private Color hoverColor;            // Cor quando o mouse está sobre o botão

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setUI(new BasicButtonUI());

        this.originalBackgroundColor = getBackground();
        this.backgroundColor = originalBackgroundColor;
        // Definir uma cor de hover padrão, pode ser ajustado em cada instância do botão
        this.hoverColor = new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), (int)(backgroundColor.getAlpha() * 0.8)); // 80% da opacidade original

        // Adicionar o MouseListener para o efeito de hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Quando o mouse entra, muda para a cor de hover
                backgroundColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Quando o mouse sai, volta para a cor original
                backgroundColor = originalBackgroundColor;
                repaint();
            }
        });
    }

    public RoundedButton() {
        this("");
    }

    public void setArc(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        // Atualizar as cores originais e de hover quando a cor de fundo é alterada
        this.originalBackgroundColor = bg;
        // Adapte a cor de hover, por exemplo, um pouco mais escura ou transparente
        // Ou, se você tem uma cor de hover específica, defina-a com setHoverColor
        this.hoverColor = new Color(originalBackgroundColor.getRed(), originalBackgroundColor.getGreen(), originalBackgroundColor.getBlue(), (int)(originalBackgroundColor.getAlpha() * 0.8));
        this.backgroundColor = originalBackgroundColor; // Garante que a cor inicial é a original
        repaint();
    }

    // Método para definir uma cor de hover personalizada
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor); // Usa a cor atual (original ou hover)
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));

        super.paintComponent(g2);
        g2.dispose();
    }

    private Shape shape;
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
        }
        return shape.contains(x, y);
    }
}