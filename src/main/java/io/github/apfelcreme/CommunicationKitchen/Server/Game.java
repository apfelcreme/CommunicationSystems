package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.Order;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.QueueOrder;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.TimeOrder;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;

import java.util.*;

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
public class Game {

    private int currentLevel = 0;

    /**
     * a list of all orders
     */
    private List<Order> orders = new ArrayList<Order>();

    public Game(final long timeBetweenLevels, final int timePerOrder, final int numberOfOrders) {
        this.orders = new ArrayList<Order>();
        KitchenServer.getInstance().log("Spiel-Start");
        new Timer().schedule(new TimerTask() {
            public void run() {
                if (currentLevel < numberOfOrders) {
                    newOrder(timePerOrder);
                    currentLevel++;
                }
            }
        }, 0, timeBetweenLevels);
    }

    /**
     * The action to be performed by this timer task.
     */
    public void newOrder(long time) {
        Order order;
        if (Math.random() >= 0.5) {
            order = new TimeOrder(UUID.randomUUID(), 2, time, 3000);
        } else {
            order = new QueueOrder(UUID.randomUUID(), 6, time);
        }
        orders.add(order);
        KitchenServer.getInstance().log("Order-Spawn: " + order.getClass().getName());
        for (Ingredient ingredient : order.getIngredients()) {
            KitchenServer.getInstance().log("  " + ingredient.getId() + " - "
                    + ingredient.getType() + "(" + ingredient.getX() + "," + ingredient.getY() + ")");
            ConnectionHandler.broadcastAddDrawable(ingredient.getId(),
                    ingredient.getQueuePos(),
                    ingredient.getType().getDrawableType(),
                    ingredient.getX(),
                    ingredient.getY());
        }
        Pot pot = new Pot(
                UUID.randomUUID(),
                40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().width - 80),
                40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().height - 80)
        );
        KitchenServer.getInstance().log("Pot-Spawn (" + pot.getX() + "," + pot.getY() + ")");
        order.setPot(pot);
        ConnectionHandler.broadcastAddDrawable(pot.getId(), -1, DrawableType.POT, pot.getX(), pot.getY());
        ConnectionHandler.broadcastNewOrder(order);
    }

    /**
     * returns the list of orders that are currently active
     *
     * @return the list of orders
     */
    public List<Order> getOrders() {
        return orders;
    }


}
