package mflix.api.daos;

import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import mflix.api.models.Session;
import mflix.api.models.User;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class UserDao extends AbstractMFlixDao {

    private final MongoCollection<User> usersCollection;
    //TODO> Ticket: User Management - do the necessary changes so that the sessions collection
    //returns a Session object
    private final MongoCollection<Session> sessionsCollection;

    private final Logger log;

    @Autowired
    public UserDao(
            MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
        super(mongoClient, databaseName);
        CodecRegistry pojoCodecRegistry =
                fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        usersCollection = db.getCollection("users", User.class).withCodecRegistry(pojoCodecRegistry).withWriteConcern(WriteConcern.MAJORITY);
        log = LoggerFactory.getLogger(this.getClass());
        sessionsCollection = db.getCollection("sessions", Session.class).withCodecRegistry(pojoCodecRegistry);
    }

    /**
     * Inserts the `user` object in the `users` collection.
     *
     * @param user - User object to be added
     * @return True if successful, throw IncorrectDaoOperation otherwise
     */
    public boolean addUser(User user) {
        //TODO > Ticket: Handling Errors - make sure to only add new users
        // and not users that already exist.
        usersCollection.insertOne(user);
        return true;
    }

    /**
     * Creates session using userId and jwt token.
     *
     * @param userId - user string identifier
     * @param jwt    - jwt string token
     * @return true if successful
     */
    public boolean createUserSession(String userId, String jwt) {
        //TODO > Ticket: Handling Errors - implement a safeguard against
        // creating a session with the same jwt token.

        // Simplistic error handling to check for nulls or empties, as more sophisticated error handling seems covered later.
        if ((userId == null || userId.isEmpty()) || (jwt == null || jwt.isEmpty())) {
            System.out.println("createUserSession called with invalid values.");
            System.out.println("UserID: " + userId);
            System.out.println("jwt: " + jwt);
            return false;
        }

        Session newSession = new Session();
        newSession.setUserId(userId);
        newSession.setUserId(jwt);
        sessionsCollection.insertOne(newSession);
        return true;
    }

    /**
     * Returns the User object matching the an email string value.
     *
     * @param email - email string to be matched.
     * @return User object or null.
     */
    public User getUser(String email) {
        User user = null;

        if (email == null || email.isEmpty()) {
            System.out.println("getUser called with invalid email address.");
            return user;
        }

        List<Bson> pipeline = new ArrayList<>();
        // match stage to find movie
        Bson match = Aggregates.match(Filters.eq("email", email));
        pipeline.add(match);
        user = usersCollection.aggregate(pipeline).first();

        return user;
    }

    /**
     * Given the userId, returns a Session object.
     *
     * @param userId - user string identifier.
     * @return Session object or null.
     */
    public Session getUserSession(String userId) {
        Session session = null;

        if (userId == null || userId.isEmpty()) {
            System.out.println("getUserSession called with invalid user ID.");
            return session;
        }

        List<Bson> pipeline = new ArrayList<>();
        // match stage to find movie
        Bson match = Aggregates.match(Filters.eq("user_id", userId));
        pipeline.add(match);
        session = sessionsCollection.aggregate(pipeline).first();

        return session;
    }

    public boolean deleteUserSessions(String userId) {
        //TODO > Ticket: Handling Errors - make this method more robust by
        // handling potential exceptions.
        // Simplistic error handling to check for nulls or empties, as more sophisticated error handling seems covered later.
        if (userId == null || userId.isEmpty()) {
            System.out.println("deleteUserSessions called with invalid userId.");
            return false;
        }

        Bson toDelete = new Document("user_id", userId);
        sessionsCollection.deleteOne(toDelete);
        return true;
    }

    /**
     * Removes the user document that match the provided email.
     *
     * @param email - of the user to be deleted.
     * @return true if user successfully removed
     */
    public boolean deleteUser(String email) {
        //TODO > Ticket: Handling Errors - make this method more robust by
        // handling potential exceptions.
        // Simplistic error handling to check for nulls or empties, as more sophisticated error handling seems covered later.
        if (email == null || email.isEmpty()) {
            System.out.println("deleteUser called with invalid email address.");
            return false;
        }

        Bson toDelete = new Document("email", email);
        usersCollection.deleteOne(toDelete);
        return true;
    }

    /**
     * Updates the preferences of an user identified by `email` parameter.
     *
     * @param email           - user to be updated email
     * @param userPreferences - set of preferences that should be stored and replace the existing
     *                        ones. Cannot be set to null value
     * @return User object that just been updated.
     */
    public boolean updateUserPreferences(String email, Map<String, String> userPreferences) {
        //TODO > Ticket: Handling Errors - make this method more robust by
        // handling potential exceptions when updating an entry.

        // Simplistic error handling to check for nulls or empties, as more sophisticated error handling seems covered later.
        if (email == null || email.isEmpty()) {
            System.out.println("updateUserPreferences called with invalid email address.");
            return false;
        } else if (userPreferences == null) {
            throw new IncorrectDaoOperation("Attempted to user preferences with a null value.");
        }

        // Retrieve the user in question
        Bson queryFilter = new Document("email", email);
        User userToUpdate = usersCollection.find(queryFilter).iterator().tryNext();

        // Another simple error handler, placeholder until the Handling Errors ticket.
        if(userToUpdate.isEmpty()) {
            System.out.println("Attempted to update user preferences for an empty user, based upon email address: " +email);
            return false;
        }

        userToUpdate.setPreferences(userPreferences);
        UpdateResult updatePreferencesResult = usersCollection.replaceOne(eq("email", email), userToUpdate, new UpdateOptions().upsert(true));

        if(updatePreferencesResult.wasAcknowledged()) {
            return true;
        } else {
            return false;
        }
    }
}
