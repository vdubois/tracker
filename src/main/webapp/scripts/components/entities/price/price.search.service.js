'use strict';

angular.module('trackerApp')
    .factory('PriceSearch', function ($resource) {
        return $resource('api/_search/prices/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
