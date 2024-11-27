package com.example.util;

import com.example.entity.Detail;
import com.example.service.DetailFunctionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    DetailFunctionService detailFunctionService;

    public static List<Detail> parseDetails(String jsonPath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(jsonPath)), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        List<Detail> details = new ArrayList<>();

        try {
            JsonNode rootNode = mapper.readTree(json);
            if (rootNode.isArray()) {
                for (JsonNode systemNode : rootNode) {
                    String subsystemName = systemNode.get("子系统名称").asText();
                    String subsystemDescribe = systemNode.get("子系统描述").asText();
                    JsonNode features = systemNode.get("功能点");

                    if (features.isArray()) {
                        for (JsonNode feature : features) {
                            Detail detail = new Detail();
                            // 这里projectId, unadjustedFp, adjustedFp和note我就没有填，先直接null
                            detail.setSubsystemName(subsystemName);
                            detail.setSubsystemDescribe(subsystemDescribe);
                            detail.setFeatureName(feature.get("功能点名称").asText());
                            detail.setComplexity(feature.get("重用程度").asText());
                            detail.setModifyType(feature.get("修改类型").asText());
                            detail.setFeatureType(feature.get("功能类型").asText());

                            details.add(detail);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return details;
    }
}

