'use strict'

angular.module('gistOfItApp').controller('GistCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {
    function messageReceived(event) {

      // check that the message is intended for us
      if (event.data.sender == "loginView") {
    	$scope.userId = event.userId;
        alert(event.data.message)
      }
    }

    window.addEventListener("message", messageReceived);

    $scope.setAddData = function(event) {
        if (event.data.recipient == "gistModalView") {
            $scope.gist = "";
            $scope.extract = event.data.feed.extract;
            $scope.url = event.data.feed.extract.url;
            $scope.$apply();
        }
    }
    
    $scope.addGist = function(gist) {
        Gistofit.addGist($scope.url, gist, $scope.userId);
        steroids.modal.hide();
    }

    window.addEventListener("message", $scope.setAddData);

}    
]);
