'use strict';

angular.module('trackerApp')
    .controller('BrandDetailController', function ($scope, $stateParams, Brand, User) {
        $scope.brand = {};
        $scope.load = function (id) {
            Brand.get({id: id}, function(result) {
              $scope.brand = result;
            });
        };
        $scope.load($stateParams.id);
    });
