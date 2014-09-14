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

'use strict';

function unescape(html, $sanitize) {
  if (!html) return '';
  html = html.replace(/&gt;/g, '>').replace(/&lt;/g, '<').replace(/&amp;/g, '&').replace();
  return $sanitize ? $sanitize(html) : html;
}

// Declare app level module which depends on filters, and services
angular.module('guestbook', [
  'guestbook.filters',
  'guestbook.services',
  'guestbook.directives',
  'ngSanitize',
  'ngRoute',
  'ngAnimate',
  'ui.comments',
 // 'doowb.angular-pusher'
])
.config(function($rootScopeProvider, $sceDelegateProvider) {
  //$rootScopeProvider.digestTtl(100);
  $sceDelegateProvider.resourceUrlWhitelist([
    'self',
    /.*\.redditmedia\.com.*/
  ]);
})
.run(function($rootScope) {
  $rootScope.baseUrl = window.location.href.replace(window.location.hash, '');
})
.config(function(commentsConfigProvider) {
  commentsConfigProvider.set({
    containerTemplate: 'views/comments.html',
    commentTemplate: 'views/comment.html',
    commentController: 'CommentCtrl',
    depthLimit: 10
  });
})
.directive('typeahead', typeheadDirective)
.directive('commenter', commenterDirective)
.factory('gistService', gistService)
.config(['$routeProvider', function($routeProvider) {
  $routeProvider
  .when('/:guestbookName*', {
    controller: 'GuestbookCtrl',
    templateUrl: 'guestbook.html'
  })
  .otherwise({ redirectTo: "/default" });
}]);

/*
.config(['PusherServiceProvider',
function(PusherServiceProvider) {
  PusherServiceProvider
    .setToken('e35338268e7cfc231c09')
    .setOptions({});
}
])
*/
