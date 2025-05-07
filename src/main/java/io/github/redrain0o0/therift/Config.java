package io.github.redrain0o0.therift;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    // Config setup borrowed from https://github.com/Reimnop/Discord4Fabric/blob/main/src/main/java/me/reimnop/d4f/Config.java

    public String token = "";
    public String webhookMiku = "";
    public String webhookRad = "";
    public String channelIdMiku = "";
    public String channelIdRad = "";
    public String serverName = "";
    public String serverIconUrl = "";

    public void writeConfig(File file) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("webhookMiku", webhookMiku);
        jsonObject.addProperty("webhookRad", webhookRad);
        jsonObject.addProperty("channelIdMiku", channelIdMiku);
        jsonObject.addProperty("channelIdRad", channelIdRad);
        jsonObject.addProperty("serverName", serverName);
        jsonObject.addProperty("serverIconUrl", serverIconUrl);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        FileWriter writer = new FileWriter(file);
        gson.toJson(jsonObject, writer);
        writer.close();
    }

    public void readConfig(File file) throws IOException {
        FileReader reader = new FileReader(file);

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(reader, JsonObject.class);

        token = getStringOrDefault(obj, "token", token);
        webhookMiku = getStringOrDefault(obj, "webhookMiku", webhookMiku);
        webhookRad = getStringOrDefault(obj, "webhookRad", webhookRad);
        channelIdMiku = getStringOrDefault(obj, "channelIdMiku", channelIdMiku);
        channelIdRad = getStringOrDefault(obj, "channelIdRad", channelIdRad);
        serverName = getStringOrDefault(obj, "serverName", serverName);
        serverIconUrl = getStringOrDefault(obj, "serverIconUrl", serverIconUrl);

        reader.close();
    }

    private Boolean getBooleanOrDefault(JsonObject obj, String name, Boolean def) {
        return obj.has(name) ? obj.get(name).getAsBoolean() : def;
    }

    private String getStringOrDefault(JsonObject obj, String name, String def) {
        return obj.has(name) ? obj.get(name).getAsString() : def;
    }

    private Integer getIntOrDefault(JsonObject obj, String name, Integer def) {
        return obj.has(name) ? obj.get(name).getAsInt() : def;
    }

    private Long getLongOrDefault(JsonObject obj, String name, Long def) {
        return obj.has(name) ? obj.get(name).getAsLong() : def;
    }
}
