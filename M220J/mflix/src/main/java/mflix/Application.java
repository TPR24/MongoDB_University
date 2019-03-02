package mflix;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.connection.SslSettings;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        String welcomeMessage =
                ""
                        + "\n"
                        + " __          __  _                            _          __  __ ______ _ _      \n"
                        + " \\ \\        / / | |                          | |        |  \\/  |  ____| (_)     \n"
                        + "  \\ \\  /\\  / /__| | ___ ___  _ __ ___   ___  | |_ ___   | \\  / | |__  | |___  __\n"
                        + "   \\ \\/  \\/ / _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ | __/ _ \\  | |\\/| |  __| | | \\ \\/ /\n"
                        + "    \\  /\\  /  __/ | (_| (_) | | | | | |  __/ | || (_) | | |  | | |    | | |>  < \n"
                        + "     \\/  \\/ \\___|_|\\___\\___/|_| |_| |_|\\___|  \\__\\___/  |_|  |_|_|    |_|_/_/\\_\\\n"
                        + "                                                                                \n"
                        + "                                                                                \n"
                        + "     ^\n"
                        + "   /'|'\\\n"
                        + "  / \\|/ \\\n"
                        + "  | \\|/ |\n"
                        + "   \\ | /\n"
                        + "    \\|/\n"
                        + "     |\n"
                        + "                       \n";
        System.out.println(welcomeMessage);

//        String URI = "mongodb+srv://m001-student:Pa55w0rd@unicluster-pbwal.mongodb.net/test?retryWrites=true";
//        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(URI)).build();
//        MongoClient mongoClient = MongoClients.create(settings);
//
//// TODO do a read on the cluster to ensure you are connected
//
//        SslSettings sslSettings = settings.getSslSettings();
//        ReadPreference readPreference = settings.getReadPreference();
//        ReadConcern readConcern = settings.getReadConcern();
//        WriteConcern writeConcern = settings.getWriteConcern();
//
//
//        System.out.println("sslSettings.isInvalidHostNameAllowed();" +sslSettings.isInvalidHostNameAllowed());
//        System.out.println("readConcern.asDocument().toString();" +readConcern.asDocument().toString());
//        System.out.println("sslSettings.isEnabled();" +sslSettings.isEnabled());
//        System.out.println("writeConcern.asDocument().toString();" +writeConcern.asDocument().toString());
//        System.out.println("readPreference.toString();" +readPreference.toString());
    }
}
