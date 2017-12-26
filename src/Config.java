import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;

public class Config{
    private class Databse{
        private String driver;
        private String url;
        private String username;
        private String password;
    }
    private Databse database;
    private Integer target_port;
    private Integer server_port;
    private String admin;

    public String getDriver() {
        return this.database.driver;
    }

    public String getUrl() {
        return this.database.url;
    }

    public String getUsername() {
        return this.database.username;
    }

    public String getPassword() {
        return this.database.password;
    }

    public Integer getTarget_port() {
        return target_port;
    }

    public Integer getServer_port() {
        return server_port;
    }

    public String getAdmin() {
        return admin;
    }

    private Config(){
        this.database = new Databse();
    }

    public static Config load(String path){
        Config config = new Config();
        try{
            FileReader fileReader = new FileReader(path);
            JsonReader jsonReader = new JsonReader(fileReader);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            config = gson.fromJson(jsonReader, config.getClass());
            return config;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}