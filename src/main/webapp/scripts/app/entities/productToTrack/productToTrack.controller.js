'use strict';

angular.module('trackerApp')
    .controller('ProductToTrackController', function ($scope, ProductToTrack, User, ProductType, Brand, Store, Price, ProductToTrackSearch, ParseLinks) {
        $scope.productToTracks = [];
        $scope.users = User.query();
        $scope.producttypes = ProductType.query();
        $scope.brands = Brand.query();
        $scope.stores = Store.query();
        $scope.prices = Price.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            ProductToTrack.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.productToTracks.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.productToTracks = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            ProductToTrack.get({id: id}, function(result) {
                $scope.productToTrack = result;
                $('#saveProductToTrackModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.productToTrack.id != null) {
                ProductToTrack.update($scope.productToTrack,
                    function () {
                        $scope.refresh();
                    },
                    function () {
                        alert("Le sélecteur que vous avez saisi n'est pas approprié. Veuillez le modifier.");
                    });
            } else {
                ProductToTrack.save($scope.productToTrack,
                    function () {
                        $scope.refresh();
                    },
                    function () {
                        alert("Le sélecteur que vous avez saisi n'est pas approprié. Veuillez le modifier.");
                    });
            }
        };

        $scope.delete = function (id) {
            ProductToTrack.get({id: id}, function(result) {
                $scope.productToTrack = result;
                $('#deleteProductToTrackConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ProductToTrack.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteProductToTrackConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ProductToTrackSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.productToTracks = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveProductToTrackModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.productToTrack = {name: null, trackingUrl: null, trackingDomSelector: null, lastKnownPrice: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
