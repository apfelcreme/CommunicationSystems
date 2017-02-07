package io.github.apfelcreme.CommunicationKitchen.Client.Drawable;

import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
public class DrawablePlayer extends Drawable {

    private Direction direction;
    private String name;
    private Color color;
    private String chat;
    private DrawableType carrying;

    public DrawablePlayer(UUID id, String name, Color color, int x, int y) {
        super(id, 0, DrawableType.PLAYER, x, y);
        this.name = name;
        this.color = color;
        this.chat = "";
        this.direction = Direction.SOUTH;
        carrying = DrawableType.NOTHING;
        calcImage();
    }

    /**
     * dg
     * calculates the displayed image depending on the direction the player is facing
     */
    private void calcImage() {
        try {
            switch (direction) {
                case SOUTH:
                case SOUTH_EAST:
                case SOUTH_WEST:
                    image = ImageIO.read(DrawablePlayer.class.getResourceAsStream("/Drawables/PLAYER.png"))
                            .getSubimage(0, 0, 40, 40);
                    break;
                case WEST:
                    image = ImageIO.read(DrawablePlayer.class.getResourceAsStream("/Drawables/PLAYER.png"))
                            .getSubimage(0, 40, 40, 40);
                    break;
                case EAST:
                    image = ImageIO.read(DrawablePlayer.class.getResourceAsStream("/Drawables/PLAYER.png"))
                            .getSubimage(0, 80, 40, 40);
                    break;
                case NORTH:
                case NORTH_EAST:
                case NORTH_WEST:
                    image = ImageIO.read(DrawablePlayer.class.getResourceAsStream("/Drawables/PLAYER.png"))
                            .getSubimage(0, 120, 40, 40);
                    break;
            }
            image = Util.colorize(image, color);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * draws the object onto the drawing board
     *
     * @param g the graphics object
     */
    @Override
    public void draw(Graphics g) {
        calcImage();
        super.draw(g);

        if (!chat.isEmpty()) {
        	g.setFont(new Font("chat", Font.BOLD, 12));
        	Color bgColor = new Color(224, 228, 231);

            FontMetrics fm = g.getFontMetrics();
            Rectangle2D rect = fm.getStringBounds(chat, g);

            g.setColor(bgColor);
            g.fillRect(getX() + 20,
            		   getY() + 20 - fm.getAscent(),
                       (int) rect.getWidth(),
                       (int) rect.getHeight());
            g.setColor(Color.BLACK);
            g.drawString(chat, getX() + 20, getY() + 20);
        }
        g.drawString(name, getX() + 20, getY());

        try {
            if (carrying != DrawableType.NOTHING) {
                g.setColor(Color.WHITE);
                g.fillOval(getX() - 10, getY() - 45, 25, 25);
                g.drawImage(ImageIO.read(DrawablePlayer.class
                        .getResourceAsStream("/Drawables/" + carrying.name() + ".png"))
                        .getScaledInstance(25, 25, Image.SCALE_SMOOTH), getX() - 10, getY() - 45, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the x coordinate
     *
     * @param x the x coordinate
     */
    @Override
    public void setX(int x) {
        if (x > this.getX()) {
            direction = Direction.EAST;
        } else if (x < this.getX()) {
            direction = Direction.WEST;
        }
        super.setX(x);
    }

    /**
     * sets the y coordinate
     *
     * @param y the y coordinate
     */
    @Override
    public void setY(int y) {
        if (y > this.getY()) {
            direction = Direction.SOUTH;
        } else if (y < this.getY()) {
            direction = Direction.NORTH;
        }
        super.setY(y);
    }

    /**
     * sets the chat string
     *
     * @param c the chat string
     */
    public void setChat(String c) {
        chat = c;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                chat = "";
            }
        }, 5000);
    }

    /**
     * returns the item the player is carrying
     *
     * @return the item the player is carrying
     */
    public DrawableType getCarrying() {
        return carrying;
    }

    /**
     * sets the item the player is carrying
     *
     * @param carrying item the player is carrying
     */
    public void setCarrying(DrawableType carrying) {
        this.carrying = carrying;
    }

}
