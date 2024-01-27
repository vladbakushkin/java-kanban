package server;

import manager.exception.ManagerLoadException;
import manager.exception.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;

    public KVTaskClient(String url) {
        if (url.endsWith("/")) {
            this.url = url;
        } else {
            this.url = url + "/";
        }
        this.apiToken = register(this.url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(url + "register");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Невозможно получить токен. Код: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка в отправке ответа.");
        }
    }

    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ManagerLoadException("Невозможно загрузить менеджер. Код: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка в отправке ответа.");
        }
    }

    public void put(String key, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200) {
                throw new ManagerLoadException("Невозможно сохранить менеджер. Код: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка в отправке ответа.");
        }
    }

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new KVTaskClient("http://localhost:8080/register/");
    }
}
