'use strict';

angular.module('trackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('productToTrack', {
                parent: 'entity',
                url: '/productToTrack',
                data: {
                    roles: ['ROLE_USER', 'ROLE_ADMIN'],
                    pageTitle: 'trackerApp.productToTrack.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/productToTrack/productToTracks.html',
                        controller: 'ProductToTrackController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('productToTrack');
                        return $translate.refresh();
                    }]
                }
            })
            .state('productToTrackDetail', {
                parent: 'entity',
                url: '/productToTrack/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.productToTrack.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/productToTrack/productToTrack-detail.html',
                        controller: 'ProductToTrackDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('productToTrack');
                        return $translate.refresh();
                    }]
                }
            });
    });
