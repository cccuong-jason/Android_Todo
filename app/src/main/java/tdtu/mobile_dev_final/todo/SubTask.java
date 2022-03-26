package tdtu.mobile_dev_final.todo;

public class SubTask {
    public String name;
    public String id;
    public String description;
    public boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SubTask(String name, String id, String description, boolean status) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.status = status;
    }
}
