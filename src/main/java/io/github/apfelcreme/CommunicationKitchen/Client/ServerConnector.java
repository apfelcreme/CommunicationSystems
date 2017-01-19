package io.github.apfelcreme.CommunicationKitchen.Client;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableIngredient;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePot;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
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
public class ServerConnector implements Runnable {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private static ServerConnector instance = null;

    public static ServerConnector getInstance() {
        synchronized (ServerConnector.class) {
            if (instance == null) {
                instance = new ServerConnector();
            }
        }
        return instance;
    }

    private ServerConnector() {
    }

    public void connect(String ip, int port) throws IOException {
        try {
            socket = new Socket(ip, port);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Login läuft...");
            send("LOGIN");
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            sendLogout(CommunicationKitchen.getInstance().getMe().getId());
            System.out.println("Verbindung geschlossen!");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a logout message to the server
     *
     * @param id the id of the player who is logging out (me)
     */
    private void sendLogout(UUID id) {
        try {
            outputStream.writeUTF("LOGOUT");
            outputStream.writeUTF(id.toString());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a move request to the server
     *
     * @param id        me (the player who did the key stroke)
     * @param direction the direction the player is moving in
     */
    public void sendPlayerMove(UUID id, Direction direction) {
        try {
            outputStream.writeUTF("MOVE");
            outputStream.writeUTF(id.toString());
            outputStream.writeUTF(direction.name());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to the server
     *
     * @param message the message
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
     * handles all incoming messages from the server
     */
    public void run() {
        try {
            while (!socket.isClosed() && inputStream != null) {
                String message = inputStream.readUTF();

                if (message.equals("LOGINOK")) {
                    System.out.println("Login erfolgreich!");
                    DrawablePlayer me = new DrawablePlayer(UUID.fromString(inputStream.readUTF()),
                            inputStream.readInt(), inputStream.readInt(),
                            Direction.getDirection(inputStream.readUTF()));
                    CommunicationKitchen.getInstance().setMe(me);
                    CommunicationKitchen.getInstance().getDrawablePlayers().add(me);
                    int w = inputStream.readInt();
                    int h = inputStream.readInt();
                    CommunicationKitchen.getInstance().getDrawablePlayers()
                            .addAll(Util.deserializePlayerList(inputStream.readUTF()));

                    CommunicationKitchen.getInstance().setSize(w + 10, h + 50);
                    DrawingBoard.getInstance().setSize(w, h);

                } else if (message.equals("NEWPLAYER")) {
                    System.out.println("Neuer Login erkannt");
                    UUID id = UUID.fromString(inputStream.readUTF());
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    Direction direction = Direction.getDirection(inputStream.readUTF());
                    if (!CommunicationKitchen.getInstance().getMe().getId().equals(id)) {
                        CommunicationKitchen.getInstance().getDrawablePlayers().add(
                                new DrawablePlayer(id, x, y, direction));
                        DrawingBoard.getInstance().repaint();
                    }

                } else if (message.equals("INGREDIENTSPAWN")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    String type = inputStream.readUTF();
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    CommunicationKitchen.getInstance().getDrawableIngredients().add(new DrawableIngredient(id, type, x, y));
                    DrawingBoard.getInstance().repaint();

                } else if (message.equals("INGREDIENTDESPAWN")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    DrawableIngredient drawableIngredient = CommunicationKitchen.getInstance().getDrawableIngredient(id);
                    if (drawableIngredient != null) {
                        CommunicationKitchen.getInstance().getDrawableIngredients().remove(drawableIngredient);
                        DrawingBoard.getInstance().repaint();
                    }

                } else if (message.equals("POTSPAWN")) {
                    UUID id = UUID.fromString(inputStream.readUTF());                    
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    CommunicationKitchen.getInstance().setDrawablePot(new DrawablePot(id, x, y));
                    DrawingBoard.getInstance().repaint(); 
                    
                } else if (message.equals("MOVE")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    Direction direction = Direction.getDirection(inputStream.readUTF());
                    DrawablePlayer drawablePlayer = CommunicationKitchen.getInstance().getDrawablePlayer(id);
                    if (drawablePlayer != null) {
                        drawablePlayer.setX(x);
                        drawablePlayer.setY(y);
                        drawablePlayer.setDirection(direction);
                        DrawingBoard.getInstance().repaint();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket wurde unerwartet geschlossen!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}