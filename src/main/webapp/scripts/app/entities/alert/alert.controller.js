'use strict';

angular.module('trackerApp')
    .controller('AlertController', function ($scope, Alert, ProductToTrack, AlertSearch, ParseLinks, Principal) {
        $scope.alerts = [];
        $scope.page = 1;
        $scope.alerts = [];
        $scope.currentUser = {};
        $scope.loadAll = function() {
            Principal.identity().then(function (identityData) {
                $scope.currentUser = identityData;
                ProductToTrack.query().$promise.then(function (productsToTrackData) {
                    $scope.productsToTrack = productsToTrackData.filter(function (product) {
                        return product.user.login === $scope.currentUser.login;
                    });
                });
                Alert.query({page: $scope.page, per_page: 20}, function(result, headers) {
                    result = result.filter(function (alert) {
                        return alert.productToTrack.user.login === $scope.currentUser.login;
                    });
                    $scope.links = ParseLinks.parse(headers('link'));
                    for (var i = 0; i < result.length; i++) {
                        $scope.alerts.push(result[i]);
                    }
                });
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.alerts = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Alert.get({id: id}, function(result) {
                $scope.alert = result;
                $('#saveAlertModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.alert.id != null) {
                Alert.update($scope.alert,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Alert.save($scope.alert,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Alert.get({id: id}, function(result) {
                $scope.alert = result;
                $('#deleteAlertConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Alert.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteAlertConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveAlertModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.alert = {priceLowerThan: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
