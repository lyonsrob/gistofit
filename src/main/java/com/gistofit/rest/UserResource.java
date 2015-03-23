package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.gistofit.domain.GistListResponse;
import com.gistofit.domain.UserServiceInfo;
import com.gistofit.model.Comment;
import com.gistofit.model.Gist;
import com.gistofit.model.Like;
import com.gistofit.model.User;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Path("/v1/user")
public class UserResource {
	
	  private final MemcacheService mc = MemcacheServiceFactory
	           .getMemcacheService();
	  
	  @POST
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public User createUser(
			  final Map<String, String> postData) {
		
		User user = ofy().load().type(User.class)
				.filter("facebookUserId", postData.get("id")).first().now();
		
		if (user == null) {
			user = new User();
		}
		
		user.setEmail(postData.get("email"));
		user.setFirstName(postData.get("first_name"));
		user.setLastName(postData.get("last_name"));
		user.setProfilePicture(postData.get("profile_picture"));
		user.setFacebookUserId(postData.get("id"));
		ofy().save().entity(user).now();
		
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
	  
	  private List<Gist> _getUserGists(Long userId) throws
	  	Exception {
	        Key<User> userKey = Key.create(User.class, userId.longValue());
	        Query<Gist> query = ofy().load().type(Gist.class).filter("user", userKey).order("-date");
	        List<Gist> gists = query.list();
	        
	        return gists;
	  }
	  
	  @GET
	  @Path("/{id}/gists/count")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Integer getUserGistCount(@PathParam("id") final String id) throws
	      Exception {

		  	Integer userGistCount = (Integer) mc.get("gistCount_" + id);
		       
	        if (userGistCount == null) {		  	
	        	Long longId = Long.parseLong(id);
		  	
	        	List<Gist> gists = _getUserGists(longId);
	        	userGistCount = gists.size();
	        	mc.put("gistCount_" + id, userGistCount);
	        }
	        
	        return userGistCount;
	  }
	  
	  @GET
	  @Path("/{id}/gists")
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Gist> getUserGists(@PathParam("id") final String id) throws
	      Exception {
		  	Long longId = Long.parseLong(id);
	        
	        return _getUserGists(longId);
	  }
	  
	  private List<Like> _getUserGistLikes(Long userId) throws
	  	Exception {
	        Key<User> userKey = Key.create(User.class, userId.longValue());
	        
	        Query<Gist> gistQuery = ofy().load().type(Gist.class).filter("user", userKey).order("-date");
	        QueryResultIterator<Gist> iterator = gistQuery.iterator();
	        List<Like> likes = new ArrayList<Like>();
	        
			while (iterator.hasNext()) {
				Key<Gist> gistKey = Key.create(Gist.class, iterator.next().getId());
				Query<Like> query = ofy().load().type(Like.class);
				query = query.filter("gist", gistKey);
				likes.addAll(query.list());
			}
			
			return likes;
	  }
	  
	  @GET
	  @Path("/{id}/gists/likes/count")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Integer getUserLikeCount(@PathParam("id") final String id) throws
	      Exception {
		  	Integer userLikeCount = (Integer) mc.get("gistLikeCount_" + id);
		       	
	        if (userLikeCount == null) {
			  	Long longId = Long.parseLong(id);
			  	
			  	List<Like> likes = _getUserGistLikes(longId);
			  	userLikeCount = likes.size();
	        	mc.put("gistLikeCount_" + id, userLikeCount);
	        }
	        
	        return userLikeCount;
	  }
	  
	  @GET
	  @Path("/{id}/gists/likes")
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Like> getUserGistLikes(@PathParam("id") final String id) throws
	      Exception {
		  	Long longId = Long.parseLong(id);

	        return _getUserGistLikes(longId);
	  }
	  
	  @GET
	  @Path("/{id}/comments")
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Comment> getUserComments(@PathParam("id") final String id) throws
	      Exception {
		  	Long longId = Long.parseLong(id);
	        Key<User> userKey = Key.create(User.class, longId.longValue());
	        Query<Comment> query = ofy().load().type(Comment.class).filter("user", userKey).order("-date");
	        List<Comment> comments = query.list();
	        
	        return comments;
	  }
}