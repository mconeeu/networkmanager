/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.database;


import com.mongodb.*;
import com.mongodb.client.internal.OperationExecutor;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MongoConnection {

    private String host, userName, password, authDatabase;
    private int port;

    @Getter
    private MongoClient client;

    public MongoConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public MongoConnection(String host, String userName, String password, String authDatabase, int port) {
        this.host = host;
        this.userName = userName;
        this.password = password;
        this.authDatabase = authDatabase;
        this.port = port;
    }

    public MongoClient connect() {
        if (userName == null) {
            return client = new MongoClient(
                    host, port
            );
        } else {
            return client = new MongoClient(
                    new ServerAddress(host, port),
                    MongoCredential.createCredential(
                            userName,
                            authDatabase,
                            password.toCharArray()
                    ),
                    MongoClientOptions.builder()
                            .sslEnabled(false)
                            .build()
            );
        }
    }

    public MongoDatabase getDatabase(Database database) {
        if (client != null) {
            try {
                Method m = Mongo.class.getDeclaredMethod("createOperationExecutor");
                m.setAccessible(true);
                OperationExecutor executor = (OperationExecutor) m.invoke(client);
                m.setAccessible(false);

                return new MongoDatabaseImpl(client, database, executor);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalStateException("MongoConnection is still not connected. Use MongoConnection#connect()");
        }
    }

    public void disconnect() {
        client.close();
    }

}
