package sg.edu.nus.iss.ws39server.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Marvel {
    
    private Integer id;
    private String name;

    private List<Marvel> chars = new ArrayList<>();
    
    public List<Marvel> getChars() {
        return chars;
    }
    public void setChars(List<Marvel> chars) {
        this.chars = chars;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Marvel [id=" + id + ", name=" + name + ", chars=" + chars + "]";
    }

    public static Marvel createForArr(JsonObject jsonObj) {
        Marvel m = new Marvel();
        m.setId(jsonObj.getInt("id"));
        m.setName(jsonObj.getString("name"));
        return m;
    }

    public static Marvel createFromJson(String json) {
        Marvel m = new Marvel();
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject jObj = reader.readObject();
        JsonObject dataObj = jObj.getJsonObject("data");
        JsonArray resultsArray = dataObj.getJsonArray("results");
        m.setChars(resultsArray.stream()
            .map(c -> (JsonObject)c)
            .map(c -> createForArr(c))
            .toList());
        
        return m;
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
            .add("id", getId())
            .add("name", getName())
            .build();
    }

}
