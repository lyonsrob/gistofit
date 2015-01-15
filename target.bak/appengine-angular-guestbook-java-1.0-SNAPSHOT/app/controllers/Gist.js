'use strict'

angular.module('gistOfItApp').controller('GistCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {

    $scope.setAddData = function(event) {
        if (event.data.recipient == "gistModalView") {
            $scope.gist = "";
            $scope.extract = event.data.feed.extract;
            $scope.url = event.data.feed.extract.url;
            $scope.$apply();
        }
    }
    
    $scope.addGist = function(gist) {
        Gistofit.addGist($scope.url, gist);
        steroids.modal.hide();
    }

    window.addEventListener("message", $scope.setAddData);

}    
]);
