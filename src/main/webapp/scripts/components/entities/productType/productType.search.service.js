'use strict';

angular.module('trackerApp')
    .factory('ProductTypeSearch', function ($resource) {
        return $resource('api/_search/productTypes/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
