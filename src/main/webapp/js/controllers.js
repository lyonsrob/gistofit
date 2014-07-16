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

var GuestbookCtrl = ['$scope', '$http', '$location', '$routeParams', '$route',
  function ($scope, $http, $location, $routeParams) {

    $scope.guestbookName = $routeParams['guestbookName'];
    retrieveGuestbook($routeParams['guestbookName']);

    function retrieveGuestbook(guestbookName) {
      $http.get('/rest/guestbook/' + encodeURIComponent(guestbookName))
          .success(function(data) {
            $scope.comments = $scope.greetings = data.greetings;
            console.log($scope.comments);

            $scope.userServiceInfo = data.userServiceInfo;
            $scope.guestbookName = data.guestbookName;
            $scope.currentGuestbookName = data.guestbookName;
            $location.path(data.guestbookName);
          })
          .error(function(data, status) {
            console.log('Retrieving a guestbook failed with the status code: ' + status);
            console.log(data);
          });
    }

    $scope.submit_form = function () {
      $http.post(
          '/rest/guestbook/' + encodeURIComponent($scope.guestbookName),
          {'content': $scope.content, 'genre': $scope.genre})
          .success(function (data) {
            $scope.content = '';
            $scope.gebre = '';
            $scope.greetings = data.greetings;
            $scope.guestbookName = data.guestbookName;
            $scope.currentGuestbookName = data.guestbookName;
            $location.path(data.guestbookName);
          })
          .error(function(data, status) {
            console.log('Posting a message failed with the status code: ' + status);
            console.log(data);
          });
    };
  }
];

var CommentCtrl = ['$scope', '$element', '$timeout', 
 function($scope, $element, $timeout) {
  var children;
  $scope.collapsed = true;
  $scope.$on('$filledNestedComments', function(nodes) {
    $scope.collapsed = true;
    $timeout(function() {
      children = nodes;
      children.addClass('collapse').removeClass('in');
      children.collapse({
        toggle: false
      });
      // Stupid hack to wait for DOM insertion prior to setting up plugin
    }, 1);
  });
  $scope.$on('$emptiedNestedComments', function(nodes) {
    children = undefined;
  });
  $scope.collapse = function() {
    $scope.collapsed = children.hasClass('in');
    children.collapse('toggle');
  };

  $scope.addChildComment = function(comment) {
    var childComment = angular.extend(comment, {
      name: '@'+comment.name,
      date: new Date(),
      profileUrl: 'https://github.com/' + comment.name
    });
    if(!$scope.comment.children) {
      $scope.comment.children = [];
    }
    $scope.comment.children.push(childComment);
  };
}];
