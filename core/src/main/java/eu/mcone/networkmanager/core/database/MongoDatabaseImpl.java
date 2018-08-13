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
import com.mongodb.client.internal.OperationExecutor;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;

public class MongoDatabaseImpl extends com.mongodb.client.internal.MongoDatabaseImpl implements eu.mcone.networkmanager.core.api.database.MongoDatabase {

    @Getter
    private Database database;
    private MongoClient mongoClient;

    MongoDatabaseImpl(MongoClient client, Database database, OperationExecutor operationExecutor) {
        super(
                database.getName(),
                client.getMongoClientOptions().getCodecRegistry(),
                client.getMongoClientOptions().getReadPreference(),
                client.getMongoClientOptions().getWriteConcern(),
                client.getMongoClientOptions().getRetryWrites(),
                client.getMongoClientOptions().getReadConcern(),
                operationExecutor
        );

        this.database = database;
        this.mongoClient = client;
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
    public MongoClient getClient() {
        return this.mongoClient;
    }
}
