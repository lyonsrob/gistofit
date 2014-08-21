/**
 * Created by Ariel Mashraki
 */

function gistCardDirective(GistofitService, embedlyService) {
  return {
    restrict:'E',
    templateUrl: 'views/gist_card.html',
    scope: true,
    controller: function($scope, GistofitService) {
        $scope.load_more = function(extract) {
            console.log(extract);
            $scope.more_extract = extract;
            $scope.navigator1.pushPage('views/more.html'); 
        }
        
        $scope.submit_comment_form = function () {
            GistofitService.commentGist($scope.gist.url, $scope.gist.id, $scope.gist_form.comment);
        }
        
        $scope.like_gist = function (url, id) {
            GistofitService.likeGist(url, id);
        }
    }, 
    link: function(scope, element) {
        scope.$watch('gist.url', function(newVal) {
            if (newVal) {
                GistofitService.getExtract(newVal)
                    .then(function(e){
                        if (e.data == undefined || e.data == '') {
                            embedlyService.extract(newVal)
                            .then(function(e){
                                GistofitService.setExtract(newVal, e.data);
                                if (newVal != e.data.url){
                                    GistofitService.setExtract(e.data.url, e.data);
                                }
                                scope.extract = e.data;
                            },
                             function(error) {
                                console.log(error);
                             });
                        } else {
                            scope.extract = e.data;
                        }
                    }, function(error) {
                        console.log(error);
                    });
            }
        });
    }
  }
}

function articleCardDirective(GistofitService,embedlyService) {
  return {
    restrict:'E',
    templateUrl: 'views/article_card.html',
    controller: function($scope, GistofitService) {
        $scope.load_more = function(gistId) {
            $scope.navigator1.pushPage('more.html'); 
        }
        
        $scope.submit_form = function () {
            GistofitService.addGist($scope.extract.url, $scope.gist_form.content);
        }
    }, 
    scope: true,
    link: function(scope, element) {
        scope.$watch('feed.link', function(newVal) {
            if (newVal) {
                GistofitService.getExtract(newVal)
                    .then(function(e){
                        if (e.data == undefined || e.data == '') {
                            embedlyService.extract(newVal)
                            .then(function(e){
                                GistofitService.setExtract(newVal, e.data);
                                if (newVal != e.data.url){
                                    GistofitService.setExtract(e.data.url, e.data);
                                }
                                scope.extract = e.data;
                            },
                             function(error) {
                                console.log(error);
                             });
                        } else {
                            scope.extract = e.data;
                        }
                    }, function(error) {
                        console.log(error);
                    });
            }
        });
    }
  }
}
