package io.github.apfelcreme.CommunicationKitchen.Client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

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
public class ColorPicker extends JPanel {

    private final int width;
    private final int height;
    private BufferedImage image;
    private Color selectedColor = Color.WHITE;

    public ColorPicker(final int width, final int height) {
        this.width = width;
        this.height = height;
        int[] data = new int[width * height];
        int index = 0;
        for (int y = 0; y < height; y++) {
            int red = (y * 255) / (height - 1);
            for (int x = 0; x < width; x++) {
                int green = (x * 255) / (width - 1);
                int blue = 128;
                data[index++] = (red << 16) | (green << 8) | blue;
            }
        }

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, data, 0, width);
        setSize(width, height + 25);

        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getX() > 0) && (e.getX() < width) && (e.getY() > 0) && (e.getY() < height)) {
                    selectedColor = new Color(image.getRGB(e.getX(), e.getY()));
                    repaint();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }


            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
            	if ((e.getX() > 0) && (e.getX() < width) && (e.getY() > 0) && (e.getY() < height)) {
                    selectedColor = new Color(image.getRGB(e.getX(), e.getY()));
                    repaint();
                }
            }
                        
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);

        g.setColor(selectedColor);
        g.fillRect(0, height + 5, width, 20);
    }

    /**
     * returns the selected color
     *
     * @return the selected color
     */
    public Color getSelectedColor() {
        return selectedColor;
    }
}
