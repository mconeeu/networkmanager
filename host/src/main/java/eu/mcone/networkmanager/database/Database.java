/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.database;

public enum  Database {

    SYSTEM("mc1system", "", "", "", 27017),
    DATA("mc1data", "", "", "", 27017),
    STATS("mc1stats", "", "", "", 27017),
    CLOUD("mc1cloud", "", "", "", 27017);

    private final String username;
    private final String password;
    private final String database;
    private final String host;
    private final int port;

    Database(final String username, final String password, final String database, final String host, final int port) {
        this.username = username;
        this.password = password;
        this.database = database;
        this.host = host;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
