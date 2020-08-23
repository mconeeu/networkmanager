/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.api.database;

import lombok.Getter;

public enum Database {

    SYSTEM(0, "mc1system"),
    STATS(1, "mc1stats"),
    GAME(2, "mc1game"),
    DATA(3, "mc1data"),
    NETWORK(4, "mc1network"),
    CLOUD(5, "mc1cloud"),
    ONEGAMING(6, "onegaming_system");

    @Getter
    private final int id;
    @Getter
    private final String name;

    Database(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
