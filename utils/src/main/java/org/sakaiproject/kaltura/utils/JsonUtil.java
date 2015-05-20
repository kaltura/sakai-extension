/**
 * Copyright 2014 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.sakaiproject.kaltura.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Utility class for JSON-specific functionality
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class JsonUtil {

    /**
     * Creates a JSON string from the given object
     * 
     * @param fromObj the object holding to be converted to JSON
     * @return the JSON string representing the object
     */
    public static String parseToJson(Object fromObj) {
        if (fromObj == null) {
            throw new IllegalArgumentException("Object to parse into JSON cannot be null.");
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        // only show fields marked as "expose" in models
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        // serialize null values
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        String rv = gson.toJson(fromObj, fromObj.getClass());

        return rv;
    }

    /**
     * Creates a list of POJOs from the given JSON string
     * 
     * @param jsonStr the JSON to parse
     * @param toClass the class of the POJO
     * @return a list of the POJOs created
     */
    public static List<Object> parseFromJson(String jsonStr, Class<?> toClass) {
        if (StringUtils.isBlank(jsonStr)) {
            throw new IllegalArgumentException("JSON string cannot be blank.");
        }
        if (toClass == null) {
            throw new IllegalArgumentException("Class cannot be null.");
        }

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonStr, JsonElement.class).getAsJsonArray();
        List<Object> rv = new ArrayList<Object>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            Object obj = gson.fromJson(jsonElement, toClass);
            rv.add(obj);
        }

        return rv;
    }

}
