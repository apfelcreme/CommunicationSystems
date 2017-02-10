package io.github.apfelcreme.CommunicationKitchen.Server.Entities;

import io.github.apfelcreme.CommunicationKitchen.Server.ConnectionHandler;
import io.github.apfelcreme.CommunicationKitchen.Server.Game;
import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.Order;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.SequenceOrder;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.SyncOrder;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

import java.awt.*;
import java.util.*;
import java.util.List;

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
public class Player {

    private UUID id;
    private String name;
    private Color color;
    private int x;
    private int y;
    private Direction direction;
    private Ingredient carrying;
    private boolean ready = false;

    public Player(UUID id, String name, Color color, int x, int y, Direction direction) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.x = x;
        this.y = y;
        this.direction = direction;
        ready = false;
    }

    public void move(Direction direction) {
        int minX = 20;
        int minY = 20;
        int maxX = KitchenServer.getInstance().getFieldDimension().width - 20;
        int maxY = KitchenServer.getInstance().getFieldDimension().height - 20;
        switch (direction) {
            case NORTH:
                y = y - 3;
                if (y < minY) {
                    y = minY;
                }
                setDirection(direction);
                break;
            case NORTH_WEST:
                x = x - 2;
                y = y - 2;
                if (x < minX) {
                    x = minX;
                }
                if (y < minY) {
                    y = minY;
                }
                setDirection(direction);
                break;
            case WEST:
                x = x - 3;
                if (x < minX) {
                    x = minX;
                }
                setDirection(direction);
                break;
            case SOUTH_WEST:
                x = x - 2;
                y = y + 2;
                if (x < minX) {
                    x = minX;
                }
                if (y > maxY) {
                    y = maxY;
                }
                setDirection(direction);
                break;
            case SOUTH:
                y = y + 3;
                if (y > maxY) {
                    y = maxY;
                }
                setDirection(direction);
                break;
            case SOUTH_EAST:
                x = x + 2;
                y = y + 2;
                if (x > maxX) {
                    x = maxX;
                }
                if (y > maxY) {
                    y = maxY;
                }
                setDirection(direction);
                break;
            case EAST:
                x = x + 3;
                if (x > maxX) {
                    x = maxX;
                }
                setDirection(direction);
                break;
            case NORTH_EAST:
                x = x + 2;
                y = y - 2;
                if (x > maxX) {
                    x = maxX;
                }
                if (y < minY) {
                    y = minY;
                }
                setDirection(direction);
                break;
        }

        // can the player pickup or deliver an ingredient?
        if (KitchenServer.getInstance().getGame() != null) {
            List<Order> copy = new ArrayList<Order>();
            copy.addAll(KitchenServer.getInstance().getGame().getRunningOrders());
            for (Order order : copy) {
                checkForOrderCompletion(order);
            }
        }

        // send a message to all clients, so they can redraw the position
        // of the player on their own GUIs
        ConnectionHandler.broadcastPlayerMove(this);
    }

    /**
     * checks if an order was completed
     *
     * @param order the order
     */
    private void checkForOrderCompletion(Order order) {
        for (Ingredient ingredient : order.getIngredients()) {
            if (ingredient.getStatus() == Ingredient.Status.MISSING) {
                if (Util.arePositionsEqual(this.x, this.y, ingredient.getX(), ingredient.getY(), 20)) {
                    KitchenServer.getInstance().log("Player catched ingredient " + ingredient.getType());
                    if (this.carrying == null) {
                        ingredient.setStatus(Ingredient.Status.IS_BEING_CARRIED);
                        this.carrying = ingredient;
                        ConnectionHandler.broadcastRemoveDrawable(ingredient.getId());
                        ConnectionHandler.broadcastAdditionToHand(id, ingredient.getType().getDrawableType());
                    }
                }
            }
        }
        Pot pot = order.getPot();
        if (pot != null) {
            if (Util.arePositionsEqual(this.x, this.y, pot.getX(), pot.getY(), 30)) {
                if (carrying != null) {

                    /*
                    INSERT THE INGREDIENTS IN THE CORRECT ORDER
                     */
                    if (order instanceof SequenceOrder) {

                        SequenceOrder queueOrder = (SequenceOrder) order;

                        //does the item being carried equal the next required item?
                        if (carrying.equals(queueOrder.getIngredients().get(queueOrder.getNextQueuePosition()))) {
                            KitchenServer.getInstance().log(this.getId() + " hat Ingredient abgelegt!");
                            queueOrder.getIngredients().get(carrying.getQueuePos() - 1).setStatus(Ingredient.Status.WAS_DELIVERED);
                            this.carrying = null;
                            ConnectionHandler.broadcastRemovalFromHand(id);
                        } else {
                            queueOrder.remove(Order.Result.FAILED, Game.Message.FAIL_SEQUENCE);
                            return;
                        }

                    /*
                    INSERT THE INGREDIENTS IN A SMALL TIME FRAME
                     */
                    } else if (order instanceof SyncOrder) {
                        SyncOrder syncOrder = (SyncOrder) order;
                        for (Ingredient ingredient : syncOrder.getIngredients()) {
                            if (ingredient.equals(carrying)) {

                                // if the ingredient is the first that is delivered: start a countdown
                                if (syncOrder.getIngredients(Ingredient.Status.WAS_DELIVERED).size() == 0) {
                                    syncOrder.startCountdown();
                                }
                                ingredient.setStatus(Ingredient.Status.WAS_DELIVERED);
                                this.carrying = null;
                                ConnectionHandler.broadcastRemovalFromHand(id);
                            }
                        }
                    }
                }
            }
        }

        // was an order completed successfully?
        if ((order.getIngredients(Ingredient.Status.MISSING).size() == 0)
                && (order.getIngredients(Ingredient.Status.IS_BEING_CARRIED).size() == 0)) {
            Game.Message reason = order instanceof SequenceOrder ? Game.Message.WIN_SEQUENCE : Game.Message.WIN_SYNC;
            order.remove(Order.Result.SUCCESS, reason);
        }
    }

    /**
     * let a player drop is load
     */
    public void dropCarrying() {
        if (carrying != null) {
            for (Order order : KitchenServer.getInstance().getGame().getRunningOrders()) {
                for (Ingredient ingredient : order.getIngredients()) {
                    if (ingredient.equals(carrying)) {
                        // to which direction shall the ingredient be thrown to?
                        switch (direction) {
                            case NORTH:
                                ingredient.setX(x);
                                ingredient.setY(y - 40);
                                break;
                            case NORTH_WEST:
                                ingredient.setX(x - 40);
                                ingredient.setY(y - 40);
                                break;
                            case WEST:
                                ingredient.setX(x - 40);
                                ingredient.setY(y);
                                break;
                            case SOUTH_WEST:
                                ingredient.setX(x - 40);
                                ingredient.setY(y + 40);
                                break;
                            case SOUTH:
                                ingredient.setX(x);
                                ingredient.setY(y + 40);
                                break;
                            case SOUTH_EAST:
                                ingredient.setX(x + 40);
                                ingredient.setY(y + 40);
                                break;
                            case EAST:
                                ingredient.setX(x + 40);
                                ingredient.setY(y);
                                break;
                            case NORTH_EAST:
                                ingredient.setX(x + 40);
                                ingredient.setY(y - 40);
                                break;
                        }
                        ingredient.setStatus(Ingredient.Status.MISSING);
                        ConnectionHandler.broadcastRemovalFromHand(id);
                        ConnectionHandler.broadcastAddDrawable(ingredient.getId(),
                                ingredient.getQueuePos(),
                                ingredient.getType().getDrawableType(),
                                ingredient.getX(),
                                ingredient.getY());
                        this.carrying = null;
                        KitchenServer.getInstance().log("Player " + id + " dropped his item");
                    }
                }
            }
        }
    }

    /**
     * returns the players id
     *
     * @return the players id
     */
    public UUID getId() {
        return id;
    }

    /**
     * returns the players name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * returns the players x coordinate
     *
     * @return the players x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * returns the players y coordinate
     *
     * @return the players y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * returns the direction the player is facing
     *
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * sets the direction the player is facing
     *
     * @param direction the direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * returns the ingredient the player is carrying
     *
     * @return the ingredient
     */
    public Ingredient getCarrying() {
        return carrying;
    }

    /**
     * sets the ingredient the player is carrying
     *
     * @param carrying an ingredient
     */
    public void setCarrying(Ingredient carrying) {
        this.carrying = carrying;
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @param ready the ready to set
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * returns the color
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name + "(" + (ready ? "bereit" : "nicht bereit") + ")";
    }

}
