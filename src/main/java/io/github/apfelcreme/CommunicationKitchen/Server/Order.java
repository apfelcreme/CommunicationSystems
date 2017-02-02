package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;

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
public class Order extends TimerTask {

    private UUID id;
    private ArrayList<Ingredient> ingredients;
    private long time;
    private Pot pot;

    public Order(UUID id, int amount, long time) {
        this.id = id;
        this.time = time;
        this.ingredients = new ArrayList<Ingredient>();

        // spawn n ingredients
        for (int i = 0; i < amount; i++) {
            Ingredient ingredient = new Ingredient(
                    UUID.randomUUID(),
                    i,
                    Ingredient.Type.random(),
                    40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().width - 60),
                    40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().height - 60));
            ingredients.add(ingredient);
        }
        new Timer().schedule(this, time);
    }

    /**
     * removes the order
     */
    public void remove() {
        // remove all ingredients from this order that are currently held by players
        for (Player player : KitchenServer.getInstance().getPlayers()) {
            for (Ingredient ingredient : ingredients) {
                if (ingredient.getStatus() == Ingredient.Status.IS_BEING_CARRIED) {
                    if (ingredient.equals(player.getCarrying())) {
                        player.setCarrying(null);
                        ConnectionHandler.broadcastRemovalFromHand(player.getId());
                    }
                }
            }
        }

        for (Ingredient ingredient : ingredients) {
            ConnectionHandler.broadcastRemoveDrawable(ingredient.getId());
        }

        ConnectionHandler.broadcastRemoveDrawable(pot.getId());
        ConnectionHandler.broadcastRemoveOrder(this);

        ingredients.clear();
    }

    /**
     * returns the order id
     *
     * @return the order id
     */
    public UUID getId() {
        return id;
    }

    /**
     * the list of ingredients
     *
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * returns all ingredients with a given status
     * @param status the status
     * @return all ingredients with the given status
     */
    public List<Ingredient> getIngredients(Ingredient.Status status) {
        List<Ingredient> ret = new ArrayList<Ingredient>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getStatus() == status) {
                ret.add(ingredient);
            }
        }
        return ret;
    }

    /**
     * returns the amount of time the players have to complete the orders
     *
     * @return the amount of time in ms
     */
    public long getTime() {
        return time;
    }

    /**
     * @return the pot
     */
    public Pot getPot() {
        return pot;
    }

    /**
     * sets the pot
     *
     * @param pot the pot
     */
    public void setPot(Pot pot) {
        this.pot = pot;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        KitchenServer.getInstance().log("Auftrag "+id+" fehlgeschlagen");
        remove();
        cancel();
        KitchenServer.getInstance().handleFailure("TIME");
        //ConnectionHandler.broadcastDamage();
    }
}
