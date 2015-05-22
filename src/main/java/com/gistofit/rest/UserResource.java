package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gistofit.model.Comment;
import com.gistofit.model.Gist;
import com.gistofit.model.Like;
import com.gistofit.model.User;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.util.Base64;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Path("/v1/user")
public class UserResource {
	
	  private final MemcacheService mc = MemcacheServiceFactory
	           .getMemcacheService();
	  
	  public byte[] getHash(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	       MessageDigest digest = MessageDigest.getInstance("SHA-256");
	       digest.reset();
	       digest.update(salt);
	       return digest.digest(password.getBytes("UTF-8"));
	  }
	  
	  
	  
	  @POST
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public User createOrFetchUser(
			  final Map<String, String> postData) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		String facebookUserId = postData.get("id");
		String emailAddress = postData.get("email");
		String password = postData.get("password"); 
		
		User user = null;
		
		if (facebookUserId != null && !facebookUserId.isEmpty()) {
			user = ofy().load().type(User.class)
				.filter("facebookUserId", postData.get("id")).first().now();
		} else if (emailAddress != null && !emailAddress.isEmpty()) {		
			User tmpUser = ofy().load().type(User.class)
					.filter("email", postData.get("email")).first().now();
			
			if (tmpUser != null) {
					try{
						if (password != null && !password.isEmpty()) {
							byte[] salt = Base64.decodeBase64(tmpUser.getSalt());
							String encodedPassword = Base64.encodeBase64String(getHash(postData.get("password"), salt));
						    if (tmpUser.getPassword().equals(encodedPassword)) {
						        return tmpUser;
						    } else {
						    	throw new InputMismatchException("password does not match");
						    }
						} else {
					    	throw new InputMismatchException("password not provided");
						}
					} catch(InputMismatchException e){
					    System.out.println (e);
						//FixMe: This should throw a proper error
					    return user;
					}
			}
		}
		
		if (user == null) {
			user = new User();

			user.setEmail(postData.get("email"));
			user.setFirstName(postData.get("first_name"));
			user.setLastName(postData.get("last_name"));
			
			if ((facebookUserId != null && !facebookUserId.isEmpty())) {
				user.setProfilePicture(postData.get("profile_picture"));
				user.setFacebookUserId(postData.get("id"));
			} else if (password != null && !password.isEmpty()) {
				final Random r = new SecureRandom();
				byte[] salt = new byte[32];
				r.nextBytes(salt);
				String encodedSalt = Base64.encodeBase64String(salt);
				user.setSalt(encodedSalt);
				user.setPassword(Base64.encodeBase64String(getHash(postData.get("password"), salt)));
			} else {	
				//FixMe: This should throw a proper error
				return new User();
			}

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