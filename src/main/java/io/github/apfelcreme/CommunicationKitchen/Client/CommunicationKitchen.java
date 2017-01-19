package io.github.apfelcreme.CommunicationKitchen.Client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableIngredient;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePot;
import io.github.apfelcreme.CommunicationKitchen.Client.Listener.KeyListener;

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
 *
 *
 *
 *
 *
 *
 *
 *
 */

    /*

    PRODUCTIVE FAILURE

    Simultane sachen machen lassen
    asynchrone sachen machen lassen (reihenfolge)
    JIGSAW (geteilte informationen)
    unter Zeitdruck sachen machen lassen
     */


public class CommunicationKitchen extends JFrame {

    private Set<DrawablePlayer> drawablePlayers = new HashSet<DrawablePlayer>();
    private Set<DrawableIngredient> drawableIngredients = new HashSet<DrawableIngredient>();
    private DrawablePlayer me = null;
    private static CommunicationKitchen instance = null;

    private JTextField chat = new JTextField("Chat");
    private JButton bSend = new JButton("Send");

    private CommunicationKitchen() {
        initGui(50, 50);
        try {
            ServerConnector.getInstance().connect("127.0.0.1", 1337);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initGui(int width, int height) {
        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(new JPanel(),
                new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(DrawingBoard.getInstance(),
                new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(chat,
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(bSend,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
//        this.addKeyListener(new KeyListener());

        InputMap inputMap = DrawingBoard.getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke wKey = KeyStroke.getKeyStroke('w');
        KeyStroke aKey = KeyStroke.getKeyStroke('a');
        KeyStroke sKey = KeyStroke.getKeyStroke('s');
        KeyStroke dKey = KeyStroke.getKeyStroke('d');
        inputMap.put(wKey, "w");
        inputMap.put(aKey, "a");
        inputMap.put(sKey, "s");
        inputMap.put(dKey, "d");
        ActionMap actionMap = DrawingBoard.getInstance().getActionMap();
        actionMap.put("w", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.UP);
            }
        });
        actionMap.put("a", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.LEFT);
            }
        });
        actionMap.put("s", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.DOWN);
            }
        });
        actionMap.put("d", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.RIGHT);
            }
        });


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerConnector.getInstance().disconnect();
                System.exit(0);
            }
        });
        bSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConnector.getInstance().sendChatMessage(me.getId(), chat.getText());
                chat.setText("");
                DrawingBoard.getInstance().requestFocus();
            }
        });
        this.setSize(new Dimension(width, height));
        this.setVisible(true);
        DrawingBoard.getInstance().requestFocus();


    }

    public DrawablePlayer getMe() {
        return me;
    }

    public void setMe(DrawablePlayer me) {
        this.me = me;
    }


    /**
	 * @return the drawablePot
	 */
	public DrawablePot getDrawablePot() {
		return drawablePot;
	}

	/**
	 * @param drawablePot the drawablePot to set
	 */
	public void setDrawablePot(DrawablePot drawablePot) {
		this.drawablePot = drawablePot;
	}

	/**
     * returns the list of drawable players
     *
     * @return the list of drawable players
     */
    public Set<DrawablePlayer> getDrawablePlayers() {
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
        for (DrawablePlayer drawablePlayer : drawablePlayers) {
            if (drawablePlayer.getId().equals(id)) {
                return drawablePlayer;
            }
        }
        return null;
    }

    /**
     * returns the list of drawable ingredients
     *
     * @return the list of drawable ingredients
     */
    public Set<DrawableIngredient> getDrawableIngredients() {
        return drawableIngredients;
    }

    /**
     * returns the drawable player object with the given id
     *
     * @param id the id
     * @return the drawable player object that is connected
     * to the player with the given id
     */
    public DrawableIngredient getDrawableIngredient(UUID id) {
        for (DrawableIngredient drawableIngredient : drawableIngredients) {
            if (drawableIngredient.getId().equals(id)) {
                return drawableIngredient;
            }
        }
        return null;
    }

    public static CommunicationKitchen getInstance() {
        synchronized (CommunicationKitchen.class) {
            if (instance == null) {
                instance = new CommunicationKitchen();
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        CommunicationKitchen.getInstance();
    }

}
