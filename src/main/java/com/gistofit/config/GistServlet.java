package com.gistofit.config;

import static com.gistofit.model.OfyService.ofy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gistofit.model.EmbedlyExtract;
import com.gistofit.rest.URLResource;
import com.gistofit.model.Gist;
import com.gistofit.model.URL;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;


public class GistServlet extends HttpServlet {
	
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	  String gistId = req.getParameter("id");
	  Long longId = Long.parseLong(gistId);
		
	  Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());

	  Gist gist = ofy().load().key(gistKey).now();
	  URL url = gist.getUrl();
	  
	  URLResource resource = new URLResource();
	  String extractData = null; 
	  
	  try {
		extractData = resource.getExtract(url.getUrl());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

		Gson gson = new GsonBuilder()
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();

		EmbedlyExtract extract = gson.fromJson(extractData,  EmbedlyExtract.class);

      req.setAttribute("gist", gist);
      req.setAttribute("extract", extract);

      req.getRequestDispatcher("/view_gist.jsp").forward(req, resp);
  }
}