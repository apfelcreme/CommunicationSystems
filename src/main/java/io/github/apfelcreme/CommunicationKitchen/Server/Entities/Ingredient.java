package io.github.apfelcreme.CommunicationKitchen.Server.Entities;

import java.util.Random;
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
public class Ingredient {

    private UUID id;
    private Type type;
    private int x;
    private int y;

    public Ingredient(UUID id, Type type, int x, int y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
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
     * returns the ingredient type
     *
     * @return the ingredient type
     */
    public Type getType() {
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
     * returns the y coordinate
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * all types of ingredients
     */
    public enum Type {
        APPLE, BEEF, BREAD, CARROT, EGG, FISH, MUSHROOM, ONION, POTATO;

        public static Type getType(String key) {
            for (Type type : values()) {
                if (type.name().equals(key)) {
                    return type;
                }
            }
            return null;
        }

        /**
         * returns a random type
         *
         * @return a random type
         */
        public static Type random() {
            return values()[new Random().nextInt(values().length)];
        }

    }
}
