'use strict';

angular.module('trackerApp')
    .controller('ProductToTrackController', function ($scope, ProductToTrack, User, ProductType, Brand, Store, Price, ProductToTrackSearch, ParseLinks, usSpinnerService, Principal) {
        $scope.productToTracks = [];
        $scope.users = User.query();
        ProductType.query().$promise.then(function (productTypeData) {
            $scope.producttypes = productTypeData.filter(function (productType) {
                return productType.user.login === $scope.currentUser.login;
            });
        });
        Brand.query().$promise.then(function (brandsData) {
            $scope.brands = brandsData.filter(function (brand) {
                return brand.user.login === $scope.currentUser.login;
            });
        });
        Store.query().$promise.then(function (storesData) {
            $scope.stores = storesData.filter(function (store) {
                return store.user.login === $scope.currentUser.login;
            });
        });
        $scope.page = 1;
        $scope.currentUser = {};
        $scope.loadAll = function() {
            Principal.identity().then(function (identityData) {
                $scope.currentUser = identityData;
                ProductToTrack.query({page: $scope.page, per_page: 20}, function(result, headers) {
                    result = result.filter(function (productToTrack) {
                        return productToTrack.user.login === $scope.currentUser.login;
                    });
                    $scope.links = ParseLinks.parse(headers('link'));
                    for (var i = 0; i < result.length; i++) {
                        $scope.productToTracks.push(result[i]);
                    }
                });
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
            usSpinnerService.spin('spinner');
            $scope.saveButtonDisabled = true;
            if ($scope.productToTrack.id != null) {
                console.log($scope.productToTrack);
                ProductToTrack.update($scope.productToTrack).$promise
                    .then(function () {
                        $scope.refresh();
                    })
                    .catch(function () {
                        alert("Le sélecteur que vous avez saisi n'est pas approprié. Veuillez le modifier.");
                    })
                    .finally(function () {
                        usSpinnerService.stop('spinner');
                        $scope.saveButtonDisabled = false;
                    });
            } else {
                console.log($scope.productToTrack);
                ProductToTrack.save($scope.productToTrack).$promise
                    .then(function () {
                        $scope.refresh();
                    })
                    .catch(function () {
                        alert("Le sélecteur que vous avez saisi n'est pas approprié. Veuillez le modifier.");
                    })
                    .finally(function () {
                        usSpinnerService.stop('spinner');
                        $scope.saveButtonDisabled = false;
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

        $scope.refresh = function () {
            $scope.reset();
            $('#saveProductToTrackModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.productToTrack = {name: null, trackingUrl: null, trackingDomSelector: null, lastKnownPrice: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            usSpinnerService.stop('spinner');
            $scope.saveButtonDisabled = false;
        };
    });
