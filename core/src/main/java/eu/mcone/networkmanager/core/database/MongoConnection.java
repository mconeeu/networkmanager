/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.database;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;

public class MongoConnection {

    private final static CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            getDefaultCodecRegistry(),
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
            client = MongoClients.create("mongodb://"+host+":"+port);
            return this;
        } else {
            client = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(
                                    new ConnectionString("mongodb://"+host+":"+port)
                            )
                            .credential(
                                    MongoCredential.createCredential(
                                            userName,
                                            authDatabase,
                                            password.toCharArray()
                                    )
                            )
                            .codecRegistry(CODEC_REGISTRY)
                            .build()
            );

            return this;
        }
    }

    public MongoDatabase getDatabase(Database database) {
        if (client != null) {
            return client.getDatabase(database.getName());
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
