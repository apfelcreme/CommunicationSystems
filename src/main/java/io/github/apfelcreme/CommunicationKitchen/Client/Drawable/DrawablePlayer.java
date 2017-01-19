package io.github.apfelcreme.CommunicationKitchen.Client.Drawable;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
public class DrawablePlayer implements Drawable {

    private UUID id = null;
    private int x = 0;
    private int y = 0;
    private BufferedImage image = null;
    private Direction direction = Direction.DOWN;
    private String chat = "";

    public DrawablePlayer(UUID id, int x, int y, Direction direction) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
        calcImage();
    }

    /**
     * creates a subimage from the sprite image depending
     * on the direction the player is direction in
     */
    private void calcImage() {
        try {
            BufferedImage baseImage = ImageIO.read(CommunicationKitchen.class.getResourceAsStream("/Chef1.png"));
            switch (direction) {
                case LEFT:
                    image = baseImage.getSubimage(0, 40, 40, 40);
                    break;
                case RIGHT:
                    image = baseImage.getSubimage(0, 80, 40, 40);
                    break;
                case UP:
                    image = baseImage.getSubimage(0, 120, 40, 40);
                    break;
                case DOWN:
                    image = baseImage.getSubimage(0, 0, 40, 40);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * draws the object onto the drawing board
     *
     * @param g the graphics object
     */
    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
        g.drawString(chat, x + 40, y + 20);
    }

    /**
     * returns the id of the player that is represented by this drawing
     *
     * @return a uuid
     */
    public UUID getId() {
        return id;
    }

    /**
     * sets x
     *
     * @param x the x coordinate
     */
    public void setX(int x) {
        this.x = x;
        calcImage();
    }

    /**
     * sets y
     *
     * @param y the y coordinate
     */
    public void setY(int y) {
        this.y = y;
        calcImage();
    }

    /**
     * returns the currently displayed image
     *
     * @return the currently displayed image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * returns the direction the player is looking in
     *
     * @return the direction the player is looking in
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * sets the direction the player is looking in
     * @param direction the direction the player is looking in
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
        calcImage();
    }

    /**
     * sets the chat message that is displayed next to the player
     * @param chat the chat message
     */
    public void setChat(String chat) {
        this.chat = chat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawablePlayer that = (DrawablePlayer) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DrawablePlayer{" +
                "id=" + id +
                ", x=" + x +
                ", direction=" + direction +
                ", y=" + y +
                '}';
    }
}
