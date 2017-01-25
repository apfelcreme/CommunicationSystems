package io.github.apfelcreme.CommunicationKitchen.Client.UI;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.TimerTask;

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
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, 100);
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

            // drawables
            synchronized (CommunicationKitchen.getInstance().getDrawables()) {
                for (Drawable drawable : CommunicationKitchen.getInstance().getDrawables()) {
                    drawable.draw(g);
                }
            }
            // end drawables

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
