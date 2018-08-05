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
import com.mongodb.client.internal.MongoDatabaseImpl;
import com.mongodb.client.internal.OperationExecutor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDBManager;
import eu.mcone.networkmanager.core.console.Logger;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MongoDatabaseManager extends MongoDatabaseImpl implements MongoDBManager {

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
    }

    @Override
    public void insertDocument(final Document document, final String collection) {
        try {
            System.out.println("Insert new document");
            MongoCollection<Document> insertCollection = getCollection(collection);
            insertCollection.insertOne(document);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertDocuments(final List<Document> documentList, final String collection) {
        try {
            System.out.println("Insert a list of documents");
            MongoCollection<Document> insertCollection = getCollection(collection);
            insertCollection.insertMany(documentList);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDocument(final String fieldName, final String fieldValue, final String updateFieldName, final String newValue, final String collection) {
        try {
            System.out.println("Update Document equals " + fieldName + " and fieldValue " + fieldValue);
            MongoCollection<Document> insertCollection = getCollection(collection);
            UpdateResult updateResult = insertCollection.updateOne(eq(fieldName, fieldValue), combine(set(updateFieldName, newValue)));
            Logger.log(collection, "Update Status: " + updateResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replace(final String fieldName, final String fieldValue, final Document replaceDocument, final String collection) {
        try {
            System.out.println("Replace fieldName  " + fieldName + " with Document");
            MongoCollection<Document> insertCollection = getCollection(collection);
            UpdateResult updateResult = insertCollection.replaceOne(eq(fieldName, fieldValue), replaceDocument);
            Logger.log(collection, "Update Status: " + updateResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDocument(final String fieldName, final String fieldValue, final String collection) {
        try {
            System.out.println("Delete document with the fieldName " + fieldName);
            MongoCollection<Document> insertCollection = getCollection(collection);
            DeleteResult deleteResult = insertCollection.deleteOne(eq(fieldName, fieldValue));
            Logger.log(collection, "Update Status: " + deleteResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDocument(final String fieldName, final String collection) {
        try {
            System.out.println("Delete all documents with the fieldName " + fieldName);
            MongoCollection<Document> insertCollection = getCollection(collection);
            DeleteResult deleteResult = insertCollection.deleteMany(eq(fieldName, ""));
            Logger.log(collection, "Update Status: " + deleteResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
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

}
