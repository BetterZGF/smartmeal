package com.aaron.group.smartmeal.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明:
 * 作者: administrator on 2017/4/19.17:24
 * 邮箱: administrator@gmail.com
 */

public class JSONUtils<T>
{

    private static Gson gson = null;

    static
    {
        if (null == gson)
        {
            gson = new GsonBuilder().serializeNulls().create();
        }
    }

    public String toJson(T t) throws JsonIOException
    {
        return gson.toJson(t);
    }

    @SuppressWarnings("unchecked")
    public T toBean(String msg, T t) throws JsonSyntaxException
    {
        // 这里起初使用
        // Type type = TypeToken<T>() {}.getType());
        // return (T) gson.fromJson(msg,type);
        // 貌似java泛型不具有传递性。只能采用Class参数的方法。
        return (T) gson.fromJson(msg, t.getClass());
    }

    public String listToJson(List<T> list) throws JsonIOException
    {
        return gson.toJson(list);
    }

    public <T> List<T> jsonToList(String json, Class<T> clazz) throws JsonSyntaxException
    {
        List<T> result = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            result.add(gson.fromJson(elem, clazz));
        }
        return result;
    }
}