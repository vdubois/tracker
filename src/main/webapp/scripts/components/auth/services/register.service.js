'use strict';

angular.module('trackerApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


