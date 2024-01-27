package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.adapter.DurationAdapter;
import manager.adapter.LocalDateTimeAdapter;
import manager.memory.history.InMemoryHistoryManager;
import manager.web.HttpTaskManager;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        try {
            return new HttpTaskManager("http://localhost:8078");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Некорректно введен URL");
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        return gsonBuilder.create();
    }
}
