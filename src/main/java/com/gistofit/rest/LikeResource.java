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

import org.json.JSONObject;

import com.gistofit.model.Gist;
import com.gistofit.model.Like;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Path("/v1/gist/{gistId}/likes")
public class LikeResource {
		
	  private final MemcacheService mc = MemcacheServiceFactory
	           .getMemcacheService();
	   
	  @POST
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Like likeGist(
			  @PathParam("gistId") final String gistId,
			  final Map<String, String> postData) {
		
		String userIdString = postData.get("userid");
		Long longId = Long.parseLong(gistId); 

		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
		
		Long userId = Long.parseLong(userIdString);
		
		Like like = ofy().load().type(Like.class)
				.filter("gist =",gistKey) 
                .filter("userId =", userId).first().now(); 
		
		if (like == null) {
			like = new Like();
			Gist gist = ofy().load().key(gistKey).now(); 
			
			like.setGist(gist);
			like.setUserId(userId);
			
			ofy().save().entity(like).now();
			
	        Object gistLikesJson = mc.get("likes_" + gistId);
	        Object myLikesJson = mc.get("my_likes_" + userIdString);
	        
        	JSONObject gistLikes = new JSONObject();
        	JSONObject myLikes = new JSONObject();


	        if (gistLikesJson != null) {
	        	gistLikes = new JSONObject(gistLikesJson.toString());
	        }
	        
	        if (myLikesJson != null) {
	        	myLikes = new JSONObject(myLikesJson.toString());
	        }
	       
	        gistLikes.put(userId.toString(), 1); 
	        gistLikes.increment("total");
	        
	        myLikes.put(gistId, 1);
	        
	        mc.put("likes_" + gistId, gistLikes.toString());
	        mc.put("my_likes_" + userIdString, myLikes.toString());
	        mc.increment("gistLikeCount_" + gist.getUser().id.toString(),1);
		}
		
	    return like;
	  }
	  
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public JSONObject getLikes(@PathParam("gistId") final String id, @QueryParam("cursor") final String cursor) throws
	      Exception {
	        
		  	Object likeJson = mc.get("likes_" + id);
	       
	        if (likeJson == null) {
		        Long longId = Long.parseLong(id);
		        Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
		        Query<Like> query = ofy().load().type(Like.class).filter("gist", gistKey).order("-created");
		        List<Like> likes = query.list();
		        	
		        JSONObject json = new JSONObject();
		        
		        for (Like like : likes) {
		        	json.put(like.userId.toString(), 1);
		        	
		        }
		        
		        json.put("total", likes.size());
		        mc.put("likes_" + id, json.toString());
		        
		        return json;
		        
	        } else {
	        	return new JSONObject(likeJson.toString());
	        }

	  }
}

