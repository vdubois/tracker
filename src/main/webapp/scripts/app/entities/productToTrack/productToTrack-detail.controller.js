'use strict';

angular.module('trackerApp')
    .controller('ProductToTrackDetailController', function ($scope, $stateParams, ProductToTrack, User, ProductType, Brand, Store, Price) {
        $scope.productToTrack = {};
        $scope.load = function (id) {
            ProductToTrack.get({id: id}, function(result) {
              $scope.productToTrack = result;
            });
        };
        $scope.load($stateParams.id);
    });
