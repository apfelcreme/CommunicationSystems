package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
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
public class KitchenServer implements Runnable {

    private static KitchenServer instance = null;

    private ServerSocket serverSocket = null;
    private Dimension fieldDimension = new Dimension(600, 400);

    private List<ConnectionHandler> clientConnections = new ArrayList<ConnectionHandler>();

    private List<Player> players = new ArrayList<Player>();

    private List<Order> orders = new ArrayList<Order>();
    
    private List<Ingredient> ingredients = new ArrayList<Ingredient>();
    
    private Pot pot;

    private KitchenServer() {
        try {
            this.serverSocket = new ServerSocket(1337);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Start handling connections...");
        new Thread(this).start();
    }

    /**
     * basically an endless queue that handles incoming connections
     */
    public void run() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Ingredient ingredient1 = new Ingredient(
                        UUID.randomUUID(),
                        Ingredient.Type.random(),
                        new Random().nextInt(getFieldDimension().width),
                        new Random().nextInt(getFieldDimension().height)
                );
                Ingredient ingredient2 = new Ingredient(
                        UUID.randomUUID(),
                        Ingredient.Type.random(),
                        new Random().nextInt(getFieldDimension().width),
                        new Random().nextInt(getFieldDimension().height)
                );
                Ingredient ingredient3 = new Ingredient(
                        UUID.randomUUID(),
                        Ingredient.Type.random(),
                        new Random().nextInt(getFieldDimension().width),
                        new Random().nextInt(getFieldDimension().height)
                );
                Pot pot = new Pot(
            			UUID.randomUUID(),
            			new Random().nextInt(getFieldDimension().width),
                        new Random().nextInt(getFieldDimension().height)
        		);
                KitchenServer.getInstance().pot = pot;
                ingredients.add(ingredient1);
                ingredients.add(ingredient2);
                ingredients.add(ingredient3);
                orders.add(new Order(ingredient1, ingredient2, ingredient3));
                ConnectionHandler.broadcastIngredientSpawn(ingredient1);
                ConnectionHandler.broadcastIngredientSpawn(ingredient2);
                ConnectionHandler.broadcastIngredientSpawn(ingredient3);
                ConnectionHandler.broadcastPotSpawn(pot);
            }
        }, 5000, 15000);
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
     * returns the size of the kitchen
     *
     * @return the size of the playground that players play on
     */
    public Dimension getFieldDimension() {
        return fieldDimension;
    }

    /**
	 * @return the pot
	 */
	public Pot getPot() {
		return pot;
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
            //TODO: macht noch nich wirklich was..
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
     * returns the list of ingredients that are currently active
     *
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * returns the server singleton instance
     *
     * @return
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
