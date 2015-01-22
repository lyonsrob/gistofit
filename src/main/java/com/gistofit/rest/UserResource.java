package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gistofit.model.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

@Path("/v1/user")
public class UserResource {
	
	  @POST
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public User createUser(
			  final Map<String, String> postData) {
		
		User user = ofy().load().type(User.class)
				.filter("facebookUserId", postData.get("facebook_userid")).first().now();
		
		if (user == null) {
			user = new User();
			user.setEmail(postData.get("email"));
			user.setFirstName(postData.get("first_name"));
			user.setLastName(postData.get("last_name"));
			user.setFacebookUserId(postData.get("facebook_userid"));
			ofy().save().entity(user).now();
		}
		
	    return user;
	  }
	  
	  @GET
	  @Path("/{id}")
	  @Produces(MediaType.APPLICATION_JSON)
	  public User getUser(@PathParam("id") final String id) throws
	      Exception {
			  Long userId = Long.parseLong(id);
			  Key<User> userKey = Key.create(User.class, userId.longValue());

			  return ofy().load().key(userKey).now();
	  }
}