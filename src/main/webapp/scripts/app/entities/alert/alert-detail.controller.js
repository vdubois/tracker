'use strict';

angular.module('trackerApp')
    .controller('AlertDetailController', function ($scope, $stateParams, Alert, ProductToTrack) {
        $scope.alert = {};
        $scope.load = function (id) {
            Alert.get({id: id}, function(result) {
              $scope.alert = result;
            });
        };
        $scope.load($stateParams.id);
    });
