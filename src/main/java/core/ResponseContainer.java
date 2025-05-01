//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;

import java.util.ArrayList;
import java.util.List;

public class ResponseContainer {
    private List<Response> responseList = new ArrayList<>();
    private static ResponseContainer instance = new ResponseContainer();

    private ResponseContainer() {}

    public static ResponseContainer getInstance() {
        return instance;
    }

    public void addResponse(Response response) {
        responseList.add(response);
    }

    public Response getResponse(int index) {
        return responseList.get(index);
    }

    public Response getLastResponse() {
        if(!responseList.isEmpty())
            return responseList.get(responseList.size() - 1);
        else
            return null;
    }

    public void clearContainer() {
        responseList.clear();
    }
}
