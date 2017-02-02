package io.github.apfelcreme.CommunicationKitchen.Client;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableOrder;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.DrawingBoard;
import io.github.apfelcreme.CommunicationKitchen.Client.UI.HeartBoard;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

    public void connect(String ip, int port, String name) throws IOException {
        try {
            socket = null;
            this.socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port));
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Login läuft...");
            sendLogin(name);
            new Thread(this).start();
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(CommunicationKitchen.getInstance(),
                    "WARNING.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            sendLogout(CommunicationKitchen.getInstance().getMe());
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
     * sends a command to let the player drop is item
     *
     * @param id me (the player who did the key stroke)
     */
    public void sendItemDrop(UUID id) {
        try {
            outputStream.writeUTF("DROP");
            outputStream.writeUTF(id.toString());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a move request to the server
     *
     * @param id      me (the player who did the key stroke)
     * @param message the message
     */
    public void sendChatMessage(UUID id, String message) {
        try {
            outputStream.writeUTF("CHAT");
            outputStream.writeUTF(id.toString());
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message that you are ready
     *
     * @param id    me (the player who pressed the button)
     * @param ready ready or not ready
     */
    public void sendReady(UUID id, boolean ready) {
        try {
            outputStream.writeUTF("READY");
            outputStream.writeUTF(id.toString());
            outputStream.writeBoolean(ready);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a login message to the server
     *
     * @param name the player name
     */
    public void sendLogin(String name) {
        try {
            outputStream.writeUTF("LOGIN");
            outputStream.writeUTF(name);
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
                    DrawablePlayer player = new DrawablePlayer(UUID.fromString(inputStream.readUTF()),
                            inputStream.readUTF(), inputStream.readInt(), inputStream.readInt());
                    CommunicationKitchen.getInstance().setMe(player.getId());
                    CommunicationKitchen.getInstance().getDrawables().add(player);
                    int w = inputStream.readInt();
                    int h = inputStream.readInt();
                    CommunicationKitchen.getInstance().getDrawables()
                            .addAll(Util.deserializePlayerList(inputStream.readUTF()));
                    CommunicationKitchen.getInstance().setHearts(inputStream.readInt());
                    CommunicationKitchen.getInstance().setSize(w + 40, h + 250);
                    DrawingBoard.getInstance().setSize(w, h);
                    CommunicationKitchen.getInstance().setVisible(true);
                    DrawingBoard.getInstance().requestFocus();
                    System.out.println("Login erfolgreich!");

                } else if (message.equals("NEWPLAYER")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    String name = inputStream.readUTF();
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    if (!CommunicationKitchen.getInstance().getMe().equals(id)) {
                        CommunicationKitchen.getInstance().getDrawables().add(
                                new DrawablePlayer(id, name, x, y));
                        DrawingBoard.getInstance().repaint();
                    }

                } else if (message.equals("LOGOUT")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    DrawablePlayer drawablePlayer = CommunicationKitchen.getInstance().getDrawablePlayer(id);
                    if (drawablePlayer != null) {
                        CommunicationKitchen.getInstance().removeDrawablePlayer(id);
                    }

                } else if (message.equals("MOVE")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    DrawablePlayer drawablePlayer = CommunicationKitchen.getInstance().getDrawablePlayer(id);
                    if (drawablePlayer != null) {
                        drawablePlayer.setX(x);
                        drawablePlayer.setY(y);
                        DrawingBoard.getInstance().repaint();
                    }

                } else if (message.equals("CHAT")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    String chat = inputStream.readUTF();
                    final DrawablePlayer drawablePlayer = CommunicationKitchen.getInstance().getDrawablePlayer(id);
                    if (drawablePlayer != null) {
                        drawablePlayer.setChat(chat);
                        DrawingBoard.getInstance().repaint();
                    }

                } else if (message.equals("ADDDRAWABLE")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    int queuePos = inputStream.readInt();
                    DrawableType drawableType = DrawableType.valueOf(inputStream.readUTF());
                    int x = inputStream.readInt();
                    int y = inputStream.readInt();
                    CommunicationKitchen.getInstance().getDrawables().add(
                            new Drawable(id, queuePos, drawableType, x, y));

                } else if (message.equals("REMOVEDRAWABLE")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    CommunicationKitchen.getInstance().removeDrawable(id);

                } else if (message.equals("ADDTOHAND")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    DrawableType carrying = DrawableType.getType(inputStream.readUTF());
                    DrawablePlayer drawablePlayer = CommunicationKitchen.getInstance().getDrawablePlayer(id);
                    if (drawablePlayer != null) {
                        drawablePlayer.setCarrying(carrying);
                    }

                } else if (message.equals("REMOVEFROMHAND")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    DrawablePlayer drawablePlayer = CommunicationKitchen.getInstance().getDrawablePlayer(id);
                    if (drawablePlayer != null) {
                        drawablePlayer.setCarrying(DrawableType.NOTHING);
                    }

                } else if (message.equals("ADDORDER")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    String type = inputStream.readUTF();
                    long time = inputStream.readLong();
                    List<UUID> ingredientIds = new ArrayList<UUID>();
                    String s = inputStream.readUTF();
                    for (String ingredientId : s.split(Pattern.quote(","))) {
                        ingredientIds.add(UUID.fromString(ingredientId));
                    }
                    CommunicationKitchen.getInstance().getOrders().add(
                            new DrawableOrder(id, type, 0, 0, time, ingredientIds));
                    CommunicationKitchen.getInstance().setRound(CommunicationKitchen.getInstance().getRound() + 1);

                } else if (message.equals("REMOVEORDER")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    CommunicationKitchen.getInstance().removeOrder(id);

                } else if (message.equals("TIMEORDERSTART")) {
                    UUID id = UUID.fromString(inputStream.readUTF());
                    DrawableOrder drawableOrder = CommunicationKitchen.getInstance().getDrawableOrder(id);
                    if (drawableOrder != null) {
                        drawableOrder.setTimeNewLimit(inputStream.readLong());
                    }
                } else if (message.equals("DRAWDAMAGE")) {
                    DrawingBoard.getInstance().setDrawDamageOnNextTick(true);

                } else if (message.equals("DRAWSUCCESS")) {
                    DrawingBoard.getInstance().setDrawSuccessOnNextTick(true);

                } else if (message.equals("SETHEARTS")) {
                    CommunicationKitchen.getInstance().setHearts(inputStream.readInt());
                    HeartBoard.getInstance().repaint();

                } else if (message.equals("MESSAGE")) {
                    CommunicationKitchen.getInstance().setMessage(inputStream.readUTF());
                }

            }
        } catch (UTFDataFormatException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Socket wurde unerwartet geschlossen!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}