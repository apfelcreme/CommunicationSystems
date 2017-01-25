package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
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
public class KitchenServer extends JFrame implements Runnable {

    private static KitchenServer instance = null;

    private ServerSocket serverSocket = null;
    private Dimension fieldDimension = new Dimension(500, 400);

    private List<ConnectionHandler> clientConnections = new ArrayList<ConnectionHandler>();

    private List<Player> players = new ArrayList<Player>();

    private List<Order> orders = new ArrayList<Order>();

    private JTextArea log;

    private IngredientSpawner ingredientSpawner = null;

    private KitchenServer() {
        try {
            setGui();
            this.serverSocket = new ServerSocket(1337);
            log("Start handling connections...");
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * initializes the GUI
     */
    private void setGui() {
        JButton bnStart = new JButton("Start Game");
        log = new JTextArea();
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.setLayout(new GridBagLayout());
        this.getContentPane().add(bnStart,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new JScrollPane(log),
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        bnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long time = 15000;
                if (ingredientSpawner != null) {
                    ingredientSpawner.cancel();
                }
                ingredientSpawner = new IngredientSpawner(time);
                new Timer().schedule(ingredientSpawner, 0, time);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (!serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                    if (ingredientSpawner != null) {
                        ingredientSpawner.cancel();
                    }
                    System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.setSize(300, 400);
        this.setVisible(true);
    }

    /**
     * basically an endless queue that handles incoming connections
     */
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                if (socket.isConnected()) {
                    clientConnections.add(new ConnectionHandler(socket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * writes a message to the log
     *
     * @param message a message
     */
    public void log(String message) {
        log.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + message + "\n");
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + message);
    }

    /**
     * returns the size of the kitchen
     *
     * @return the size of the playground that players play on
     */
    public Dimension getFieldDimension() {
        return fieldDimension;
    }

    /**
     * returns the player list
     *
     * @return the list of players that are currently logged in
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * returns the player with the given id
     *
     * @param id the id
     * @return the player
     */
    public Player getPlayer(UUID id) {
        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }
        return null;
    }

    /**
     * removes a player from the player list
     *
     * @param id the player id
     */
    public void removePlayer(UUID id) {
        for (Iterator<Player> plIterator = players.iterator(); plIterator.hasNext(); ) {
            if (plIterator.next().getId().equals(id)) {
                plIterator.remove();
            }
        }
    }

    /**
     * returns a list of client connections
     *
     * @return a list of connection handlers
     */
    public List<ConnectionHandler> getClientConnections() {
        return clientConnections;
    }

    /**
     * returns the list of orders that are currently active
     *
     * @return the list of orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * returns the server singleton instance
     *
     * @return the server singleton instance
     */
    public static KitchenServer getInstance() {
        if (instance == null) {
            instance = new KitchenServer();
        }
        return instance;
    }

    public static void main(String[] args) {
        KitchenServer.getInstance();
    }

}
