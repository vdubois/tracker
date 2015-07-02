'use strict';

angular.module('trackerApp')
    .controller('ProductTypeDetailController', function ($scope, $stateParams, ProductType, User) {
        $scope.productType = {};
        $scope.load = function (id) {
            ProductType.get({id: id}, function(result) {
              $scope.productType = result;
            });
        };
        $scope.load($stateParams.id);
    });
