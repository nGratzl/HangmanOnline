package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.hangman.Hangman;
import org.jboss.resteasy.reactive.RestForm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Path("/api")
public class GreetingResource {
    @Inject
    public Hangman hangman;
    private String html = """
             <!DOCTYPE html>
            <html>
            <head>
            <title>Page Title</title>
            </head>
            <body style="text-align:center">
            
            <h1>Hangman</h1>
            <br>
            <h2>Game</h2>
            <div id="inputdiv" style="border:1px solid black;margin:10px;padding:10px">
                <form id="inputform" action="http://localhost:8080/api/hello" method="POST">
                    <label for="finput">Input:</label>
                    <input type="text" id="finput" name="finput">
                    <input type="submit" value="Submit">
                </form>
            </div>
            <h2>Settings</h2>
            <div style="border:1px solid black;margin:10px;padding:10px">
               <form action="http://localhost:8080/api/hello" method="POST">
                <label for="fdifficulty">Difficulty:    </label>
                <input type="number" id="fdifficulty" name="fdifficulty">
                <br>
                <hr>
                <label for="ftries">Tries:</label>
                <input type="number" id="ftries" name="ftries">
                <input type="submit" value="New game">
               </form>
            </div>
            
            </body>
            </html>
            """;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/hello")
    public String hello() {
        Document parse = Jsoup.parse(html);
        if(!hangman.isGameStarted()) {
            parse.body().getElementById("inputform").remove();
            return parse.html();
        }
        return html;
    }

    @POST
    @Path("/hello")
    @Produces(MediaType.TEXT_HTML)
    public String input(@RestForm("finput") String input, @RestForm("fdifficulty") int difficulty, @RestForm("ftries") int tries) {
        if(input != null) {
            System.out.println(hangman.getNextUserCharacter(input));
            hangman.updateGame(input.charAt(0));

        } else {
            Hangman.chooseDifficultyMode(difficulty);
            Hangman.scanHowManyFailedTriesMax(tries);
            Hangman.chooseRandomWord();
        }
        return buildHtml();
    }
    private String buildHtml() {
        Document parse = Jsoup.parse(html);
        if(Hangman.allCharactersCorrect()){
            parse.body().getElementById("inputform").remove();
            parse.body().getElementById("inputdiv").prepend("Gewonnen! Das Wort war " + Hangman.returnTargetWord());
            return parse.html();
        }
        if(Hangman.checkIfGameLost()){
            parse.body().getElementById("inputform").remove();
            parse.body().getElementById("inputdiv").prepend("Verloren! Das Wort war " + Hangman.returnTargetWord());
            return parse.html();
        }
        parse.body().getElementById("inputform").prepend("<p>" + hangman.generateOutput() + "</p>");
        return parse.html();
    }

}
