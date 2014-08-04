/**
 * Created by Ariel Mashraki
 */

function gistCardDirective(GistofitService) {
  return {
    restrict:'E',
    templateUrl: 'views/gist_card.html',
    scope: true,
    controller: function($scope, GistofitService) {
        $scope.load_more = function(gistId) {
            $scope.navigator1.pushPage(gistId + '.html'); 
        }
        
        $scope.submit_form = function () {
            GistofitService.addGist($scope.extract.url, $scope.gist_form.content);
        }
        
        $scope.like_gist = function (url, id) {
            GistofitService.likeGist(url, id);
        }
    }, 
    link: function(scope, element) {
        scope.$watch('gist.url', function(newVal) {
            var previousEmbedCode = scope.embedCode;
            if (newVal) {
                GistofitService.getExtract(newVal)
                    .then(function(e){
                        console.log(e.data);
                        scope.extract = e.data;
                    }, function(error) {
                        console.log(error);
                    });
            }
            }
        );
    }
  }
}

function articleCardDirective(GistofitService) {
  return {
    restrict:'E',
    templateUrl: 'views/article_card.html',
    controller: function($scope, GistofitService) {
        $scope.load_more = function(gistId) {
            $scope.navigator1.pushPage(gistId + '.html'); 
        }
        
        $scope.submit_form = function () {
            GistofitService.addGist($scope.extract.url, $scope.gist_form.content);
        }
    }, 
    scope: true,
    link: function(scope, element) {
        scope.$watch('feed.link', function(newVal) {
            var previousEmbedCode = scope.embedCode;
            if (newVal) {
                GistofitService.getExtract(newVal)
                    .then(function(e){
                        console.log(e.data);
                        scope.extract = e.data;
                    }, function(error) {
                        console.log(error);
                    });
            }
        });
    }
  }
}
