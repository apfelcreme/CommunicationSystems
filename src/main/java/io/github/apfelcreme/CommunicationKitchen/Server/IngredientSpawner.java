package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;

import java.util.Random;
import java.util.TimerTask;
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
public class IngredientSpawner extends TimerTask {

    private long time;

    public IngredientSpawner(long time) {
        this.time = time;
        KitchenServer.getInstance().log("Spiel-Start");
    }

    /**
     * The action to be performed by this timer task.
     */
    public void run() {
        //TODO: Hier: anz spieler eintragen
        //TODO:                                    |
        //TODO:                                    V

    	Order order = new Order(UUID.randomUUID(), 3, time-100); // We need some time (100 ms) to prevent the ingredient spawner from creating a new order when the game stops because the time is over (failure) 
        KitchenServer.getInstance().getOrders().add(order);
        KitchenServer.getInstance().log("Order-Spawn: ");
        int z = 0;
        for (Ingredient ingredient : order.getIngredients()) {
            KitchenServer.getInstance().log("  " + ingredient.getId() + " - "
                    + ingredient.getType() + "(" + ingredient.getX() + "," + ingredient.getY() + ")");
            ConnectionHandler.broadcastAddDrawable(ingredient.getId(), z + 1, ingredient.getType().getDrawableType(),
                    ingredient.getX(), ingredient.getY());
            z++;
        }
        Pot pot = new Pot(
                UUID.randomUUID(),
                40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().width - 80),
                40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().height - 80)
        );
        KitchenServer.getInstance().log("Pot-Spawn (" + pot.getX() + "," + pot.getY() + ")");
        order.setPot(pot);
        ConnectionHandler.broadcastAddDrawable(pot.getId(), 0, DrawableType.POT, pot.getX(), pot.getY());
        ConnectionHandler.broadcastNewOrder(order);
    }
}

