'use strict';

angular.module('trackerApp')
    .factory('ProductType', function ($resource, DateUtils) {
        return $resource('api/productTypes/:id', {}, {
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
