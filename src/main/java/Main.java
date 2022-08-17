import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import res.Res;

import java.io.*;
import java.util.List;

public class Main {
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=dE4KDz1xkXHypmNqg9BiP8dcTZKFXaoqO31JfLxr");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            Res res = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
            });
            System.out.println(res.getUrl());
            try (CloseableHttpResponse responseImg = httpClient.execute(new HttpGet(res.getUrl()))) {
                HttpEntity entity = responseImg.getEntity();
                if (entity != null) {
                    String filePath = "StargateMilkyWay_Oudoux_960.jpg";
                    try (
                            BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))
                    ) {
                        int inByte;
                        while ((inByte = bis.read()) != -1) bos.write(inByte);
                    } catch (IOException e) {
                        System.out.println("IOException при чтении картинки");
                    }
                }
            } catch (IOException e) {
                System.out.println("IOException при запросе 2");
            }
        } catch (IOException e) {
            System.out.println("IOException при запросе 1");
        }

    }
}
