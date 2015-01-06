/**
 * Created by moran on 21/06/14.
 */

var angularEmbedly = angular.module('angular-embedly', []);
//service
(function (module) {
    module.provider('embedlyService', function () {
        var key;
        this.setKey = function(userKey) {
            key = userKey;
            return key;
        }
        this.getKey = function() {
            return key;
        }

        function embedly($http) {
            this.embed = function(inputUrl) {
                var escapedUrl = encodeURI(inputUrl);
                var embedlyRequest = 'http://api.embed.ly/1/oembed?key=' + key + '&url=' +  escapedUrl;
                return $http({method: 'GET', url: embedlyRequest});
            };
            this.extract = function(inputUrl, maxWidth, maxHeight) {
                var escapedUrl = encodeURI(inputUrl);
                var embedlyRequest = 'http://api.embed.ly/1/extract?key=' + key + '&url=' +  escapedUrl;

                if (maxWidth > 0 && maxHeight > 0) {
                    embedlyRequest = embedlyRequest + "&image_width=" + maxWidth + "&image_height=" + maxHeight + "&errorUrl=http%3A%2F%2Fmedia.tumblr.com%2Ftumblr_m9e0vfpA7K1qkbsaa.jpg&image_grow=true&image_method=resize";
                } 

                return $http({method: 'GET', url: embedlyRequest});
            };
        }


        this.$get = function($http) {
            return new embedly($http);
        };

    })
//controller
    module.controller('emEmbedCtrl', function($scope) {
        $scope.embedCode = '';
    })
//directive
    module.directive('emEmbed', function(embedlyService) {
        return {
            restrict: 'E',
            scope:{
                urlsearch: '@'
            },
            controller: 'emEmbedCtrl',
            link: function(scope, element) {
                scope.$watch('urlsearch', function(newVal) {
                    var previousEmbedCode = scope.embedCode;
                    if (newVal) {
                        embedlyService.embed(newVal)
                            .then(function(data){
                                switch(data.data.type) {
                                    case 'video':
                                        scope.embedCode = data.data.html;
                                        break;
                                    case 'photo':
                                        scope.embedCode = '<img src="' + data.data.url + '">';
                                        break;
                                    default:
                                        scope.embedCode = '';
                                }
                                if(previousEmbedCode !== scope.embedCode) {
                                    // embed code was changed from last call and has to be replaced in DOM
                                    element.html('<div>' + scope.embedCode + '</div>');
                                }
                            }, function(error) {
                                // promise rejected
                                var previousEmbedCode = scope.embedCode;
                                scope.embedCode = '';
                                if(previousEmbedCode !== scope.embedCode) {
                                    element.html('<div>' + scope.embedCode + '</div>');
                                }
                            });
                    }
                });
            }
        };
    })
})(angularEmbedly);

// The contents of individual model .js files will be concatenated into dist/models.js

/*
(function(){
// Protects views where AngularJS is not loaded from errors
if ( typeof angular == 'undefined' ) {
	return;
};
*/

angular.module("gistOfItApp").factory('GistofitService', ['$http', 'embedlyService', function ($http, Embedly) {
    var baseURL = 'http://127.0.0.1:8080/rest/v1';
    //var baseURL = 'https://erudite-flag-623.appspot.com/rest/v1';

    function buildURL (method) {
        return baseURL + method;
    }
    var Gistofit = {};

    Gistofit.getNewest = function (id) {
        var url = buildURL('/gist/newest');
        return $http({method: 'GET', url: url, params: {last_seen: id}});
    };
    Gistofit.getRecent = function (cursor) {
        var url = buildURL('/gist/recent');
        return $http({method: 'GET', url: url, params: {cursor: cursor}});
    };
    Gistofit.getTrending = function (cursor) {
        var url = buildURL('/gist/trending');
        return $http({method: 'GET', url: url});
    };
    Gistofit.getExtract = function (obj, url) {
        var escapedUrl = encodeURIComponent(url);
        var extractUrl = buildURL('/url/'+ escapedUrl + '/extract');

        $http({method: 'GET', url: extractUrl}).then(function (response) {
            obj.extract = response.data;
            if (obj.extract == undefined || obj.extract == '') {
                Embedly.extract(url).then(function(e){
                    Gistofit.setExtract(url, e.data);
                    obj.extract = e.data;
                },
                 function(error) {
                    console.log(error);
                 });
            } 
        });
    };
    Gistofit.setExtract = function (inputUrl, data) {
        var escapedUrl = encodeURIComponent(inputUrl);
        var url = buildURL('/url/'+ escapedUrl + '/extract'); 
        return $http({method: 'POST', url: url, data: data, headers: {'Content-Type': 'text/plain'}});
    };
    Gistofit.addGist = function (inputUrl, content) {
        url = buildURL ('/gist/url/' + encodeURIComponent(inputUrl));
        var data = {'content': content};
        return $http({method: 'POST', url: url, data: data});
        //return $http({method: 'POST', url: url, data: data, withCredentials: true});
    };
    Gistofit.deleteGist = function (id) {
        url = buildURL ('/gist/' + id);
        return $http({method: 'DELETE', url: url, data: {}, headers: {'Content-Type': 'application/json'}});
        //return $http({method: 'POST', url: url, data: data, withCredentials: true});
    };
    Gistofit.likeGist = function (id) {
        url = buildURL ('/like/' + id);
        return $http.post(url, {});
    };
    Gistofit.commentGist = function (id, comment) {
        url = buildURL ('/comment/' + id);
        return $http.post(url, {comment: comment});
    };
    Gistofit.getComments = function (id) {
        url = buildURL ('/comment/' + id);
        return $http({method: 'GET', url: url});
    };
    Gistofit.searchTopUrls = function (query) {
        var url = buildURL('/search/top/urls/?keyword=' + query); 
        return $http({method: 'GET', url: url});
    };
    
    return Gistofit; 
}]);
