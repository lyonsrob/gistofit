package com.gistofit.domain;


import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.LinkedHashSet;

public class GistSearch {
	  private static final String INDEX_NAME = "Gists";
		
	  public static Index getIndex() {
	    IndexSpec indexSpec = IndexSpec.newBuilder().setName(INDEX_NAME).build();
	    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	  }

	  public Document buildDocument(
	      String keywordEntity, String url, int rank) {
	    Document.Builder builder = Document.newBuilder()
	        .addField(Field.newBuilder().setName("url").setText(url))
	        .addField(Field.newBuilder().setName("keyword_entity").setText(keywordEntity))
	        .addField(Field.newBuilder().setName("suggest").setText(buildSuggestions(keywordEntity)))
	        .setRank(rank);
	     
	    Document doc = builder.build();

	    return doc;
	  }

	  private String buildSuggestions(String str) {
		  String suggestions = "";
		  
		  for (String word : str.split(" ")) {
			  String prefix = " ";
			  for (char letter : word.toCharArray()) {
				  prefix = prefix + letter;
				  suggestions = suggestions.concat(prefix);
			  }
		  }
		  
		  return suggestions;
	  }
	  
	  public LinkedHashSet<String> getKeywords(String searchString, int resultCount) {

		    // TODO(user): Use memcache
		    Results<ScoredDocument> results = getIndex().search(searchString);
		    
		    LinkedHashSet<String> keywords = new LinkedHashSet<String>();
		    
		    for (ScoredDocument document : results) {
		      if (keywords.size() >= resultCount) {
		        break;
		      }
		      
		      keywords.add(document.getOnlyField("keyword_entity").getText().toLowerCase());
		    
		    }
		    return keywords;
		  }
	  
	  public LinkedHashSet<String> getUrls(String searchString, int resultCount) {

	    // TODO(user): Use memcache
	    Results<ScoredDocument> results = getIndex().search(searchString);
	    
	    LinkedHashSet<String> urls = new LinkedHashSet<String>();
	    
	    for (ScoredDocument document : results) {
	      if (urls.size() >= resultCount) {
	        break;
	      }
	      
	      urls.add(document.getOnlyField("url").getText());
	    
	    }
	    
	    return urls;
	  }
}