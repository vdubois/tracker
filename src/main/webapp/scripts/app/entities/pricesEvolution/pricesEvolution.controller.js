'use strict';

angular.module('trackerApp')
    .controller('PricesEvolutionController', function ($scope, PricesEvolution, ProductToTrack, User, ProductTypeSearch, ParseLinks, Account) {

        $scope.showChart = false;
        
        ProductToTrack.query(function (productToTrackResult) {
            $scope.productsToTrack = productToTrackResult;
        });
        
        $scope.loadGraphDataForProductToTrack = function (productToTrackId) {
            PricesEvolution.get({id: productToTrackId}, function (pricesEvolutionResult) {
                console.log(pricesEvolutionResult);
                $scope.data = [
                    {
                        x: "2015-06-19T14:35:15.725Z",
                        val_0: 300,
                        val_1: 0,
                        val_2: 0,
                        val_3: 0
                    },
                    {
                        x: "2015-06-20T14:35:15.725Z",
                        val_0: 362,
                        val_1: 3.894,
                        val_2: 8.47,
                        val_3: 14.347
                    },
                    {
                        x: "2015-06-21T14:35:15.725Z",
                        val_0: 380,
                        val_1: 7.174,
                        val_2: 13.981,
                        val_3: 19.991
                    },
                    {
                        x: "2015-06-22T14:35:15.725Z",
                        val_0: 350,
                        val_1: 9.32,
                        val_2: 14.608,
                        val_3: 13.509
                    }
                ];
                $scope.options = {
                    axes: {x: {type: "date"}},
                    series: [
                        {
                            y: "val_0",
                            label: "On the left !",
                            color: "#8c564b"
                        }
                    ],
                    tooltip: {
                        mode: "scrubber",
                        formatter: function (x, y, series) {
                            return moment(x).fromNow() + ' : ' + y;
                        }
                    }
                };
                $scope.data.forEach(function(row) {
                    row.x = new Date(row.x);
                });
                $scope.showChart = true;
            });
        };
        
        /*$scope.data = [
            {
                x: "2015-06-19T14:35:15.725Z",
                val_0: 300,
                val_1: 0,
                val_2: 0,
                val_3: 0
            },
            {
                x: "2015-06-20T14:35:15.725Z",
                val_0: 362,
                val_1: 3.894,
                val_2: 8.47,
                val_3: 14.347
            },
            {
                x: "2015-06-21T14:35:15.725Z",
                val_0: 380,
                val_1: 7.174,
                val_2: 13.981,
                val_3: 19.991
            },
            {
                x: "2015-06-22T14:35:15.725Z",
                val_0: 350,
                val_1: 9.32,
                val_2: 14.608,
                val_3: 13.509
            }
        ];*/

        /*$scope.options = {
            axes: {x: {type: "date"}},
            series: [
                {
                    y: "val_0",
                    label: "On the left !",
                    color: "#8c564b"
                }
            ],
            tooltip: {
                mode: "scrubber",
                formatter: function (x, y, series) {
                    return moment(x).fromNow() + ' : ' + y;
                }
            }
        };*/

        /*$scope.data.forEach(function(row) {
            row.x = new Date(row.x);
        });*/
        /*$scope.productTypes = [];
        $scope.users = User.query();
        $scope.account = Account.get();
        $scope.page = 1;
        $scope.loadAll = function() {
            ProductType.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.productTypes.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.productTypes = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            ProductType.get({id: id}, function(result) {
                $scope.productType = result;
                $('#saveProductTypeModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.productType.id != null) {
                ProductType.update($scope.productType,
                    function () {
                        $scope.refresh();
                    });
            } else {
                ProductType.save($scope.productType,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            ProductType.get({id: id}, function(result) {
                $scope.productType = result;
                $('#deleteProductTypeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ProductType.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteProductTypeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ProductTypeSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.productTypes = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveProductTypeModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.productType = {name: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };*/
    });
