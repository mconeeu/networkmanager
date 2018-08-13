/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.database;

import lombok.Getter;

public enum Database {

    SYSTEM(0, "mc1system"),
    STATS(1, "mc1stats"),
    DATA(2, "mc1data"),
    CLOUD(3, "mc1cloud");

    @Getter
    private int id;
    @Getter
    private String name;

    Database(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
