/**
 * Created by Ariel Mashraki
 */

function gisturlDirective($timeout, gistService) {
  return {
    restrict:'E',
    templateUrl: 'views/gisturl.html',
    link: function(scope, elm, attr) {
    }//end linkFn
  }
}
