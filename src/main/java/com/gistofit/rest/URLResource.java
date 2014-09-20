package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import com.gistofit.model.EmbedlyExtract;
import com.gistofit.model.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gistofit.domain.GistSearch;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.search.Index;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;

@Path("/v1/url")
public class URLResource {

	private static final int CACHE_PERIOD = 86400;

	private final MemcacheService mc = MemcacheServiceFactory
			.getMemcacheService();

	//TODO: Need to tell GsonMessageBody to ignore an object that is already JSON?
	@GET
	@Path("/{url}/extract")
	@Produces(MediaType.TEXT_PLAIN)
	public String getExtract(
			@DefaultValue("default") @PathParam("url") final String url) throws
			Exception {
		String mcKey = url + "_extract";
		return (String) mc.get(mcKey);
	}


	//TODO: Need to tell GsonMessageBody to ignore an object that is already JSON?
	@POST
	@Path("/{url}/extract")
	@Consumes(MediaType.TEXT_PLAIN)
	public String setExtract(
			@DefaultValue("default") @PathParam("url") final String urlString,
			final String extractData) throws
			Exception {
		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject)parser.parse(extractData);
		Gson gson = new GsonBuilder()
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();

		EmbedlyExtract extract = gson.fromJson(obj, EmbedlyExtract.class);

		String cleanUrl = extract.getUrl();
		URL url = ofy().load().key(Key.create(URL.class, cleanUrl)).now();

		if (url == null) {
			url = new URL(cleanUrl);
			ofy().save().entity(url).now();

			GistSearch search = new GistSearch();
			Index index = GistSearch.getIndex();

			for (EmbedlyExtract.Keyword keyword : extract.getKeywords()) {
				index.put(search.buildDocument(keyword.getName(), cleanUrl, keyword.getScore()));
			}

			for (EmbedlyExtract.Entity entity : extract.getEntities()) {
				index.put(search.buildDocument(entity.getName(), cleanUrl, entity.getCount()));		
			}			
		}


		String mcKey = cleanUrl + "_extract";
		mc.put(mcKey, extractData, Expiration.byDeltaSeconds(CACHE_PERIOD),
				SetPolicy.SET_ALWAYS);
		mcKey = extract.getOriginalUrl() + "_extract";
		mc.put(mcKey, extractData, Expiration.byDeltaSeconds(CACHE_PERIOD),
				SetPolicy.SET_ALWAYS);

		return extractData;
	}
}
