//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;

import core.*;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class RequestContainer {
    private static RequestContainer instance = new RequestContainer();
    private List<Request> requestList = new ArrayList<>();

    private RequestContainer() {
    }

    public static RequestContainer getInstance() {
        return instance;
    }

    public void addRequest(Request request) {
        // Create new Request object, to differentiate from the currentRequest:
        Request reqToAdd = new Request(request);

        requestList.add(reqToAdd);

    }

    public Request getRequest(int index) {
        return requestList.get(index);
    }

    public Request getLastRequest() {
        if (!requestList.isEmpty()) {
            return getRequest(requestList.size() - 1);
        } else
            return null;
    }

    public void clearContainer() {
        requestList.clear();
    }

    public void printAllRequests() {
        System.out.println("Printing all requests in the RequestContainer ..... :\n");

        for (int i = 0; i < requestList.size(); i++) {
            System.out.println("Request[" + i + "]:\n");
            System.out.println("Object ref= " + ObjectUtils.identityToString(requestList.get(i)));
            System.out.println(requestList.get(i));

        }
    }
}
