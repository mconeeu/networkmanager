/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public interface MongoDBManager {

    void connect();

    void connectAuthentication();

    void connectAuthentication(final String username, final String password, final String database);

    void closeConnection();

    void insertDocument(final Document document, final String collection);

    void insertDocuments(final List<Document> documentList, final String collection);

    void updateDocument(final String fieldName, final String fieldValue, final String updateFieldName, final String newValue, final String collection);

    void replace(final String fieldName, final String fieldValue, final Document replaceDocument, final String collection);

    void deleteDocument(final String fieldName, final String fieldValue, final String collection);

    void deleteDocument(final String fieldName, final String collection);

    FindIterable<Document> getDocumentsInCollection(final String collection);

    MongoCollection<Document> getCollection(final String key);

    MongoDatabase getMongoDatabase(final String key);

    MongoDatabase getMongoDatabase();

    MongoClient getClient();
}
