package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId;

    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public void addSubtaskId(int id) {
        subtasksId.add(id);
    }

    public void clearSubtasksId() {
        subtasksId.clear();
    }

    public void removeSubtaskId(int id) {
        subtasksId.remove((Integer) id);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }

    @Override
    public String toString() {
        String result;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        result = "tasks.Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", ID=" + super.getId() +
                ", status='" + super.getStatus() + '\'';
        if (subtasksId != null) {
            result += ", QTY ST=" + subtasksId.size();
        } else {
            result += ", QTY ST=0";
        }
        if (startTime != null) {
            result += ", duration=" + '\'' + duration + '\'' +
                    ", startTime=" + '\'' + startTime.format(formatter) + '\'' +
                    ", endTime=" + '\'' + getEndTime().format(formatter) + '\'' +
                    "}";
        } else {
            result += ", duration=" + '\'' + null + '\'' + ", startTime=" + '\'' + null + '\'' + "}";
        }
        return result;
    }
}
