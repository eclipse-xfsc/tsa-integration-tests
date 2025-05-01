//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package api.test.rest;

import java.util.List;

public class RestSessionContainer {
    private String taskID;
    private String taskListID;
    private List<String> groupTaskIDs;
    private List<String> importIDs;

    public List<String> getImportIDs() {
        return importIDs;
    }

    public void setImportIDs(List<String> importIDs) {
        this.importIDs = importIDs;
    }

    public List<String> getGroupTaskIDs() {
        return groupTaskIDs;
    }

    public void setGroupTaskIDs(List<String> groupTaskIDs) {
        this.groupTaskIDs = groupTaskIDs;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getTaskListID() {
        return taskListID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setTaskListID(String taskListID) {
        this.taskListID= taskListID;
    }
}