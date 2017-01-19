package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

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
public class ConnectionHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ConnectionHandler(Socket socket) {
        try {
            this.socket = socket;
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                String message = inputStream.readUTF();
                if (message.equals("LOGIN")) {
                    System.out.println("Login request by [" + socket.getInetAddress().getHostName() + "]");

                    // send ok to the connecting player
                    UUID newId = UUID.randomUUID();
                    sendLoginConfirmation(
                            newId, // player id
                            200, // new player x coordinate
                            200, // new player y coordinate
                            Direction.DOWN, // the direction the player is facing
                            KitchenServer.getInstance().getFieldDimension().width, // field width
                            KitchenServer.getInstance().getFieldDimension().height, // field height
                            Util.serializePlayerList(KitchenServer.getInstance().getPlayers())
                    );

                    // send info to all other players
                    broadcastNewPlayerArrival(
                            newId, // player id
                            200, // new player x coordinate
                            200, // new player y coordinate
                            Direction.DOWN // the direction the player is facing
                    );
                    KitchenServer.getInstance().getPlayers().add(new Player(newId, 200, 200, Direction.DOWN));
                    System.out.println("Login granted");
                } else if (message.equals("MOVE")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    Direction direction = Direction.getDirection(inputStream.readUTF());
                    Player player = KitchenServer.getInstance().getPlayer(id);
                    System.out.println("Player Move: [" + id.toString() + "] -> " + direction.name());
                    if (player != null) {
                        player.move(direction);
                    }
                } else if (message.equals("LOGOUT")) {
                    System.out.println("Logout announced by [" + socket.getInetAddress().getHostName() + "]");
                    UUID id = UUID.fromString(inputStream.readUTF());
                    KitchenServer.getInstance().removePlayer(id);
                    KitchenServer.getInstance().getClientConnections().remove(this);
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to a player that a player has arrived
     *
     * @param id        the player id
     * @param x         his x coordinate
     * @param y         his y coordinate
     * @param direction the direction the player is facing
     */
    public static void broadcastNewPlayerArrival(UUID id, int x, int y, Direction direction) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                connectionHandler.getOutputStream().writeUTF("NEWPLAYER");
                connectionHandler.getOutputStream().writeUTF(id.toString());
                connectionHandler.getOutputStream().writeInt(x);
                connectionHandler.getOutputStream().writeInt(y);
                connectionHandler.getOutputStream().writeUTF(direction.name());
                connectionHandler.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to a player that a new ingredient has spawned
     *
     * @param ingredient the ingredient
     */
    public static void broadcastIngredientSpawn(Ingredient ingredient) {
        try {
            System.out.println("Ingredient-Spawn: " + ingredient.getType() + " (" + ingredient.getX() + "," + ingredient.getY() + ")");
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                connectionHandler.getOutputStream().writeUTF("INGREDIENTSPAWN");
                connectionHandler.getOutputStream().writeUTF(ingredient.getId().toString());
                connectionHandler.getOutputStream().writeUTF(ingredient.getType().name());
                connectionHandler.getOutputStream().writeInt(ingredient.getX());
                connectionHandler.getOutputStream().writeInt(ingredient.getY());
                connectionHandler.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to a player that an ingredient has despawned
     *
     * @param ingredient the ingredient
     */
    public static void broadcastIngredientDespawn(Ingredient ingredient) {
        try {
            System.out.println("Ingredient-Despawn: " + ingredient.getType() + " (" + ingredient.getX() + "," + ingredient.getY() + ")");
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                connectionHandler.getOutputStream().writeUTF("INGREDIENTDESPAWN");
                connectionHandler.getOutputStream().writeUTF(ingredient.getId().toString());
                connectionHandler.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * sends a message to a player that a pot has spawned
     *
     * @param pot  - the pot
     */
    public static void broadcastPotSpawn(Pot pot) {
        try {
            System.out.println("Pot-Spawn (" + pot.getX() + "," + pot.getY() + ")");
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                connectionHandler.getOutputStream().writeUTF("POTSPAWN");
                connectionHandler.getOutputStream().writeUTF(pot.getId().toString());                
                connectionHandler.getOutputStream().writeInt(pot.getX());
                connectionHandler.getOutputStream().writeInt(pot.getY());
                connectionHandler.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts a players new position to all clients after he moved
     *
     * @param id        the players id
     * @param x         the new x coordinate
     * @param y         the new y coordinate
     * @param direction the direction the player is facing
     */
    public static void broadcastPlayerMove(UUID id, int x, int y, Direction direction) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                connectionHandler.getOutputStream().writeUTF("MOVE");
                connectionHandler.getOutputStream().writeUTF(id.toString());
                connectionHandler.getOutputStream().writeInt(x);
                connectionHandler.getOutputStream().writeInt(y);
                connectionHandler.getOutputStream().writeUTF(direction.name());
                connectionHandler.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a login confirmation
     *
     * @param id                   the id the new player is given
     * @param x                    the x coordinate he spawns on
     * @param y                    the y coordinate he spawns on
     * @param direction            the direction the player is facing
     * @param width                the width of the playing field
     * @param height               the height of the playing field
     * @param serializedPlayerList a serialized list of all players that are currently logged in
     */
    private void sendLoginConfirmation(UUID id, int x, int y, Direction direction, int width, int height,
                                       String serializedPlayerList) {
        try {
            outputStream.writeUTF("LOGINOK");
            outputStream.writeUTF(id.toString());
            outputStream.writeInt(x);
            outputStream.writeInt(y);
            outputStream.writeUTF(direction.name());
            outputStream.writeInt(width);
            outputStream.writeInt(height);
            outputStream.writeUTF(serializedPlayerList);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a signle command to the client
     *
     * @param message a command
     */
    public void send(String message) {
        try {
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the output stream
     *
     * @return the output stream
     */
    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }


    /**
     * returns the input stream
     *
     * @return the input stream
     */
    public ObjectInputStream getInputStream() {
        return inputStream;
    }
}
