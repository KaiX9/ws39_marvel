package sg.edu.nus.iss.ws39server.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.ws39server.model.Character;
import sg.edu.nus.iss.ws39server.model.Marvel;
import sg.edu.nus.iss.ws39server.repository.MarvelRepository;

@Service
public class MarvelService {
    
    @Autowired
    private MarvelRepository marvelRepo;

    @Value("${marvel.key}")
    private String marvelKey;

    @Value("${private.key}")
    private String privateKey;

    public String timestamp = Instant.now().toString();
    public List<String> comments = new ArrayList<String>();

    public List<Marvel> getCharacters(String name, Integer limit, Integer offset) {

        String marvelUrl = UriComponentsBuilder
            .fromUriString("https://gateway.marvel.com:443/v1/public/characters")
            .queryParam("nameStartsWith", name)
            .queryParam("limit", limit)
            .queryParam("offset", offset)
            .queryParam("ts", timestamp)
            .queryParam("apikey", marvelKey)
            .queryParam("hash", this.getHashKey())
            .toUriString();
        
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.getForEntity(marvelUrl, String.class);
        Marvel m = Marvel.createFromJson(resp.getBody());
        List<Marvel> chars = m.getChars();
        return chars;
    }

    public Optional<Character> getCharDetails(Integer id) {

        Optional<Character> character = this.marvelRepo.getChar(id);
        if (character.isEmpty()) {
            String charUrl = UriComponentsBuilder
            .fromUriString("https://gateway.marvel.com:443/v1/public/characters/{id}")
            .queryParam("ts", timestamp)
            .queryParam("apikey", marvelKey)
            .queryParam("hash", this.getHashKey())
            .buildAndExpand(id)
            .toUriString();

            RestTemplate template = new RestTemplate();
            try {
                ResponseEntity<String> resp = template.getForEntity(charUrl, String.class);
                Character c = Character.createFromJson(resp.getBody());
                c.setComments(this.marvelRepo.getComments(id));
                this.marvelRepo.saveChar(c);
                System.out.println("get from api");
                return Optional.of(c);
            } catch (HttpClientErrorException ex) {
                ex.printStackTrace();
            }
        }
        return character;
            
    }

    public String getHashKey() {

        String input = timestamp + privateKey + marvelKey;
        String md5Hex = DigestUtils.md5Hex(input);
        System.out.println("Hash: " + md5Hex);

        return md5Hex;

    }

}
