package com.fiap58.pagamento.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public JsonElement serialize(Instant instant, Type srcType,
                                 JsonSerializationContext context) {

        return new JsonPrimitive(instant.getEpochSecond());
    }

    @Override
    public Instant deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {

        return Instant.ofEpochSecond(Long.parseLong(json.toString()));
    }
}
