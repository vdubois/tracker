'use strict';

angular.module('trackerApp')
    .factory('ProductToTrackSearch', function ($resource) {
        return $resource('api/_search/productToTracks/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
