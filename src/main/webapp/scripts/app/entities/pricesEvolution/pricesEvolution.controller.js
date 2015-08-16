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
                    var data = pricesEvolutionResult.map(function (element) {
                        return parseFloat(element.value, 10);
                    });
                    Highcharts.setOptions({
                        lang: {
                            months: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août',
                                'Septembre', 'Octobre', 'Novembre', 'Décembre'],
                            shortMonths: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août',
                                'Septembre', 'Octobre', 'Novembre', 'Décembre'],
                            resetZoom: 'Annuler le zoom'
                        }
                    });                    
                        
                    $scope.chartConfig = {
                        options: {
                            chart: {
                                type: 'line',
                                zoomType: 'x'
                            },
                            tooltip: {
                                headerFormat: '<b>{series.name}</b><br>',
                                pointFormat: '{point.x:%e %B} : {point.y:.2f}€'
                            },
                            xAxis: {
                                type: 'datetime',
                                title: {
                                    text: 'Date'
                                },
                                labels: {
                                    x: 3,
                                    y: -3
                                }
                            },
                            yAxis: { // left y axis
                                title: {
                                    text: 'Prix'
                                },
                                showFirstLabel: false
                            },
                            plotOptions: {
                                line: {
                                    lineWidth: 4,
                                    pointInterval: 3600000 * 12,
                                    pointStart: Date.UTC(2015, 4, 31, 0, 0, 0)
                                }
                            }
                        },
                        series: [{
                            name: 'Prix',
                            data: data
                        }],
                        title: {
                            text: 'Evolution des prix pour ' + productToTrack.name + ' (marque ' 
                                + productToTrack.brand.name + ' chez ' + productToTrack.store.name + ')'
                        },
                        loading: false,
                        useHighStocks: false,
                        size: {
                            width: 1000,
                            height: 450
                        },
                        func: function (chart) {
                            console.log(chart);
                        }
                    };
                    /*$scope.data = pricesEvolutionResult;
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
                    };*/
                    /*$scope.data.forEach(function(row) {
                        row.date = new Date(row.date);
                    });*/
                    $scope.showChart = true;
                    
                }).finally(function () {
                });
        };
    });
