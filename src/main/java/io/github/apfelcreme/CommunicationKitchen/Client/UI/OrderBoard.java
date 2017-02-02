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
public class OrderBoard extends JLabel {

    private static OrderBoard instance = null;

    private OrderBoard() {
        try {
            BufferedImage image = ImageIO.read(OrderBoard.class.getResourceAsStream("/orderbar2.png"));
            setSize(image.getWidth(), image.getHeight());
            this.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, 100);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int o = 0;
        try {
            synchronized (CommunicationKitchen.getInstance().getOrders()) {
                for (DrawableOrder order : CommunicationKitchen.getInstance().getOrders()) {
                    int i = 25;
                    BufferedImage typeImage = null;
                    if (order.getType().equals("SEQUENCEORDER")) {
                        typeImage = ImageIO.read(OrderBoard.class.getResourceAsStream("/checklist.png"));
                    } else if (order.getType().equals("SYNCORDER")) {
                        typeImage = ImageIO.read(OrderBoard.class.getResourceAsStream("/clock.png"));
                    }
                    if (typeImage != null) {
                        g.drawImage(typeImage, 7, o + 7, null);
                    }
                    for (Drawable drawable : order.getIngredients()) {
                        g.drawImage(drawable.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH), i, o, null);
                        if (drawable.getQueuePos() > 0) {
                            g.drawString(Integer.toString(drawable.getQueuePos()), i + 10, o + 25);
                        }
                        i += 25;
                    }
                    g.setColor(Color.BLACK);
                    g.drawString(new SimpleDateFormat("ss").format(new Date(order.getTimeCreated()
                            + order.getTimeLimit() - System.currentTimeMillis())), i + 10, o + 17);
                    o += 27;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the order board instance
     *
     * @return the order board instance
     */
    public static OrderBoard getInstance() {
        if (instance == null) {
            instance = new OrderBoard();
        }
        return instance;
    }
}
