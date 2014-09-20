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

package com.gistofit.config;

import com.gistofit.rest.GsonMessageBodyHandler;
import com.gistofit.rest.URLResource;
import com.gistofit.rest.GistResource;
import com.gistofit.rest.CommentResource;
import com.gistofit.rest.LikeResource;
import com.gistofit.rest.SearchResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class Resources extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<Class<?>>();
    s.add(URLResource.class);
    s.add(GistResource.class);
    s.add(CommentResource.class);
    s.add(LikeResource.class);
    s.add(SearchResource.class);
    s.add(GsonMessageBodyHandler.class);
    return s;
  }
}
