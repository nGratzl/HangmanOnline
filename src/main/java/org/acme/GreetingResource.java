package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.hangman.Hangman;
import org.jboss.resteasy.reactive.RestForm;

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
            <body>
            
            <h1>My First Heading</h1>
            
             <form action="http://localhost:8080/api/hello" method="POST">
              <label for="finput">Input:</label>
              <input type="text" id="finput" name="finput">
              <input type="submit" value="Submit">
             
            </form>
            
            </body>
            </html>
            """;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/hello")
    public String hello() {
        return html;
    }

    @POST
    @Path("/hello")
    @Produces(MediaType.TEXT_HTML)
    public String input(@RestForm("finput") String input) {
        System.out.println(hangman.getNextUserCharacter(input));
        return html;
    }

}
