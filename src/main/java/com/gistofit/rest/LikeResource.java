package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import java.util.HashMap;
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
import com.gistofit.model.User;

//import com.google.appengine.api.memcache.Expiration;
//import com.google.appengine.api.memcache.MemcacheService;
//import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
//import com.google.appengine.api.memcache.MemcacheServiceFactory;



import java.util.Collections;

//import net.sf.jsr107cache.Cache;
//import net.sf.jsr107cache.CacheException;
//import net.sf.jsr107cache.CacheFactory;
//import net.sf.jsr107cache.CacheManager;
//
//import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Path("/v1/gist/{id}/likes")
public class LikeResource {
//
//    
//		static {
//			Cache cache;
//	        Map props = new HashMap();
//	        props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
//
//	        try {
//	            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
//	            cache = cacheFactory.createCache(props);
//	        } catch (CacheException e) {
//	            // ...
//	        }
//	  }
	  @POST
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Like likeGist(
			  @PathParam("id") final String id,
			  final Map<String, String> postData) {
		
		String userIdString = postData.get("userid");
		Long longId = Long.parseLong(id); 

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
			
		}
		
	    return like;
	  }
	  
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Like> getLikes(@PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
	      Exception {
			  Long longId = Long.parseLong(id);
			  Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
			  Query<Like> query = ofy().load().type(Like.class).filter("gist", gistKey).order("-created");
			  
			  return query.list();  
	  }
}

