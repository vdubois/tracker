'use strict';

angular.module('trackerApp')
    .controller('StoreDetailController', function ($scope, $stateParams, Store, User) {
        $scope.store = {};
        $scope.load = function (id) {
            Store.get({id: id}, function(result) {
              $scope.store = result;
            });
        };
        $scope.load($stateParams.id);
    });
