package sg.edu.nus.iss.ws39server.controller;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.ws39server.model.Character;
import sg.edu.nus.iss.ws39server.model.Marvel;
import sg.edu.nus.iss.ws39server.repository.MarvelRepository;
import sg.edu.nus.iss.ws39server.service.MarvelService;

@RestController
// @CrossOrigin(origins="*")
@RequestMapping(path="/api")
public class MarvelRestController {

    @Autowired
    private MarvelService marvelSvc;

    @Autowired
    private MarvelRepository marvelRepo;
    
    @GetMapping(path="/characters")
    public ResponseEntity<String> getCharacters(@RequestParam String name, 
        @RequestParam(defaultValue = "20") Integer limit, 
        @RequestParam(defaultValue = "0") Integer offset) {
        
        List<Marvel> chars = this.marvelSvc.getCharacters(name, limit, offset);

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (Marvel c : chars) {
            arrBuilder.add(c.toJSON());
        }

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(arrBuilder.build().toString());
    }

    @GetMapping(path="/characters/{characterId}")
    public ResponseEntity<String> getCharDetails(@PathVariable Integer characterId) {

        Optional<Character> character = this.marvelSvc.getCharDetails(characterId);
        if (!character.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(character.get().toJSONIntoRedis().toString());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("error: character with %s cannot be found".formatted(characterId.toString()));
        }
    }

    @PostMapping(path="/characters/{characterId}")
    public ResponseEntity<String> postComments(@PathVariable Integer characterId, 
        @RequestBody String payload) {
            
        System.out.println("payload: " + payload);
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jObj = reader.readObject();
        String comments = jObj.getString("comments");

        try {
            this.marvelRepo.postComments(characterId, comments);
            JsonObject result = Json.createObjectBuilder()
                            .add("message", "post successful")
                            .build();

            return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsonObject result = Json.createObjectBuilder()
                            .add("error", "not successful")
                            .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(result.toString());
        }
    }

}
