package com.vomarek.hideitem.data.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vomarek.hideitem.data.PlayerState;
import org.bson.Document;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public class MongoDB implements Database {
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final String databaseName;
    private final String collectionName;

    public MongoDB(final YamlConfiguration config) {

        host = config.getString("mongodb.host", "localhost");
        port = config.getInt("mongodb.port", 27017);
        databaseName = config.getString("mongodb.database", "hideItem");
        collectionName = config.getString("mongodb.collection", "playerStates");
        username = config.getString("mongodb.username", "root");
        password = config.getString("mongodb.password", "");


        createConnection();
    }

    @Override
    public void createConnection() {
        ConnectionString connectionString = new ConnectionString(
                "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=" + databaseName
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();
        client = MongoClients.create(settings);
        database = client.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
    }

    private boolean isRegistered(UUID uuid) {
        return collection.find(new Document("UUID", uuid.toString())).first() != null;
    }

    @Override
    public void setState(UUID uuid, PlayerState state) {
        if(isRegistered(uuid)) {
            updateValue(uuid, "state", state.getId());
        } else {
            Document document = new Document("UUID", uuid.toString());
            document.append("state", state.getId());
            collection.insertOne(document);
        }
    }

    @Override
    public PlayerState getState(UUID uuid) {
        if(isRegistered(uuid)) {
            Document document = collection.find(new Document("UUID", uuid.toString())).first();
            if(document == null) return null;
            return PlayerState.valueOf(document.getString("state"));
        }
        return null;
    }

    @Override
    public void close() {
        client.close();
    }

    private void updateValue(UUID uuid, String id, Object value) {
        Document query = new Document("UUID", uuid.toString());
        Document newDocument = new Document(id, value);
        Document updateObject = new Document("$set", newDocument);
        collection.updateOne(query,
                updateObject);
    }
}
