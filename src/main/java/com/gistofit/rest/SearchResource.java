package com.gistofit.rest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gistofit.domain.GistSearch;
import com.gistofit.model.Gist;

@Path("/v1/search")
public class SearchResource {

	@GET
	@Path("/{keyword}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Gist> searchGists(@DefaultValue("default") @PathParam("keyword") final String keyword) throws
	Exception {
		GistSearch gistSearch = new GistSearch();
		LinkedHashSet<String> urls = gistSearch.getUrls(keyword, 15);
		List<Gist> gists = new ArrayList<Gist>();
		
		for (String url : urls) {
			gists.addAll(GistResource.getGists(url, null).getGists());
		}
		
		return gists;
	}
	
	@GET
	@Path("/top/keywords")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedHashSet<String> searchKeywords(@QueryParam("keyword") final String keyword) throws
	Exception {
		GistSearch gistSearch = new GistSearch();
		LinkedHashSet<String> top = gistSearch.getKeywords(keyword, 15);
		return top;
	}
	
	@GET
	@Path("/top/urls")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedHashSet<String> searchTopUrls(@QueryParam("keyword") final String keyword) throws
	Exception {
		GistSearch gistSearch = new GistSearch();
		LinkedHashSet<String> top = gistSearch.getUrls(keyword, 15);
		return top;
	}
}
