package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.Order;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.SequenceOrder;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.SyncOrder;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

import java.awt.*;
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
            outputStream.flush();
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
                    KitchenServer.getInstance().log("Login request by [" + socket.getInetAddress().getHostName() + "]");

                    // send ok to the connecting player
                    UUID newId = UUID.randomUUID();
                    String name = inputStream.readUTF();
                    Color color = new Color(inputStream.readInt());
                    sendLoginConfirmation(
                            newId, // player id
                            name,
                            color,
                            200, // new player x coordinate
                            200, // new player y coordinate
                            KitchenServer.getInstance().getFieldDimension().width, // field width
                            KitchenServer.getInstance().getFieldDimension().height, // field height
                            Util.serializePlayerList(KitchenServer.getInstance().getPlayers()),
                            KitchenServer.getInstance().getGame() != null
                                    ? KitchenServer.getInstance().getGame().getCurrentLives() : 0
                    );

                    // send info to all other players
                    broadcastNewPlayerArrival(
                            newId, // player id
                            name, // the player name
                            color, // the player color
                            200, // new player x coordinate
                            200 // new player y coordinate
                    );
                    KitchenServer.getInstance().addPlayer(new Player(newId, name, color, 200, 200, Direction.SOUTH));
                    KitchenServer.getInstance().log("Login granted");

                } else if (message.equals("MOVE")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    Direction direction = Direction.getDirection(inputStream.readUTF());
                    Player player = KitchenServer.getInstance().getPlayer(id);
                    if (player != null) {
                        player.move(direction);
                    }

                } else if (message.equals("DROP")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    Player player = KitchenServer.getInstance().getPlayer(id);
                    if (player != null) {
                        player.dropCarrying();
                    }

                } else if (message.equals("LOGOUT")) {
                    KitchenServer.getInstance().log("Logout announced by [" + socket.getInetAddress().getHostName() + "]");
                    UUID id = UUID.fromString(inputStream.readUTF());
                    KitchenServer.getInstance().removePlayer(id);
                    KitchenServer.getInstance().getClientConnections().remove(this);
                    ConnectionHandler.broadcastLogout(id);
                    socket.close();

                } else if (message.equals("READY")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    Boolean ready = inputStream.readBoolean();
                    Player player = KitchenServer.getInstance().getPlayer(id);
                    if (player != null) {
                        player.setReady(ready);
                        KitchenServer.getInstance().log("Player " + id + " is " + (ready ? "" : "not ") + "ready");
                    }
                    KitchenServer.getInstance().getPlayerListGui().repaint();

                } else if (message.equals("PAUSE")) {
                	// TODO: Implement game pausing
                    UUID id = UUID.fromString(inputStream.readUTF());                    
                    Player player = KitchenServer.getInstance().getPlayer(id);
                    if (player != null) {
                        player.setReady(false);
                        KitchenServer.getInstance().log("Player " + id + " paused the game");
                    }
                    KitchenServer.getInstance().getPlayerListGui().repaint();

                } else if (message.equals("CHAT")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    String chat = inputStream.readUTF();
                    KitchenServer.getInstance().log("Chat-Message from [" + id + "]: " + chat);
                    broadcastChatMessage(id, chat);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * sends a message to a player that a player has arrived
     *
     * @param id    the player id
     * @param name  the players name
     * @param color the player color
     * @param x     his x coordinate
     * @param y     his y coordinate
     */
    public static void broadcastNewPlayerArrival(UUID id, String name, Color color, int x, int y) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("NEWPLAYER");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().writeUTF(name);
                    connectionHandler.getOutputStream().writeInt(color.getRGB());
                    connectionHandler.getOutputStream().writeInt(x);
                    connectionHandler.getOutputStream().writeInt(y);
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts that a player has quit
     *
     * @param id the players id
     */
    private static void broadcastLogout(UUID id) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("LOGOUT");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to all clients to add a drawable at the given position
     *
     * @param id            the id
     * @param queuePosition for ingredients: the order position   | -1 = pot, -2 = clock symbol
     * @param type          the drawable type
     * @param x             the x coordinate
     * @param y             the y coordinate
     */
    public static void broadcastAddDrawable(UUID id, Integer queuePosition, DrawableType type, int x, int y) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("ADDDRAWABLE");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().writeInt(queuePosition);
                    connectionHandler.getOutputStream().writeUTF(type.name());
                    connectionHandler.getOutputStream().writeInt(x);
                    connectionHandler.getOutputStream().writeInt(y);
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to all clients to remove a drawable at the given position
     *
     * @param id the id
     */
    public static void broadcastRemoveDrawable(UUID id) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("REMOVEDRAWABLE");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts to all clients that a player has picked up an ingredient
     *
     * @param id           the player
     * @param drawableType the ingredient
     */
    public static void broadcastAdditionToHand(UUID id, DrawableType drawableType) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("ADDTOHAND");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().writeUTF(drawableType.name());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts a message that an ingredient was removed from a players hand
     *
     * @param id the player
     */
    public static void broadcastRemovalFromHand(UUID id) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("REMOVEFROMHAND");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts a message that a new order was created
     *
     * @param order the order
     */
    public static void broadcastNewOrder(Order order) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("ADDORDER");
                    connectionHandler.getOutputStream().writeUTF(order.getId().toString());
                    connectionHandler.getOutputStream().writeUTF(order instanceof SequenceOrder ? "SEQUENCEORDER" : "SYNCORDER");
                    connectionHandler.getOutputStream().writeLong(order.getTime());
                    String ingredients = "";
                    for (Ingredient ingredient : order.getIngredients(Ingredient.Status.MISSING)) {
                        ingredients += ingredient.getId() + ",";
                    }
                    connectionHandler.getOutputStream().writeUTF(ingredients);
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts a message that a new order was created
     *
     * @param order the order
     */
    public static void broadcastRemoveOrder(Order order) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("REMOVEORDER");
                    connectionHandler.getOutputStream().writeUTF(order.getId().toString());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * broadcasts a message that a countdown for a time order has begun
     *
     * @param syncOrder the order
     */
    public static void broadcastTimerStart(SyncOrder syncOrder) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("TIMEORDERSTART");
                    connectionHandler.getOutputStream().writeUTF(syncOrder.getId().toString());
                    connectionHandler.getOutputStream().writeLong(syncOrder.getTimeFrame());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * broadcasts a players new position to all clients after he moved
     *
     * @param player the player
     */
    public static void broadcastPlayerMove(Player player) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("MOVE");
                    connectionHandler.getOutputStream().writeUTF(player.getId().toString());
                    connectionHandler.getOutputStream().writeInt(player.getX());
                    connectionHandler.getOutputStream().writeInt(player.getY());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts a message to all players
     *
     * @param id   the sending player id
     * @param chat the chat message
     */
    public static void broadcastChatMessage(UUID id, String chat) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("CHAT");
                    connectionHandler.getOutputStream().writeUTF(id.toString());
                    connectionHandler.getOutputStream().writeUTF(chat);
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * forces all clients to draw a damage image
     */
    public static void broadcastDamage() {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("DRAWDAMAGE");
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * forces all clients to draw a success image
     */
    public static void broadcastOrderSuccess() {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("DRAWSUCCESS");
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * forces all clients to show a message in the hint box
     */
    public static void broadcastMessage(Game.Message message) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("MESSAGE");
                    connectionHandler.getOutputStream().writeUTF(message.getMessage());
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * broadcasts the current amount of hearts to all players
     *
     * @param lives the amount of lives the players have left
     */
    public static void broadcastLives(int lives) {
        try {
            for (ConnectionHandler connectionHandler : KitchenServer.getInstance().getClientConnections()) {
                synchronized (connectionHandler.getOutputStream()) {
                    connectionHandler.getOutputStream().writeUTF("SETHEARTS");
                    connectionHandler.getOutputStream().writeInt(lives);
                    connectionHandler.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a login confirmation
     *
     * @param id                   the id the new player is given
     * @param name                 the player name
     * @param color                the player color
     * @param x                    the x coordinate he spawns on
     * @param y                    the y coordinate he spawns on
     * @param width                the width of the playing field
     * @param height               the height of the playing field
     * @param serializedPlayerList a serialized list of all players that are currently logged in
     * @param lives                the amount of lives the players have
     */
    private void sendLoginConfirmation(UUID id, String name, Color color, int x, int y, int width, int height,
                                       String serializedPlayerList, int lives) {
        try {
            synchronized (outputStream) {
                outputStream.writeUTF("LOGINOK");
                outputStream.writeUTF(id.toString());
                outputStream.writeUTF(name);
                outputStream.writeInt(color.getRGB());
                outputStream.writeInt(x);
                outputStream.writeInt(y);
                outputStream.writeInt(width);
                outputStream.writeInt(height);
                outputStream.writeUTF(serializedPlayerList);
                outputStream.writeInt(lives);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the output stream
     *
     * @return the output stream
     * -    private DrawablePot drawablePot = null;
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
