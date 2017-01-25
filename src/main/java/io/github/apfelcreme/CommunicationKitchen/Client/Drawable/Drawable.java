package io.github.apfelcreme.CommunicationKitchen.Client.Drawable;

import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

/**
 * Copyright (C) 2016 Lord36 aka Apfelcreme
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
public class Drawable {

    private UUID id;
    private int queuePos;
    private DrawableType type;

    private int x;
    private int y;

    protected BufferedImage image;

    public Drawable(UUID id, int queuePos, DrawableType type, int x, int y) {
        this.id = id;
        this.queuePos = queuePos;
        this.type = type;
        this.x = x;
        this.y = y;
        try {
            this.image = ImageIO.read(Drawable.class
                    .getResourceAsStream("/Drawables/" + type.name() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the id
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * returns the number that is being drawn under the item
     * @return the number that is being drawn under the item
     */
    public int getQueuePos() {
        return queuePos;
    }

    /**
     * returns the type
     *
     * @return the type
     */
    public DrawableType getType() {
        return type;
    }

    /**
     * returns the x coordinate
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * sets the x coordinate
     *
     * @param x the x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * returns the y coordinate
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * sets the y coordinate
     *
     * @param y the y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * returns the image
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * draws the object onto the drawing board
     *
     * @param g the graphics object
     */
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x - (image.getWidth() / 2), y - (image.getHeight() / 2), null);
        }
        g.setColor(Color.BLACK);
        if (queuePos > 0) {
            g.drawString(Integer.toString(queuePos), x - (image.getWidth() / 2) - 2, y + 20);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drawable drawable = (Drawable) o;

        return !(id != null ? !id.equals(drawable.id) : drawable.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
