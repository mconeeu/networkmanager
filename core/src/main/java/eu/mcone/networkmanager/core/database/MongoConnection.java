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
import com.mongodb.client.MongoDatabase;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class MongoConnection {

    private CodecRegistry codecRegistry;

    @Getter
    private MongoClientSettings.Builder clientSettingsBuilder;
    @Getter
    private MongoClient client;

    public MongoConnection(String host, int port) {
        this(MongoClientSettings.builder()
                .applyConnectionString(
                        new ConnectionString("mongodb://" + host + ":" + port)
                )
        );
    }

    public MongoConnection(String host, String userName, String password, String authDatabase, int port) {
        this(MongoClientSettings.builder()
                .applyConnectionString(
                        new ConnectionString("mongodb://" + host + ":" + port)
                )
                .credential(
                        MongoCredential.createCredential(
                                userName,
                                authDatabase,
                                password.toCharArray()
                        )
                )
        );

    }

    private MongoConnection(MongoClientSettings.Builder settings) {
        this.clientSettingsBuilder = settings;
    }

    public MongoConnection codecRegistry(CodecRegistry... registries) {
        clientSettingsBuilder.codecRegistry(CodecRegistries.fromRegistries(registries));
        return this;
    }

    public MongoConnection connect() {
        client = MongoClients.create(clientSettingsBuilder.build());
        return this;
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

}
