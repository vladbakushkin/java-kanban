package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, long duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        String result;
        result = "tasks.Subtask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", ID=" + super.getId() +
                ", status='" + super.getStatus() + '\'' +
                ", epicId='" + epicId + '\'';
        if (startTime != null) {
            result += ", duration=" + '\'' + duration.toMinutes() + '\'' +
                    ", startTime=" + '\'' + startTime.format(formatter) + '\'' +
                    ", endTime=" + '\'' + getEndTime().format(formatter) + '\'' +
                    "}";
        } else {
            result += ", duration=" + '\'' + null + '\'' + ", startTime=" + '\'' + null + '\'' + "}";
        }
        return result;
    }
}
