'use strict';

angular.module('trackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('alert', {
                parent: 'entity',
                url: '/alert',
                data: {
                    roles: ['ROLE_USER', 'ROLE_ADMIN'],
                    pageTitle: 'trackerApp.alert.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/alert/alerts.html',
                        controller: 'AlertController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('alert');
                        return $translate.refresh();
                    }]
                }
            })
            .state('alertDetail', {
                parent: 'entity',
                url: '/alert/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'trackerApp.alert.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/alert/alert-detail.html',
                        controller: 'AlertDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('alert');
                        return $translate.refresh();
                    }]
                }
            });
    });
