package com.example.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AIConfiguration {

    static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    static class RequestBody {
        String model;
        Message[] messages;

        public RequestBody(String model, Message[] messages) {
            this.model = model;
            this.messages = messages;
        }
    }

//    public static void main(String[] args){
//        AIConfiguration.sendReq("admin_op_manual.txt", "config_extract_sub_outline.txt", "result_extract_sub.txt");
//        AIConfiguration.sendReq("test_prompt.txt", "test_config.txt", "result_test.txt");
//        AIConfiguration.sendReq("admin_op_manual.txt", "config_extract_sub_fp.txt", "result_extract_sub_fp.txt");
//        AIConfiguration.sendReq("a.docx", "config_sum.txt", "result.txt");
//    }

    public static void sendReq(String promptPath, String rolePath, String resultPath) {
        try {
            // 添加功能 A
            // 从"./prompts/config_extract_sub_outline.txt"中读取内容，作为系统消息
            String systemContent = new String(Files.readAllBytes(Paths.get("./prompts/"+rolePath)), StandardCharsets.UTF_8);
//            String systemContent = AIConfiguration.readDocx("./prompts/" + rolePath);

            // 从"./prompts/extract_fp.txt"中读取提示，作为用户消息
//            String userContent = new String(Files.readAllBytes(Paths.get("./prompts/"+promptPath)), StandardCharsets.UTF_8);
            String userContent = AIConfiguration.readDocx("./prompts/" + promptPath);

            // 创建请求体
            RequestBody requestBody = new RequestBody(
                    "qwen-plus",
                    new Message[] {
                            new Message("system", systemContent),
                            new Message("user", userContent)
                    }
            );

            // 将请求体转换为 JSON
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(requestBody);

            // 创建 URL 对象
            URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 POST
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            // 若没有配置环境变量，请用百炼API Key将下行替换为：String apiKey = "sk-xxx";
//            String apiKey = System.getenv("DASHSCOPE_API_KEY");
            String apiKey = "sk-d25a1ea4d0fa4d908be7044f1ff58971";
//            if (apiKey == null || apiKey.isEmpty()) {
//                System.err.println("API Key未设置！");
//                return;
//            }
            System.out.println(apiKey);
            String auth = "Bearer " + apiKey;
            httpURLConnection.setRequestProperty("Authorization", auth);

            httpURLConnection.setInstanceFollowRedirects(false);

            // 启用输入输出流
            httpURLConnection.setDoOutput(true);

            // 写入请求体
            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 读取响应体
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            String content = jsonObject
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            // 输出content到终端
//            System.out.println("Assistant's Response: " + content);


            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./results/" + resultPath))) {
                writer.write(content.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            System.exit(0);
//        }
    }

    public static String readDocx(String Path) throws IOException {
        File file = new File(Path);
        String str = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument xdoc = new XWPFDocument(fis);
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            str = extractor.getText();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}