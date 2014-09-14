// The contents of individual model .js files will be concatenated into dist/models.js

/*
(function(){
// Protects views where AngularJS is not loaded from errors
if ( typeof angular == 'undefined' ) {
	return;
};
*/

angular.module("gistOfItApp").factory('GistofitService', ['$http', function ($http) {
    var domain = 'http://127.0.0.1:8080/';
    //var domain = 'https://erudite-flag-623.appspot.com/';

    function buildURL (method) {
        return domain + method;
    }

    return {
        getNewest: function (id) {
            var url = buildURL('rest/gists/newest');
            return $http({method: 'GET', url: url, params: {last_seen: id}});
        },
        getRecent: function (cursor) {
            var url = buildURL('rest/gists/recent');
            return $http({method: 'GET', url: url, params: {cursor: cursor}});
        },
        getTrending: function () {
            var url = buildURL('rest/gists/trending');
            return $http({method: 'GET', url: url});
        },
        getExtract: function (inputUrl, width, height, retina) {
            var escapedUrl = encodeURIComponent(inputUrl);
            var url = buildURL('rest/gists/'+ escapedUrl + '/extract'); 
            return $http({method: 'GET', url: url});
        },
        setExtract: function (inputUrl, data) {
            var escapedUrl = encodeURIComponent(inputUrl);
            var url = buildURL('rest/gists/'+ escapedUrl + '/extract'); 
            return $http({method: 'POST', url: url, data: data, headers: {'Content-Type': 'text/plain'}});
        },
        addGist: function (url, content) {
            url = buildURL ('rest/gists/' + encodeURIComponent(url));
            var data = {'content': content};
            return $http({method: 'POST', url: url, data: data, withCredentials: true});
        },
        likeGist: function (url, id) {
            url = buildURL ('rest/gists/' + encodeURIComponent(url) + "/" + id + "/like");
            return $http.post(url, {});
        },
        commentGist: function (url, id, comment) {
            url = buildURL ('rest/gists/' + encodeURIComponent(url) + "/" + id + "/comment");
            return $http.post(url, {comment: comment});
        },
        getComments: function (url, id) {
            url = buildURL ('rest/gists/' + encodeURIComponent(url) + "/" + id + "/comments");
            console.log(url);
            return $http({method: 'GET', url: url});
        },
        searchTopUrls: function (query) {
            var url = buildURL('rest/gists/search/top/urls/?keyword='+ query); 
            return $http({method: 'GET', url: url});
        }
    }
}]);

//});
