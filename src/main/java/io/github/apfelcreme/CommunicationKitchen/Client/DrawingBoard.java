package io.github.apfelcreme.CommunicationKitchen.Client;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableIngredient;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
public class DrawingBoard extends JPanel {

    private static DrawingBoard instance = null;

    public static DrawingBoard getInstance() {
        if (instance == null) {
            instance = new DrawingBoard();
        }
        return instance;
    }

    private DrawingBoard() {
        this.setSize(CommunicationKitchen.WIDTH, CommunicationKitchen.HEIGHT);
    }

    /**
     * draws the drawing board
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);

            // floor
            BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/floortile_small.png"));
            for (int x = 0; x < getSize().width; x += image.getWidth()) {
                for (int y = 0; y < getSize().height; y += image.getHeight()) {
                    g.drawImage(image, x, y, this);
                }
            }
            // end floor

            // players
            for (DrawablePlayer drawablePlayer : CommunicationKitchen.getInstance().getDrawablePlayers()) {
                drawablePlayer.draw(g);
            }
            // end players

            // ingredients
            for (DrawableIngredient drawableIngredient : CommunicationKitchen.getInstance().getDrawableIngredients()) {
                drawableIngredient.draw(g);
            }
            // end ingredients
            
            // pot
            CommunicationKitchen.getInstance().getDrawablePot().draw(g);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
