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

    private boolean drawDamageOnNextTick = false;
    private boolean drawSuccessOnNextTick = false;
    private BufferedImage damageImage;
    private BufferedImage successImage;
    private BufferedImage floorImage;

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
        try {
            damageImage = ImageIO.read(this.getClass().getResourceAsStream("/damage.png"));
            successImage = ImageIO.read(this.getClass().getResourceAsStream("/success.png"));
            floorImage = ImageIO.read(this.getClass().getResourceAsStream("/floortile_small.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * draws the drawing board
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // floor
        for (int x = 0; x < getSize().width; x += floorImage.getWidth()) {
            for (int y = 0; y < getSize().height; y += floorImage.getHeight()) {
                g.drawImage(floorImage, x, y, this);
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

        if (drawDamageOnNextTick) {
            g.drawImage(damageImage, 0, 0, null);
        }

        if (drawSuccessOnNextTick) {
            g.drawImage(successImage, 0, 0, null);
        }
    }

    private void setDrawDamage(boolean drawDamage) {
        this.drawDamageOnNextTick = drawDamage;
    }

    private void setDrawSuccess(boolean drawSuccess) {
        this.drawSuccessOnNextTick = drawSuccess;
    }

    /**
     * make the drawing board draw the damage frame on the next tick
     *
     * @param drawDamageOnNextTick true or false
     */
    public void setDrawDamageOnNextTick(final boolean drawDamageOnNextTick) {
        this.drawDamageOnNextTick = drawDamageOnNextTick;
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setDrawDamage(false);
            }
        }, 200);
    }

    /**
     * make the drawing board draw the success frame on the next tick
     *
     * @param drawSuccessOnNextTick true or false
     */
    public void setDrawSuccessOnNextTick(final boolean drawSuccessOnNextTick) {
        this.drawSuccessOnNextTick = drawSuccessOnNextTick;
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setDrawSuccess(false);
            }
        }, 200);
    }
}
