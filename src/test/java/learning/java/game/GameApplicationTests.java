package learning.java.game;

import learning.java.game.controller.GameControllerSingle;
import learning.java.game.dao.Dao;
import learning.java.game.dao.GamesDao;
import learning.java.game.dao.PlayersDao;
import learning.java.game.model.Figure;
import learning.java.game.model.Game;
import learning.java.game.model.Player;
import learning.java.game.model.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GamesDao dao;

    @Autowired
    private PlayersDao playersDao;

    @Test
    void testPostGame() throws Exception {
        String id = playersDao.create(new Player("player")).toString();
        String request = readFromJson("/post/create/create_game_request.json")
                .replace("${id}", id);
        String response = readFromJson("/post/create/create_game_response.json")
                .replace("${id}", id);

        mockMvcPostRequest("/game", request, response, status().isOk());
    }

    @Test
    void testFailIfPostRequestWithIncorrectBody() throws Exception {
        String url = "/game";
        String id = playersDao.create(new Player("player")).toString();
        String request1 = readFromJson("/post/create/create_incorrect_request1.json")
                .replace("${id}", id);
        String request2 = readFromJson("/post/create/create_incorrect_request2.json")
                .replace("${id}", id);
        String request4 = readFromJson("/post/create/create_incorrect_request4.json")
                .replace("${id}", UUID.randomUUID().toString());
        String request3 = readFromJson("/post/create/create_incorrect_request3.json");
        String response = readFromJson("/bad.response/bad_request.json");
        ResultMatcher badRequest = status().isBadRequest();

        mockMvcPostRequest(url, request1, response, badRequest);
        mockMvcPostRequest(url, request2, response, badRequest);
        mockMvcPostRequest(url, request3, response, badRequest);
        try {
            mockMvcPostRequest(url, request4, response, badRequest);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGameFromId() throws Exception {
        String id = createGameAndReturnId(dao);
        String urlTemplate = "/game/" + id;
        String response = readFromJson("/get/get_game_response.json")
                .replace("${id}", id);

        mockMvcGetRequest(urlTemplate, response, status().isOk());
    }

    @Test
    void testFailTryGetGameWithIncorrectId() throws Exception {
        String id = createGameAndReturnId(dao);
        String urlTemplateCorrect = "/game/" + id;
        String urlTemplateIncorrect1 = "/game/" + UUID.randomUUID();
        String urlTemplateIncorrect2 = "/game/" + "Incorrect path";

        String responseCorrect = readFromJson("/get/get_game_response.json")
                .replace("${id}", id);
        String responseIncorrect1 = readFromJson("/bad.response/not_found.json");
        String responseIncorrect2 = readFromJson("/bad.response/bad_request.json");

        mockMvcGetRequest(urlTemplateCorrect, responseCorrect,status().isOk());
        mockMvcGetRequest(urlTemplateIncorrect1, responseIncorrect1, status().isNotFound());
        mockMvcGetRequest(urlTemplateIncorrect2, responseIncorrect2, status().isBadRequest());
    }

    @Test
    void testTurn() throws Exception {
        //create game and check figure into games field on point(2,2)
        Game game = createGameXO(dao);
        Point point = new Point(2, 2);
        Figure figureOld = game.getField().getFigure(point);
        assertNull(figureOld);

        //make post request and check response with out games field,
        //because when we set figure into field GameService set one more random figure
        String id = game.getId().toString();
        String url = "/game/" + id + "/turn";
        String request = readFromJson("/post/turn/turn_game_request.json");
        String response = readFromJson("/post/turn/turn_game_response.json")
                .replace("${id}", id)
                .replace("${id1}", game.getPlayer1().getPlayer().getId().toString());

        mockMvcPostRequest(url, request, response, status().isOk());

        //repeatedly check figure on point(2,2)
        Game actualGame = dao.read(game.getId());
        Figure figureActual = actualGame.getField().getFigure(point);
        assertEquals(Figure.X, figureActual);
    }

    @Test
    void testFailWhenTryTurnWIthIncorrectGameId() throws Exception {
        Game game = createGameXO(dao);
        String id = game.getId().toString();
        String url = "/game/" + id + "/turn";
        String urlBad1 = "/game/" + UUID.randomUUID() + "/turn";
        String urlBad2 = "/game/" + "Incorrect path" + "/turn";

        String request = readFromJson("/post/turn/turn_game_request.json");
        String response = readFromJson("/post/turn/turn_game_response.json")
                .replace("${id}", id)
                .replace("${id1}", game.getPlayer1().getPlayer().getId().toString());
        String responseBad1 = readFromJson("/bad.response/not_found.json");
        String responseBad2= readFromJson("/bad.response/bad_request.json");

        mockMvcPostRequest(url, request, response, status().isOk());
        mockMvcPostRequest(urlBad1, request, responseBad1, status().isNotFound());
        mockMvcPostRequest(urlBad2, request, responseBad2, status().isBadRequest());
    }

    @Test
    void testFailWhenTryTurnWIthIncorrectBodyRequest() throws Exception {
        String id = createGameAndReturnId(dao);
        String url = "/game/" + id + "/turn";

        String requestBad1 = readFromJson("/post/turn/turn_incorrect_request1.json");
        String requestBad2 = readFromJson("/post/turn/turn_incorrect_request2.json");
        String requestBad3 = readFromJson("/post/turn/turn_incorrect_request3.json");
        String response = readFromJson("/bad.response/bad_request.json");
        ResultMatcher badRequest = status().isBadRequest();

        mockMvcPostRequest(url, requestBad1, response, badRequest);
        mockMvcPostRequest(url, requestBad2, response, badRequest);
        mockMvcPostRequest(url, requestBad3, response, badRequest);

    }

    //further are private methods

    private Game createGameXO(Dao<Game, UUID> dao)  {
        GameControllerSingle controllerSingle = new GameControllerSingle();
        Game game = controllerSingle.newGame(Figure.X, new Player("player"), null);
        playersDao.create(game.getPlayer1().getPlayer());
        dao.create(game);
        return game;
    }


    private String readFromJson(String json)
            throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(json);
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    private void mockMvcGetRequest(String urlTemplate,
                                   String response,
                                   ResultMatcher resultMatcher) throws Exception {

        mockMvc.perform(get(urlTemplate))
                .andExpect(resultMatcher)
                .andExpect(content().json(response));
    }

    private String createGameAndReturnId(Dao<Game, UUID> dao)  {
        Game expectedGame = createGameXO(dao);
        return expectedGame.getId().toString();
    }

    private MvcResult mockMvcPostRequest(String urlTemplate,
                                         String request,
                                         String response,
                                         ResultMatcher resultMatcher) throws Exception {

        return  mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(resultMatcher)
                .andExpect(content().json(response))
                .andReturn();
    }

}
