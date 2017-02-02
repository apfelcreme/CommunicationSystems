package io.github.apfelcreme.CommunicationKitchen.Client;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableOrder;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.BorderPanel;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.DrawingBoard;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.OrderBoard;
import io.github.apfelcreme.CommunicationKitchen.Server.Order;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
 */

    /*

    PRODUCTIVE FAILURE

    Simultane sachen machen lassen
    asynchrone sachen machen lassen (reihenfolge)
    JIGSAW (geteilte informationen)
    unter Zeitdruck sachen machen lassen
     */


public class CommunicationKitchen extends JFrame {

    private Vector<Drawable> drawables = new Vector<Drawable>();

    private Vector<DrawableOrder> orders = new Vector<DrawableOrder>();

    private UUID me = null;
    private static CommunicationKitchen instance = null;

    private JTextField chat = new JTextField("Chat");
    private JButton bSend = new JButton("Send");
    
    private JPanel messagePanel;
    private JTextArea hintBox = new JTextArea();
    private JButton bnConfirm = new JButton("OK");

    private CommunicationKitchen() {
        String ip = JOptionPane.showInputDialog(this, "IP", "127.0.0.1");
        try {
            initGui(50, 50);
            ServerConnector.getInstance().connect(ip, 1337);
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
        gridBagLayout.rowHeights = new int[]{25, 100, 25};
        this.getContentPane().setLayout(gridBagLayout);

        chat.setBackground(new Color(67, 67, 67));
        chat.setForeground(Color.WHITE);
        chat.setBorder(BorderFactory.createEmptyBorder());

        JPanel chatBg = new JPanel();
        chatBg.setLayout(new GridBagLayout());
        chatBg.add(chat, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(3, 0, 3, 0), 0, 0));
        chatBg.setBackground(new Color(47, 47, 47));
        
        hintBox.setLineWrap(true);
        hintBox.setWrapStyleWord(true);
        hintBox.setBackground(new Color(67, 67, 67));
        hintBox.setForeground(Color.WHITE);
        hintBox.setBorder(BorderFactory.createEmptyBorder());
        messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());
        messagePanel.add(hintBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(3, 0, 3, 0), 0, 0));
        messagePanel.add(bnConfirm, new GridBagConstraints(0, 1, 1, 1, 0, 0,
        		GridBagConstraints.NORTH, GridBagConstraints.NONE,
        		new Insets(3, 0, 3, 0), 0, 0));
        messagePanel.setBackground(new Color(47, 47, 47));

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
                new GridBagConstraints(0, 1, 1, 3, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(OrderBoard.getInstance(),
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));        
        this.getContentPane().add(DrawingBoard.getInstance(),
                new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(messagePanel,
                new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(chatBg,
                new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Right.png"))
                        .getScaledInstance(25, 1000, Image.SCALE_SMOOTH))),
                new GridBagConstraints(2, 1, 1, 3, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));


        //BOTTOM
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/BottomLeft.png")))),
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/Bottom.png"))
                        .getScaledInstance(1000, 25, Image.SCALE_SMOOTH))),
                new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new BorderPanel(this, new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Border/BottomRight.png")))),
                new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

        this.dispose();
        this.setUndecorated(true);
        this.setBackground(new Color(200, 0, 0, 0));


        //Key Inputs
        InputMap inputMap = DrawingBoard.getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke('w'), "w");
        inputMap.put(KeyStroke.getKeyStroke('a'), "a");
        inputMap.put(KeyStroke.getKeyStroke('s'), "s");
        inputMap.put(KeyStroke.getKeyStroke('d'), "d");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
        ActionMap actionMap = DrawingBoard.getInstance().getActionMap();
        actionMap.put("w", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me, Direction.UP);
            }
        });
        actionMap.put("a", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me, Direction.LEFT);
            }
        });
        actionMap.put("s", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me, Direction.DOWN);
            }
        });
        actionMap.put("d", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me, Direction.RIGHT);
            }
        });
        actionMap.put("space", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendItemDrop(me);
            }
        });
        actionMap.put("enter", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendChatMessage(me, chat.getText());
                chat.setText("");
                DrawingBoard.getInstance().requestFocus();
            }
        });

        //End Key Inputs

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerConnector.getInstance().disconnect();
                System.exit(0);
            }
        });
        bSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendChatMessage(me, chat.getText());
                chat.setText("");
                DrawingBoard.getInstance().requestFocus();
            }
        });
        bnConfirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerReady(me);                
                messagePanel.setVisible(false);
                DrawingBoard.getInstance().setVisible(true);
                DrawingBoard.getInstance().requestFocus();                
            }
        });
        this.setSize(new Dimension(width, height));
        DrawingBoard.getInstance().requestFocus();
        setMessage("SPIELANLEITUNG");
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
	 * @return the hintBox
	 */
	public void setMessage(String message) {
		hintBox.setText(message);
		DrawingBoard.getInstance().setVisible(false);
		messagePanel.setVisible(true);
		bnConfirm.requestFocusInWindow();
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
