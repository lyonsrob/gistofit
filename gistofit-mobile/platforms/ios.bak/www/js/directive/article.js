/**
 * Created by Ariel Mashraki
 */

function articleDirective(GistofitService, embedlyService) {
  return {
    restrict: 'EA',
    templateUrl: 'views/article.html',
    transclude: true,
    replace: true,
    scope: { 'extract': '=extractData' },
    link: function(scope, element) {
        console.log(scope.extract);
    }
  }
}
