package tdtu.mobile_dev_final.todo;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Todo {
    private String name;
    private String phone;
    private String avatar;
    private String description;
    private Boolean status;
    private Date createdAt;
    private Date updatedAt;
    private List<String> tags;
    private String startDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String endDate;
    private ArrayList<SubTask> taskList;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<SubTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<SubTask> taskList) {
        this.taskList = taskList;
    }

    public Todo() {
        //None constructor
    }

    public Todo(String name, String phone, String avatar, String description, Boolean status, Date createdAt, Date updatedAt, List<String> tags, String startDate, String endDate, ArrayList<SubTask> taskList, String id) {
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags;
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskList = taskList;
        this.id = id;
    }

    public static List<Todo> mapData(JSONArray resultList) {
        List<Todo> todoList = new ArrayList<>();
        for (int i=0; i < resultList.length(); i++) {

            Todo todo = new Todo();
            Todo gsonTodo = null;
            SubTask gsonSubTask = null;

            try {
                gsonTodo = new Gson().fromJson(resultList.get(i).toString(), Todo.class);
                todo.setName(gsonTodo.name);
                todo.setAvatar(gsonTodo.avatar);
                todo.setDescription(gsonTodo.description);
                todo.setStatus(gsonTodo.status);
                todo.setCreatedAt(gsonTodo.createdAt);
                todo.setUpdatedAt(gsonTodo.updatedAt);
                todo.setTags(gsonTodo.tags);
                todo.setStartDate(gsonTodo.startDate);
                todo.setEndDate(gsonTodo.endDate);
                todo.setTaskList(gsonTodo.taskList);
                todo.setPhone(gsonTodo.phone);
                todo.setId(gsonTodo.id);

                todoList.add(todo);

            } catch (JSONException e) {
                Log.e("BUG", String.valueOf(e));
//                e.printStackTrace();
            } ;
        }
        return todoList;
    }
}