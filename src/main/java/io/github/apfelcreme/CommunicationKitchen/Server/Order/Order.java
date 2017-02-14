package io.github.apfelcreme.CommunicationKitchen.Server.Order;

import io.github.apfelcreme.CommunicationKitchen.Server.ConnectionHandler;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;
import io.github.apfelcreme.CommunicationKitchen.Server.Game;
import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;
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
public abstract class Order extends TimerTask {

    private UUID id;
    protected ArrayList<Ingredient> ingredients;
    private long time;
    private long remainingTime;
    private Pot pot;

    private Game.Message endingMessage = Game.Message.FAIL_TIME;


    public Order(UUID id, long time) {
        this.id = id;
        this.time = time;
        this.remainingTime = time;
        new Timer().schedule(this, 100, 100);

        pot = new Pot(
                UUID.randomUUID(),
                40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().width - 80),
                40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().height - 80)
        );
        ConnectionHandler.broadcastAddDrawable(pot.getId(), -1, DrawableType.POT, pot.getX(), pot.getY());
    }

    /**
     * removes the order
     *
     * @param result the result of the order
     */
    public void remove(Order.Result result) {
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

        if (result == Result.SUCCESS) {
            KitchenServer.getInstance().log("Bestellung erfolgreich beendet");
            KitchenServer.getInstance().getGame().getRunningOrders().remove(this);
            KitchenServer.getInstance().getGame().getSuccessfulOrders().add(this);
            KitchenServer.getInstance().getGame().handleSuccess(endingMessage);
        } else {
            KitchenServer.getInstance().log("Bestellung ist fehlgeschlagen!");
            KitchenServer.getInstance().getGame().getRunningOrders().remove(this);
            KitchenServer.getInstance().getGame().getFailedOrders().add(this);
            KitchenServer.getInstance().getGame().handleFailure(endingMessage);
        }

        for (Ingredient ingredient : ingredients) {
            ConnectionHandler.broadcastRemoveDrawable(ingredient.getId());
        }

        ConnectionHandler.broadcastRemoveDrawable(pot.getId());
        ConnectionHandler.broadcastRemoveOrder(this);

        ingredients.clear();

        this.cancel();

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
     *
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
     * sets the message the game is ending with
     *
     * @param endingMessage the message
     */
    public void setEndingMessage(Game.Message endingMessage) {
        this.endingMessage = endingMessage;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        if (!KitchenServer.getInstance().getGame().isPaused()) {
            remainingTime -= 100;
        }
        if (remainingTime <= 0) {
            remove(Result.FAILED);
        }
    }

    /**
     * the result of the order
     */
    public enum Result {
        SUCCESS, FAILED
    }
}
