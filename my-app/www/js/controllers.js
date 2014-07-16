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
      $http.get('http://localhost:8080/rest/guestbook/' + encodeURIComponent(guestbookName) + '?jsonp=?')
          .success(function(data) {
            $scope.greetings = data.greetings;
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
          'http://localhost:8080/rest/guestbook/' + encodeURIComponent($scope.guestbookName),
          {'content': $scope.content})
          .success(function (data) {
            $scope.content = '';
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
    
    $scope.direction = 'left';
    $scope.currentIndex = 0;

    $scope.setCurrentSlideIndex = function (index) {
        $scope.direction = (index > $scope.currentIndex) ? 'left' : 'right';
        $scope.currentIndex = index;
    };

    $scope.isCurrentSlideIndex = function (index) {
        return $scope.currentIndex === index;
    };

    $scope.prevSlide = function () {
        $scope.direction = 'left';
        $scope.currentIndex = ($scope.currentIndex < $scope.greetings.length - 1) ? ++$scope.currentIndex : 0;
    };

    $scope.nextSlide = function () {
        $scope.direction = 'right';
        $scope.currentIndex = ($scope.currentIndex > 0) ? --$scope.currentIndex : $scope.greetings.length - 1;
    };
  }

];
