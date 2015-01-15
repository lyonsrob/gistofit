'use strict'

angular.module('gistOfItApp').controller('CommentsCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {
    $scope.comments = {};
    $scope.loadComments = function (event) {
        if (event.data.recipient == "commentsView") {
            $scope.id = event.data.id;
            $scope.url = event.data.url;
            Gistofit.getComments($scope.url, $scope.id).then(function (response) {
                $scope.comments = response.data;
              });
              $scope.$apply();
        }
    };

    $scope.add = function () {
        var message = $scope.message;
        var id = $scope.id;
        var url = $scope.url;

        if (message != '') {
            Gistofit.commentGist(url, id, message);
        }
    }

    steroids.logger.log('loading comments!');
    
    window.addEventListener("message", $scope.loadComments);
    
    steroids.view.navigationBar.update({
        buttons: {
            left: [],
            right: [],
        }
    });

}]);
