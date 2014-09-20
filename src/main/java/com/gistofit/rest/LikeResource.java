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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gistofit.model.Gist;
import com.gistofit.model.Like;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Path("/v1/like")
public class LikeResource {
	
	  @POST
	  @Path("/{id}")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Gist likeGist(
			  @PathParam("id") final String id,
			  final Map<String, String> postData) {
		
		Long longId = Long.parseLong(id); 
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
	    	
		//Date date = new Date();
		Like like = new Like();
		
		Gist gist = ofy().load().key(gistKey).now(); 
		
		like.setGist(gist);
		//like.setCreated(date);
		
		ofy().save().entity(like).now();
		
	    return gist;
	  }
	  
	  
	  @GET
	  @Path("/{id}")
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Like> getLikes(@PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
	      Exception {
			  Long longId = Long.parseLong(id);
			  Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
			  Query<Like> query = ofy().load().type(Like.class).filter("gist", gistKey).order("-created");
			  
			  return query.list();  
	  }
}
