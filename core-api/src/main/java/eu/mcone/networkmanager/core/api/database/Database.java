/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.api.database;

import lombok.Getter;

public enum Database {

    SYSTEM(0, "mc1system"),
    STATS(1, "mc1stats"),
    DATA(2, "mc1data"),
    NETWORK(3, "mc1network"),
    CLOUD(4, "mc1cloud");

    @Getter
    private int id;
    @Getter
    private String name;

    Database(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
