package io.github.apfelcreme.CommunicationKitchen.Util;

import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;

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
        if (!s.isEmpty()) {
            System.out.println(s);
            for (String player : s.split(Pattern.quote("|"))) {
                drawablePlayers.add(new DrawablePlayer(
                        UUID.fromString(player.split(",")[0]),
                        Integer.valueOf(player.split(",")[1]),
                        Integer.valueOf(player.split(",")[2]),
                        Direction.getDirection(player.split(",")[3])
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
            ret = ret + player.getId() + "," + player.getX() + "," + player.getY() + "," + player.getDirection() + "|";
        }
        return ret;
    }
}
