import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description, String status) {
        super(name, description, status);
        subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        String result;
        result = "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", ID=" + super.getId() +
                ", status='" + super.getStatus() + '\'';
        if (subtasksId != null) {
            result += ", quantity subtasks=" + subtasksId.size() + "}";
        } else {
            result += ", quantity subtasks=0}";
        }
        return result;
    }
}
