'use strict';

angular.module('trackerApp')
    .controller('PricesEvolutionController', function ($scope, PricesEvolution, ProductToTrack, User, ProductTypeSearch, ParseLinks, Account) {

        $scope.showChart = false;
        
        ProductToTrack.query(function (productToTrackResult) {
            $scope.productsToTrack = productToTrackResult;
        });
        
        $scope.loadGraphDataForProductToTrack = function (productToTrack) {
            PricesEvolution.get({id: productToTrack.id}).$promise
                .then(function (pricesEvolutionResult) {
                    $scope.data = pricesEvolutionResult;
                    $scope.options = {
                        axes: {date: {type: "date"}},
                        series: [{
                            y: "value",
                            label: productToTrack.name,
                            color: "#8c564b"
                        }],
                        tooltip: {
                            mode: "scrubber",
                            formatter: function (x, y, series) {
                                return moment(x).fromNow() + ' : ' + y;
                            }
                        }
                    };
                    $scope.data.forEach(function(row) {
                        row.date = new Date(row.date);
                    });
                    $scope.showChart = true;
                    
                }).finally(function () {
                });
        };
    });
