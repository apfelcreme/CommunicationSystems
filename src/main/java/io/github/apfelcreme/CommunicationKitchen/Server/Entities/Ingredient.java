package io.github.apfelcreme.CommunicationKitchen.Server.Entities;

import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;

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
    private int queuePos;
    private Type type;
    private int x;
    private int y;
    private Status status;

    public Ingredient(UUID id, int queuePos, Type type, int x, int y) {
        this.id = id;
        this.queuePos = queuePos;
        this.type = type;
        this.x = x;
        this.y = y;
        status = Status.MISSING;
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
     * returns the queue position
     *
     * @return the queue position
     */
    public int getQueuePos() {
        return queuePos;
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
     * sets the x coordinate
     *
     * @param x the x coordinate
     */
    public void setX(int x) {
        if ((x + 40) <= KitchenServer.getInstance().getFieldDimension().width) {
            this.x = x;
        } else {
            this.x = KitchenServer.getInstance().getFieldDimension().width - 40;
        }
        if (x >= 0) {
            this.x = x;
        } else {
            this.x = 0;
        }
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
     * sets the y coordinate
     *
     * @param y the y coordinate
     */
    public void setY(int y) {
        if ((y + 40) <= KitchenServer.getInstance().getFieldDimension().height) {
            this.y = y;
        } else {
            this.y = KitchenServer.getInstance().getFieldDimension().height - 40;
        }
        if (y >= 0) {
            this.y = y;
        } else {
            this.y = 0;
        }
    }

    /**
     * returns the status the ingredient is in atm
     * @return the current status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * sets the current status
     * @param status the new status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", qPos=" + queuePos +
                ", t=" + type +
                ", s=" + status +
                '}';
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
         * returns the matching drawable type
         *
         * @return the matching drawable type
         */
        public DrawableType getDrawableType() {
            switch (this) {
                case APPLE:
                    return DrawableType.APPLE;
                case BEEF:
                    return DrawableType.BEEF;
                case BREAD:
                    return DrawableType.BREAD;
                case CARROT:
                    return DrawableType.CARROT;
                case EGG:
                    return DrawableType.EGG;
                case FISH:
                    return DrawableType.FISH;
                case MUSHROOM:
                    return DrawableType.MUSHROOM;
                case ONION:
                    return DrawableType.ONION;
                case POTATO:
                    return DrawableType.POTATO;
            }
            return DrawableType.APPLE;
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

    /**
     * represents the status of the ingredient in the order
     */
    public enum Status {
        MISSING, WAS_DELIVERED, IS_BEING_CARRIED
    }
}
