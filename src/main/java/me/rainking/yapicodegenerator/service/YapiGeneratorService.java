package me.rainking.yapicodegenerator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.rainking.yapicodegenerator.entity.BodyField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Rain
 * @date 2018/11/12
 */
@Service
@Slf4j
public class YapiGeneratorService {

    private List<String> classHolder = new ArrayList<>();
    private Queue<JSONObject> queue = new LinkedBlockingQueue<>();

    public String generate(String yapiJsonStr){
        JSONObject jsonObject = JSONObject.parseObject(yapiJsonStr);
        queue.add(jsonObject);
        int time = 0;
        while(queue.size() > 0) {
            JSONObject properties = queue.remove();

            generateClass(properties);
            if(time >=3) {break;}
            time++;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String str:classHolder) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    private void generateClass(JSONObject jsonObject) {
        log.info(jsonObject.toString());
        JSONObject properties = jsonObject.getJSONObject("properties");
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("-------------------" + "\n");
        javaCode.append("类名字 " + jsonObject.getString("title") + " " +jsonObject.getString("description") + "\n");
        for(String key : properties.keySet()){
            BodyField bodyField = JSONObject.parseObject(properties.get(key).toString(), BodyField.class);
            javaCode.append("/**\n");
            javaCode.append(" * " + bodyField.getDescription());
            javaCode.append("\n */\n");
            javaCode.append("private " + bodyField.getJavaType() + " " + key + ";\n\n");
            if("object".equals(bodyField.getType())) {

                queue.add(JSON.parseObject(properties.get(key).toString()));
            }

        }
        javaCode.append("-------------------" + "\n");
        classHolder.add(javaCode.toString());
    }

//    public String generate1(String yapiJsonStr){
//        JSONObject jsonObject = JSONObject.parseObject(yapiJsonStr);
//        JSONObject properties = jsonObject.getJSONObject("properties");
//
//        StringBuilder javaCode = new StringBuilder();
//        for(String key : properties.keySet()){
//            BodyField bodyField = JSONObject.parseObject(properties.get(key).toString(), BodyField.class);
//            javaCode.append("/**\n");
//            javaCode.append(" * " + bodyField.getDescription());
//            javaCode.append("\n */\n");
//            javaCode.append("private " + bodyField.getJavaType() + " " + key + ";\n\n");
//        }
//        return javaCode.toString();
//    }
}
