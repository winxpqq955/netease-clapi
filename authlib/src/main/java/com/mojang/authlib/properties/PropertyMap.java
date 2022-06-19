/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingMultimap
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.mojang.authlib.properties;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Type;
import java.util.Map;

public class PropertyMap
extends ForwardingMultimap<String, Property> {
    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    protected Multimap<String, Property> delegate() {
        return this.properties;
    }

    public static class Serializer
    implements JsonSerializer<PropertyMap>,
    JsonDeserializer<PropertyMap> {
        public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            PropertyMap result;
            block5: {
                block4: {
                    result = new PropertyMap();
                    if (!(json instanceof JsonObject)) break block4;
                    JsonObject object = (JsonObject)json;
                    for (Map.Entry entry : object.entrySet()) {
                        if (!(entry.getValue() instanceof JsonArray)) continue;
                        for (JsonElement element : (JsonArray)entry.getValue()) {
                            result.put((String) entry.getKey(), new Property((String)entry.getKey(), element.getAsString()));
                        }
                    }
                    break block5;
                }
                if (!(json instanceof JsonArray)) break block5;
                for (JsonElement element : (JsonArray)json) {
                    if (!(element instanceof JsonObject)) continue;
                    JsonObject object = (JsonObject)element;
                    String name = object.getAsJsonPrimitive("name").getAsString();
                    String value = object.getAsJsonPrimitive("value").getAsString();
                    if (object.has("signature")) {
                        result.put(name, new Property(name, value, object.getAsJsonPrimitive("signature").getAsString()));
                        continue;
                    }
                    result.put(name, new Property(name, value));
                }
            }
            return result;
        }

        public JsonElement serialize(PropertyMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray result = new JsonArray();
            for (Property property : src.values()) {
                JsonObject object = new JsonObject();
                object.addProperty("name", property.getName());
                object.addProperty("value", property.getValue());
                if (property.hasSignature()) {
                    object.addProperty("signature", property.getSignature());
                }
                result.add((JsonElement)object);
            }
            return result;
        }
    }
}

