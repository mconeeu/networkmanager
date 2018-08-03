/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.database;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.text.MessageFormat;

public class MongoDBManager implements eu.mcone.networkmanager.api.database.MongoDBManager {

    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private String username = null;
    @Getter
    private String password = null;
    @Getter
    private String database = null;

    private MongoClient client;
    private MongoDatabase mongoDatabase;

    public MongoDBManager(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public MongoDBManager(final Database database) {
        this.host = database.getHost();
        this.port = database.getPort();
        this.username = database.getUsername();
        this.password = database.getPassword();
        this.database = database.getDatabase();
    }

    public void connect() {
        this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}", host, port)));
    }

    public void connectAuthentication() {
        this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}@{3}:{4}/{5}", username, password, host, port, database)));
    }

    public void connectAuthentication(final String username, final String password, final String database) {
        this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}@{3}:{4}/{5}", username, password, host, port, database)));
    }

    public void closeConnection() {
        this.client.close();
    }

    public MongoDatabase getDatabase(final String database) {
        this.mongoDatabase = this.client.getDatabase(database);
        return this.mongoDatabase;
    }

    public MongoCollection<Document> getCollection(final String key) {
        return this.mongoDatabase.getCollection(key);
    }

    public MongoClient getClient() {
        return this.client;
    }
}
