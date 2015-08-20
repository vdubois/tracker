'use strict';

angular.module('trackerApp')
    .controller('PricesEvolutionController', function ($scope, PricesEvolution, ProductToTrack, $translate) {

        $scope.showChart = false;
        
        ProductToTrack.query(function (productToTrackResult) {
            $scope.productsToTrack = productToTrackResult;
        });
        
        $scope.loadGraphDataForProductToTrack = function (productToTrack) {
            PricesEvolution.get({id: productToTrack.id}).$promise
                .then(function (pricesEvolutionResult) {
                    var firstPointDate = new Date(pricesEvolutionResult[0].date);
                    var data = pricesEvolutionResult.map(function (element) {
                        var pointDate = new Date(element.date);
                        return [Date.UTC(pointDate.getFullYear(), pointDate.getMonth(), pointDate.getDate()), parseFloat(element.value, 10)];
                    });
                    var months = [];
                    var resetZoom = '';
                    var price = '';
                    $translate('months').then(function (translation) {
                        months = translation.split('|');
                        return $translate('resetZoom');
                    }).then(function (translation) {
                        resetZoom = translation;
                        return $translate('price');
                    }).then(function (translation) {
                        price = translation;
                    }).catch(function (error) {
                        console.error(error);
                    }).finally(function () {
                        Highcharts.setOptions({
                            lang: {
                                months: months,
                                shortMonths: months,
                                resetZoom: resetZoom
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
                                        text: price
                                    },
                                    showFirstLabel: false
                                },
                                plotOptions: {
                                    line: {
                                        lineWidth: 4,
                                        pointInterval: 3600000 * 12,
                                        pointStart: Date.UTC(firstPointDate.getFullYear(), firstPointDate.getMonth(), firstPointDate.getDate(), 0, 0, 0)
                                    }
                                }
                            },
                            series: [{
                                name: price,
                                data: data
                            }],
                            title: {
                                text: productToTrack.productType.name + ' ' + productToTrack.name + ' - '
                                + productToTrack.brand.name + ' (' + productToTrack.store.name + ')'
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
                        $scope.showChart = true;
                    });                     
                    
                }).finally(function () {
                });
        };
    });
