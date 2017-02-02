package io.github.apfelcreme.CommunicationKitchen.Client;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableOrder;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.BorderPanel;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.DrawingBoard;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.HeartBoard;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.OrderBoard;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.multi.MultiLabelUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Timer;

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

/**
 * food-sprites: https://ccrgeek.wordpress.com/rpg-maker-ace/graphics/character-sprites/
 * alarm-clock: http://downloadicons.net/android-powered-alarm-clock-icon-74359
 * order-symbol: http://www.flaticon.com/free-icon/medical-notes-symbol-of-a-list-paper-on-a-clipboard_45945
 */

    /*

    PRODUCTIVE FAILURE

    Simultane sachen machen lassen
    asynchrone sachen machen lassen (reihenfolge)
    JIGSAW (geteilte informationen)
    unter Zeitdruck sachen machen lassen
     */


public class CommunicationKitchen extends JFrame {

    private static CommunicationKitchen instance = null;

    private Vector<Drawable> drawables = new Vector<Drawable>();

    private Vector<DrawableOrder> orders = new Vector<DrawableOrder>();

    private Set<Integer> keysPressed;

    private UUID me = null;
    private int hearts = 0;
    private int round = 0;

    private JTextField chat = new JTextField("Chat");
    private JPanel messagePanel;
    private JButton messagePanelButton = new JButton("Ok!");
    private JButton bnSend = new JButton("Send");
    private JLabel hintBox = new JLabel();
    private JToggleButton bnReady = new JToggleButton();

    static {
        UIManager.put("ToggleButton.select", new Color(0, 150, 0));
    }

