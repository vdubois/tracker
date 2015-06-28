'use strict';

angular.module('trackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('brand', {
                parent: 'entity',
                url: '/brand',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.brand.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/brand/brands.html',
                        controller: 'BrandController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('brand');
                        return $translate.refresh();
                    }]
                }
            })
            .state('brandDetail', {
                parent: 'entity',
                url: '/brand/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.brand.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/brand/brand-detail.html',
                        controller: 'BrandDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('brand');
                        return $translate.refresh();
                    }]
                }
            });
    });
