/* globals $ */
'use strict';

angular.module('trackerApp')
    .directive('trackerAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
