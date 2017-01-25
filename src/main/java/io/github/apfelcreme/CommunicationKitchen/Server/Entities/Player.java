package io.github.apfelcreme.CommunicationKitchen.Server.Entities;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.sun.org.apache.xpath.internal.operations.Or;
import io.github.apfelcreme.CommunicationKitchen.Server.ConnectionHandler;
import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;
import io.github.apfelcreme.CommunicationKitchen.Server.Order;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

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
    private int x;
    private int y;
    private Direction direction;
    private Ingredient carrying;

    public Player(UUID id, int x, int y, Direction direction) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void move(Direction direction) {
        switch (direction) {

            case LEFT:
                x = x - 10;
                if (x < 0) {
                    x = 0;
                }
                setDirection(Direction.LEFT);
                break;
            case RIGHT:
                x = x + 10;
                if (x > KitchenServer.getInstance().getFieldDimension().width - 40) {
                    x = KitchenServer.getInstance().getFieldDimension().width - 40;
                }
                setDirection(Direction.RIGHT);
                break;
            case UP:
                y = y - 10;
                if (y < 0) {
                    y = 0;
                }
                setDirection(Direction.UP);
                break;
            case DOWN:
                y = y + 10;
                if (y > (KitchenServer.getInstance().getFieldDimension().height - 40)) {
                    y = KitchenServer.getInstance().getFieldDimension().height - 40;
                }
                setDirection(Direction.DOWN);
                break;
        }

        // can the player pickup or deliver an ingredient?
        for (Iterator<Order> it = KitchenServer.getInstance().getOrders().iterator(); it.hasNext(); ) {
            Order order = it.next();
            boolean orderCancelled = false;
            for (Ingredient ingredient : order.getIngredients()) {
                if (ingredient.getStatus() == Ingredient.Status.MISSING) {
                    if (Util.arePositionsEqual(this.x, this.y, ingredient.getX(), ingredient.getY(), 25)) {
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
                        int alreadyDeliveredCounter = order.getIngredients(Ingredient.Status.WAS_DELIVERED).size();

                        //does the item being carried equal the next required item?
                        if (carrying.equals(order.getIngredients().get(alreadyDeliveredCounter))) {
                            KitchenServer.getInstance().log(this.getId() + " hat Ingredient abgelegt!");
                            order.getIngredients().get(carrying.getQueuePos()).setStatus(Ingredient.Status.WAS_DELIVERED);
                            this.carrying = null;
                            ConnectionHandler.broadcastRemovalFromHand(id);
                        } else {
                            order.remove();
                            order.cancel();
                            KitchenServer.getInstance().log("Die Bestellung" + order.getId()
                                    + " ist fehlgeschlagen!");
                            orderCancelled = true;
                        }
                    }
                }
            }

            // was an order completed successfully?
            if ((order.getIngredients(Ingredient.Status.MISSING).size() == 0)
                    && (order.getIngredients(Ingredient.Status.IS_BEING_CARRIED).size() == 0)
                    && !orderCancelled) {
                KitchenServer.getInstance().log(getId().toString() + " hat Bestellung " + order.getId()
                        + " erfolgreich beendet");
                order.cancel();
                order.remove();
                it.remove();
            }
        }

        // send a message to all clients, so they can redraw the position
        // of the player on their own GUIs
        ConnectionHandler.broadcastPlayerMove(this);
    }

    /**
     * let a player drop is load
     */
    public void dropCarrying() {
        if (carrying != null) {
            for (Order order : KitchenServer.getInstance().getOrders()) {
                for (Ingredient ingredient : order.getIngredients()) {
                    if (ingredient.equals(carrying)) {
                        // to which direction shall the ingredient be thrown to?
                        switch (direction) {
                            case LEFT:
                                ingredient.setX(ingredient.getX() - 40);
                                ingredient.setY(y);
                                break;
                            case RIGHT:
                                ingredient.setX(ingredient.getX() + 40);
                                ingredient.setY(y);
                                break;
                            case UP:
                                ingredient.setY(ingredient.getY() - 40);
                                ingredient.setX(x);
                                break;
                            case DOWN:
                                ingredient.setY(ingredient.getY() + 40);
                                ingredient.setX(x);
                                break;
                        }
                        ingredient.setStatus(Ingredient.Status.MISSING);
                        ConnectionHandler.broadcastRemovalFromHand(id);
                        ConnectionHandler.broadcastAddDrawable(ingredient.getId(), ingredient.getQueuePos() + 1,
                                ingredient.getType().getDrawableType(), ingredient.getX(), ingredient.getY());
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

}
