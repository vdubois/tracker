'use strict';

angular.module('trackerApp')
    .factory('AlertSearch', function ($resource) {
        return $resource('api/_search/alerts/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
