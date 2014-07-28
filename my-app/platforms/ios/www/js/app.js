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
angular.module('guestbook', [
  'onsen.directives',
  'guestbook.filters',
  'guestbook.services',
  'guestbook.directives',
  'ngSanitize',
  'ngAnimate',
  'ngTouch',
  'angular-embedly'
])
.directive('slider', sliderDirective)
.directive('gistCard', gistcardDirective)
.directive('openExternal', openExternalDirective)
.config(function(embedlyServiceProvider){ embedlyServiceProvider.setKey('42f4925174814d68b90d0758d932fe14'); });

document.addEventListener('deviceready', function() {
    angular.bootstrap(document, ['guestbook']);
}, false);
