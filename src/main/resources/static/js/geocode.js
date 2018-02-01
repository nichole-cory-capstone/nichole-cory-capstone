$(document).ready(function() {
    "use strict";
    $('#search-btn').click(function (e) {
        // e.preventDefault();
        var inputCity = $('#city-name').val();
            var url = '/search?location=';
            window.location.href = url + inputCity;
            return false;
        });
    $('#register-btn').click(function (e) {
        // e.preventDefault();
        window.location.href = "/register";
        return false;
    });
});