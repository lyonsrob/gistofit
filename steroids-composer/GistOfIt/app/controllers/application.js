angular.module('gistOfItApp', [
'angular-embedly',
'mgcrea.pullToRefresh'
])
.config(function(embedlyServiceProvider){
        embedlyServiceProvider.setKey('42f4925174814d68b90d0758d932fe14');
})
.filter('domain', function() {
  return function(url) {
   var matches,
        output = "",
        urls = /\w+:\/\/([\w|\.]+)/;

    matches = urls.exec( url );

    if ( matches !== null ) output = matches[1];

    return output; 
  };
});
