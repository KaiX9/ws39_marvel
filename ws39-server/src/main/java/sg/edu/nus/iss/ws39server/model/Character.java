package sg.edu.nus.iss.ws39server.model;

import java.io.StringReader;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class Character {
    
    private Integer id;
    private String name;
    private String description;
    private String imagePath;
    private List<String> comments;

    public List<String> getComments() {
        return comments;
    }
    public void setComments(List<String> comments) {
        this.comments = comments;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Character [id=" + id + ", name=" + name + ", description=" + description + ", imagePath=" + imagePath
                + ", comments=" + comments + "]";
    }

    public static Character createFromJson(String json) {
        Character c = new Character();
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject jObj = reader.readObject();
        JsonObject dataObj = jObj.getJsonObject("data");
        JsonArray resultsArray = dataObj.getJsonArray("results");
        for (JsonValue jv : resultsArray) {
            JsonObject resultObj = (JsonObject) jv;
            c.setId(resultObj.getInt("id"));
            c.setName(resultObj.getString("name"));
            c.setDescription(resultObj.getString("description"));
            JsonObject thumbnailObj = resultObj.getJsonObject("thumbnail");
            c.setImagePath(thumbnailObj.getString("path") + "." + thumbnailObj.getString("extension"));
        }    
        return c;
    }

    public static String createComments(JsonObject jObj) {
        String comment = jObj.getString("comment");
        return comment;
    }

    public static Character createFromJsonInRedis(String json) {
        Character c = new Character();
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject jObj = reader.readObject();
        c.setId(jObj.getInt("id"));
        c.setName(jObj.getString("name"));
        c.setDescription(jObj.getString("description"));
        c.setImagePath(jObj.getString("imagePath"));
        JsonArray commentsArray = jObj.getJsonArray("comments");
        c.setComments(commentsArray.stream()
            .map(com -> (JsonObject)com)
            .map(com -> createComments(com))
            .toList());
        return c;
    }

    public JsonObject toJSONIntoRedis() {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (String c : comments) {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("comment", c);
            arrayBuilder.add(objBuilder.build());
        }

        return Json.createObjectBuilder()
            .add("id", getId())
            .add("name", getName())
            .add("description", getDescription())
            .add("imagePath", getImagePath())
            .add("comments", arrayBuilder.build())
            .build();
    }
    
}
