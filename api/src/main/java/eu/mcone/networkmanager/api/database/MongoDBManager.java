/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.database;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;

public interface MongoDBManager {

    void connect();

    void connectAuthentication();

    void connectAuthentication(final String username, final String password, final String database);

    void closeConnection();

    MongoCollection<Document> getCollection(final String key);

    MongoDatabase getDatabase(final String key);

    MongoClient getClient();
}
