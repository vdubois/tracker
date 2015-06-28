'use strict';

angular.module('trackerApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
