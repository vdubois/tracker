'use strict';

angular.module('trackerApp')
    .controller('StoreController', function ($scope, Store, User, StoreSearch, ParseLinks) {
        $scope.stores = [];
        $scope.users = User.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Store.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.stores.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.stores = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Store.get({id: id}, function(result) {
                $scope.store = result;
                $('#saveStoreModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.store.id != null) {
                Store.update($scope.store,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Store.save($scope.store,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Store.get({id: id}, function(result) {
                $scope.store = result;
                $('#deleteStoreConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Store.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteStoreConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            StoreSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.stores = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveStoreModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.store = {name: null, baseDomSelector: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
