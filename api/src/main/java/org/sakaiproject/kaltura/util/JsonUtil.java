/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for JSON-specific functionality
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
@Slf4j
public class JsonUtil {

    /**
     * Creates a JSON string from the given object
     * 
     * @param fromObj the object holding to be converted to JSON
     * @return the JSON string representing the object
     */
    public static String parseToJson(Object fromObj) {
        if (fromObj == null) {
            throw new IllegalArgumentException("Object to parse into JSON cannot be null");
        }

        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(fromObj);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize to json", e);
            json = "";
        }

        return json;
    }

    /**
     * Creates a list of POJOs from the given JSON string
     * 
     * @param jsonStr the JSON to parse
     * @param toClass the class of the POJO
     * @return a list of the POJOs created
     */
    public static <T> List<T> parseFromJson(String json, Class<T> toClass) throws ClassNotFoundException {
        Objects.requireNonNull(toClass, "Class cannot be null");
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException("JSON string cannot be blank");
        }

        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + toClass.getName() + ";");
        T[] objs;
        try {
             objs = mapper.readValue(json, arrayClass);
        } catch (IOException e) {
            log.warn("Could not deserialize json", e);
            objs = (T[]) Array.newInstance(toClass, 0);
        }

        return Arrays.asList(objs);
    }
}
