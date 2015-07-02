'use strict';

angular.module('trackerApp')
    .factory('StoreSearch', function ($resource) {
        return $resource('api/_search/stores/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
