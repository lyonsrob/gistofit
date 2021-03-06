angular.module('gistOfItApp', [
'angular-embedly',
'mgcrea.pullToRefresh',
'ngSanitize',
'ngAnimate'
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
})
.directive('whenScrolled', function() {
    return function(scope, elm, attr) {
        var raw = elm[0];
        
        elm.bind('scroll', function() {
            if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                scope.$apply(attr.whenScrolled);
            }
        });
    };
});

steroids.on('ready', function() {
    steroids.statusBar.onTap(
      function() {
            var target = $(".content");
         
            //disable touch scroll to kill existing inertial movement
            target.css({
                '-webkit-overflow-scrolling' : 'auto',
                'overflow-y' : 'hidden'
            });
         
            //animate
            target.animate({ scrollTop: 0}, 300, "swing", function(){
         
                //re-enable touch scrolling
                target.css({
                    '-webkit-overflow-scrolling' : 'touch',
                    'overflow-y' : 'scroll'
                });
            });
        }
    );
});
    
// Search view
var searchView = new steroids.views.WebView("http://localhost/views/Search/search.html");
searchView.preload(); // Prelaod for faster view transitions
    
var navButton = new steroids.buttons.NavigationBarButton();
navButton.imagePath = "/icons/navicon@2x.png"
navButton.onTap = function() { 
    steroids.drawers.show();
};

var searchButton = new steroids.buttons.NavigationBarButton();
searchButton.imagePath = "/icons/search@2x.png"
searchButton.onTap = function() { 
    var fastSlide = new steroids.Animation({  transition: "slideFromRight",  duration: .2});

    // Navigate to your view
    steroids.layers.push(
    {
        view: searchView,
        animation: fastSlide
    });
};

steroids.view.navigationBar.update({
    overrideBackButton: false,
    buttons: {
      left: [navButton],
      right: [searchButton]
    }
});

