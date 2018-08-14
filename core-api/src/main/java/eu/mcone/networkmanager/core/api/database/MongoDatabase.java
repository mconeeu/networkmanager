/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.api.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface MongoDatabase extends com.mongodb.client.MongoDatabase {

    Database getDatabase();

    FindIterable<Document> getDocumentsInCollection(final String collection);

    MongoCollection<Document> getCollection(final String key);

}
