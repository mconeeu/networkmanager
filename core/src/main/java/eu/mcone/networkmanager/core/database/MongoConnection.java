/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.database;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.internal.OperationExecutor;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MongoConnection {

    private final static CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            MongoClient.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(
                    PojoCodecProvider.builder()
                            .automatic(true)
                            .build(),
                    new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY)
            )
    );

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

    public MongoConnection connect() {
        if (userName == null) {
            client = new MongoClient(
                    host, port
            );

            return this;
        } else {
            client = new MongoClient(
                    new ServerAddress(host, port),
                    MongoCredential.createCredential(
                            userName,
                            authDatabase,
                            password.toCharArray()
                    ),
                    MongoClientOptions.builder()
                            .codecRegistry(CODEC_REGISTRY)
                            .sslEnabled(false)
                            .build()
            );

            return this;
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

    public static MongoCollection addCodecProviders(MongoCollection collection, CodecProvider... providers) {
        return collection.withCodecRegistry(CodecRegistries.fromRegistries(CODEC_REGISTRY, CodecRegistries.fromProviders(providers)));
    }

}
