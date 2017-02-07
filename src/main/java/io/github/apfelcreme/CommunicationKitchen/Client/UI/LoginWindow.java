package io.github.apfelcreme.CommunicationKitchen.Client.UI;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Copyright (C) 2017 Lord36 aka Apfelcreme
 * <p>
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * @author Lord36 aka Apfelcreme
 */
public class LoginWindow extends JFrame implements ActionListener {

    private JButton bnLogin;
    private JTextField ip;
    private JTextField name;
    private ColorPicker colorPicker;

    public LoginWindow() {
        try {
            initGui(300, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * inits the gui
     *
     * @param width  the window width
     * @param height the window height
     * @throws IOException
     */
    private void initGui(int width, int height) throws IOException {

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{25, width - 50, 25};
        gridBagLayout.rowHeights = new int[]{25, height - 50, 25};
        this.setLayout(gridBagLayout);

        JPanel container = new JPanel();
        container.setBackground(new Color(47, 47, 47));
        container.setLayout(new GridBagLayout());
        container.setOpaque(true);

        // IP
        ip = new JTextField("127.0.0.1");
        ip.setBackground(new Color(67, 67, 67));
        ip.setForeground(new Color(200, 200, 200));
        ip.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel ipLabel = new JLabel("IP-Adresse");
        ipLabel.setForeground(new Color(200, 200, 200));
        container.add(ipLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        container.add(ip, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 0), 0, 0));

        // Name
        name = new JTextField("");
        name.setBackground(new Color(67, 67, 67));
        name.setForeground(new Color(200, 200, 200));
        name.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel nameLabel = new JLabel("Spielername");
        nameLabel.setForeground(new Color(200, 200, 200));
        container.add(nameLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
        container.add(name, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 0), 0, 0));

        // Color

        colorPicker = new ColorPicker(width - 45, 200);
        colorPicker.setBackground(new Color(47, 47, 47));
        colorPicker.setOpaque(true);
        JLabel colorLabel = new JLabel("Farbe");
        colorLabel.setForeground(new Color(200, 200, 200));
        container.add(colorLabel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
        container.add(colorPicker, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 0), 0, 0));

        bnLogin = new JButton("Login");
        bnLogin.setFocusPainted(false);
        bnLogin.setBackground(new Color(67, 67, 67));
        bnLogin.setForeground(new Color(200, 200, 200));
        bnLogin.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bnLogin.setRolloverEnabled(false);
        bnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bnLogin.setBackground(new Color(87, 87, 87));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bnLogin.setBackground(new Color(67, 67, 67));
            }
        });
        bnLogin.addActionListener(this);
        container.add(bnLogin, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 0), 0, 0));

        //TOP
        BorderPanel buttons = new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Top.png"))
                .getScaledInstance(1000, 25, Image.SCALE_SMOOTH)));
        JButton bnClose = new JButton(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Close.png"))));
        bnClose.setRolloverIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/CloseHover.png"))));
        bnClose.setContentAreaFilled(false);
        bnClose.setFocusPainted(false);
        bnClose.setBorder(BorderFactory.createEmptyBorder());
        bnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel emptySpace = new JPanel();
        emptySpace.setBackground(new Color(0, 0, 0, 0));
        buttons.setLayout(new GridBagLayout());

        buttons.add(emptySpace, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(7, 0, 0, 0), 0, 0));
        buttons.add(bnClose, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(7, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/TopLeft.png")))),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(buttons,
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/TopRight.png")))),
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

        //MID
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Left.png"))
                        .getScaledInstance(25, 1000, Image.SCALE_SMOOTH))),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(container,
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Right.png"))
                        .getScaledInstance(25, 1000, Image.SCALE_SMOOTH))),
                new GridBagConstraints(2, 1, 1, 1, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));


        //BOTTOM
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/BottomLeft.png")))),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Bottom.png"))
                        .getScaledInstance(1000, 25, Image.SCALE_SMOOTH))),
                new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/BottomRight.png")))),
                new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

        this.setSize(width + 4, height + 15);
        this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/Drawables/CARROT.png")));
        this.setUndecorated(true);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setVisible(true);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bnLogin) {
            if (!ip.getText().isEmpty() && !name.getText().isEmpty()) {
                CommunicationKitchen.getInstance().initialize(ip.getText(), name.getText(), colorPicker.getSelectedColor());
                this.dispose();
            }
        }
    }

    /**
     * start :)
     *
     * @param args the start parameters
     */
    public static void main(String[] args) {
        new LoginWindow();
    }
}
