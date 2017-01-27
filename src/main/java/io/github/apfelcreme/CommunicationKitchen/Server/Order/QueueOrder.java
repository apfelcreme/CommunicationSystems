package io.github.apfelcreme.CommunicationKitchen.Server.Order;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;

import java.util.ArrayList;
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
public class QueueOrder extends Order {

    public QueueOrder(UUID id, int amount, long time) {
        super(id, time);

        // spawn n ingredients
        this.ingredients = new ArrayList<Ingredient>();
        for (int i = 1; i <= amount; i++) {
            Ingredient ingredient = new Ingredient(
                    UUID.randomUUID(),
                    i,
                    Ingredient.Type.random(),
                    40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().width - 60),
                    40 + new Random().nextInt(KitchenServer.getInstance().getFieldDimension().height - 60));
            ingredients.add(ingredient);
        }
    }

    /**
     * returns the counter for the next ingredient that has to be inserted
     * @return the counter
     */
    public int getNextQueuePosition() {
        return getIngredients(Ingredient.Status.WAS_DELIVERED).size();
    }
}
