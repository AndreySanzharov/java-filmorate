package ru.yandex.practicum.filmorate.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class StringToDurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String durationString = jsonParser.getText();

        return Duration.ofMinutes(Long.parseLong(durationString));
    }
}
