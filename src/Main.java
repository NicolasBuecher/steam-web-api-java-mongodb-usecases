import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.achievementpercentages.GetGlobalAchievementPercentagesForApp;
import com.lukaspradel.steamapi.data.json.appnews.GetNewsForApp;
import com.lukaspradel.steamapi.data.json.friendslist.GetFriendList;
import com.lukaspradel.steamapi.data.json.getglobalstatsforgame.GetGlobalStatsForGame;
import com.lukaspradel.steamapi.data.json.getplayerbans.GetPlayerBans;
import com.lukaspradel.steamapi.data.json.getschemaforgame.GetSchemaForGame;
import com.lukaspradel.steamapi.data.json.isplayingsharedgame.IsPlayingSharedGame;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.data.json.playerachievements.GetPlayerAchievements;
import com.lukaspradel.steamapi.data.json.playerstats.GetUserStatsForGame;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.data.json.recentlyplayedgames.GetRecentlyPlayedGames;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.*;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import static java.util.Arrays.asList;

public class Main
{

    public static void main(String[] args)
    {

        /*************************
         * 1. Database Connexion *
         *************************/

        System.out.println("\n1. Database Connexion");

        // Create the URI containing the server address, the port and the authentication IDs
        MongoClientURI uri = new MongoClientURI("mongodb://IDENTIFIANT:PASSWORD@SERVER_ADRESS:PORT/?authSource=DATABASE");

        // Create the MongoDB client with the previous information
        MongoClient mongoClient = new MongoClient(uri);

        // Get the buecher database
        MongoDatabase db = mongoClient.getDatabase("DATABASE");


        /*********************
         * 2. Data Insertion *
         *********************/

        System.out.println("\n2. Data Insertion");

        // Create an english date format to parse date strings
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        // Use a try/catch block to handle parsing exceptions
        try {
            // Get restaurants collection and insert the following document inside
            // Create restaurants collection before if it doesn't exist
            db.getCollection("restaurants").insertOne(
                    new Document("address",
                            new Document()
                                    .append("street", "1 Avenue")
                                    .append("zipcode", "10075")
                                    .append("building", "1480")
                                    .append("coord", asList(-73.0, 40.0))
                    )
                    .append("borough", "Manhattan")
                    .append("cuisine", "Italian")
                    .append("grades", asList(
                            new Document()
                                    .append("date", format.parse("2014-10-01T00:00:00Z"))
                                    .append("grade", "A")
                                    .append("score", 11),
                            new Document()
                                    .append("date", format.parse("2014-01-16T00:00:00Z"))
                                    .append("grade", "B")
                                    .append("score", 17)))
                    .append("name", "Vella")
                    .append("restaurant_id", "41704620")
            );
            // Get restaurants collection and insert the following documents inside
            db.getCollection("restaurants").insertMany(asList(
                    // First document
                    new Document("address",
                            new Document()
                                    .append("street", "2 Avenue")
                                    .append("zipcode", "10075")
                                    .append("building", "1480")
                                    .append("coord", asList(-73.0, 40.0))
                    )
                    .append("borough", "Manhattan")
                    .append("cuisine", "Mexican")
                    .append("grades", asList(
                            new Document()
                                    .append("date", format.parse("2014-10-01T00:00:00Z"))
                                    .append("grade", "A")
                                    .append("score", 11),
                            new Document()
                                    .append("date", format.parse("2014-01-16T00:00:00Z"))
                                    .append("grade", "B")
                                    .append("score", 17)))
                    .append("name", "Mexico")
                    .append("restaurant_id", "41704621"),
                    // Second document
                    new Document("address",
                            new Document()
                                    .append("street", "3 Avenue")
                                    .append("zipcode", "10075")
                                    .append("building", "1480")
                                    .append("coord", asList(-73.0, 40.0))
                    )
                    .append("borough", "Manhattan")
                    .append("cuisine", "French")
                    .append("grades", asList(
                            new Document()
                                    .append("date", format.parse("2014-10-01T00:00:00Z"))
                                    .append("grade", "A")
                                    .append("score", 11),
                            new Document()
                                    .append("date", format.parse("2014-01-16T00:00:00Z"))
                                    .append("grade", "B")
                                    .append("score", 17)))
                    .append("name", "Paris")
                    .append("restaurant_id", "41704622")

            ));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get all documents from restaurants collection
        FindIterable<Document> iterable = db.getCollection("restaurants").find();

        // For each document found, print its content
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });


