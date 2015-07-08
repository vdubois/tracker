'use strict';

angular.module('trackerApp')
    .factory('ProductToTrack', function ($resource, DateUtils) {
        return $resource('api/productToTracks/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
