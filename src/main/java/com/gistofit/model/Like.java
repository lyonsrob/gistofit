package com.gistofit.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
@Cache
public class Like extends Jsonifiable {

  @Index
  @Load Ref<Gist> gist;
	  
  public static Key<Like> key(long id) {
	    return Key.create(Like.class, id);
  }
  
  /**
   * Primary identifier of this URL.  Specific to GistOfIt.
   */
  @Id
  @Getter
  @Expose
  public Long id;

  @Index
  @Getter
  @Setter
  Date created;
  
  public Gist getGist() { return gist.get(); }
  public void setGist(Gist value) { gist = Ref.create(value); }
}