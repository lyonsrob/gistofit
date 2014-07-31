/**
 * Created by Ariel Mashraki
 */

function gistcardDirective(embedlyService) {
  return {
    restrict:'E',
    templateUrl: 'views/gist_card.html',
    scope:{
        url: '@',
        extract: '@',
    },
    link: function(scope, element) {
        scope.$watch('url', function(newVal) {
            var previousEmbedCode = scope.embedCode;
            if (newVal) {
                embedlyService.extract(newVal)
                    .then(function(e){
                        console.log(e.data);
                        scope.extract = e.data;
                    }, function(error) {
                        console.log(error);
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
  }
}

function articlecardDirective(embedlyService) {
  return {
    restrict:'E',
    templateUrl: 'views/article_card.html',
    scope:{
        url: '@',
        extract: '@',
    },
    link: function(scope, element) {
        scope.$watch('url', function(newVal) {
            var previousEmbedCode = scope.embedCode;
            if (newVal) {
                embedlyService.extract(newVal)
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
