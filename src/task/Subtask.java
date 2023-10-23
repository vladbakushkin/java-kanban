package task;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", ID=" + super.getId() +
                ", status='" + super.getStatus() + '\'' +
                ", epicId='" + epicId + '\'' +
                '}';
    }
}
