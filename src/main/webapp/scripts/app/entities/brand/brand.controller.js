'use strict';

angular.module('trackerApp')
    .controller('BrandController', function ($scope, Brand, User, BrandSearch, ParseLinks) {
        $scope.brands = [];
        $scope.users = User.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Brand.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.brands.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.brands = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Brand.get({id: id}, function(result) {
                $scope.brand = result;
                $('#saveBrandModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.brand.id != null) {
                Brand.update($scope.brand,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Brand.save($scope.brand,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Brand.get({id: id}, function(result) {
                $scope.brand = result;
                $('#deleteBrandConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Brand.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteBrandConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            BrandSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.brands = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveBrandModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.brand = {name: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
