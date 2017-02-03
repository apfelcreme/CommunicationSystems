package io.github.apfelcreme.CommunicationKitchen.Util;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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
public class Util {

    /**
     * deserializes a string
     *
     * @param s a serialized list
     * @return a real list
     */
    public static List<DrawablePlayer> deserializePlayerList(String s) {
        List<DrawablePlayer> drawablePlayers = new ArrayList<DrawablePlayer>();
        if (!s.equals("")) {
            for (String player : s.split(Pattern.quote("|"))) {
                drawablePlayers.add(new DrawablePlayer(
                        UUID.fromString(player.split(",")[0]),
                        player.split(",")[1],
                        new Color(Integer.valueOf(player.split(",")[2])),
                        Integer.valueOf(player.split(",")[3]),
                        Integer.valueOf(player.split(",")[4])
                ));
            }
        }
        return drawablePlayers;
    }

    /**
     * stores all players in a string
     *
     * @param players a player list
     * @return a string
     */
    public static String serializePlayerList(List<Player> players) {
        String ret = "";
        for (Player player : players) {
            ret += player.getId() + "," + player.getName() + "," + player.getColor().getRGB() + "," + player.getX()
                    + "," + player.getY() + "|";
        }
        return ret;
    }

    /**
     * checks if the positions of objects are ~ equal
     *
     * @param x1        x of object 1
     * @param y1        y of object 1
     * @param x2        x of object 2
     * @param y2        y of object 2
     * @param tolerance the distance that is allowed between both
     * @return true or false
     */
    public static boolean arePositionsEqual(int x1, int y1, int x2, int y2, int tolerance) {
        return (x1 < (x2 + tolerance)) && (x1 > (x2 - tolerance)) && (y1 < (y2 + tolerance)) && (y1 > (y2 - tolerance));
    }

    /**
     * colorizes the player image
     *
     * @param image the player image
     * @param color the player color
     * @return a colorized image
     */
    public static BufferedImage colorize(BufferedImage image, Color color) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) == new Color(255, 0, 0).getRGB()) {
                    image.setRGB(x, y, color.brighter().getRGB());
                } else if (image.getRGB(x, y) == new Color(0, 255, 0).getRGB()) {
                    image.setRGB(x, y, color.darker().getRGB());
                } else if (image.getRGB(x, y) == new Color(0, 0, 255).getRGB()) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
        return image;
    }
}
