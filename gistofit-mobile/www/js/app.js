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

function unescape(html, $sanitize) {
  if (!html) return '';
  html = html.replace(/&gt;/g, '>').replace(/&lt;/g, '<').replace(/&amp;/g, '&').replace();
  return $sanitize ? $sanitize(html) : html;
}

// Declare app level module which depends on filters, and services
angular.module('gistofit', [
  'onsen',
  'gistofit.filters',
  'gistofit.services',
  'gistofit.directives',
  'angular-embedly',
  'ngSanitize',
  'ngAnimate',
  'ngTouch',
  'ui.comments',
  'mgcrea.pullToRefresh'
])
//.directive('slider', sliderDirective)
.directive('commenter', commenterDirective)
.directive('gistCard', gistCardDirective)
.directive('articleCard', articleCardDirective)
.directive('openExternal', openExternalDirective)
.config(function(embedlyServiceProvider){
        embedlyServiceProvider.setKey('42f4925174814d68b90d0758d932fe14');
  })
.config(function(commentsConfigProvider) {
  commentsConfigProvider.set({
    containerTemplate: 'views/comments.html',
    commentTemplate: 'views/comment.html',
    commentController: 'CommentCtrl',
    depthLimit: 10
  });
})
.controller('CommentCtrl', function($scope, $element, $timeout) {
  var children;
  $scope.collapsed = true;
  $scope.$on('$filledNestedComments', function(nodes) {
    $scope.collapsed = true;
    $timeout(function() {
      children = nodes;
      children.addClass('collapse').removeClass('in');
      children.collapse({
        toggle: false
      });
      // Stupid hack to wait for DOM insertion prior to setting up plugin
    }, 1);
  });
  $scope.$on('$emptiedNestedComments', function(nodes) {
    children = undefined;
  });
  $scope.collapse = function() {
    $scope.collapsed = children.hasClass('in');
    children.collapse('toggle');
  };

  $scope.addChildComment = function(comment) {
    var childComment = angular.extend(comment, {
      name: '@'+comment.name,
      date: new Date(),
      profileUrl: 'https://github.com/' + comment.name
    });
    if(!$scope.comment.children) {
      $scope.comment.children = [];
    }
    $scope.comment.children.push(childComment);
  };
  
  $scope.addParentComment = function(comment) {
    var parentComment = angular.extend(comment, {
      name: '@'+comment.name,
      date: new Date(),
      profileUrl: 'https://github.com/' + comment.name
    });
    $scope.comments.push(parentComment);
  };
});

document.addEventListener("deviceready", onDeviceReady, false);

var show_welcome; 

function onDeviceReady() {
    angular.bootstrap(document, ['gistofit']);
//    var gaPlugin;
//    gaPlugin = window.plugins.gaPlugin;
//    gaPlugin.init(successHandler, errorHandler, "UA-53420229-1", 10);
    var applaunchCount = window.localStorage.getItem('launchCount');
    show_welcome = applaunchCount ? 0 : 1;
    window.localStorage.setItem('launchCount',1);
    console.log(window.localStorage);
}
