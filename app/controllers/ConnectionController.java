package controllers;

import models.ConnectionRequest;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by lubuntu on 10/23/16.
 */
public class ConnectionController extends Controller {

    public Result sendRequest(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null || User.find.byId(senderId) == null || User.find.byId(receiverId) == null) {
            return ok();
        } else {
            ConnectionRequest request = new ConnectionRequest();
            request.sender = User.find.byId(senderId);
            request.receiver = User.find.byId(receiverId);
            request.status = ConnectionRequest.Status.WAITING;
            ConnectionRequest.db().save(request);
        }
        return ok();
    }

    public Result acceptRequest(Long requestId) {

        //create a request obj for the ID
        ConnectionRequest request = ConnectionRequest.find.byId(requestId);

        //set request status as accepted
        request.status = ConnectionRequest.Status.ACCEPTED;

        User sender = User.find.byId(request.sender.id);
        User receiver = User.find.byId(request.receiver.id);

        sender.connections.add(request.receiver);
        receiver.connections.add(request.sender);

        ConnectionRequest.db().update(request);
        User.db().update(sender);
        User.db().update(receiver);
        return ok();
    }
}