        /******************
         * 3. Data Update *
         ******************/

        System.out.println("\n3. Data Update");

        // Initialize a variable to store information about update queries results
        UpdateResult ur;

        // Update the first document with name equal to Mexico modifying the cuisine field and adding a lastModified field
        ur = db.getCollection("restaurants").updateOne(new Document("name", "Mexico"),
                new Document("$set", new Document("cuisine", "American (New)"))
                        .append("$currentDate", new Document("lastModified", true)));

        // Print the number of documents which have been updated (useless, but who cares ?)
        System.out.println("Number of updates : " + ur.getModifiedCount());

        // Update all documents with street equal to 1 Avenue and cusine equal to Italian modifying the cuisine and name fields and adding a lastModified field
        ur = db.getCollection("restaurants").updateMany(new Document("address.street", "1 Avenue").append("cuisine", "Italian"),
                new Document("$set", new Document("cuisine", "Spanish").append("name", "Tapas"))
                        .append("$currentDate", new Document("lastModified", true)));

        // Print the number of documents which have been updated
        System.out.println("Number of updates : " + ur.getModifiedCount());

        // For each document found, print its content
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });


        /********************
         * 4. Data deletion *
         ********************/

        System.out.println("\n4. Data Deletion");

        // Initialize a variable to store information about delete queries results
        DeleteResult dr;

        // Delete the first document with cuisine equal to Category To Be Determinated
        dr = db.getCollection("restaurants").deleteOne(new Document("cuisine", "Category To Be Determined"));

        // Print the number of documents which have been deleted (useless, but who cares ?)
        System.out.println("Number of deletions : " + dr.getDeletedCount());

        // Delete all the documents with cuisine equal to French
        dr = db.getCollection("restaurants").deleteMany(new Document("cuisine", "French"));

        // Print the number of documents which have been deleted
        System.out.println("Number of deletions : " + dr.getDeletedCount());

        // For each document found, print its content
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });

        // Delete all documents in restaurants collection
        dr = db.getCollection("restaurants").deleteMany(new Document());

        // Print the number of documents which have been deleted
        System.out.println("Number of deletions : " + dr.getDeletedCount());

        // For each document found, print its content (useless, but who cares ?)
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });

        // Drop the restaurants collection, including any indexes
        db.getCollection("restaurants").drop();


        /***************************
         * 5. SteamWebAPI Connexion *
         ***************************/

        System.out.println("\n5. SteamWebAPI Connexion");

        // Create a SteamWebApi client with the Steaw Web Api Key.
        SteamWebApiClient client = new SteamWebApiClient.SteamWebApiClientBuilder("YOUR-WEB-API-KEY").build();


        /********************
         * 6. GetNewsForApp *
         ********************/

        System.out.println("\n6. GetNewsForApp");

        // Create an instance of the necessary request : create a GetNewsForApp request with appid 570 (Dota 2)
        // First alternative
        GetNewsForAppRequest request = SteamWebApiRequestFactory.createGetNewsForAppRequest(570, 10, 500);
        // Second alternative
        GetNewsForAppRequest request2 = new GetNewsForAppRequest.GetNewsForAppRequestBuilder(570).count(10).maxLength(500).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetNewsForApp getNewsForApp = client.<GetNewsForApp> processRequest(request);
            GetNewsForApp getNewsForApp2 = client.<GetNewsForApp> processRequest(request2);

            // Print the result of the requests
            System.out.println("" + getNewsForApp.getAppnews().getNewsitems());
            System.out.println("" + getNewsForApp2.getAppnews().getNewsitems());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /********************************************
         * 7. GetGlobalAchievementPercentagesForApp *
         ********************************************/

        System.out.println("\n7. GetGlobalAchievementPercentageForApp");

        // Create an instance of the necessary request : create a GetGlobalAchievementPercentagesForApp request with appid 440 (Team Fortress 2)
        // First alternative
        GetGlobalAchievementPercentagesForAppRequest request3 = SteamWebApiRequestFactory.createGetGlobalAchievementPercentagesForAppRequest(440);
        // Second alternative
        GetGlobalAchievementPercentagesForAppRequest request4 = new GetGlobalAchievementPercentagesForAppRequest.GetGlobalAchievementPercentagesForAppRequestBuilder(440).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetGlobalAchievementPercentagesForApp getGlobalAchievementPercentagesForApp = client.<GetGlobalAchievementPercentagesForApp> processRequest(request3);
            GetGlobalAchievementPercentagesForApp getGlobalAchievementPercentagesForApp2 = client.<GetGlobalAchievementPercentagesForApp> processRequest(request4);

            // Print the result of the requests
            System.out.println("" + getGlobalAchievementPercentagesForApp.getAchievementpercentages().getAchievements());
            System.out.println("" + getGlobalAchievementPercentagesForApp2.getAchievementpercentages().getAchievements());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /****************************
         * 8. GetGlobalStatsForGame *
         ****************************/

        System.out.println("\n8. GetGlobalStatsForGame");

        // Create an instance of the necessary request : create a GetGlobalStatsForGame request with appid 17740 (Empires Mod) and the list of achievement names wanted
        // First alternative
        GetGlobalStatsForGameRequest request5 = SteamWebApiRequestFactory.createGetGlobalStatsForGameRequest(17740, 2, asList("EMPIRES_JUST_IN_TIME", "EMPIRES_PACIFIST")); // Achievement names taken from last request using appid 17740
        // Second alternative
        GetGlobalStatsForGameRequest request6 = new GetGlobalStatsForGameRequest.GetGlobalStatsForGameRequestBuilder(17740, 1, asList("global.map.emp_isle")).buildRequest(); // Achievement names taken from Steam Web API Wiki

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            // ERROR 400 BAD REQUEST, nothing to do here, I tried everything
            GetGlobalStatsForGame getGlobalStatsForGame = client.<GetGlobalStatsForGame> processRequest(request5);
            GetGlobalStatsForGame getGlobalStatsForGame2 = client.<GetGlobalStatsForGame> processRequest(request6);

            // Print the result of the requests
            System.out.println("" + getGlobalStatsForGame);
            System.out.println("" + getGlobalStatsForGame2);

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /*************************
         * 9. GetPlayerSummaries *
         *************************/

        System.out.println("\n9. GetPlayerSummaries");

        // Create an instance of the necessary request : create a GetPlayerSummaries request with steamids from Robin Walker and Nicolas Buecher
        // First alternative
        GetPlayerSummariesRequest request7 = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(asList("76561197960435530", "76561198184078213"));
        // Second alternative
        GetPlayerSummariesRequest request8 = new GetPlayerSummariesRequest.GetPlayerSummariesRequestBuilder(asList("76561197960435530", "76561198184078213")).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetPlayerSummaries getPlayerSummaries = client.<GetPlayerSummaries> processRequest(request7);
            GetPlayerSummaries getPlayerSummaries2 = client.<GetPlayerSummaries> processRequest(request8);

            // Print the result of the requests
            System.out.println("" + getPlayerSummaries.getResponse().getPlayers());
            System.out.println("" + getPlayerSummaries2.getResponse().getPlayers());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /*********************
         * 10. GetFriendList *
         *********************/

        System.out.println("\n10. GetFriendList");

        // Create an instance of the necessary request : create a GetFriendList request with steamid from Robin Walker
        // First alternative
        GetFriendListRequest request9 = SteamWebApiRequestFactory.createGetFriendListRequest("76561197960435530", GetFriendListRequest.Relationship.FRIEND);
        // Second alternative
        GetFriendListRequest request10 = new GetFriendListRequest.GetFriendListRequestBuilder("76561197960435530").relationship(GetFriendListRequest.Relationship.FRIEND).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetFriendList getFriendList = client.<GetFriendList> processRequest(request9);
            GetFriendList getFriendList2 = client.<GetFriendList> processRequest(request10);

            // Print the result of the requests
            System.out.println("" + getFriendList.getFriendslist().getFriends());
            System.out.println("" + getFriendList2.getFriendslist().getFriends());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /*****************************
         * 11. GetPlayerAchievements *
         *****************************/

        System.out.println("\n11. GetPlayerAchievements");

        // Create an instance of the necessary request : create a GetPlayerAchievements request with steamid from Robin Walker and appid 440 (Team Fortress 2)
        // First alternative
        GetPlayerAchievementsRequest request11 = SteamWebApiRequestFactory.createGetPlayerAchievementsRequest(440, "76561197960435530", "english");
        // Second alternative
        GetPlayerAchievementsRequest request12 = new GetPlayerAchievementsRequest.GetPlayerAchievementsRequestBuilder("76561197960435530", 440).language("french").buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetPlayerAchievements getPlayerAchievements = client.<GetPlayerAchievements> processRequest(request11);
            GetPlayerAchievements getPlayerAchievements2 = client.<GetPlayerAchievements> processRequest(request12);

            // Print the result of the requests
            System.out.println("" + getPlayerAchievements.getPlayerstats().getAchievements());
            System.out.println("" + getPlayerAchievements2.getPlayerstats().getAchievements());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /***************************
         * 12. GetUserStatsForGame *
         ***************************/

        System.out.println("\n12. GetUserStatsForGame");

        // Create an instance of the necessary request : create a GetUserStatsForGame request with steamid from Robin Walker and appid 440 (Team Fortress 2)
        // First alternative
        GetUserStatsForGameRequest request13 = SteamWebApiRequestFactory.createGetUserStatsForGameRequest(440, "76561197960435530", "english");
        // Second alternative
        GetUserStatsForGameRequest request14 = new GetUserStatsForGameRequest.GetUserStatsForGameRequestBuilder("76561197960435530", 440).language("french").buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetUserStatsForGame getUserStatsForGame = client.<GetUserStatsForGame> processRequest(request13);
            GetUserStatsForGame getUserStatsForGame2 = client.<GetUserStatsForGame> processRequest(request14);

            // Print the result of the requests
            System.out.println("" + getUserStatsForGame.getPlayerstats().getAchievements());
            System.out.println("" + getUserStatsForGame2.getPlayerstats().getAchievements());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /*********************
         * 13. GetOwnedGames *
         *********************/

        System.out.println("\n13. GetOwnedGames");

        // Create an instance of the necessary request : create a GetOwnedGames request with steamid from Robin Walker
        // First alternative
        GetOwnedGamesRequest request15 = SteamWebApiRequestFactory.createGetOwnedGamesRequest("76561197960435530", true, true, new ArrayList<Integer>());
        // Second alternative
        GetOwnedGamesRequest request16 = new GetOwnedGamesRequest.GetOwnedGamesRequestBuilder("76561197960435530").includeAppInfo(true).includePlayedFreeGames(true).buildRequest(); // appIdsFilter also exists and allows to select a few games with their ids

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetOwnedGames getOwnedGames = client.<GetOwnedGames> processRequest(request15);
            GetOwnedGames getOwnedGames2 = client.<GetOwnedGames> processRequest(request16);

            // Print the result of the requests
            System.out.println("" + getOwnedGames.getResponse().getGames());
            System.out.println("" + getOwnedGames2.getResponse().getGames());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /******************************
         * 14. GetRecentlyPlayedGames *
         ******************************/

        System.out.println("\n14. GetRecentlyPlayedGames");

        // Create an instance of the necessary request : create a GetRecentlyPlayedGames request with steamid from Robin Walker
        // First alternative
        GetRecentlyPlayedGamesRequest request17 = SteamWebApiRequestFactory.createGetRecentlyPlayedGamesRequest("76561197960435530", 5);
        // Second alternative
        GetRecentlyPlayedGamesRequest request18 = new GetRecentlyPlayedGamesRequest.GetRecentlyPlayedGamesRequestBuilder("76561197960435530").count(5).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetRecentlyPlayedGames getRecentlyPlayedGames = client.<GetRecentlyPlayedGames> processRequest(request17);
            GetRecentlyPlayedGames getRecentlyPlayedGames2 = client.<GetRecentlyPlayedGames> processRequest(request18);

            // Print the result of the requests
            System.out.println("" + getRecentlyPlayedGames.getResponse().getGames());
            System.out.println("" + getRecentlyPlayedGames2.getResponse().getGames());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /***************************
         * 15. IsPlayingSharedGame *
         ***************************/

        System.out.println("\n15. IsPlayingSharedGame");

        // Create an instance of the necessary request : create a IsPlayingSharedGame request with steamid from Robin Walker and appId 240 (Counter-Strike: Source)
        // First alternative
        IsPlayingSharedGameRequest request19 = SteamWebApiRequestFactory.createIsPlayingSharedGameRequest("76561197960435530", 240);
        // Second alternative
        IsPlayingSharedGameRequest request20 = new IsPlayingSharedGameRequest.IsPlayingSharedGameRequestBuilder("76561197960435530", 240).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            IsPlayingSharedGame isPlayingSharedGame = client.<IsPlayingSharedGame> processRequest(request19);
            IsPlayingSharedGame isPlayingSharedGame2 = client.<IsPlayingSharedGame> processRequest(request20);

            // Print the result of the requests, 0 means that the game is not borrowed or the borrower currently doesn't play this game
            System.out.println("" + isPlayingSharedGame.getResponse().getLenderSteamid());
            System.out.println("" + isPlayingSharedGame2.getResponse().getLenderSteamid());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /************************
         * 16. GetSchemaForGame *
         ************************/

        System.out.println("\n16. GetSchemaForGame");

        // Create an instance of the necessary request : create a GetSchemaForGame request with appId 218620 (PAYDAY 2)
        // First alternative
        GetSchemaForGameRequest request21 = SteamWebApiRequestFactory.createGetSchemaForGameRequest(218620);
        // Second alternative
        GetSchemaForGameRequest request22 = new GetSchemaForGameRequest.GetSchemaForGameRequestBuilder(218620).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetSchemaForGame getSchemaForGame = client.<GetSchemaForGame> processRequest(request21);
            GetSchemaForGame getSchemaForGame2 = client.<GetSchemaForGame> processRequest(request22);

            // Print the result of the requests, a lot of possibilities here (game name, version, achievements, stats...)
            System.out.println("" + getSchemaForGame.getGame().getAvailableGameStats().getAchievements());
            System.out.println("" + getSchemaForGame2.getGame().getAvailableGameStats().getStats());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /*********************
         * 17. GetPlayerBans *
         *********************/

        System.out.println("\n17. GetPlayerBans");

        // Create an instance of the necessary request : create a GetPlayerBans request with steamids from Robin Walker and Nicolas Buecher
        // First alternative
        GetPlayerBansRequest request23 = SteamWebApiRequestFactory.createGetPlayerBansRequest(asList("76561197960435530", "76561198184078213"));
        // Second alternative
        GetPlayerBansRequest request24 = new GetPlayerBansRequest.GetPlayerBansRequestBuilder(asList("76561197960435530", "76561198184078213")).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {

            // Finally say to the SteamWebAPI client to process requests
            GetPlayerBans getPlayerBans = client.<GetPlayerBans> processRequest(request23);
            GetPlayerBans getPlayerBans2 = client.<GetPlayerBans> processRequest(request24);

            // Print the result of the requests
            System.out.println("" + getPlayerBans.getPlayers());
            System.out.println("" + getPlayerBans2.getPlayers());

        } catch (SteamApiException e) {
            e.printStackTrace();
        }


        /**********************
         * 18. Example of use *
         **********************/

        System.out.println("18. Example of use");
        System.out.println("How many games in common do I have with my friends ?");

        // Créer un document à partir des données récoltées de Steam
        // Exemples de création de document :
        // new Document(<clé>, <valeur>);
        // new Document(<clé>, new Document(<clé2>, <valeur2>);
        // new Document(<clé>, <valeur>).append(<clé2>, <valeur2>);
        // new Document(<clé>, new Document(<clé2>, <valeur2>).append(<clé2>, <valeur3>)).append(<clé4>, <valeur4>);

        // Example : How many games do you have in common with your friends ?

        System.out.println("    A. Initialization");

        // Robin Walker Steam ID : 76561197960435530 (301 friends)
        // Unknown player : 76561197960435531 (24 friends)
        String STEAM_ID = "76561197960435531"; // The Steam ID of the player you want take a look

        ArrayList<String> steamIds = new ArrayList<>();
        ArrayList<Integer> gameIds = new ArrayList<>();
        ArrayList<Document> games = new ArrayList<>();
        ArrayList<Document> documents = new ArrayList<>();

        System.out.println("    B. GetFriendListRequest");

        // Create a GetFriendList request with STEAM_ID
        GetFriendListRequest request25 = new GetFriendListRequest.GetFriendListRequestBuilder(STEAM_ID).relationship(GetFriendListRequest.Relationship.FRIEND).buildRequest();

        // Surround by a try/catch block to handle SteamAPIException
        try {
            // Process GetFriendList request
            GetFriendList getFriendList = client.<GetFriendList> processRequest(request25);

            // Store the number of friends
            int numberOfFriends = getFriendList.getFriendslist().getFriends().size();

            // Check if there is friends. If not, exit program.
            if (numberOfFriends == 0)
            {
                System.out.println("You got no friends.");
                System.out.println("    C. THE END");
                System.exit(0);
            }
            else
            {
                System.out.println("    C. Collect Steam IDs");

                // For each friend, collect steamid
                for (int i = 0; i < numberOfFriends; i++)
                {
                    steamIds.add(getFriendList.getFriendslist().getFriends().get(i).getSteamid());
                }

                // Eventually add the main steamid in order to get its game list too
                steamIds.add(STEAM_ID);

                System.out.println("    D. GetOwnedGames, Collect App IDs, Create Documents");

                // For each steamid collected, get the associated game IDs and create MongoDB documents
                for (int i = 0; i < steamIds.size(); i++)
                {
                    // Create a GetOwnedGames request with current steamid and process request
                    GetOwnedGamesRequest request26 = new GetOwnedGamesRequest.GetOwnedGamesRequestBuilder(steamIds.get(i)).buildRequest();
                    GetOwnedGames getOwnedGames = client.<GetOwnedGames> processRequest(request26);

                    // Store the number of games of the current player (getGameCount() should be faster thant size() but sometimes it can be null)
                    int numberOfOwnedGames = getOwnedGames.getResponse().getGameCount()!=null?getOwnedGames.getResponse().getGameCount():getOwnedGames.getResponse().getGames().size();

                    // For each game owned, create a document with its appId and its name, then add it to an ArrayList of Document
                    for (int j = 0; j < numberOfOwnedGames; j++)
                    {
                        games.add(new Document("id", getOwnedGames.getResponse().getGames().get(j).getAppid()).append("name", getOwnedGames.getResponse().getGames().get(j).getName()));
                    }

                    // Once all the owned games are transformed in documents, create a document with the steamid and add them to it
                    documents.add(new Document("steamId", steamIds.get(i)).append("games", games.clone()));

                    // Clear the list to use it again
                    games.clear();

                    System.out.println(i + " : " + documents.get(i));
                }
            }

        } catch (SteamApiException e) {
            e.printStackTrace();
        }

        System.out.println("    E. Insert data in MongoDB");

        // Insert all the documents created in MongoDB
        db.getCollection("players").insertMany(documents);

        // Get all documents from players collection
        FindIterable<Document> iterable2 = db.getCollection("players").find();

        // For each document found, print its content
        iterable2.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });

        System.out.println("    F. Queries");

        // Get the document associated to the main steamid and remove it from the collection
        // Then get the game list associated and store it
        games = (ArrayList) db.getCollection("players").findOneAndDelete(Filters.eq("steamId", STEAM_ID)).get("games");

        // For each game, extract its appId
        for (int i = 0; i < games.size(); i++)
        {
            gameIds.add(games.get(i).getInteger("id"));
        }

        // Main aggregate query, it works by stages represented by each element of the list parameter of the aggregate method
        AggregateIterable<Document> iterable3 = db.getCollection("players").aggregate(asList(
                new Document("$match", new Document("games.id", new Document("$in", gameIds))),                     // First, eliminate as many documents as possible keeping only documents with at least one game id from the main list
                new Document("$project", new Document("gameIds", "$games.id").append("_id", 0)),                    // Then, include a new field called gameIds containing an array of integers (appIds) and remove all other fields of the response
                new Document("$unwind", "$gameIds"),                                                                // Break the gameIds array and create a new Document object for each element of the array
                new Document("$match", new Document("gameIds", new Document("$in", gameIds))),                      // Only keep documents with one game id from the main list
                new Document("$group", new Document("_id", "$gameIds").append("count", new Document("$sum", 1))),   // Group the result by gameIds and count their occurrences
                new Document("$sort", new Document("_id", 1))                                                       // Finally, sort the results by ascending ids
        ));
        // NOTE : j'aimerais utiliser les noms des jeux dans le résultat mais on ne récupère que des "null" depuis l'API Steam

        // For each document created, print its content
        iterable3.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                System.out.println(document);
            }
        });

        System.out.println("    G. Remove data from MongoDB, Drop Collection");

        // Delete all documents in players collection
        dr = db.getCollection("players").deleteMany(new Document());

        // Print the number of documents which have been deleted
        System.out.println("Number of deletions : " + dr.getDeletedCount());

        // Drop the players collection, including any indexes
        db.getCollection("players").drop();

        System.out.println("    H. THE END");

    }
}
