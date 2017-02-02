package io.github.apfelcreme.CommunicationKitchen.Client.UI;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawableOrder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

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
public class HeartBoard extends JLabel {

    private static HeartBoard instance = null;

    private BufferedImage heartImage = null;

    private HeartBoard() {
        try {
            this.setBackground(new Color(47, 47, 47));
            heartImage = (BufferedImage) ImageIO.read(OrderBoard.class.getResourceAsStream("/heart.png"))
                    .getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 10; x < CommunicationKitchen.getInstance().getHearts() * heartImage.getWidth(); x+= heartImage.getWidth()) {
            g.drawImage(heartImage, x, 5, null);
        }
    }

    /**
     * returns the heart board instance
     *
     * @return the hear board instance
     */
    public static HeartBoard getInstance() {
        if (instance == null) {
            instance = new HeartBoard();
        }
        return instance;
    }
}
