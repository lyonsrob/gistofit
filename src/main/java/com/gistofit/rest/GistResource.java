package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gistofit.domain.GistListResponse;
import com.gistofit.domain.UserServiceInfo;
import com.gistofit.model.Gist;
import com.gistofit.model.URL;
import com.gistofit.model.User;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Path("/v1/gist")
public class GistResource {
	
	private final MemcacheService mc = MemcacheServiceFactory
			.getMemcacheService();
	  
	public static GistListResponse getGists(String url, String cursor) {
		Query<Gist> query = ofy().load().type(Gist.class).limit(5);

		if (url != null) {
			Key<URL> urlKey = Key.create(URL.class, url);
			query = query.filter("url", urlKey);
		}

		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		query = query.order("-date");

		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}

		String nextCursor = "";
		Long lastSeen = null;
		
		if (gists.size() > 0) {
			nextCursor = iterator.getCursor().toWebSafeString();
			lastSeen = gists.get(0).date.getTime();
		}

		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor, lastSeen);
	}

	private GistListResponse getNewestGists(String lastSeenString) throws ParseException {
		if (lastSeenString == null)
			return new GistListResponse(null, null, null, null);

		Query<Gist> query = ofy().load().type(Gist.class).filter("date >", new Date(Long.parseLong(lastSeenString))).limit(5).order("-date");
		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}

		String nextCursor = null;

		Long lastSeen = null; 

		if (gists.size() > 0) {
			nextCursor = iterator.getCursor().toWebSafeString();
			lastSeen = gists.get(0).date.getTime();
		}

		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor, lastSeen);
	}

	private GistListResponse getTrendingGists(String cursor) {
		Query<Gist> query = ofy().load().type(Gist.class).limit(15);

		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}

		String nextCursor = iterator.getCursor().toWebSafeString();
		Long lastSeen = null;
		
		if (gists.size() > 0) {
			nextCursor = iterator.getCursor().toWebSafeString();
			lastSeen = gists.get(0).date.getTime();
		}

		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor, lastSeen);
	}

	@GET
	@Path("/newest")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getNewest(@QueryParam("last_seen") final String lastSeen) throws
	Exception {
		return getNewestGists(lastSeen);
	}

	@GET
	@Path("/recent")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getRecent(@QueryParam("cursor") final String cursor) throws
	Exception {
		return getGists(null, cursor);
	}

	@GET
	@Path("/trending")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getTrending(@QueryParam("cursor") final String cursor) throws
	Exception {
		return getTrendingGists(cursor);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Gist getSingleGist(
			@PathParam("id") final String id) throws
			Exception {
		
		Long longId = Long.parseLong(id);
		
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());

		return ofy().load().key(gistKey).now();
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Gist updateSingleGist(
			@PathParam("id") final String id, final Map<String, String> postData) throws
			Exception {
		String content = postData.get("content");
		Long longId = Long.parseLong(id);
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
		Gist gist = ofy().load().key(gistKey).now();
		gist.setContent(content);
		
		ofy().save().entity(gist).now();
		
		return ofy().load().key(gistKey).now();
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteSingleGist(
			@PathParam("id") final String id, final Map<String, String> postData) throws
			Exception {
		Long longId = Long.parseLong(id);
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
		Gist gist = ofy().load().key(gistKey).now();
		
		try {
			ofy().delete().entity(gist).now();
		} catch(Exception e) {
			throw e;
		}
	}

	@POST
	@Path("/url/{url}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Gist addGist(
			@DefaultValue("default") @PathParam("url") final String gistUrl,
			final Map<String, String> postData) {
		UserService userService = UserServiceFactory.getUserService();

		// We set the above parent key on each Greeting entity in order to make the queries strong
		// consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
		// rate more than 1 write/second.
		String content = postData.get("content");

		URL url = ofy().load().key(Key.create(URL.class, gistUrl)).now();

		if (url == null) {
			url = new URL(gistUrl);
			ofy().save().entity(url).now();
		}
		
		Long userId = Long.parseLong(postData.get("userId"));
		User user = ofy().load().key(Key.create(User.class, userId)).now();

		Key<Gist> gistKey = null; 

		if (content != null && content.length() > 0) {
			Gist gist = new Gist();
			gist.setUrl(url);;
			gist.setUser(user);
			gist.setContent(content);
			gistKey = ofy().save().entity(gist).now();
	        mc.increment("gistCount_" + user.id.toString(),1);
		}
		
		return ofy().load().key(gistKey).now();
	}

	@GET
	@Path("url/{url}")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getGistsForURL(
			@DefaultValue("default") @PathParam("url") final String url,
			@QueryParam("cursor") final String cursor) throws
			Exception {
		return GistResource.getGists(url, cursor);
	}
}
