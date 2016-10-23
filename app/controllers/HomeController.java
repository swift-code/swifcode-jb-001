package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ConnectionRequest;
import models.Profile;
import models.User;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lubuntu on 10/23/16.
 */
public class HomeController extends Controller {

    @Inject
    ObjectMapper objectMapper;

  public Result getProfile(Long id){
      ObjectNode data=objectMapper.createObjectNode();
      ObjectNode connectionData=objectMapper.createObjectNode();
      User user=User.find.byId(id);
      Profile profile=user.profile.find.byId(id);



      data.put("id",profile.id );
      data.put("firstName",profile.firstName );
      data.put("lastName",profile.lastName );
      data.put("email",user.email);
      data.put("company",profile.company );
      //friend List
      data.set("connections",objectMapper.valueToTree(user.connections.stream().map(connection->{
          ObjectNode connectionJson=objectMapper.createObjectNode();
          User connectionUser=User.find.byId(connection.id);
          Profile connectionProfile=Profile.find.byId(connection.profile.id);
          connectionJson.put("id",connectionUser.id);
          connectionJson.put("firstName",connectionProfile.firstName );
          connectionJson.put("lastName",connectionProfile.lastName );
          connectionJson.put("email",connectionUser.email);
          connectionJson.put("company",connectionProfile.company );
          return connectionJson;
      }).collect(Collectors.toList())));



      //requests pending
      data.set("connectionRequestReceived",objectMapper.valueToTree(user.conenctionRequestsReceived.stream().filter(x->x.status.equals(ConnectionRequest.Status.WAITING) ).map(connectionRequestReceived->{
          ObjectNode connectionJson=objectMapper.createObjectNode();
          Profile senderProfile=Profile.find.byId(connectionRequestReceived.sender.id);
          connectionJson.put("id",connectionRequestReceived.id);
          connectionJson.put("firstName",senderProfile.firstName );
          connectionJson.put("lastName",senderProfile.lastName );
          connectionJson.put("company",senderProfile.company );
          return connectionJson;
      }).collect(Collectors.toList())));

      //suggestions
      data.set("suggestions",objectMapper.valueToTree(User.find.all().stream()
              .filter(x->!user.equals(x)) //remove me
              .filter(x->!user.connections.contains(x)) //remove friends
              .filter(x->!user.conenctionRequestsReceived.stream().map(y->y.sender).collect(Collectors.toList()).contains(x)) //remove users who sent request to me
              .filter(x->!user.connectionRequestsSent.stream().map(y->y.receiver).collect(Collectors.toList()).contains(x))
              .map(suggestion -> {
                  ObjectNode suggestionJson = objectMapper.createObjectNode();
                  Profile suggestionProfile = Profile.find.byId(suggestion.profile.id);
                  suggestionJson.put("id",suggestion.id);
                  suggestionJson.put("firstName",suggestionProfile.firstName);
                  suggestionJson.put("lastName",suggestionProfile.lastName);
                  return suggestionJson;
              })
              .collect(Collectors.toList())));
      return ok(data);

      /*
      {
        "email" : "",
        "suggestions" : [
            {
                "id" : "",
                "firstName" : "",
                ..
            },
            {
                ...
            }
        ],
        "connections" : [
            {

            },
            {

            }
        ]
      }
      */
  }

}


