package mflix.api.daos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import mflix.api.models.Session;
import mflix.api.models.User;
import mflix.config.MongoDBConfiguration;
import org.bson.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@SpringBootTest(classes = {MongoDBConfiguration.class})
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class UserTest extends TicketTest {

    private UserDao dao;

    private static String email = "gryffindor@hogwarts.edu";
    private User testUser;
    private String jwt;
    @Autowired
    MongoClient mongoClient;

    @Value("${spring.mongodb.database}")
    String databaseName;

    @Before
    public void setup() {

        this.dao = new UserDao(mongoClient, databaseName);
        this.testUser = new User();
        this.testUser.setName("Hermione Granger");
        this.testUser.setEmail(email);
        this.testUser.setHashedpw("somehashedpw");
        this.jwt = "somemagicjwt";
        mongoClient
                .getDatabase("mflix")
                .getCollection("users")
                .deleteOne(new Document("email", "log@out.com"));
    }

    @After
    public void tearDownClass() {
        MongoDatabase db = mongoClient.getDatabase("mflix");
        db.getCollection("users").deleteMany(new Document("email", email));
        db.getCollection("users").deleteMany(new Document("email", "log@out.com"));
        db.getCollection("sessions").deleteMany(new Document("user_id", "log@out" +
                ".com"));
    }

    @Test
    public void testGetUser() {
        // Valid values, extracted using Compass.
        String validEmail = "sean_bean@gameofthron.es";
        String expectedName = "Ned Stark";

        User user = dao.getUser(validEmail);

        Assert.assertEquals(expectedName, user.getName());
        Assert.assertEquals(validEmail, user.getEmail());

        User userEmpty = dao.getUser("");
        Assert.assertNull(userEmpty);
        User userNull = dao.getUser(null);
        Assert.assertNull(userNull);
    }

    @Test
    public void testGetUserSession() {
        // Valid values, extracted using Compass.
        String validUserId = "t3qulfeem@kwiv5.6ur";
        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MTk5MDkzMjEsIm5iZiI6MTUxOTkwOTMyMSwianRpIjoiNmJlZDAwMWYtNTFiYi00NzVhLTgxZDAtMDcwNGE5Mjk0MWZlIiwiZXhwIjoxNTE5OTEwMjIxLCJpZGVudGl0eSI6eyJlbWFpbCI6InQzcXVsZmVlbUBrd2l2NS42dXIiLCJuYW1lIjoiM2lveHJtZnF4IiwicGFzc3dvcmQiOm51bGx9LCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MiLCJ1c2VyX2NsYWltcyI6eyJ1c2VyIjp7ImVtYWlsIjoidDNxdWxmZWVtQGt3aXY1LjZ1ciIsIm5hbWUiOiIzaW94cm1mcXgiLCJwYXNzd29yZCI6bnVsbH19fQ.ejtr_NyZyBronWMKuE0RFTjWej--T0zGrdc_iymGtVs";

        Session userSession = dao.getUserSession(validUserId);

        Assert.assertEquals(expectedJwt, userSession.getJwt());
        Assert.assertEquals(validUserId, userSession.getUserId());

        Session userSessionEmpty = dao.getUserSession("");
        Assert.assertNull(userSessionEmpty);
        Session userSessionNull = dao.getUserSession(null);
        Assert.assertNull(userSessionNull);
    }

    @Test
    public void testRegisterUser() {

        assertTrue(
                "Should have correctly created the user - check your write user method",
                dao.addUser(testUser)); // add string explanation

        User user = dao.getUser(testUser.getEmail());
        Assert.assertEquals(testUser.getName(), user.getName());
        Assert.assertEquals(testUser.getEmail(), user.getEmail());
        Assert.assertEquals(testUser.getHashedpw(), user.getHashedpw());
    }

    @Test
    public void testLogin() {
        dao.addUser(testUser);
        boolean result = dao.createUserSession(testUser.getEmail(), jwt);
        assertTrue("Should be able to create user session.", result);
        Session session = dao.getUserSession(testUser.getEmail());
        assertEquals(
                "The user email needs to match the `session` user_id field",
                testUser.getEmail(),
                session.getUserId());
        assertEquals("jwt key needs to match the session `jwt`", jwt, session.getJwt());
    }

    @Test
    public void testLogout() {
        String email = "log@out.com";
        Document logOutUser = new Document("email", email);
        mongoClient.getDatabase("mflix").getCollection("users").insertOne(logOutUser);
        Document logOutUserSession = new Document("user_id", email);
        mongoClient.getDatabase("mflix").getCollection("sessions").insertOne(logOutUserSession);

        assertTrue(
                "Should have deleted user from sessions collection - check your logout method",
                dao.deleteUser(email));
        Session session = dao.getUserSession(email);
        assertNull("All sessions for user should have been deleted after logout", session);
    }

    @Test
    public void testDeleteUser() {
        dao.addUser(testUser);
        assertTrue(
                "You should be able to delete correctly the testDb user. Check your delete filter",
                dao.deleteUser(testUser.getEmail()));

        assertNull(
                "Should not find any sessions after deleting a user. deleteUser() method needs to remove the user sessions data!",
                dao.getUserSession(testUser.getEmail()));

        assertNull(
                "User data should not be found after user been deleted. Make sure you delete data from users collection",
                dao.getUser(testUser.getEmail()));
    }
}
