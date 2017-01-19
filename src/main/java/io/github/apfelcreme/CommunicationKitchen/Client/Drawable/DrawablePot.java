package io.github.apfelcreme.CommunicationKitchen.Client.Drawable;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;

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
public class DrawablePot implements Drawable {

    private UUID id;
    private int x;
    private int y;
    private BufferedImage image;

    public DrawablePot(UUID id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        try {
            image = ImageIO.read(CommunicationKitchen.class.getResourceAsStream("/pot.png"));
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
    }

    /**
     * returns the id
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * returns x
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * returns y
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }
}
