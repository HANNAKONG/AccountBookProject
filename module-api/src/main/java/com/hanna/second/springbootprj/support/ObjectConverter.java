package com.hanna.second.springbootprj.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class ObjectConverter {

    public static Map<CategoryType, BigDecimal> convertJsonToMap(String jsonAmount) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<CategoryType, BigDecimal> resultMap = new HashMap<>();

        if (jsonAmount != null) {
            try {
                // JSON 문자열을 JsonNode로 변환
                JsonNode jsonNodeData = objectMapper.readTree(jsonAmount);

                // JsonNode의 각 필드를 순회하면서 Map으로 변환
                Iterator<Map.Entry<String, JsonNode>> fields = jsonNodeData.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String key = field.getKey();

                    // key를 CategoryType으로 변환
                    CategoryType categoryType = CategoryType.valueOf(key);

                    BigDecimal value = field.getValue().decimalValue();
                    resultMap.put(categoryType, value);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Invalid JSON format", e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category type in JSON", e);
            }
        }

        return resultMap;
    }

    public static String convertMapToJson(Map<CategoryType, BigDecimal> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Map을 JSON 문자열로 변환
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Map to JSON", e);
        }
    }

}




