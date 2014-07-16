/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

// Declare app level module which depends on filters, and services
angular.module('guestbook', [
  'onsen.directives',
  'guestbook.filters',
  'guestbook.services',
  'guestbook.directives',
  'ngSanitize',
  'ngRoute',
  'ngAnimate',
  'ngTouch',
])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider
  .when('/:guestbookName*', {
    controller: 'GuestbookCtrl',
    templateUrl: 'guestbook.html'
  })
  .otherwise({ redirectTo: '/default' });
}]);
/*.animation('.slide-animation', function () {
    return {
        beforeAddClass: function (element, className, done) {
            var scope = element.scope();

            console.log(element);
            console.log(element.width());

            if (className == 'ng-hide') {
                var finishPoint = element.width();
                if(scope.direction !== 'right') {
                    finishPoint = -finishPoint;
                }
                TweenMax.to(element, 0.5, {left: finishPoint, onComplete: done });
            }
            else {
                done();
            }
        },
        removeClass: function (element, className, done) {
            var scope = element.scope();

            if (className == 'ng-hide') {
                element.removeClass('ng-hide');

                var startPoint = element.width();
                if(scope.direction === 'right') {
                    startPoint = -startPoint;
                }

                TweenMax.fromTo(element, 0.5, { left: startPoint }, {left: 0, onComplete: done });
            }
            else {
                done();
            }
        }
    };
});*/

document.addEventListener('deviceready', function() {
    angular.bootstrap(document, ['guestbook']);
    }, false);
