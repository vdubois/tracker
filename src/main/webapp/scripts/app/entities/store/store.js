'use strict';

angular.module('trackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('store', {
                parent: 'entity',
                url: '/store',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.store.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/store/stores.html',
                        controller: 'StoreController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('store');
                        return $translate.refresh();
                    }]
                }
            })
            .state('storeDetail', {
                parent: 'entity',
                url: '/store/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.store.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/store/store-detail.html',
                        controller: 'StoreDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('store');
                        return $translate.refresh();
                    }]
                }
            });
    });
