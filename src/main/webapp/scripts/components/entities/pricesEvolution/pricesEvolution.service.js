'use strict';

angular.module('trackerApp')
    .factory('PricesEvolution', function ($resource, DateUtils) {
        return $resource('api/pricesevolution/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                },
                isArray: true
            }
        });
    });
