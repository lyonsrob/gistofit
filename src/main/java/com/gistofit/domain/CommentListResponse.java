/*
 * Copyright 2013 Google Inc.
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

package com.gistofit.domain;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Takashi Matsuo <tmatsuo@google.com>
 * Date: 4/8/13
 * Time: 11:43 AM
 */
public class CommentListResponse {

  private final String url;

  private final List<Comment> comments;

  private final UserServiceInfo userServiceInfo;
  
  private final String nextCursor;
  
  public CommentListResponse(String url, List<Comment> comments,
                           UserServiceInfo userServiceInfo, String nextCursor) {
    this.url = url;
    this.comments = comments;
    this.userServiceInfo = userServiceInfo;
    this.nextCursor = nextCursor;
  }

  public String getURL() {
    return url;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public UserServiceInfo getUserServiceInfo() {
    return userServiceInfo;
  }
  
  public String getNextCursor() {
	  return nextCursor;
  }
}
