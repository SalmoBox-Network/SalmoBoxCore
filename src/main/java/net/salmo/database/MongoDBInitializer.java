package net.salmo.database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.salmo.SalmoBoxPlugin;
import net.salmo.managers.DatabaseManager;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public final class MongoDBInitializer {

    public static DatabaseManager initialize(final SalmoBoxPlugin plugin) throws MongoClientException {
        FileConfiguration config = plugin.getConfig();

        final String user = getConfigValueWithCheck(config, "database.user", "MongoDB user");
        final String pass = getConfigValueWithCheck(config, "database.password", "MongoDB password");
        final String host = getConfigValueWithCheck(config, "database.host", "MongoDB host");
        final String databaseName = getConfigValueWithCheck(config, "database.database", "MongoDB database name");
        final String collection = getConfigValueWithCheck(config, "database.collection", "MongoDB collection");

        if (databaseName.contains(" ")) {
            throw new MongoConfigurationException("Database name cannot contain spaces: '" + databaseName + "'");
        }

        final int threadsPool = config.getInt("database.thread-pool", 4);
        final int port = config.getInt("database.port", 27017);
        final int connectionTimeOut = config.getInt("database.connect-timeout-seconds", 3);
        final int readTimeOut = config.getInt("database.read-timeout-seconds", 2);

        if (port < 1024 || port > 49151) {
            throw new MongoConfigurationException("MongoDB port needs to be between 1024 - 49151. See IANA registered ports");
        }

        final String uri = "mongodb://" + user + ":" + pass + "@" + host + ":" + port + "/?authSource=" + databaseName;

        final ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        final MongoClientSettings settings = MongoClientSettings.builder()
                .serverApi(serverApi)
                .applyConnectionString(new ConnectionString(uri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                        .readTimeout(readTimeOut, TimeUnit.SECONDS)
                )
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(5000, TimeUnit.MILLISECONDS)
                )
                .build();

        try {
            final MongoClient client = MongoClients.create(settings);
            final MongoDatabase mongoDatabase = client.getDatabase(databaseName);

            try {
                mongoDatabase.runCommand(new Document("ping", 1));
            } catch (MongoException e) {
                client.close();
                throw new MongoClientException("Failed to connect to MongoDB: " + e.getMessage(), e);
            }

            final MongoCollection<BsonDocument> mongoCollection = mongoDatabase.getCollection(collection, BsonDocument.class);

            try {
                mongoCollection.countDocuments();
            } catch (MongoException e) {
            }

            final ExecutorService executor = Executors.newFixedThreadPool(threadsPool);

            return new DatabaseManager(executor, client, mongoCollection);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("databaseName does not contain")) {
                throw new MongoConfigurationException("Invalid database name: '" + databaseName + "'. Database names cannot contain spaces or special characters.");
            }
            throw new MongoClientException("Invalid MongoDB configuration: " + e.getMessage(), e);
        } catch (MongoConfigurationException e) {
            throw new MongoClientException("MongoDB configuration error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MongoClientException("Failed to initialize MongoDB: " + e.getMessage(), e);
        }
    }

    private static String getConfigValueWithCheck(FileConfiguration config, String path, String description) {
        String value = config.getString(path);
        if (value == null) {
            throw new MongoConfigurationException(description + " is null or not found in config.yml at path: " + path);
        }
        if (value.trim().isEmpty()) {
            throw new MongoConfigurationException(description + " is empty in config.yml at path: " + path);
        }
        return value.trim();
    }
}