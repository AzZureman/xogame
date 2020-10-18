package learning.java.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class GameExceptionHandler {

    //    @ExceptionHandler(value = {GameOverException.class})
//    public ResponseEntity<Object> gameOverExceptionHandler(GameOverException ex) {
//        GameOverException gameEx = (GameOverException) ex;
//        return new ResponseEntity<>(new GameOverMessage(gameEx.getGame()), HttpStatus.OK);
//    }
    @ExceptionHandler(value = {GameOverException.class})
    public ResponseEntity<Object> handleGameOverExceptions(Exception ex){

        HttpStatus badRequest = HttpStatus.NOT_FOUND;

        GameOverException gameEx = (GameOverException) ex;
        GameOverMessage message = new GameOverMessage(gameEx.getGame());

        return new ResponseEntity<>(message, badRequest);
    }

}
