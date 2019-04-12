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
import org.bson.UuidRepresentation;
import org.bson.codecs.Codec;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;

public class MongoConnection {

    private CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
            getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(
                    PojoCodecProvider.builder()
                            .automatic(true)
                            .build(),
                    new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY)
            )
    );

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

    public MongoConnection withCodecs(Codec<?>... codec) {
        codecRegistry = CodecRegistries.fromRegistries(
                codecRegistry,
                CodecRegistries.fromCodecs(codec)
        );
        return this;
    }

    public MongoConnection withCodecProviders(CodecProvider... provider) {
        codecRegistry = CodecRegistries.fromRegistries(
                codecRegistry,
                CodecRegistries.fromProviders(provider)
        );
        return this;
    }

    public MongoConnection connect() {
        client = MongoClients.create(clientSettingsBuilder.codecRegistry(codecRegistry).build());
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
