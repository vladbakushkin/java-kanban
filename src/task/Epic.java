package task;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;

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

    @Override
    public String toString() {
        String result;
        result = "tasks.Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", ID=" + super.getId() +
                ", status='" + super.getStatus() + '\'';
        if (subtasksId != null) {
            result += ", QTY ST=" + subtasksId.size() + "}";
        } else {
            result += ", QTY ST=0}";
        }
        return result;
    }
}
