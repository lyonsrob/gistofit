/**
 * Created by Ariel Mashraki
 */

function sliderDirective() {
  return {
    restrict:'E',
    templateUrl: 'views/slider.html',
    link: function(scope, elm, attr) {

        scope.direction = 'left';
        scope.currentIndex = 0;

        scope.setCurrentSlideIndex = function (index) {
            scope.direction = (index > scope.currentIndex) ? 'left' : 'right';
            scope.currentIndex = index;
        };

        scope.isCurrentSlideIndex = function (index) {
            return scope.currentIndex === index;
        };

        scope.prevSlide = function (greetings) {
            console.log(greetings);
            scope.direction = 'left';
            scope.currentIndex = (scope.currentIndex < greetings.length - 1) ? ++scope.currentIndex : 0;
        };

        scope.nextSlide = function (greetings) {
            console.log(greetings);
            scope.direction = 'right';
            scope.currentIndex = (scope.currentIndex > 0) ? --scope.currentIndex : greetings.length - 1;
        };

    }//end linkFn
  }
}
