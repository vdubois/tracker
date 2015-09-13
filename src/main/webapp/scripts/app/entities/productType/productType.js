'use strict';

angular.module('trackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('productType', {
                parent: 'entity',
                url: '/productType',
                data: {
                    roles: ['ROLE_USER', 'ROLE_ADMIN'],
                    pageTitle: 'trackerApp.productType.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/productType/productTypes.html',
                        controller: 'ProductTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('productType');
                        return $translate.refresh();
                    }]
                }
            })
            .state('productTypeDetail', {
                parent: 'entity',
                url: '/productType/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.productType.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/productType/productType-detail.html',
                        controller: 'ProductTypeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('productType');
                        return $translate.refresh();
                    }]
                }
            });
    });
