package org.example.handlers.requestHandlers;

import org.example.models.requests.AbstractRequest;
import org.example.models.requests.NewIdentityRequest;

public class NewIdentityRequestHandler extends AbstractRequestHandler{
    private NewIdentityRequest request;

    public NewIdentityRequestHandler(AbstractRequest request) {
        this.request = (NewIdentityRequest)  request;
    }


    @Override
    public void processRequest() {
        String identity =(String) request.getIdentity();
        System.out.println(identity);

    }
}
