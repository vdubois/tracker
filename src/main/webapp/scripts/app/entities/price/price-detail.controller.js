'use strict';

angular.module('trackerApp')
    .controller('PriceDetailController', function ($scope, $stateParams, Price, ProductToTrack) {
        $scope.price = {};
        $scope.load = function (id) {
            Price.get({id: id}, function(result) {
              $scope.price = result;
            });
        };
        $scope.load($stateParams.id);
    });
