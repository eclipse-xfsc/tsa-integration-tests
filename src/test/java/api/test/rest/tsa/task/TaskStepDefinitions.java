package api.test.rest.tsa.task;

import api.test.core.BaseStepDefinitions;
import api.test.rest.RestGeneralStepDefinitions;
import api.test.rest.RestSessionContainer;
import com.jayway.jsonpath.JsonPath;
import core.*;
import cucumber.api.java.en.And;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TaskStepDefinitions extends BaseStepDefinitions{
    private static final Logger logger = LogManager.getLogger(RestGeneralStepDefinitions.class.getSimpleName());
    RestSessionContainer restSessionContainer;
    Request currentRequest;

    public TaskStepDefinitions(RestSessionContainer restSessionContainer, Request currentRequest, DataContainer dataContainer) {
        super(dataContainer);
        this.restSessionContainer = restSessionContainer;
        this.currentRequest = currentRequest;
    }

    @And("^I execute the Task \\{(.*)\\} via TSA Task API$")
    public void iExecuteTheTaskTSATaskAPI(String path) throws Throwable {
        currentRequest.setPath("/v1/task/" + path);

        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);

        if (getLastResponse().getStatusCode() == 200) {
            String responseBody = getLastResponse().getBody();
            Object result = JsonPath.read(responseBody, "$.taskID");
            String taskId = result.toString();

            restSessionContainer.setTaskID(taskId);

        }
    }

    @And("^I execute the taskList \\{(.*)\\} via TSA Task API$")
    public void iExecuteTheTaskListTSATaskAPI(String path) throws Throwable {
        currentRequest.setPath("/v1/taskList/" + path);

        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);

        if (getLastResponse().getStatusCode() == 200) {
            String responseBody = getLastResponse().getBody();
            Object result = JsonPath.read(responseBody, "$.taskListID");
            String taskListId = result.toString();

            restSessionContainer.setTaskListID(taskListId);
        }
    }

    @And("^I get the Task result with key \\{(.*)\\}$")
    public void iGetTheTaskResultWithKey(String suffix) {
        currentRequest.setPath("v1/taskResult/"+ suffix);

        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @And("^I get the current Task Result via TSA Task API$")
    public void iGetTheCurrentTaskResultTSATaskAPI() {
        String currentTask = restSessionContainer.getTaskID();
        iGetTheTaskResultWithKey(currentTask);
    }

    @And("^I get the current taskList Status via TSA Task API$")
    public void iGetTheCurrentTaskListStatusTSATaskAPI() {
        String currentTask = restSessionContainer.getTaskListID();
        currentRequest.setPath("v1/taskListStatus/"+ currentTask);

        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);

        if (getLastResponse().getStatusCode() == 200) {
            String responseBody = getLastResponse().getBody();

            List<String> taskResult = JsonPath.read(responseBody, "$..tasks..id");
            restSessionContainer.setGroupTaskIDs(taskResult);
        }
    }

    @And("^I get the result of Task \\{(\\d+)\\}$")
    public void iGetTheResultOfTask(int id) {
        iGetTheTaskResultWithKey( restSessionContainer.getGroupTaskIDs().get(id));
    }

}