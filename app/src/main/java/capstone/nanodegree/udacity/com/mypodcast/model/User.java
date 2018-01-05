package capstone.nanodegree.udacity.com.mypodcast.model;

import java.util.HashMap;

/**
 * Created by jem001 on 26/12/2017.
 */

public class User {
    private String name;
    private String email;
    private HashMap<String,Object> timestampJoined;

    public User() {
    }

    public User(String name, String email, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public void setTimestampJoined(HashMap<String, Object> timestampJoined) {
        this.timestampJoined = timestampJoined;
    }
}