    private CommunicationKitchen() {
        String ip = JOptionPane.showInputDialog(this, "IP", "127.0.0.1");
        String name = JOptionPane.showInputDialog(this, "Name", "Name");
        try {
            initGui(50, 50);
            ServerConnector.getInstance().connect(ip, 1337, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * init all gui stuff
     *
     * @param width  the window width
     * @param height the window height
     */
    public void initGui(int width, int height) throws IOException {

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{25, 100, 25};
        gridBagLayout.rowHeights = new int[]{25, 25, 100, 30, 380, 30, 25};
        this.getContentPane().setLayout(gridBagLayout);

        chat.setBackground(new Color(67, 67, 67));
        chat.setForeground(Color.WHITE);
        chat.setBorder(BorderFactory.createEmptyBorder());

        bnSend.setForeground(new Color(200, 200, 200));
        bnSend.setBackground(new Color(67, 67, 67));
        bnSend.setBorder(BorderFactory.createEmptyBorder());
        bnSend.setFocusPainted(false);
        bnSend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bnSend.setBackground(new Color(87, 87, 87));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bnSend.setBackground(new Color(67, 67, 67));
            }
        });

        JPanel chatBg = new JPanel();
        chatBg.setLayout(new GridBagLayout());
        chatBg.add(chat, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 0), 0, 0));
        chatBg.add(bnSend, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 0), 0, 0));
        chatBg.setBackground(new Color(47, 47, 47));

        hintBox.setOpaque(true);
        hintBox.setBackground(new Color(67, 67, 67));
        hintBox.setForeground(new Color(200, 200, 200));
        hintBox.setBorder(BorderFactory.createEmptyBorder());
        hintBox.setVerticalAlignment(SwingConstants.CENTER);
        hintBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        messagePanelButton.setFocusPainted(false);
        messagePanelButton.setBackground(new Color(67, 67, 67));
        messagePanelButton.setForeground(new Color(200, 200, 200));
        messagePanelButton.setBorder(BorderFactory.createEmptyBorder());
        messagePanelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMessageBoard(!messagePanel.isVisible());
            }
        });
        messagePanelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                messagePanelButton.setBackground(new Color(87, 87, 87));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                messagePanelButton.setBackground(new Color(67, 67, 67));
            }
        });
        messagePanel = new JPanel();
        messagePanel.setBackground(new Color(0, 0, 0, 0));
        messagePanel.setLayout(new GridBagLayout());
        messagePanel.add(hintBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(3, 0, 0, 0), 0, 0));
        messagePanel.add(messagePanelButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        messagePanel.setBackground(new Color(47, 47, 47));

        bnReady.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/ready.png"))));
        bnReady.setBackground(new Color(47, 47, 47));
        bnReady.setFocusPainted(false);
        bnReady.setBorder(BorderFactory.createEmptyBorder());
        bnReady.setRolloverEnabled(false);
        bnReady.setFocusable(false);

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
                ServerConnector.getInstance().disconnect();
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
                new GridBagConstraints(0, 1, 1, 5, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(HeartBoard.getInstance(),
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(OrderBoard.getInstance(),
                new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(bnReady,
                new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(DrawingBoard.getInstance(),
                new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(messagePanel,
                new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(chatBg,
                new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Right.png"))
                        .getScaledInstance(25, 1000, Image.SCALE_SMOOTH))),
                new GridBagConstraints(2, 1, 1, 5, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));


        //BOTTOM
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/BottomLeft.png")))),
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Bottom.png"))
                        .getScaledInstance(1000, 25, Image.SCALE_SMOOTH))),
                new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/BottomRight.png")))),
                new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

        this.dispose();
        this.setUndecorated(true);
        this.setBackground(new Color(0, 0, 0, 0));

        //Key Inputs
        initKeys();


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerConnector.getInstance().disconnect();
                System.exit(0);
            }
        });
        bnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendChatMessage(me, chat.getText());
                chat.setText("");
                DrawingBoard.getInstance().requestFocus();
            }
        });
        bnReady.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendReady(me, bnReady.isSelected());
                toggleMessageBoard(!bnReady.isSelected());
                DrawingBoard.getInstance().requestFocus();
            }
        });
        this.setSize(new Dimension(width, height));
        DrawingBoard.getInstance().requestFocus();

        System.out.println(new File(CommunicationKitchen.class.getResource("/checklist.png").toExternalForm()).exists());

            setMessage(
                    "<u><b>Spielanleitung:</b></u><br />" +
                            "Ziel des Spiels ist es, alle Bestellungen abzuarbeiten, ohne dabei Fehler zu machen. Du wirst dich " +
                            "mit deinen Mitspielern abstimmen müssen, da sonst entweder die Zeit nicht ausreicht, oder ihr Fehler " +
                            "in der Zubereitung der Gerichte macht. <br />" +
                            "Folgende Typen von Aufträgen müsst ihr meistern: " +
                            "<left><ol>" +
//                            "<li><img src=\"" + new File(CommunicationKitchen.class.getResource("/clock.png").toExternalForm()).getAbsolutePath() + "\" />" +
                            "<li>&nbsp;auf Zeit: Nachdem einer von euch die erste Zutat abgeliefert hat, müsst ihr die anderen direkt danach einwerfen. </li><br />" +
//                            "<li><img src=\"" + new File(CommunicationKitchen.class.getResource("/checklist.png").toExternalForm()).getAbsolutePath() + "\" />" +
                            "<li>&nbsp;der Reihe nach: Die Zutaten müssen in einer festen Reihenfolge abgeliefert werden.</li>" +
                            "</ol>" +
                            "Wenn ihr die Bestellung nicht erfolgreich abschließen könnt, verliert ihr ein Leben!" +
                            "</left>");

    }

    /**
     * returns the client id
     *
     * @return the client io
     */
    public UUID getMe() {
        return me;
    }

    /**
     * sets the client id
     *
     * @param me the client id
     */
    public void setMe(UUID me) {
        this.me = me;
    }

    /**
     * returns the list of drawable players
     *
     * @return the list of drawable players
     */
    public Set<DrawablePlayer> getDrawablePlayers() {
        Set<DrawablePlayer> drawablePlayers = new HashSet<DrawablePlayer>();
        for (Drawable drawable : drawables) {
            if (drawable instanceof DrawablePlayer) {
                drawablePlayers.add((DrawablePlayer) drawable);
            }
        }
        return drawablePlayers;
    }

    /**
     * returns the drawable player object with the given id
     *
     * @param id the id
     * @return the drawable player object that is connected
     * to the player with the given id
     */
    public DrawablePlayer getDrawablePlayer(UUID id) {
        for (DrawablePlayer drawablePlayer : getDrawablePlayers()) {
            if (drawablePlayer.getId().equals(id)) {
                return drawablePlayer;
            }
        }
        return null;
    }

    /**
     * removes a player object from the list
     * @param id the player id
     */
    public void removeDrawablePlayer(UUID id) {
        removeDrawable(id);
    }

    /**
     * returns a list of drawable objects
     *
     * @return a list of drawable objects
     */
    public Vector<Drawable> getDrawables() {
        return drawables;
    }


    /**
     * returns the drawable object with the given id
     *
     * @param id the id
     * @return the drawable
     */
    public Drawable getDrawable(UUID id) {
        for (Drawable drawable : getDrawables()) {
            if (drawable.getId().equals(id)) {
                return drawable;
            }
        }
        return null;
    }

    /**
     * removes an ingredient from the list of drawables
     *
     * @param id the ingredient id
     */
    public void removeDrawable(UUID id) {
        for (Iterator<Drawable> it = drawables.iterator(); it.hasNext(); ) {
            Drawable drawable = it.next();
            if (drawable.getId().equals(id)) {
                it.remove();
            }
        }
    }

    /**
     * returns the order list
     *
     * @return the order list
     */
    public Vector<DrawableOrder> getOrders() {
        return orders;
    }

    /**
     * removes an order from the list of orders
     *
     * @param id the order id
     */
    public void removeOrder(UUID id) {
        for (Iterator<DrawableOrder> it = orders.iterator(); it.hasNext(); ) {
            DrawableOrder drawableOrder = it.next();
            if (drawableOrder.getId().equals(id)) {
                it.remove();
            }
        }
    }

    /**
     * returns the drawable order with the given id
     *
     * @param id the order id
     * @return the order with the given id
     */
    public DrawableOrder getDrawableOrder(UUID id) {
        for (DrawableOrder drawableOrder : orders) {
            if (drawableOrder.getId().equals(id)) {
                return drawableOrder;
            }
        }
        return null;
    }

    /**
     * sets the hintbox's message
     *
     * @param message the message
     */
    public void setMessage(String message) {
        hintBox.setText("<html><left>" + message + "</left></html>");
        toggleMessageBoard(true);
    }

    /**
     * shows or hides the message board
     */
    private void toggleMessageBoard(boolean visible) {
        DrawingBoard.getInstance().setVisible(!visible);
        messagePanel.setVisible(visible);
        bnReady.requestFocusInWindow();
    }

    /**
     * returns the amount of hearts the players have
     *
     * @return the amount of hearts the players have
     */
    public int getHearts() {
        return hearts;
    }

    /**
     * sets the amount of hearts the player has
     *
     * @param hearts the new amount of hearts
     */
    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    /**
     * returns the current round
     *
     * @return the current round
     */
    public int getRound() {
        return round;
    }

    /**
     * sets the current round
     *
     * @param round the new round number
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * initializes all key events
     */
    public void initKeys() {
        keysPressed = new HashSet<Integer>();

        InputMap inputMap = DrawingBoard.getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "w_pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "w_released");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "a_pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "a_released");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "s_pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "s_released");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "d_pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "d_released");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enter_pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "enter_released");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space_pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "space_released");
        ActionMap actionMap = DrawingBoard.getInstance().getActionMap();
        actionMap.put("w_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.add(KeyEvent.VK_W);
                execute();
            }
        });
        actionMap.put("w_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.remove(KeyEvent.VK_W);
                execute();
            }
        });
        actionMap.put("a_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.add(KeyEvent.VK_A);
                execute();
            }
        });
        actionMap.put("a_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.remove(KeyEvent.VK_A);
                execute();
            }
        });
        actionMap.put("s_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.add(KeyEvent.VK_S);
                execute();
            }
        });
        actionMap.put("s_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.remove(KeyEvent.VK_S);
                execute();
            }
        });
        actionMap.put("d_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.add(KeyEvent.VK_D);
                execute();
            }
        });
        actionMap.put("d_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.remove(KeyEvent.VK_D);
                execute();
            }
        });
        actionMap.put("enter_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.add(KeyEvent.VK_ENTER);
                execute();
            }
        });
        actionMap.put("enter_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.remove(KeyEvent.VK_ENTER);
                execute();
            }
        });
        actionMap.put("space_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.add(KeyEvent.VK_SPACE);
                execute();
            }
        });
        actionMap.put("space_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                keysPressed.remove(KeyEvent.VK_SPACE);
                execute();
            }
        });
    }

    /**
     * executes key events
     */
    private void execute() {
        if (!keysPressed.isEmpty()) {
            if (keysPressed.contains(KeyEvent.VK_W)) {
                if (keysPressed.contains(KeyEvent.VK_A)) {
                    ServerConnector.getInstance().sendPlayerMove(me, Direction.NORTH_WEST);
                } else if (keysPressed.contains(KeyEvent.VK_D)) {
                    ServerConnector.getInstance().sendPlayerMove(me, Direction.NORTH_EAST);
                } else {
                    ServerConnector.getInstance().sendPlayerMove(me, Direction.NORTH);
                }
            } else if (keysPressed.contains(KeyEvent.VK_S)) {
                if (keysPressed.contains(KeyEvent.VK_A)) {
                    ServerConnector.getInstance().sendPlayerMove(me, Direction.SOUTH_WEST);
                } else if (keysPressed.contains(KeyEvent.VK_D)) {
                    ServerConnector.getInstance().sendPlayerMove(me, Direction.SOUTH_EAST);
                } else {
                    ServerConnector.getInstance().sendPlayerMove(me, Direction.SOUTH);
                }
            } else if (keysPressed.contains(KeyEvent.VK_A)) {
                ServerConnector.getInstance().sendPlayerMove(me, Direction.WEST);
            } else if (keysPressed.contains(KeyEvent.VK_D)) {
                ServerConnector.getInstance().sendPlayerMove(me, Direction.EAST);
            } else if (keysPressed.contains(KeyEvent.VK_SPACE)) {
                ServerConnector.getInstance().sendItemDrop(me);
            } else if (keysPressed.contains(KeyEvent.VK_ENTER)) {
                ServerConnector.getInstance().sendChatMessage(me, chat.getText());
            }
        }
    }

    /**
     * returns the client instance
     *
     * @return the client instance
     */
    public static CommunicationKitchen getInstance() {
        synchronized (CommunicationKitchen.class) {
            if (instance == null) {
                instance = new CommunicationKitchen();
            }
        }
        return instance;
    }

    /**
     * start :)
     *
     * @param args the start parameters
     */
    public static void main(String[] args) {
        CommunicationKitchen.getInstance();
    }

}
