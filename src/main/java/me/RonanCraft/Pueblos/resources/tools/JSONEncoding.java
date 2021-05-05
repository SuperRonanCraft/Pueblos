package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG_MEMBER;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.text.ParseException;
import java.util.*;

@SuppressWarnings("ALL")
public class JSONEncoding {

    //String gets
    public static String getJsonFromList(String id, List<String> list) {
        if (list == null)
            return null;
        JSONObject obj = new JSONObject();
        obj.put(id, list);
        //System.out.print(obj.toString());
        return obj.toString();
    }

    public static String getJsonFromBoundingBox(BoundingBox position) {
        if (position == null)
            return null;
        Map obj = new LinkedHashMap();
        obj.put("world", position.getWorld().getName());
        obj.put("x_1", position.getMaxX());
        obj.put("z_1", position.getMaxZ());
        obj.put("x_2", position.getMinX());
        obj.put("z_2", position.getMinZ());
        String json = JSONObject.toJSONString(obj);
        return json.toString();
    }

    public static BoundingBox getPosition(String json) {
        try {
            JSONObject obj = (JSONObject) JSONValue.parse(json);
            Map chunk_info = (Map) obj;
            String world = chunk_info.get("world").toString();
            int x_1 = Integer.valueOf(chunk_info.get("x_1").toString());
            int z_1 = Integer.valueOf(chunk_info.get("z_1").toString());
            int x_2 = Integer.valueOf(chunk_info.get("x_2").toString());
            int z_2 = Integer.valueOf(chunk_info.get("z_2").toString());
            return new BoundingBox(Bukkit.getWorld(world), x_1, z_1, x_2, z_2);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getJsonFromMembers(List<ClaimMember> members) {
        if (members == null)
            return null;
        List<Map> array = new ArrayList<>();
        for (ClaimMember member : members) {
            HashMap<String, Object> obj = new HashMap();
            obj.put("uuid", member.uuid.toString());
            obj.put("name", member.name);
            obj.put("date", HelperDate.getDate(member.date));
            HashMap<String, Object> obj2 = new HashMap<>();
            for (Map.Entry<CLAIM_FLAG_MEMBER, Object> flag : member.getFlags().entrySet()) {
                obj2.put(flag.getKey().name(), flag.getValue());
            }
            obj.put("flags", obj2);
            array.add(obj);
        }
        //System.out.println(JSONArray.toJSONString(array));
        return JSONArray.toJSONString(array);
    }

    public static String getJsonFromRequests(List<ClaimRequest> requests) {
        if (requests == null)
            return null;
        List<Map> array = new ArrayList<>();
        for (ClaimRequest request : requests) {
            HashMap<String, Object> obj = new HashMap();
            obj.put("uuid", request.id.toString());
            obj.put("name", request.name);
            obj.put("date", HelperDate.getDate(request.date));
            array.add(obj);
        }
        //System.out.println(JSONArray.toJSONString(array));
        return JSONArray.toJSONString(array);
    }

    public static String getJsonFromFlags(HashMap<CLAIM_FLAG, Object> flags) {
        if (flags == null)
            return null;
        List<Map> array = new ArrayList<>();
        for (Map.Entry<CLAIM_FLAG, Object> flag : flags.entrySet()) {
            HashMap<String, Object> obj = new HashMap();
            obj.put("flag", flag.getKey().name());
            obj.put("value", flag.getValue());
            array.add(obj);
        }
        return JSONArray.toJSONString(array);
    }

    //Object gets
    public static HashMap<CLAIM_FLAG, Object> getFlags(String json) {
        if (json == null)
            return null;
        try {
            JSONArray obj = (JSONArray) JSONValue.parse(json);
            if (obj == null)
                return null;
            HashMap<CLAIM_FLAG, Object> flags = new HashMap<>();
            for (Object o : obj) {
                Map flag_info = (Map) o;
                CLAIM_FLAG flag = CLAIM_FLAG.valueOf(flag_info.get("flag").toString());
                Object value = flag_info.get("value");
                flags.put(flag, value);
            }
            return flags;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ClaimRequest> getRequests(String json, ClaimMain claim) {
        if (json == null)
            return null;
        try {
            JSONArray obj = (JSONArray) JSONValue.parse(json);
            List<ClaimRequest> requests = new ArrayList<>();
            for (Object o : obj) {
                Map member_info = (Map) o;
                String uuid = member_info.get("uuid").toString();
                String name = member_info.get("name").toString();
                String date = member_info.get("date").toString();
                ClaimRequest request = new ClaimRequest(UUID.fromString(uuid), name, HelperDate.getDate(date), claim);
                requests.add(request);
            }
            return requests;
        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ClaimMember> getMember(String json, ClaimMain claim) {
        if (json == null)
            return null;
        try {
            JSONArray obj = (JSONArray) JSONValue.parse(json);
            List<ClaimMember> members = new ArrayList<>();
            for (Object o : obj) {
                Map member_info = (Map) o;
                String uuid = member_info.get("uuid").toString();
                String name = member_info.get("name").toString();
                String date = member_info.get("date").toString();
                HashMap<CLAIM_FLAG_MEMBER, Object> flags = new HashMap<>();
                for (Map.Entry<String, Object> flag : ((HashMap<String, Object>) member_info.get("flags")).entrySet()) {
                    CLAIM_FLAG_MEMBER _flag = CLAIM_FLAG_MEMBER.valueOf(flag.getKey());
                    Object _value = flag.getValue();
                    flags.put(_flag, _value);
                }
                ClaimMember member = new ClaimMember(UUID.fromString(uuid), name, HelperDate.getDate(date), claim);
                member.setFlag(flags, false);
                members.add(member);
            }
            return members;
        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getListFromJson(String id, String json) {
        try {
            JSONObject obj = (JSONObject) JSONValue.parse(json);
            return (List<String>) obj.get(id);
        } catch (NullPointerException e) {
            return null;
        }
    }
}