/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoDatabaseImpl;
import com.mongodb.client.internal.OperationExecutor;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDBManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDatabaseManager extends MongoDatabaseImpl implements MongoDBManager {

    private String database;
    private MongoClient mongoClient;

    MongoDatabaseManager(MongoClient client, Database database, OperationExecutor operationExecutor) {
        super(
                database.getName(),
                client.getMongoClientOptions().getCodecRegistry(),
                client.getMongoClientOptions().getReadPreference(),
                client.getMongoClientOptions().getWriteConcern(),
                client.getMongoClientOptions().getRetryWrites(),
                client.getMongoClientOptions().getReadConcern(),
                operationExecutor
        );

        this.database = database.getName();
        this.mongoClient = client;
    }

    @Override
    public Boolean containsValue(final Object objectValue, final String collection) {
        try {
            MongoCollection<Document> databaseCollection = getCollection(collection);
            for (Document document : databaseCollection.find()) {
                return document.containsValue(objectValue);
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object> getObject(final String key, final Object objectValue, final String object, final String collection) {
        try {
            List<Object> result = new ArrayList<>();

            for (Document documents : getCollection(collection).find(eq(key, objectValue))) {
                result.add(documents.get(object));
            }

            return result;
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FindIterable<Document> getDocumentsInCollection(final String collection) {
        MongoCollection<Document> databaseCollection = getCollection(collection);
        return databaseCollection.find();
    }

    @Override
    public MongoCollection<Document> getCollection(final String collection) {
        try {
            return super.getCollection(collection);
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MongoDatabase getMongoDatabase(String key) {
        return getClient().getDatabase(key);
    }

    @Override
    public MongoDatabase getMongoDatabase() {
        return getClient().getDatabase(this.database);
    }

    @Override
    public MongoClient getClient() {
        return this.mongoClient;
    }
}
