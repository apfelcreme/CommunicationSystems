package io.github.apfelcreme.CommunicationKitchen.Server.Order;

import io.github.apfelcreme.CommunicationKitchen.Server.ConnectionHandler;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Game;
import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;

import java.util.*;

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
public class SyncOrder extends Order {

    private long timeFrame;

    public SyncOrder(UUID id, int amount, long time, long timeFrame) {
        super(id, time);
        this.timeFrame = timeFrame;

        // spawn n ingredients
        this.ingredients = new ArrayList<Ingredient>();
        for (int i = 0; i < amount; i++) {
            Ingredient ingredient = new Ingredient(
                    UUID.randomUUID(),
                    -2,
                    Ingredient.Type.random(),
                    40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().width - 60),
                    40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().height - 60));
            ingredients.add(ingredient);
        }
    }

    /**
     * returns the amount of time available after the first ingredient was put into the pot
     *
     * @return the amount of time available after the first ingredient was put into the pot
     */
    public long getTimeFrame() {
        return timeFrame;
    }

    /**
     * starts the cooldown
     */
    public void startCountdown() {
        ConnectionHandler.broadcastTimerStart(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if ((getIngredients(Ingredient.Status.IS_BEING_CARRIED).size() != 0)
                        || (getIngredients(Ingredient.Status.MISSING).size() != 0)) {
                    remove(Result.FAILED, Game.Message.FAIL_SYNC);
                }
            }
        }, timeFrame);
    }
}
