package com.hanna.second.springbootprj.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ObjectConverter {

    public static String convertObjectNodeToJson(ObjectNode objectNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        try {
            // JsonNode를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JsonNode to JSON", e);
        }
    }

    public static ObjectNode convertJsonToObjectNode(String jsonAmount) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeData = null;

        if (jsonAmount != null) {
            try {
                // JSON 문자열을 JsonNode로 변환
                jsonNodeData = objectMapper.readTree(jsonAmount);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Invalid JSON format", e);
            }
        }

        // JsonNode가 null인 경우 빈 ObjectNode 반환
        return jsonNodeData != null ? (ObjectNode) jsonNodeData.deepCopy() : objectMapper.createObjectNode();
    }

    public static String convertMapToJson(Map<String, BigDecimal> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Map을 JSON 문자열로 변환
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Map to JSON", e);
        }
    }

}




