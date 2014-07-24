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

angular.module('guestbook').controller('GistCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {

    $scope.loadRecentGists = function() {
        var url = 'http://localhost:8080/rest/recent?JSON_CALLBACK=?';

        Gistofit.getGists(url).then(function (response) {
            console.log(response);
            $scope.gists = response.data.gists; 
            $scope.cursor = response.data.cursor; 

            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }

    $scope.submit_form = function () {
      $http.post(
          'http://localhost:8080/rest/guestbook/' + encodeURIComponent($scope.guestbookName),
          {'content': $scope.content, 'genre': $scope.genre})
          .success(function (data) {
            $scope.content = '';
            $scope.genre = '';
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

    $scope.loadRecentGists();
}    
]);

angular.module('guestbook').factory('GistofitService', ['$http', function ($http) {
    return {
        getGists: function (url) {
            return $http.get(url);
        }
    }
}]);
