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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gistofit.model.Comment;
import com.gistofit.model.Gist;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Path("/v1/gist/{id}/comments")
public class CommentResource {
	private List<Comment> getGistComments(Key<Gist> key, String cursor) {
		List<Comment> comments = new ArrayList<Comment>();
		Query<Comment> query = ofy().load().type(Comment.class).limit(5);
		if (key != null)
			query = query.filter("gist", key);

		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		query = query.order("-created");
		QueryResultIterator<Comment> iterator = query.iterator();
		while (iterator.hasNext()) {
			comments.add(iterator.next());
		}

		String nextCursor = iterator.getCursor().toWebSafeString();
		return comments;
	}



	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Key<Comment> commentGist(
			@PathParam("id") final String id,
			final Map<String, String> postData) {
		UserService userService = UserServiceFactory.getUserService();

		Long longId = Long.parseLong(id); 
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());

		//Date date = new Date();
		Comment comment = new Comment();

		Gist gist = ofy().load().key(gistKey).now(); 

		comment.setGist(gist);
		comment.setUser("user");
		comment.setContent(postData.get("content"));
		//comment.setCreated(date);

		return ofy().save().entity(comment).now();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getComments(@PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
	Exception {
		Long longId = Long.parseLong(id);
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());

		return getGistComments(gistKey, cursor);
	}	
}
