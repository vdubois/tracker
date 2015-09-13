'use strict';

angular.module('trackerApp')
    .controller('PricesEvolutionController', function ($scope, PricesEvolution, ProductToTrack, $translate, Principal) {

        $scope.showChart = false;
        $scope.currentUser = {};

        Principal.identity().then(function (identityData) {
            $scope.currentUser = identityData;
            ProductToTrack.query(function (productToTrackResultData) {
                productToTrackResultData = productToTrackResultData.filter(function (productToTrack) {
                    return productToTrack.user.login === $scope.currentUser.login;
                });
                var result = [];
                var productsToTrackResult = [];
                productToTrackResultData.forEach(function(item) {
                    if(result.indexOf(item.name) < 0) {
                        result.push(item.name);
                        productsToTrackResult.push(item);
                    }
                });
                $scope.productsToTrack = productsToTrackResult;
            });
        });
        
        $scope.loadGraphDataForProductToTrack = function (productToTrack) {
            PricesEvolution.get({id: productToTrack.name}).$promise
                .then(function (pricesEvolutionResult) {
                    var series = [];
                    var data = [];
                    for (var propertyName in pricesEvolutionResult) {
                        if (pricesEvolutionResult.hasOwnProperty(propertyName) 
                            && propertyName !== '$promise'
                            && propertyName !== '$resolved') {
                            data = pricesEvolutionResult[propertyName].map(function (element) {
                                var pointDate = new Date(element.createdAt);
                                return [Date.UTC(pointDate.getFullYear(), pointDate.getMonth(), pointDate.getDate(),
                                    pointDate.getHours(), pointDate.getMinutes(), pointDate.getSeconds()), parseFloat(element.value, 10)];
                            });
                            series.push({name: propertyName, data: data});
                        }
                    }
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
                                        lineWidth: 4
                                    }
                                }
                            },
                            series: series,
                            title: {
                                text: productToTrack.productType.name + ' ' + productToTrack.name + ' - '
                                + productToTrack.brand.name
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
