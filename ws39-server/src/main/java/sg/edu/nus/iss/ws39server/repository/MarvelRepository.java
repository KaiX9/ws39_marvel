package sg.edu.nus.iss.ws39server.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.ws39server.model.Character;

@Repository
public class MarvelRepository {
    
    @Autowired @Qualifier("marvel")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveChar(Character character) {
        this.redisTemplate.opsForValue().set(character.getId().toString(), 
            character.toJSONIntoRedis().toString(), 60, TimeUnit.MINUTES);
    }

    public Optional<Character> getChar(Integer id) {
        String json = this.redisTemplate.opsForValue().get(id.toString());
        System.out.println("json: " + json);
        if ((null == json || json.trim().length() <= 0)) {
            return Optional.empty();
        }
        return Optional.of(Character.createFromJsonInRedis(json));
    }

    public void postComments(Integer id, String comments) {
        Document doc = new Document();
        doc.append("id", id);
        doc.append("comments", comments);
        doc.append("timestamp", LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        this.mongoTemplate.insert(doc, "comments");
    }

    public List<String> getComments(Integer id) {
        Query q = new Query();
        q.addCriteria(Criteria.where("id").is(id));
        q.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        q.limit(10);

        return mongoTemplate.find(q, Document.class, "comments")
                            .stream()
                            .map(d -> d.getString("comments"))
                            .toList();
    }

}
