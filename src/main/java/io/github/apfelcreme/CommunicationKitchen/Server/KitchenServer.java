package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.Order;

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
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;

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

    private Dimension fieldDimension = new Dimension(460, 380);

    /**
     * a list of all players
     */
    private List<Player> players = new ArrayList<Player>();

    /**
     * a list of all client connections
     */
    private List<ConnectionHandler> clientConnections = new ArrayList<ConnectionHandler>();

    /**
     * the log textfield
     */
    private JTextArea log;

    private IngredientSpawner ingredientSpawner = null;       
    
    private final int lives = 1;
    
    private int currentLives = lives;
    
    private final int rounds = 1;
    
    private int currentRound = 0;
    private JList<UUID> playerListGui = new JList<UUID>();

    /**
     * the game instance
     */
    private Game game;

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

        DefaultListModel<UUID> playerList = new DefaultListModel<UUID>();
        playerListGui.setModel(playerList);

        this.setLayout(new GridBagLayout());
        this.getContentPane().add(bnStart,
                new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(new JScrollPane(log),
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(playerListGui,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
        bnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (allPlayersReady()) {
                    game = new Game(10000, 10000, 10);
                } else {
                    JOptionPane.showMessageDialog(KitchenServer.getInstance(), "Es sind noch nicht alle Spieler bereit!",
                            "Fehler", JOptionPane.OK_OPTION);
                }
//                long time = 15000;
//                if (ingredientSpawner != null) {
//                    ingredientSpawner.cancel();
//                }
//                ingredientSpawner = new IngredientSpawner(time);
//                new Timer().schedule(ingredientSpawner, 0, time);
            	startGame();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (!serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                    System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.setSize(600, 400);
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
            } catch (SocketException e) {
                System.out.println("Socket ist nicht verfügbar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Starts a game if all players are ready
     * 
     * @param time
     * @return true if game is started, otherwise false
     */
    public boolean startGame(long time) {
    	
    	currentLives = lives;
    	currentRound = 0;
    	
    	for (Player player : players) {
    		if (!player.isReady()) {
    			return false;
    		}
    	}
    	if (ingredientSpawner != null) {
            ingredientSpawner.cancel();
        }
        ingredientSpawner = new IngredientSpawner(time);             
        new Timer().schedule(ingredientSpawner, 0, time);      
        
        return true;
    }
    
    public boolean startGame() {
    	return startGame(15000);
    }
    
    /**
     * Stops the game and broadcasts either a success or a failure message.
     * 
     * @param failure - true if the game stops due to failure, otherwise false
     * @param reason - the reason for failing or the learned skill in case of success
     */
    public void stopGame(boolean failure, String reason) {
    	
    	if (failure) {
    		ConnectionHandler.broadcastGameOver(reason);
    		
    	} else {
    		ConnectionHandler.broadcastSuccess(reason);
    	}
    	
    	if (ingredientSpawner != null) {
            ingredientSpawner.cancel();
        }

    	this.log("Stop game due to " + (failure ? "failure. " : "success. ") + "Reason: " + reason);
//    	orders.clear();    	
    }
    
    /**
     * Handles a failure. Stops the game if necessary (no more lives)
     * 
     * @param reason - the reason for failing
     */
    public void handleFailure(String reason) {
    	
    	currentLives--;
    	
    	if (currentLives <= 0) {
    		stopGame(true, reason);
    		
    	} else {
    		ConnectionHandler.broadcastDamage();
    	}
    }

    /**
     * Handles a success. Stops the game if necessary (all rounds at the current level completed)
     * 
     * @param learnedSkill - the learned skill
     */
    public void handleSuccess(String learnedSkill) {
    	
    	currentRound++;
    	
    	if (currentRound >= rounds) {
    		stopGame(false, learnedSkill);    			
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
                ((DefaultListModel) playerListGui.getModel()).removeElement(id);
            }
        }
    }

    /**
     * adds a player to the player list
     *
     * @param player the player
     */
    public void addPlayer(Player player) {
        players.add(player);
        ((DefaultListModel<UUID>) playerListGui.getModel()).addElement(player.getId());
    }

    /**
     * checks if all players are ready
     *
     * @return true or false
     */
    public boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
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
     * returns the game instance
     *
     * @return the game instance
     */
    public Game getGame() {
        return game;
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
