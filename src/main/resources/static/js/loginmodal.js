$(document).ready(function(){


    $('#myButton').click(function(e) {
        $('#loginModal').modal('show');
        e.preventDefault();
        console.log("test");
    });



    $('#profileBtn').click(function(e) {
        $('#profileModal').modal('show');
        e.preventDefault();
        console.log("test");
    });

    $('.demo.modal')
        .modal()
    ;
    $('.standard.demo.modal')
        .modal('attach events', '.standard.demo.button')

});