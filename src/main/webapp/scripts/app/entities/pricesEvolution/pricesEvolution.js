'use strict';

angular.module('trackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('pricesEvolution', {
                parent: 'entity',
                url: '/pricesEvolution',
                data: {
                    roles: ['ROLE_USER', 'ROLE_ADMIN'],
                    pageTitle: 'trackerApp.productToTrack.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/pricesEvolution/pricesEvolution.html',
                        controller: 'PricesEvolutionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('pricesEvolution');
                        return $translate.refresh();
                    }]
                }
            });
    });
