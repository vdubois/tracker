'use strict';

angular.module('trackerApp')
    .factory('BrandSearch', function ($resource) {
        return $resource('api/_search/brands/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
