'use strict'

angular.module('gistOfItApp').controller('LoginCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {
    $scope.addonsUndefined = steroids.addons === void 0;
    if (!$scope.addonsUndefined) {
      $scope.ready = false;
      $scope.loginStatus = false;
      $scope.firstName = "Not fetched yet.";
      steroids.addons.facebook.ready.then(function() {
        return steroids.addons.facebook.getLoginStatus().then(function(response) {
          return $scope.$apply(function() {
            if ($scope.loginStatus = response.status === 'connected') {
            	steroids.initialView.dismiss();
            }
        });
        });
      });
      $scope.facebookLogin = function() {
        return steroids.addons.facebook.login().then(function() {
          return $scope.$apply(function() {
        	  var dismissAnimation = new steroids.Animation({
        		  transition: "flipHorizontalFromRight",
        		  duration: 1.0,
        		  curve: "easeInOut"
        		});

        		steroids.initialView.dismiss({
        		  animation: dismissAnimation
        		});
        	  return $scope.loginStatus = true;
          });
        });
      };
      $scope.facebookGraphQuery = function() {
        return steroids.addons.facebook.api('/me', {
          fields: 'first_name, last_name, picture.type(normal)'
        }).then(function(response) {
          return $scope.$apply(function() {
            $scope.firstName = response.first_name;
            $scope.lastName = response.last_name;
            $scope.profilePicture = response.picture.data.url;
	    return;
          });
        });
      };
      return $scope.facebookLogout = function() {
        return steroids.addons.facebook.logout().then(function() {
          return $scope.$apply(function() {
            return $scope.loginStatus = false;
          });
        });
      };
    }
}]);
