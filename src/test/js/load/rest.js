'use strict';

var flow = {
  before: [],      // operations to do before anything
  beforeMain: [],  // operations to do before each iteration
  main: [  // the main flow for each iteration, #{INDEX} is unique iteration counter token
    { post: 'http://localhost:8080/rest/gists/gawker.com/4925812092436480/like', json: '{}' },
    { get: 'http://localhost:8080/rest/gists/gawker.com/4925812092436480' }
  ],
  after: []        // operations to do after everything is done
};

module.exports = flow;
