// The contents of individual model .js files will be concatenated into dist/models.js

/*
(function(){
// Protects views where AngularJS is not loaded from errors
if ( typeof angular == 'undefined' ) {
	return;
};
*/

angular.module("gistOfItApp").factory('GistofitService', ['$http', 'embedlyService', function ($http, Embedly) {
    //var baseURL = 'http://127.0.0.1:8080/rest/v1';
    var baseURL = 'https://erudite-flag-623.appspot.com/rest/v1';

    function buildURL (method) {
        return baseURL + method;
    }

    return {
        getNewest: function (id) {
            var url = buildURL('/gist/newest');
            return $http({method: 'GET', url: url, params: {last_seen: id}});
        },
        getRecent: function (cursor) {
            var url = buildURL('/gist/recent');
            return $http({method: 'GET', url: url, params: {cursor: cursor}});
        },
        getTrending: function (cursor) {
            var url = buildURL('/gist/trending');
            return $http({method: 'GET', url: url});
        },
        getExtract: function (inputUrl, width, height, retina) {
            var escapedUrl = encodeURIComponent(inputUrl);
            var url = buildURL('/url/'+ escapedUrl + '/extract'); 
            return $http({method: 'GET', url: url});
        },
        setExtract: function (inputUrl, data) {
            var escapedUrl = encodeURIComponent(inputUrl);
            var url = buildURL('/url/'+ escapedUrl + '/extract'); 
            return $http({method: 'POST', url: url, data: data, headers: {'Content-Type': 'text/plain'}});
        },
        addGist: function (inputUrl, content) {
            url = buildURL ('/gist/url/' + encodeURIComponent(inputUrl));
            var data = {'content': content};
            return $http({method: 'POST', url: url, data: data});
            //return $http({method: 'POST', url: url, data: data, withCredentials: true});
        },
        deleteGist: function (id) {
            url = buildURL ('/gist/' + id);
            return $http({method: 'DELETE', url: url, data: {}, headers: {'Content-Type': 'application/json'}});
            //return $http({method: 'POST', url: url, data: data, withCredentials: true});
        },
        likeGist: function (id) {
            url = buildURL ('/like/' + id);
            return $http.post(url, {});
        },
        commentGist: function (id, comment) {
            url = buildURL ('/comment/' + id);
            return $http.post(url, {comment: comment});
        },
        getComments: function (id) {
            url = buildURL ('/comment/' + id);
            console.log(url);
            return $http({method: 'GET', url: url});
        },
        searchTopUrls: function (query) {
            var url = buildURL('/search/top/urls/?keyword=' + query); 
            return $http({method: 'GET', url: url});
        }
    }
}]);

//});
