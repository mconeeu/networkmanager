/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.api.database;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public interface MongoDBManager extends MongoDatabase {

    List<Object> getObject(final String key, final Object objectValue, final String object, final String collection);

    Boolean containsValue(final Object objectValue, final String collection);

    FindIterable<Document> getDocumentsInCollection(final String collection);

    MongoCollection<Document> getCollection(final String key);

    MongoDatabase getMongoDatabase(final String key);

    MongoDatabase getMongoDatabase();

    MongoClient getClient();

}
