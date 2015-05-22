/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gistofit.model;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * User of the GistOfIt application.  This instance maintains all information
 * needed to interact with and manage a single user.  That includes things like
 * their tokens for social services or third-party APIs, their role within
 * GistOfIt, their profile information, etc.
 *
 * Data members of this class are intentionally public in order to allow Gson
 * to function effectively when generating JSON representations of the class.
 *
 * @author vicfryzel@google.com (Vic Fryzel)
 */
@Entity
@Cache
@EqualsAndHashCode(of="id", callSuper=false)
public class User extends Jsonifiable {
	
  /**
   * @param id ID of User for which to get a Key.
   * @return Key representation of given User's ID.
   */
  public static Key<User> key(long id) {
    return Key.create(User.class, id);
  }

  /**
   * Primary identifier of this User.  Specific to GistOfIt.
   */
  @Id
  @Getter
  @Expose
  public Long id;

  /**
   * Primary email address of this User.
   */
  @Getter
  @Setter
  @Index
  @Expose
  public String email;
  
  /**
   * First name of this User.
   */
  @Getter
  @Setter
  @Index
  @Expose
  public String firstName;

  
  /**
   * Last name of this User.
   */
  @Getter
  @Setter
  @Index
  @Expose
  public String lastName;

  @Getter
  @Setter
  transient String salt;
  
  @Getter
  @Setter
  transient String password;
  
  @Getter
  @Setter
  @Expose
  public String gender;
  
  @Getter
  @Setter
  @Expose
  public String ageRange;
  
  @Getter
  @Setter
  @Expose
  public String profilePicture;

  /**
   * UUID identifier of this User within Facebook products.
   */
  @Getter
  @Setter
  @Index
  @Expose
  public String facebookUserId;
  
  @Getter
  @Setter
  public ArrayList<FB_Like> Related;
	
  public static class FB_Like
	{
		@Getter @Setter String Description;
		@Getter @Setter String Title;
		@Getter @Setter String Url; 
		@Getter @Setter int ThumbnailWidth;
		@Getter @Setter double Score;
		@Getter @Setter int ThumbnailHeight;
		@Getter @Setter String ThumbnailUrl;
	}
  
}
